package my.quartz.alone.timertask;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 获取 JobDetail 中的 参数
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        Long time = dataMap.getLongValue("time");
        System.out.println(time + "----hello, job");
    }
}
