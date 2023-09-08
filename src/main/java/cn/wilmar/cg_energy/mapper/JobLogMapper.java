package cn.wilmar.cg_energy.mapper;

import cn.wilmar.cg_energy.entity.BadPoint;
import cn.wilmar.cg_energy.entity.JobLog;
import cn.wilmar.cg_energy.util.CgEnergyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: fengzixin
 * @Date: 2022/8/25
 */
@Repository
public interface JobLogMapper extends CgEnergyMapper<JobLog> {


}
