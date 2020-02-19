package my.quartz.alone;

import my.quartz.alone.timertask.HelloJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 *  整个 Quartz 的使用方法
 */
public class CronJobTest {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        // cron表达式
        String cronExp = "* * * * * ? *";
        // 传给 Job 的参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("time", System.currentTimeMillis());

        // 创建一个JobDetail实例，与HelloJob.class绑定
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                .withIdentity("myJob", "job-group")
                .usingJobData(dataMap)
                .build();
        // 创建一个Trigger实例
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "trigger-group")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
        // 创建Scheduler实例
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail, trigger);

        Thread.sleep(10*1000);  // 10s 后结束
        // 结束scheduler
        scheduler.shutdown();
    }
}
