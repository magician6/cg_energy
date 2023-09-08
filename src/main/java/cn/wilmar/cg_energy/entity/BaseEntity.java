package cn.wilmar.cg_energy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity基础父类
 * 包含查询分页参数、查询分组参数、排序字段参数，以及通用的起始/结束日期区间查询。
 *
 * @Author: fengzixin
 * @Date: 2022/8/18
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"cellStyleMap"})
public abstract class BaseEntity implements Serializable {

    @Transient
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime queryDateStart;

    @Transient
    @JsonIgnore
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryDateEnd;

    //@Column(updatable = false)
    //private String createdBy;

    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant createdDate;

    //private String lastModifiedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant lastModifiedDate;

    /**
     * 设置插入记录时的字段
     */
    public void initInsertProps() {
        //this.setCreatedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
        this.setCreatedDate(Instant.now());
        //this.setLastModifiedBy(this.getCreatedBy());
        this.setLastModifiedDate(this.getCreatedDate());
    }

    /**
     * 设置插入记录时的字段
     */
    public void initInsertProps(String operator, Instant now) {
        //this.setCreatedBy(operator);
        this.setCreatedDate(now);
        //this.setLastModifiedBy(operator);
        this.setLastModifiedDate(now);
    }

    /**
     * 设置更新时的字段
     */
    public void initUpdateProps() {
        //this.setLastModifiedBy(SecurityUtils.getCurrentUserLogin().orElse(""));
        this.setLastModifiedDate(Instant.now());
    }

}
