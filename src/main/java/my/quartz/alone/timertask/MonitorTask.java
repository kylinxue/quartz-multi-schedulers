package my.quartz.alone.timertask;

import my.quartz.alone.FileSizeAnalyser;
import my.quartz.alone.MonitorAlerter;
import my.quartz.alone.entity.RegulationEntity;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

public class MonitorTask implements Job {
    private Scheduler scheduler;
    private volatile String jobGroup, jobName;    // 保证线程之间可见
    private volatile String triggerGroup, triggerName;
    // 监控周期
    private String cronExp;
    // 监控规则
    private RegulationEntity regulation;

    String dir = "/";

    public MonitorTask() {

    }

    public MonitorTask(String jobGroup, String jobName, String triggerGroup, String triggerName, String cronExp, RegulationEntity regulation) {
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.triggerGroup = triggerGroup;
        this.triggerName = triggerName;
        this.cronExp = cronExp;
        this.regulation = regulation;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 外部接收监控周期和监控规则
        cronExp = context.getJobDetail().getJobDataMap().getString("cron_expression");
        regulation = (RegulationEntity) context.getJobDetail().getJobDataMap().getOrDefault("regulation", new RegulationEntity("default", 0, 0));

        float fileSize = FileSizeAnalyser.fileSize(dir);
        if (regulation.hitRegulation(fileSize)) {
            return;
        }
        // 不符合规则，发送告警
        System.out.println(regulation.getName() + " alert: ");
        MonitorAlerter.alert();
    }


    public void startJob(String schedulerName) {
        // 传给 Job 的参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("regulation", regulation);
        // 创建一个JobDetail实例，与MonitorTask.class绑定
        JobDetail jobDetail = JobBuilder.newJob(MonitorTask.class)
                .withIdentity(jobName, jobGroup)
                .usingJobData(dataMap)
                .build();
        // 创建一个Trigger实例
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
                .build();
        // 创建不同的Scheduler实例
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", schedulerName);
        props.put("org.quartz.threadPool.threadCount", "10");
        try {
            schedulerFactory.initialize(props);
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void stopJob() throws SchedulerException {
        if (scheduler.isStarted())
            scheduler.shutdown(true);
    }


    public static void main(String[] args) {
        // cron表达式
        String cronExp = "* * * * * ? *";
        // 具体监控规则
        RegulationEntity regulation = new RegulationEntity("job-1", -1, 10/*M*/);
        // 传给 Job 的参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("regulation", regulation);

        // 创建一个JobDetail实例，与HelloJob.class绑定
        JobDetail jobDetail = JobBuilder.newJob(MonitorTask.class)
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
        Scheduler scheduler = null;
        try {
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(10 * 1000);  // 10s 后结束
                scheduler.shutdown(true);
            } catch (SchedulerException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
