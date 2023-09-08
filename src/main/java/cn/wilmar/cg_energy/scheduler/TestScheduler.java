//package cn.wilmar.cg_energy.scheduler;
//
//import cn.wilmar.cg_energy.service.PointValueService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.SchedulingConfigurer;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//import org.springframework.scheduling.support.CronTrigger;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.TimeZone;
//import java.util.concurrent.ScheduledFuture;
//
//import static cn.wilmar.cg_energy.util.Contants.ZONE_ID;
//
///**
// * @Author: fengzixin
// * @Date: 2022/8/25
// */
//@Slf4j
//@Configuration
//public class TestScheduler implements SchedulingConfigurer {
//
//    @Autowired
//    CgEnergyDynamicScheduler cgEnergyDynamicScheduler;
//    @Autowired
//    PointValueService pointValueService;
//
//    private ScheduledTaskRegistrar scheduledTaskRegistrar;
//    private Set<ScheduledFuture> futures = new HashSet<>();
//
//
//
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        if (scheduledTaskRegistrar == null) {
//            scheduledTaskRegistrar = taskRegistrar;
//        }
//        if (taskRegistrar.getScheduler() == null) {
//            taskRegistrar.setScheduler(cgEnergyDynamicScheduler.taskScheduler());
//        }
//        addCronTasks(taskRegistrar);
//    }
//
//    public void reloadTasks() {
//        for (ScheduledFuture future : futures) {
//            future.cancel(true);
//        }
//        addCronTasks(scheduledTaskRegistrar);
//    }
//
//    private void addCronTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
//        final CronTrigger cronTrigger =new CronTrigger("", TimeZone.getTimeZone(ZONE_ID));
//        final ScheduledFuture<?> future = scheduledTaskRegistrar.getScheduler()
//                .schedule(() -> retryBadPoints(), cronTrigger);
//
//    }
//
//    public void retryBadPoints() {
//
//        //pointValueService
//    }
//}
