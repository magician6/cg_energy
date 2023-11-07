package cn.wilmar.cg_energy.service;
import cn.wilmar.cg_energy.entity.BadPoint;
import cn.wilmar.cg_energy.entity.Factory;
import cn.wilmar.cg_energy.entity.JobLog;
import cn.wilmar.cg_energy.entity.PointValue;
import cn.wilmar.cg_energy.exception.ServiceException;
import cn.wilmar.cg_energy.mapper.BadPointMapper;
import cn.wilmar.cg_energy.mapper.FactoryMapper;
import cn.wilmar.cg_energy.mapper.JobLogMapper;
import cn.wilmar.cg_energy.mapper.PointValueMapper;
import cn.wilmar.cg_energy.util.CgEnergyUtils;
import cn.wilmar.cg_energy.vm.PointDto;
import cn.wilmar.cg_energy.vm.PointValueCheckDto;
import cn.wilmar.cg_energy.vm.PointValueDto;
import cn.wilmar.cg_energy.vm.PointValueQueryDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 点位数据相关service
 * @Author: fengzixin
 * @Date: 2022/8/22
 */
@Slf4j
@Service
public class PointValueService {

    @Autowired
    FactoryMapper factoryMapper;
    @Autowired
    PointValueMapper pointValueMapper;
    @Autowired
    InterfaceService interfaceService;
    @Autowired
    BadPointMapper badPointMapper;
    @Autowired
    JobLogMapper jobLogMapper;
    //@Autowired
    //JdbcTemplate jdbcTemplate;

    /**
     * 保存点位数据
     * @param factoryId
     */
    public void savePointValue(Long factoryId, String dataType, List<String> badPointNames) {
        String jobName = "保存点位数据 " + dataType;
        try {
            long start = System.currentTimeMillis();
            LocalDateTime now = LocalDateTime.now();
            Factory factory = factoryMapper.selectByPrimaryKey(factoryId);
            // 获取CPM点位
            List<PointDto> allPoints = interfaceService.getCpmPoints(factory.getCode());
            if (allPoints == null || allPoints.size() <= 0) {
                log.error("未查询到CPM点位...");
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(false)
                        .finishTime(LocalDateTime.now())
                        .failMessage("未查询到CPM点位...")
                        .build());
                return;
            }

            List<PointDto> points = new ArrayList<>();

            if (badPointNames != null && badPointNames.size() > 0) {
                List<String> collect = badPointNames.stream().distinct().collect(Collectors.toList());
                for (String badPointName : collect) {
                    Optional<PointDto> op = allPoints.stream().filter(i -> Objects.equals(i.getFullName(), badPointName)).findFirst();
                    if (op.isPresent()) {
                        points.add(op.get());
                    }
                }
            } else {
                points = allPoints;
            }

            if ((badPointNames != null && badPointNames.size() > 0) && (points == null || points.size() <= 0)) {
                log.error("坏数据点位：【" + StringUtils.join(badPointNames, ",") + "】未在最新查询中找到对应点位");
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(false)
                        .finishTime(LocalDateTime.now())
                        .failMessage("坏数据点位：【" + StringUtils.join(badPointNames, ",") + "】未在最新查询中找到对应点位")
                        .build());
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.CHINA);
            List<String> pointNames = points.stream().map(PointDto::getFullName).collect(Collectors.toList());
            Map<String, Object> resultMap = getPointValueDto(factory, pointNames, points, now, dataType);
            PointValueDto valueDto = (PointValueDto)resultMap.get("pointValueDto");
            List<PointValueDto.Result> results = valueDto.getResults();
            List<PointValue> pointValues =(List<PointValue>) resultMap.get("pointValues");

            // 失败的点位List
            List<PointValue> badPoints = new ArrayList<>();

            // 封装pointValue数据 根据fullName匹配result数据
            for (PointValue pointValue : pointValues) {
                Optional<PointValueDto.Result> op = results.stream().filter(i -> Objects.equals(i.getName(), pointValue.getFullName())).findFirst();
                if (!op.isPresent()) {
                    badPoints.add(pointValue);
                    continue;
                }
                PointValueDto.Result result = op.get();
                if (result.getData() == null) {
                    badPoints.add(pointValue);
                    continue;
                }
                PointValueDto.Result.Data data = result.getData();
                // 非good数据
                if (!data.getIsGood()) {
                    pointValue.setIsGood(false);
                    badPoints.add(pointValue);
                    continue;
                }
                pointValue.setIsGood(true);

                try {
                    BigDecimal value = new BigDecimal(data.getValue());
                    pointValue.setValue(value);
                    // 单位取值以pims为准
                    if (data.getUom() != null) {
                        pointValue.setUnit(data.getUom());
                    }
                    if (value == null || data.getTimestamp() == null) {
                        pointValue.setIsGood(false);
                        badPoints.add(pointValue);
                        continue;
                    }
                    Date parse = dateFormat.parse(data.getTimestamp());
                    pointValue.setResDate(new java.sql.Date(parse.getTime()));
                    Date parse1 = timeFormat.parse(data.getTimestamp().replace("Z", "+0000"));
                    pointValue.setResTime(new java.sql.Time(parse1.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("为点位实体赋值PIMS数据失败：" + e.getMessage());
                    pointValue.setIsGood(false);
                    badPoints.add(pointValue);
                    continue;
                }

            }
            List<PointValue> goodList = pointValues.stream().filter(i -> i.getIsGood()!= null && i.getIsGood() == true).collect(Collectors.toList());
            // 存入数据库
            for (PointValue pointValue : goodList) {
                pointValueMapper.insertData(pointValue);
            }
            //List<Object []> insertList1 = new ArrayList<>();
            //for (PointValue pv : pointValues) {
            //    insertList1.add(new Object[] {pv.getFactoryId(), pv.getDataType(), pv.getQueryDate(), pv.getQueryTime(), pv.getResDate(), pv.getResTime(),
            //            pv.getIsGood(), pv.getName(), pv.getFullName(), pv.getDesc(), pv.getUnit(), pv.getWorkshop(), pv.getWorkshopDesc(), pv.getWorkshopDescEn(),
            //            pv.getEnergyType(), pv.getEnergyType2Desc(), pv.getEnergyType2DescEn(), pv.getEnergyClass(), pv.getEnergyClassDesc(), pv.getValue()});
            //}
            //jdbcTemplate.batchUpdate("insert into point_value (factory_id,data_type,query_date,query_time,res_date,res_time,status,name," +
            //        "full_name,description,unit,workshop,workshop_desc,workshop_desc_en,energy_type,energy_type_desc,energy_type_desc_en," +
            //        "energy_class,energy_class_en,value) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", insertList1);

            long end = System.currentTimeMillis();
            log.info("工厂" + factory.getCode() + "更新 " + goodList.size() + " 条数据，耗时：" + (end - start) + "ms");
            // 删除对应类型的Bad点位
            badPointMapper.deleteByFactoryIdAndDataType(factoryId, dataType);

            // 测试用数据
            //if (badPointNames == null) {
            //    badPoints.add(pointValues.get(0));
            //    badPoints.add(pointValues.get(1));
            //    badPoints.add(pointValues.get(2));
            //    badPoints.add(pointValues.get(3));
            //}
            // 如果包含非Good数据的点位
            if (badPoints.size() > 0) {
                // 如果10分钟后不是该时刻了，则放弃尝试
                if (LocalDateTime.now().getHour() != LocalDateTime.now().plusMinutes(10L).getHour() && dataType.equals("day")) {
                    jobLogMapper.insertSelective(JobLog.builder()
                            .factoryId(factoryId)
                            .jobName(jobName)
                            .executeStatus(true)
                            .finishTime(LocalDateTime.now())
                            .executeTime(end - start)
                            .successQuantity(goodList.size())
                            .badQuantity(badPoints.size())
                            .build());
                    return;
                }
                // 保存非good数据
                List<BadPoint> badPointList = new ArrayList<>();
                for (PointValue badPoint : badPoints) {
                    BadPoint bp = BadPoint.builder().factoryId(factoryId).dataType(dataType).queryTime(now).fullName(badPoint.getFullName()).build();
                    badPointList.add(bp);
                }
                badPointMapper.insertList(badPointList);
                // 如果包含坏数据的点位，则开启新线程在10分钟后执行
                //new Thread(){
                //    @Override
                //    public void run() {
                //        try {
                //            System.out.println(Thread.activeCount());
                //            log.info("开启新线程...");
                //            Thread.sleep(60000);
                //            log.info("等待10分钟...开始获取数据...");
                //            System.out.println(Thread.activeCount());
                //            //savePointValue1(factoryId, dataType, badPoints.stream().map(PointValue::getFullName).collect(Collectors.toList()));
                //        } catch (Exception e) {
                //            e.printStackTrace();
                //        }
                //    }
                //}.start();
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(true)
                        .finishTime(LocalDateTime.now())
                        .executeTime(end - start)
                        .successQuantity(goodList.size())
                        .badQuantity(badPoints.size())
                        .build());
                return;
            }
            jobLogMapper.insertSelective(JobLog.builder()
                    .factoryId(factoryId)
                    .jobName(jobName)
                    .executeStatus(true)
                    .finishTime(LocalDateTime.now())
                    .executeTime(end - start)
                    .successQuantity(goodList.size())
                    .build());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            jobLogMapper.insertSelective(JobLog.builder()
                    .factoryId(factoryId)
                    .jobName(jobName)
                    .executeStatus(false)
                    .finishTime(LocalDateTime.now())
                    .failMessage("发生异常：" + e.getMessage())
                    .build());
        }
    }

    /**
     * 保存点位数据
     * @param factoryCode
     */
    public void savePointValueByQueryTime(String factoryCode, String dataType, List<String> badPointNames, String queryTime) {
        String jobName = "保存点位数据 " + dataType;
        Factory factory = factoryMapper.getByCode(factoryCode);
        if (factory == null) {
            throw new ServiceException(factoryCode + "工厂不存在");
        }
        Long factoryId = factory.getId();
        try {
            long start = System.currentTimeMillis();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.parse(queryTime, df);
            // 获取CPM点位
            List<PointDto> allPoints = interfaceService.getCpmPoints(factoryCode);
            if (allPoints == null || allPoints.size() <= 0) {
                log.error("未查询到CPM点位...");
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(false)
                        .finishTime(LocalDateTime.now())
                        .failMessage("未查询到CPM点位...")
                        .build());
                return;
            }

            List<PointDto> points = new ArrayList<>();

            if (badPointNames != null && badPointNames.size() > 0) {
                List<String> collect = badPointNames.stream().distinct().collect(Collectors.toList());
                for (String badPointName : collect) {
                    Optional<PointDto> op = allPoints.stream().filter(i -> Objects.equals(i.getFullName(), badPointName)).findFirst();
                    if (op.isPresent()) {
                        points.add(op.get());
                    }
                }
            } else {
                points = allPoints;
            }

            if ((badPointNames != null && badPointNames.size() > 0) && (points == null || points.size() <= 0)) {
                log.error("坏数据点位：【" + StringUtils.join(badPointNames, ",") + "】未在最新查询中找到对应点位");
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(false)
                        .finishTime(LocalDateTime.now())
                        .failMessage("坏数据点位：【" + StringUtils.join(badPointNames, ",") + "】未在最新查询中找到对应点位")
                        .build());
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.CHINA);
            List<String> pointNames = points.stream().map(PointDto::getFullName).collect(Collectors.toList());
            Map<String, Object> resultMap = getPointValueDto(factory, pointNames, points, now, dataType);
            PointValueDto valueDto = (PointValueDto)resultMap.get("pointValueDto");
            List<PointValueDto.Result> results = valueDto.getResults();
            List<PointValue> pointValues =(List<PointValue>) resultMap.get("pointValues");
            // ADD BY QIANWEI FOR S3 PUT
            S3Service s3S = new S3Service();
            s3S.AmazonS3Util();
            s3S.sourFileName = "C:/TEST_2023-11-07" + ".CSV";
            ListToCsvConverter listCsv = new ListToCsvConverter();

            // 失败的点位List
            List<PointValue> badPoints = new ArrayList<>();

            // 封装pointValue数据 根据fullName匹配result数据
            for (PointValue pointValue : pointValues) {
                Optional<PointValueDto.Result> op = results.stream().filter(i -> Objects.equals(i.getName(), pointValue.getFullName())).findFirst();
                if (!op.isPresent()) {
                    badPoints.add(pointValue);
                    continue;
                }
                PointValueDto.Result result = op.get();
                if (result.getData() == null) {
                    badPoints.add(pointValue);
                    continue;
                }
                PointValueDto.Result.Data data = result.getData();
                // 非good数据
                if (!data.getIsGood()) {
                    pointValue.setIsGood(false);
                    badPoints.add(pointValue);
                    continue;
                }
                pointValue.setIsGood(true);

                try {
                    BigDecimal value = new BigDecimal(data.getValue());
                    pointValue.setValue(value);
                    // 单位取值以pims为准
                    if (data.getUom() != null) {
                        pointValue.setUnit(data.getUom());
                    }
                    if (value == null || data.getTimestamp() == null) {
                        pointValue.setIsGood(false);
                        badPoints.add(pointValue);
                        continue;
                    }
                    Date parse = dateFormat.parse(data.getTimestamp());
                    pointValue.setResDate(new java.sql.Date(parse.getTime()));
                    Date parse1 = timeFormat.parse(data.getTimestamp().replace("Z", "+0000"));
                    pointValue.setResTime(new java.sql.Time(parse1.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("为点位实体赋值PIMS数据失败：" + e.getMessage());
                    pointValue.setIsGood(false);
                    badPoints.add(pointValue);
                    continue;
                }

            }
            List<PointValue> goodList = pointValues.stream().filter(i -> i.getIsGood()!= null && i.getIsGood() == true).collect(Collectors.toList());
            // 存入数据库
            for (PointValue pointValue : goodList) {
                pointValueMapper.insertData(pointValue);
            }
//            // ADD BY QIANWEI 点位数据转换为CSV，暂存本地
//            listCsv.convert(goodList,s3S.sourFileName);
//            // ADD BY QIANWEI 本地csv文件写入S3
//            s3S.fileObjKeyName = "goodList_" +  System.currentTimeMillis() + ".csv";
//            s3S.putObject(s3S.sourFileName,s3S.fileObjKeyName);

            long end = System.currentTimeMillis();
            log.info("工厂" + factory.getCode() + "更新 " + goodList.size() + " 条数据，耗时：" + (end - start) + "ms");
            // 删除对应类型的Bad点位
            badPointMapper.deleteByFactoryIdAndDataType(factoryId, dataType);

            // 如果包含非Good数据的点位
            if (badPoints.size() > 0) {
                // 如果10分钟后不是该时刻了，则放弃尝试
                if (LocalDateTime.now().getHour() != LocalDateTime.now().plusMinutes(10L).getHour() && dataType.equals("day")) {
                    jobLogMapper.insertSelective(JobLog.builder()
                            .factoryId(factoryId)
                            .jobName(jobName)
                            .executeStatus(true)
                            .finishTime(LocalDateTime.now())
                            .executeTime(end - start)
                            .successQuantity(goodList.size())
                            .badQuantity(badPoints.size())
                            .build());
                    return;
                }
                // 保存非good数据
                List<BadPoint> badPointList = new ArrayList<>();
                for (PointValue badPoint : badPoints) {
                    BadPoint bp = BadPoint.builder().factoryId(factoryId).dataType(dataType).queryTime(now).fullName(badPoint.getFullName()).build();
                    badPointList.add(bp);
                }
                badPointMapper.insertList(badPointList);
                jobLogMapper.insertSelective(JobLog.builder()
                        .factoryId(factoryId)
                        .jobName(jobName)
                        .executeStatus(true)
                        .finishTime(LocalDateTime.now())
                        .executeTime(end - start)
                        .successQuantity(goodList.size())
                        .badQuantity(badPoints.size())
                        .build());
                return;
            }
            jobLogMapper.insertSelective(JobLog.builder()
                    .factoryId(factoryId)
                    .jobName(jobName)
                    .executeStatus(true)
                    .finishTime(LocalDateTime.now())
                    .executeTime(end - start)
                    .successQuantity(goodList.size())
                    .build());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            jobLogMapper.insertSelective(JobLog.builder()
                    .factoryId(factoryId)
                    .jobName(jobName)
                    .executeStatus(false)
                    .finishTime(LocalDateTime.now())
                    .failMessage("发生异常：" + e.getMessage())
                    .build());
        }
    }

    private Map<String, Object> getPointValueDto(Factory factory, List<String> pointNames, List<PointDto> points, LocalDateTime now, String dataType) throws Exception {
        Map<String, Object> map = new HashMap<>();

        PointValueQueryDto queryDto = new PointValueQueryDto();
        queryDto.setPointNames(pointNames);
        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.sql.Date queryDate = new java.sql.Date(dateFormat.parse(nowStr).getTime());
        java.sql.Time queryTime = new java.sql.Time(timeFormat.parse(nowStr).getTime());

        queryDto.setTimestamp(nowStr);
        //queryDto.setTimestamp("2022-08-08 10:00:00");
        queryDto.setGoodOnly(true);
        String postContent = CgEnergyUtils.jacksonWriteString(queryDto);
        PointValueDto valueDto = interfaceService.getPimsPointValue(postContent);

        List<PointValue> pointValues = new ArrayList<>();

        for (PointDto point : points) {
            PointValue pointValue = PointValue.builder()
                    .factoryId(factory.getId())
                    .factoryCode(factory.getCode())
                    .dataType(dataType)
                    .name(point.getName())
                    .fullName(point.getFullName())
                    .desc(point.getDesc())
                    .unit(point.getUnit())
                    .workshop(point.getWorkshop())
                    .workshopDesc(point.getWorkshopDesc())
                    .workshopDescEn(point.getWorkshopDescEn())
                    .energyType(point.getEnergyType())
                    .energyType2Desc(point.getEnergyType2Desc())
                    .energyType2DescEn(point.getEnergyType2DescEn())
                    .energyClass(point.getEnergyClass())
                    .energyClassDesc(point.getEnergyClassDesc())
                    .queryDate(queryDate)
                    .queryTime(queryTime)
                    .isRenewEnergy(point.getIsRenewEnergy() == 1 ? true : false)
                    .persistence(false)
                    .groupLine(point.getGroupLine())
                    .groupLineDesc(point.getGroupLineDesc())
                    .groupLineDescEn(point.getGroupLineDescEn())
                    .build();
            //BeanUtils.copyProperties(pointValue, point);
            pointValues.add(pointValue);
        }
        map.put("pointValueDto", valueDto);
        map.put("pointValues", pointValues);
        return map;
    }


    /**
     * 删除指定日期的数据
     * @param factoryId
     */
    public void deleteDailyData(Long factoryId, LocalDateTime date) {
        try {
            Factory factory = factoryMapper.selectByPrimaryKey(factoryId);
            log.info("工厂 " + factory.getCode() + " 执行每日删除前一天数据任务...");
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date queryDate = new java.sql.Date(dateFormat.parse(dateStr).getTime());
            // 校验前一天数据
            checkBeforeDelete(factory, queryDate);

            int i = pointValueMapper.deleteCurrentDayData(factoryId, queryDate);
            log.info(LocalDateTime.now().toString() + "删除 " + factory.getCode() + " 工厂 " + dateFormat.parse(dateStr).toString() + " 数据，共：" + i + "条");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除前进行数据检查
     * 筛选出需要永久化的数据（同一点位中后一时刻比前一时刻数值大的（场景：计量表计数重置））
     * @param factory
     * @param queryDate
     */
    public void checkBeforeDelete(Factory factory, java.sql.Date queryDate) {
        List<PointValueCheckDto> list = pointValueMapper.getCurrentDayData(factory.getId(), queryDate);
        if (CollectionUtils.isEmpty(list)) {
            log.info("每日定时清除点位数据...未查询到 " + factory.getCode() + " 工厂 - " + queryDate + " 的点位数据");
            return;
        }
        // 需要保持，永久不能删除的点位数据ID
        List<Long> persistingIds = new ArrayList<>();
        Map<String, List<PointValueCheckDto>> map = list.stream().collect(Collectors.groupingBy(PointValueCheckDto::getName));
        for (Map.Entry<String, List<PointValueCheckDto>> entry : map.entrySet()) {
            String name = entry.getKey(); // 点位名称
            List<PointValueCheckDto> checkList = entry.getValue();
            // 根据查询时间进行排序
            checkList.sort(Comparator.comparing(PointValueCheckDto::getQueryTime));
            // 第一条数据调取之前的最新一条数据进行比较
            if (!CollectionUtils.isEmpty(checkList)) {
                PointValueCheckDto checkDto = checkList.get(0);
                PointValueCheckDto lastDataBefore = pointValueMapper.getPointLastDataBeforeQueryDate(factory.getId(), name, queryDate);
                if (lastDataBefore != null) {
                    if (lastDataBefore.getValue() != null && checkDto.getValue() != null
                            && lastDataBefore.getValue().compareTo(checkDto.getValue()) > 0) {
                        persistingIds.add(checkDto.getId());
                        persistingIds.add(lastDataBefore.getId());
                        log.info("点位 " + name + " 在[" + queryDate + "]-"
                                + lastDataBefore.getQueryTime() + "~" + checkDto.getQueryTime() + " 时的数值从{"
                                + lastDataBefore.getValue() + "}到{ " + checkDto.getValue() + "}，更新为永久数据...");
                    }
                }
            }
            // 循环同一点位每小时的数据，与后一小时的数值进行对比
            for (int i = 0; i < checkList.size() - 1; i++) {
                PointValueCheckDto preDto = checkList.get(i);
                PointValueCheckDto nextDto = checkList.get(i + 1);
                if (preDto.getValue() != null && nextDto.getValue() != null) {
                    if (preDto.getValue().compareTo(nextDto.getValue()) > 0) {
                        persistingIds.add(preDto.getId());
                        persistingIds.add(nextDto.getId());
                        log.info("点位 " + name + " 在[" + queryDate + "]-"
                                + preDto.getQueryTime() + "~" + nextDto.getQueryTime() + " 时的数值从{"
                                + preDto.getValue() + "}到{" + nextDto.getValue() + "}，更新为永久数据...");
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(persistingIds)) {
            // 更新数据为永久数据
            log.info("updateIds:" + StringUtils.join(persistingIds, ","));
            pointValueMapper.updateDataPersistence(persistingIds);
            // 复制点位数据到month和year type中
            int copyCount = pointValueMapper.copyDataToAssignedDataType(persistingIds, "month");
            log.info("复制月数据 " + copyCount + " 条...");
            copyCount = pointValueMapper.copyDataToAssignedDataType(persistingIds, "year");
            log.info("复制年数据 " + copyCount + " 条...");
        }
    }

    /**
     * 删除30天前的数据
     * @param factoryId
     */
    public void delete30DailyData(Long factoryId, LocalDateTime date) {
        try {
            Factory factory = factoryMapper.selectByPrimaryKey(factoryId);
            log.info("工厂 " + factory.getCode() + " 执行每日删除30天前数据任务...");
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date queryDate = new java.sql.Date(dateFormat.parse(dateStr).getTime());
            int i = pointValueMapper.delete30DayData(factoryId, queryDate);
            log.info(LocalDateTime.now().toString() + "删除 " + factory.getCode() + " 工厂 " + dateFormat.parse(dateStr).toString() + " 数据，共：" + i + "条");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
