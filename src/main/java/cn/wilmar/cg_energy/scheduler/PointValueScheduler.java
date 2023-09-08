package cn.wilmar.cg_energy.scheduler;

import cn.wilmar.cg_energy.entity.BadPoint;
import cn.wilmar.cg_energy.entity.Factory;
import cn.wilmar.cg_energy.mapper.BadPointMapper;
import cn.wilmar.cg_energy.mapper.FactoryMapper;
import cn.wilmar.cg_energy.service.PointValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: fengzixin
 * @Date: 2022/8/24
 */
@Slf4j
@Configuration
public class PointValueScheduler {

    @Autowired
    PointValueService pointValueService;
    @Autowired
    FactoryMapper factoryMapper;
    @Autowired
    BadPointMapper badPointMapper;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void savePerHourPointValue() {
        List<Factory> allFactories = factoryMapper.getAllFactories();
        for (Factory factory : allFactories) {
            log.info(factory.getCode() + "工厂每小时保存点位数据.....");
            pointValueService.savePointValue(factory.getId(), "day", null);
        }
    }

    /**
     * 每年1月1日0点保存年度点位数据
     */
//    @Scheduled(cron = "0 0 0 1 1 ?")
    public void saveAnnualPointValue() {
        List<Factory> allFactories = factoryMapper.getAllFactories();
        for (Factory factory : allFactories) {
            log.info(factory.getCode() + "工厂每年1月1日0时保存年度点位数据.....");
            pointValueService.savePointValue(factory.getId(), "year", null);
        }
    }

    /**
     * 每年12月31日23点保存年度点位数据
     */
//    @Scheduled(cron = "0 0 23 31 12 ?")
    public void saveAnnualEndPointValue() {
        List<Factory> allFactories = factoryMapper.getAllFactories();
        for (Factory factory : allFactories) {
            log.info(factory.getCode() + "工厂每年12月31日23时保存年度点位数据.....");
            pointValueService.savePointValue(factory.getId(), "year", null);
        }
    }

    /**
     * 每月1日0点保存月度点位数据
     */
    //@Scheduled(cron = "0 0 0 1 1/1 ?")
//    @Scheduled(cron = "0 0 0 1 * ?")
    public void saveMensalPointValue() {
        List<Factory> allFactories = factoryMapper.getAllFactories();
        for (Factory factory : allFactories) {
            log.info(factory.getCode() + "工厂每月1日0时保存月度点位数据.....");
            pointValueService.savePointValue(factory.getId(), "month", null);
        }
    }

    /**
     * 每月末23点保存月度点位数据
     */
    //@Scheduled(cron = "0 0 0 1 1/1 ?")
//    @Scheduled(cron = "0 0 23 L * ?")
    public void saveMensalEndPointValue() {
        List<Factory> allFactories = factoryMapper.getAllFactories();
        for (Factory factory : allFactories) {
            log.info(factory.getCode() + "工厂每月末23时保存月度点位数据.....");
            pointValueService.savePointValue(factory.getId(), "month", null);
        }
    }

    ///**
    // * 每月1日0点保存月度点位数据
    // */
    //@Scheduled(cron = "0 55 16 25 8 ?")
    //public void test() {
    //    List<Factory> allFactories = factoryMapper.getAllFactories();
    //    for (Factory factory : allFactories) {
    //        log.info(factory.getCode() + "工厂每月1日0时保存月度点位数据.....");
    //        pointValueService.savePointValue(factory.getId(), "day", null);
    //    }
    //}
    ///**
    // * 每月1日0点保存月度点位数据
    // */
    //@Scheduled(cron = "0 55 16 25 8 ?")
    //public void test1() {
    //    List<Factory> allFactories = factoryMapper.getAllFactories();
    //    for (Factory factory : allFactories) {
    //        log.info(factory.getCode() + "工厂每月1日0时保存月度点位数据.....");
    //        pointValueService.savePointValue(factory.getId(), "month", null);
    //    }
    //}
    ///**
    // * 每月1日0点保存月度点位数据
    // */
    //@Scheduled(cron = "0 55 16 25 8 ?")
    //public void test2() {
    //    List<Factory> allFactories = factoryMapper.getAllFactories();
    //    for (Factory factory : allFactories) {
    //        log.info(factory.getCode() + "工厂每月1日0时保存月度点位数据.....");
    //        pointValueService.savePointValue(factory.getId(), "year", null);
    //    }
    //}

    /**
     * 每天1点32分删除前一天数据
     */
//    @Scheduled(cron = "0 32 1 * * ?")
    public void clearPreDailyPointValue() {
        log.info("定时删除前一天点位数据...");
        List<Factory> allFactories = factoryMapper.getAllFactories();
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1L);
        for (Factory factory : allFactories) {
            pointValueService.deleteDailyData(factory.getId(), yesterday);
        }
    }

    /**
     * 每天0点35分清空30天前day数据
     */
//    @Scheduled(cron = "0 35 0 * * ?")
    public void clearPre30DailyPointValue() {
        log.info("定时删除30天前day点位数据...");
        List<Factory> allFactories = factoryMapper.getAllFactories();
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(30L);
        for (Factory factory : allFactories) {
            pointValueService.delete30DailyData(factory.getId(), deleteDate);
        }
    }

    /**
     * 10/20/30/40/50分时查询badPoint数据，如果有则重新获取
     */
//    @Scheduled(cron = "0 10,20,30,40,50 * * * ?")
    public void retryBadPointValues() {
        log.info("定时读取非Good数据...");
        List<BadPoint> allBadPoints = badPointMapper.getAllBadPoints();
        if (allBadPoints != null && allBadPoints.size() > 0) {
            Map<Long, List<BadPoint>> facMap = new HashMap<>();
            allBadPoints.forEach(i -> {
                List<BadPoint> badPointList = facMap.computeIfAbsent(i.getFactoryId(), k -> new ArrayList<>());
                badPointList.add(i);
            });
            for (Map.Entry<Long, List<BadPoint>> facEntry : facMap.entrySet()) {
                Long factoryId = facEntry.getKey();
                List<BadPoint> facPoints = facEntry.getValue();
                if (facPoints != null && facPoints.size() > 0) {
                    Map<String, List<BadPoint>> typeMap = new HashMap<>();
                    facPoints.forEach(i -> {
                        List<BadPoint> list = typeMap.computeIfAbsent(i.getDataType(), k -> new ArrayList<>());
                        list.add(i);
                    });
                    for (Map.Entry<String, List<BadPoint>> entry : typeMap.entrySet()) {
                        String dataType = entry.getKey();
                        List<BadPoint> badPoints = entry.getValue();
                        if (badPoints != null && badPoints.size() > 0) {
                            if (dataType.equals("day") && LocalDateTime.now().getHour() != LocalDateTime.now().plusMinutes(9L).getHour()) {
                                badPointMapper.deleteByFactoryIdAndDataType(factoryId, dataType);
                                continue;
                            }
                            if ((dataType.equals("year") || dataType.equals("month")) && LocalDateTime.now().getDayOfMonth() != LocalDateTime.now().plusMinutes(9L).getDayOfMonth()) {
                                log.info("删除" + dataType + "bad点位数据...");
                                badPointMapper.deleteByFactoryIdAndDataType(factoryId, dataType);
                                continue;
                            }
                            pointValueService.savePointValue(factoryId, dataType, badPoints.stream().map(BadPoint::getFullName).collect(Collectors.toList()));
                        }
                    }
                }

            }
        }
    }

}
