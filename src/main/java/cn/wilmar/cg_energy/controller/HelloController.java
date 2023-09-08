package cn.wilmar.cg_energy.controller;

import cn.wilmar.cg_energy.service.InterfaceService;
import cn.wilmar.cg_energy.service.PointValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @Author: fengzixin
 * @Date: 2022/8/18
 */
@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    InterfaceService muleService;
    @Autowired
    PointValueService pointValueService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() throws Exception {
        //pointValueService.savePointValue(1L, "day", null);
        return "HELLO! -" + LocalDateTime.now().toString() + "- v0.0.2  2022-11-07 15:35:01";
    }

    @PostMapping("/saveData")
    @ResponseBody
    public String saveData(String queryTime, String dataType, String factoryCode) throws Exception {
        if (StringUtils.isEmpty(queryTime)) {
            return "Bad Request param: queryTime";
        }
        if (StringUtils.isEmpty(dataType)) {
            return "Bad Request param: dataType";
        }
        if (StringUtils.isEmpty(factoryCode)) {
            return "Bad Request param: factoryCode";
        }
        if (!Objects.equals("day", dataType) && !Objects.equals("month", dataType) && !Objects.equals("year", dataType)) {
            return "Bad Request param: dataType not in day/month/year";
        }
        pointValueService.savePointValueByQueryTime(factoryCode, dataType, null, queryTime);
        return "success";
    }

    @PostMapping("/delete")
    @ResponseBody
    public String delete(Long factoryId, String queryTime) {
        if (factoryId == null) {
            return "Bad Request param: factoryId";
        }
        if (StringUtils.isEmpty(queryTime)) {
            return "Bad Request param: queryTime";
        }
        LocalDate parse = LocalDate.parse(queryTime);
        LocalDateTime localDateTime = parse.atStartOfDay();
        pointValueService.deleteDailyData(factoryId, localDateTime);
        return "delete success...";
    }
}
