package cn.wilmar.cg_energy.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

/**
 * 点位数据
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
@Getter
@Setter
@Builder
@ToString
@Table(name = "BI_Plant_PointValue")
public class PointValue {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    // 工厂ID
    private Long factoryId;
    // 工厂代码
    private String factoryCode;
    // 数据类型(年/月/小时)
    private String dataType;
    // 点位名称
    private String name;
    // 点位完整名称
    private String fullName;
    // 完整点位名称
    @Transient
    private String path;
    // 消息
    @Transient
    private String message;
    // 是否good数据
    @Column(name = "status")
    private Boolean isGood;
    // 时间戳
    @Transient
    private String timestamp;
    // 点位值
    private BigDecimal value;
    // 数据单位
    @Transient
    private String uom;
    // 返回数据的日期
    private Date resDate;
    // 返回数据的时间
    private Time resTime;
    // 查询的日期
    private Date queryDate;
    // 查询的时间
    private Time queryTime;

    // 描述
    @Column(name = "description")
    private String desc;
    // 单位
    private String unit;
    // 车间
    private String workshop;
    // 车间描述
    private String workshopDesc;
    // 车间描述（英文）
    private String workshopDescEn;
    // 能源类型
    private String energyType;
    // 能源类型描述
    private String energyType2Desc;
    // 能源类型描述（英文）
    private String energyType2DescEn;
    // 能源类型
    private String energyClass;
    // 能源类型描述
    private String energyClassDesc;
    // 是否再生能源
    private Boolean isRenewEnergy;
    // 持久化
    private Boolean persistence;
    // 集团产线
    private String groupLine;
    // 集团产线描述
    private String groupLineDesc;
    // 集团产线描述（英文）
    private String groupLineDescEn;

}
