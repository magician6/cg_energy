package cn.wilmar.cg_energy.service;

import cn.wilmar.cg_energy.entity.PointValue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * List转csv服务
 * @Author: Qianwei
 * @Date: 2023/11/07
 */
public class ListToCsvConverter {

    public void convert(List<PointValue> list, String filePath) {
        StringBuilder csvContent = new StringBuilder();
        // 添加表头
        csvContent.append("factory_id;data_type;query_date;query_time;res_date;res_time;status;name;" +
                "full_name;description;unit;workshop;workshop_desc;workshop_desc_en;" +
                "energy_type;energy_type_desc;energy_type_desc_en;" +
                "energy_class_en;energy_class;value;factory_code;is_renew_energy;" +
                "persistence;group_line;group_line_desc;group_line_desc_en").append("\n");

       // 遍历List中的每个对象
        for (PointValue point : list) {
            // 添加对象的属性值，并用分号分隔
            csvContent.append(point.getFactoryId())
                    .append(";")
                    .append(point.getDataType())
                    .append(";")
                    .append(point.getQueryDate())
                    .append(";")
                    .append(point.getQueryTime())
                    .append(";")
                    .append(point.getResDate())
                    .append(";")
                    .append(point.getResTime())
                    .append(";")
                    .append(point.getIsGood())
                    .append(";")
                    .append(point.getName())
                    .append(";")
                    .append(point.getFullName())
                    .append(";")
                    .append(point.getDesc())
                    .append(";")
                    .append(point.getUnit())
                    .append(";")
                    .append(point.getWorkshop())
                    .append(";")
                    .append(point.getWorkshopDesc())
                    .append(";")
                    .append(point.getWorkshopDescEn())
                    .append(";")
                    .append(point.getEnergyType())
                    .append(";")
                    .append(point.getEnergyType2Desc())
                    .append(";")
                    .append(point.getEnergyType2DescEn())
                    .append(";")
                    .append(point.getEnergyClass())
                    .append(";")
                    .append(point.getEnergyClassDesc())
                    .append(";")
                    .append(point.getValue())
                    .append(";")
                    .append(point.getFactoryCode())
                    .append(";")
                    .append(point.getIsRenewEnergy())
                    .append(";")
                    .append(point.getPersistence())
                    .append(";")
                    .append(point.getGroupLine())
                    .append(";")
                    .append(point.getGroupLineDesc())
                    .append(";")
                    .append(point.getGroupLineDescEn())
                    .append("\n");
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            // 将StringBuilder的内容写入文件
            writer.write(csvContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
