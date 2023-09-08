package cn.wilmar.cg_energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = "cn.wilmar.cg_energy.mapper")
public class CgEnergyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CgEnergyApplication.class, args);
    }

}
