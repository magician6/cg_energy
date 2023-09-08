package cn.wilmar.cg_energy.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * 任务日志
 * @Author: fengzixin
 * @Date: 2022/8/25
 */

@Getter
@Setter
@Builder
@ToString
@Table(name = "BI_Plant_JobLog")
public class JobLog {
    // 工厂ID
    private Long factoryId;
    // 任务名称
    private String jobName;
    // 执行状态 true：成功 false：失败
    private Boolean executeStatus;
    // 任务完成时间
    private LocalDateTime finishTime;
    // 执行时长（ms）
    private Long executeTime;
    // 成功数量 （点位数）
    private Integer successQuantity;
    // 非good数据点位数
    private Integer badQuantity;
    // 失败信息
    private String failMessage;

}
