//package com.dosomething.quartzJob;
//
//import java.util.Date;
//
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.dosomething.util.LogUtils;
//
//@DisallowConcurrentExecution
//public class HeartbeatJob implements Job {
//	         
//	public HeartbeatJob() {
//	}
//
//	public void execute(JobExecutionContext ctx) throws JobExecutionException {
//		try {
//			//執行權限的控管交由ScheduleType負責
//			
//			//System.out.println(">>>"+new Date());
//		} catch (Exception e) {
//			LogUtils.coral.error(e.getMessage(), e);
//		}
//	}
//}
