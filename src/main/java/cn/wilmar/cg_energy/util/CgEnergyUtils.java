package cn.wilmar.cg_energy.util;

import cn.wilmar.cg_energy.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * 项目工具类
 * @Author: fengzixin
 * @Date: 2022/8/19
 */
public class CgEnergyUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Jackson 序列化对象
     *
     * @param object 对象
     * @return 序列化JSON字符串
     */
    public static String jacksonWriteString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ServiceException("对象序列化JSON异常");
        }
    }

    /**
     * 返回 UUID
     *
     * @return UUID
     */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 补足长度
     *
     * @param num num
     * @return 补足长度后的字符串
     */
    public static String makeUpLength(Integer num, Integer needLength) {
        String intStr = Integer.toString(num);
        StringBuilder s = new StringBuilder();
        if (intStr.length() < needLength) {
            int len = needLength - intStr.length();
            for (int i = 0; i < len; i++) {
                s.append("0");
            }
        }
        s.append(num);
        return s.toString();
    }
}
