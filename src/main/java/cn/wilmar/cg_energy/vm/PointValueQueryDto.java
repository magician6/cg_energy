package cn.wilmar.cg_energy.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * PIMS点位信息查询DTO
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
@Data
public class PointValueQueryDto {

    @JsonProperty("PointNames")
    private List<String> pointNames;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("RetrieveGoodValueOnly")
    private Boolean goodOnly;

    /**
     * 请求报文示例
     * {
     *     "PointNames": [
     *         "CN.QZH.CCP01.COP06.KWH.KWH17018.TOT",
     *         "CN.DGN.REF00.NEU01.FT.FT100.TOT",
     *         "CN.LYG.CCP01.COP01.ALYS.UP.MAN.IN"
     *     ],
     *     "Timestamp": "2022-08-08 16:27:00",
     *     "RetrieveGoodValueOnly": false
     * }
     */

}
