package cn.wilmar.cg_energy.mapper;

import cn.wilmar.cg_energy.entity.BadPoint;
import cn.wilmar.cg_energy.entity.PointValue;
import cn.wilmar.cg_energy.util.CgEnergyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: fengzixin
 * @Date: 2022/8/25
 */
@Repository
public interface BadPointMapper extends CgEnergyMapper<BadPoint> {

    List<BadPoint> getAllBadPoints();

    int deleteByFactoryIdAndDataType(@Param("factoryId") Long factoryId, @Param("dataType") String dataType);
    int insertBadPoint(@Param("entity") PointValue pointValue);
}
