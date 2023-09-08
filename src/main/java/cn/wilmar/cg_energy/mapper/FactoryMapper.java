package cn.wilmar.cg_energy.mapper;

import cn.wilmar.cg_energy.entity.Factory;
import cn.wilmar.cg_energy.util.CgEnergyMapper;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
@Repository
//@CacheNamespaceRef(FactoryMapper.class)
//public interface FactoryMapper {
public interface FactoryMapper extends CgEnergyMapper<Factory> {

    List<Factory> getAllFactories();

    int insertFactory(@Param("factory") Factory factory);

    Factory getByCode(@Param("code") String code);
}
