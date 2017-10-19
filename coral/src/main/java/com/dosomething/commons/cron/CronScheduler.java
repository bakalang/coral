//package com.dosomething.commons.cron;
//
//import java.util.Date;
//
//import org.quartz.CronScheduleBuilder;
//import org.quartz.CronTrigger;
//import org.quartz.Job;
//import org.quartz.JobBuilder;
//import org.quartz.JobDetail;
//import org.quartz.JobKey;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.TriggerBuilder;
//import org.quartz.impl.StdSchedulerFactory;
//
//import com.dosomething.commons.type.ScheduleType;
//import com.dosomething.util.LogUtils;
//
//
//
///**
// * Cron Scheduler Setting
// * 
// * @author Miles
// */
//public class CronScheduler {
//
//	private static final CronScheduler instance = new CronScheduler();
//	private Scheduler scheduler;
//
//	private CronScheduler() {
//		try {
//			this.scheduler = StdSchedulerFactory.getDefaultScheduler();
//
//			this.scheduler.start();
//
//		} catch (SchedulerException e) {
//			LogUtils.coral.error("CronScheduler Error.", e);
//		}
//	}
//
//	public final static CronScheduler getInstance() {
//		return instance;
//	}
//
//	private JobKey getJobKey(ScheduleType scheduleType) {
//		return JobKey.jobKey(scheduleType.getName(), scheduleType.getGroup());
//	}
//
//	/**
//	 * 開始執行Cron Schedule Job
//	 * 
//	 * @param scheduleType
//	 * @throws SchedulerException
//	 */
//	public void createCronScheduleJob(ScheduleType scheduleType) throws SchedulerException {
//
//		Class<? extends Job> jobClass = scheduleType.getJobClass();
//
//		// 建構job信息
//		JobDetail job = JobBuilder.newJob(jobClass)
//			.withIdentity(scheduleType.getName(), scheduleType.getGroup()).build();
//
//		CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder
//			.cronSchedule(scheduleType.getCronSchedule());
//
//		CronTrigger cronTrigger = TriggerBuilder.newTrigger()
//			.withIdentity(scheduleType.getTriggerName(), scheduleType.getGroup())//
//			.withSchedule(cronScheduleBuilder).build();
//
//		Date d2 = scheduler.scheduleJob(job, cronTrigger);
//
//		LogUtils.coral.debug(job.getKey() + " has been scheduled to run at: " + d2
//			+ " and repeat based on expression: " + cronTrigger.toString());
//	}
//
//	public void runOnce(ScheduleType scheduleType) throws SchedulerException {
//		scheduler.triggerJob(getJobKey(scheduleType));
//	}
//
//	// 暫停任務
//	public void pauseJob(ScheduleType scheduleType) throws SchedulerException {
//		scheduler.pauseJob(getJobKey(scheduleType));
//	}
//
//	// 恢復任務
//	public void resumeJob(ScheduleType scheduleType) throws SchedulerException {
//		scheduler.resumeJob(getJobKey(scheduleType));
//	}
//
//	// 刪除定時任務
//	public void deleteScheduleJob(ScheduleType scheduleType) throws SchedulerException {
//		scheduler.deleteJob(getJobKey(scheduleType));
//	}
//
//	public void shutdown() {
//		if (this.scheduler != null) {
//			try {
//				this.scheduler.shutdown(true);
//			} catch (SchedulerException e) {
//				// ignore
//			}
//		}
//	}
//
//}