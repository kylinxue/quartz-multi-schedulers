package my.quartz.alone;

import my.quartz.alone.entity.RegulationEntity;
import my.quartz.alone.timertask.MonitorTask;
import org.quartz.SchedulerException;

import java.util.HashSet;

public class MonitorTaskTest {
    public static void main(String[] args) throws InterruptedException, SchedulerException {
        // cron表达式
        String cronExp = "* * * * * ? *";
        // 具体监控规则
        RegulationEntity regulation1 = new RegulationEntity("job-1", -1, 10/*M*/);
        RegulationEntity regulation2 = new RegulationEntity("job-2", -1, 10/*M*/);
        HashSet<MonitorTask> tasks = new HashSet<>();
        MonitorTask task1 = new MonitorTask("1", "1", "1", "1", cronExp, regulation1);
        MonitorTask task2 = new MonitorTask("2", "2", "2", "2", cronExp, regulation2);
        task1.startJob("1");
        Thread.sleep(3*1000);
        task2.startJob("2");

        Thread.sleep(10*1000);
        System.out.println("1 stop");
        task1.stopJob();
        Thread.sleep(10*1000);
        System.out.println("2 stop");
        task2.stopJob();
    }


}
