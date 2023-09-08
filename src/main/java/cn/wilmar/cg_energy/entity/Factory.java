package cn.wilmar.cg_energy.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 工厂
 * @Author: fengzixin
 * @Date: 2022/8/22
 */
@Getter
@Setter
@ToString
@Builder
@Table(name = "BI_Plant_Factory")
public class Factory {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    // 工厂代码
    private String code;
    // 工厂名称
    private String name;
    // 备注
    private String remark;
}
