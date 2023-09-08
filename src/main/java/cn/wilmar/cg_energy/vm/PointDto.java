package cn.wilmar.cg_energy.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 根据CPM接口获取点位
 * [{"Name":"PWR1102.TOT","FullName":"CN.CHQ.REF01.UTI01.KWH.PWR1102.TOT","Desc":"精炼车间总表","Unit":"kW.h","Workshop":"REF01","WorkshopDesc":"精炼","WorkshopDescEn":"Refined Oils","EnergyType":"Elec","EnergyType2Desc":"市政电力","EnergyType2DescEn":"Municipal electricity"},{"Name":"PWR0035.TOT","FullName":"CN.CHQ.UTI00.UTI01.KWH.PWR0035.TOT","Desc":"35KV总电表","Unit":"kW.h","Workshop":"TOT01","WorkshopDesc":"总量","EnergyType":"Elec","EnergyType2Desc":"市政电力","EnergyType2DescEn":"Municipal electricity"}]
 * @Author: fengzixin
 * @Date: 2022/8/18
 */

@Data
public class PointDto {

    @JsonProperty("Name")
    private String name;
    @JsonProperty("FullName")
    private String fullName;
    @JsonProperty("Desc")
    private String desc;
    @JsonProperty("Unit")
    private String unit;
    @JsonProperty("Workshop")
    private String workshop;
    @JsonProperty("WorkshopDesc")
    private String workshopDesc;
    @JsonProperty("WorkshopDescEn")
    private String workshopDescEn;
    @JsonProperty("EnergyType")
    private String energyType;
    @JsonProperty("EnergyTypeDesc")
    private String energyType2Desc;
    @JsonProperty("EnergyTypeDescEn")
    private String energyType2DescEn;
    @JsonProperty("EnergyClass")
    private String energyClass;
    @JsonProperty("EnergyClassDesc")
    private String energyClassDesc;
    @JsonProperty("IsRenewEnergy")
    private int isRenewEnergy;

}
