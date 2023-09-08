package cn.wilmar.cg_energy.service;

import cn.wilmar.cg_energy.exception.ServiceException;
import cn.wilmar.cg_energy.util.CgEnergyUtils;
import cn.wilmar.cg_energy.vm.PointDto;
import cn.wilmar.cg_energy.vm.PointValueDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.wilmar.cg_energy.util.Contants.*;

/**
 * 对外接口服务
 * @Author: fengzixin
 * @Date: 2022/8/18
 */

@Service
@Slf4j
public class InterfaceService {
    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    @Value("${cg_energy.cpm.point-path}")
    private String cpmUrl;
    @Value("${cg_energy.pims.point-value-path}")
    private String pimsUrl;

    //@Async
    public List<PointDto> getCpmPoints(String factoryCode) throws URISyntaxException {
        log.info("获取CPM点位...");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI uri = new URIBuilder(cpmUrl).setParameter("Factory", factoryCode).build();
        HttpGet request = new HttpGet(uri);
        //HttpGet request = new HttpGet("https://devhub.wilmarapps.com/CPM2_IS/rest/ShowScreen/EnergySensors?Factory=CG01");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        // 请求头 (HTTP Headers)
        request.addHeader(CONTENT_TYPE, "application/json");
        request.addHeader(SOURCE_SYS_ID, "CG_ENERGY");
        request.addHeader(TRANS_ID, CgEnergyUtils.getUuid());
        request.addHeader(SUBMIT_TIME, now);

        Header[] allHeaders = request.getAllHeaders();
        String logString = Arrays.stream(allHeaders).map(Header::toString).collect(Collectors.joining(" , "));
        log.info(logString);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String s = EntityUtils.toString(entity);
                log.info("get CPM points:\n{}", s);
                List<PointDto> pointDtos = mapper.readValue(s, new TypeReference<List<PointDto>>() {});
                return pointDtos;
            } else {
                log.error("读取CPM点位失败...");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取CPM点位失败，失败信息：" + e.getMessage());
            throw new ServiceException("读取CPM点位失败:" + e.getMessage());
        }
    }

    /**
     * 通过PIMS接口获取点位数据
     * @param postContent 接口请求数据
     * @return
     */
    //@Async
    public PointValueDto getPimsPointValue(String postContent) throws IOException {
        log.info("获取PIMS点位数据...content:\n{}", postContent);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(pimsUrl);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
        // 请求头 (HTTP Headers)
        request.addHeader(CONTENT_TYPE, "application/json");
        request.addHeader(SOURCE_SYS_ID, "CG_ENERGY");
        request.addHeader(TRANS_ID, CgEnergyUtils.getUuid());
        request.addHeader(SUBMIT_TIME, now);
        //request.addHeader("Connection", "keep-alive");
        //request.addHeader("Accept-Encoding", "gzip, deflate, br");
        //request.addHeader("Accept", "*/*");
        //request.addHeader("User-Agent", "PostmanRuntime/7.28.0");

        Header[] allHeaders = request.getAllHeaders();
        String logString = Arrays.stream(allHeaders).map(Header::toString).collect(Collectors.joining(" , "));
        log.info(logString);

        request.setEntity(new StringEntity(postContent));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String s = EntityUtils.toString(entity);
                log.info("get PIMS point values:\n{}", s);
                PointValueDto pointValueDto = mapper.readValue(s, PointValueDto.class);
                return pointValueDto;
            } else {
                log.error("获取pims点位数据失败...");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取pims点位数据失败，失败信息：" + e.getMessage());
            throw new ServiceException("获取pims点位数据失败:" + e.getMessage());
        }

    }

    //public static void main(String[] args) throws IOException {
    //    getCpmPoints();
    //}
}
