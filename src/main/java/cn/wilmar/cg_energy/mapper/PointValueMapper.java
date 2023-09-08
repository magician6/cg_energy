package cn.wilmar.cg_energy.mapper;

import cn.wilmar.cg_energy.entity.PointValue;
import cn.wilmar.cg_energy.util.CgEnergyMapper;
import cn.wilmar.cg_energy.vm.PointValueCheckDto;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
@Repository
//@CacheNamespaceRef(PointValueMapper.class)
//public interface PointValueMapper {
public interface PointValueMapper extends CgEnergyMapper<PointValue> {

    int insertData(@Param("entity") PointValue pointValue);

    int deleteCurrentDayData(@Param("factoryId") Long factoryId, @Param("currentDate") java.sql.Date currentDate);

    int delete30DayData(@Param("factoryId") Long factoryId, @Param("delDate") java.sql.Date delDate);

    List<PointValueCheckDto> getCurrentDayData(@Param("factoryId") Long factoryId, @Param("currentDate") java.sql.Date currentDate);

    PointValueCheckDto getPointLastDataBeforeQueryDate(@Param("factoryId") Long factoryId, @Param("name") String name, @Param("currentDate") java.sql.Date currentDate);

    int updateDataPersistence(@Param("ids") List<Long> ids);

    int copyDataToAssignedDataType(@Param("ids") List<Long> ids, @Param("dataType") String dataType);
}
