package cn.wilmar.cg_energy.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 非Good数据的点位表
 * @Author: fengzixin
 * @Date: 2022/8/24
 */
@Getter
@Setter
@Builder
@ToString
@Table(name = "BI_Plant_BadPoint")
public class BadPoint {

    private Long factoryId;

    private String fullName;

    private String dataType;

    private LocalDateTime queryTime;

}
