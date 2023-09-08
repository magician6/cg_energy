//package cn.wilmar.cg_energy.scheduler;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
///**
// * @Author: fengzixin
// * @Date: 2022/8/24
// */
//@Configuration
//public class CgEnergyDynamicScheduler {
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        // 定义任务执行线程池核心线程数
//        taskScheduler.setPoolSize(10);
//        taskScheduler.setRemoveOnCancelPolicy(true);
//        taskScheduler.setThreadNamePrefix("CgEnergyScheduler-");
//        taskScheduler.initialize();
//        return taskScheduler;
//    }
//
//
//
//}
