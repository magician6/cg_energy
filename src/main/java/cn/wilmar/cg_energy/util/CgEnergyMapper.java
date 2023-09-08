package cn.wilmar.cg_energy.util;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 用来给 Entity Mapper 继承的项目通用 Mapper
 * @Author: fengzixin
 * @Date: 2022/8/18
 */
public interface CgEnergyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
