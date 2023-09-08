package cn.wilmar.cg_energy.vm;


import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * 点位数据校验Dto
 * @Author: fengzixin
 * @Date: 2023/4/24
 */
@Data
public class PointValueCheckDto {
    private Long id;
    // 工厂ID
    private Long factoryId;
    // 工厂代码
    private String factoryCode;
    // 数据类型(年/月/小时)
    private String dataType;
    // 点位名称
    private String name;
    // 是否good数据
    private Boolean isGood;
    // 点位值
    private BigDecimal value;
    // 返回数据的日期
    private Date resDate;
    // 返回数据的时间
    private Time resTime;
    // 查询的日期
    private Date queryDate;
    // 查询的时间
    private Time queryTime;
}
