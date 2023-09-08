package cn.wilmar.cg_energy.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * PIMS返回点位信息结果DTO
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
@Data
public class PointValueDto {

    // 是否成功
    @JsonProperty("IsSuccess")
    private Boolean isSuccess;
    // 返回结果编码
    @JsonProperty("ResultCode")
    private String resultCode;
    // 返回结果消息
    @JsonProperty("ResultMessage")
    private String resultMessage;
    // 返回结果
    @JsonProperty("Result")
    private List<Result> results;

    @Data
    public static class Result {

        // 点位名称 CN.SHH.REF00.DEW01.AG.AG4017.PV
        @JsonProperty("Name")
        private String name;
        // 完整点位名称，包含路径 \\CHNAWSPIMSDAP\CN.SHH.REF00.DEW01.AG.AG4017.PV
        @JsonProperty("Path")
        private String path;
        // 点位数据
        @JsonProperty("Data")
        private Data data;
        // 消息
        @JsonProperty("Message")
        private String message;

        @lombok.Data
        public static class Data {

            // 是否good数据
            @JsonProperty("IsGood")
            private Boolean isGood;
            // 数据状态 Bad,Good,QualityMask,SubstatusMask,BadSubstituteValue,UncertainSubstituteValue,Substituted,Constant,Annotated
            @JsonProperty("Status")
            private String status;
            // 时间戳 UTC时间格式 2022-08-08T08:27:00Z
            @JsonProperty("Timestamp")
            private String timestamp;
            // 点位的值，可能是数值或字符串
            @JsonProperty("Value")
            private String value;
            // 数据的单位，可能为空
            @JsonProperty("UOM")
            private String uom;
        }
    }

    /**
     * 相应报文示例
     * {
     *     "IsSuccess": true,
     *     "ResultCode": null,
     *     "ResultMessage": null,
     *     "Result": [
     *         {
     *             "Name": "CN.QZH.CCP01.COP06.KWH.KWH17018.TOT",
     *             "Path": "\\\\CHNAWSPIMSDAP\\CN.QZH.CCP01.COP06.KWH.KWH17018.TOT",
     *             "Data": {
     *                 "IsGood": true,
     *                 "Status": "Good",
     *                 "Timestamp": "2022-08-08T08:27:00Z",
     *                 "Value": 144897.0,
     *                 "UOM": null
     *             },
     *             "Message": null
     *         },
     *         {
     *             "Name": "CN.DGN.REF00.NEU01.FT.FT100.TOT",
     *             "Path": "\\\\CHNAWSPIMSDAP\\CN.DGN.REF00.NEU01.FT.FT100.TOT",
     *             "Data": {
     *                 "IsGood": false,
     *                 "Status": "Bad",
     *                 "Timestamp": "2022-08-05T04:16:50Z",
     *                 "Value": "Comm Fail",
     *                 "UOM": null
     *             },
     *             "Message": null
     *         },
     *         {
     *             "Name": "CN.LYG.CCP01.COP01.ALYS.UP.MAN.IN",
     *             "Path": null,
     *             "Data": null,
     *             "Message": "[-12011]PI Point not found '\\\\CHNAWSPIMSDAP\\CN.LYG.CCP01.COP01.ALYS.UP.MAN.IN'."
     *         }
     *     ]
     * }
     */

}
