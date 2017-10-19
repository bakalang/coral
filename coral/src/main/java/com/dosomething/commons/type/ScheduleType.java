//package com.dosomething.commons.type;
//
//import org.quartz.Job;
//
//import com.dosomething.commons.cron.CronScheduler;
//import com.dosomething.quartzJob.JustwebJob;
//import com.dosomething.quartzJob.HeartbeatJob;
//import com.dosomething.util.LogUtils;
//
//
//public enum ScheduleType {
//
////	JustwebJob(JustwebJob.class, "* * * * * ?") {
////
////		@Override
////		public void execute() {
////			try {
////				if (ServerInfoUtils.isPlayerServer()) {
////					CronScheduler.getInstance().createCronScheduleJob(this);
////					return;
////				}
////			} catch (Exception e) {
////				LogUtils.coral.error(e.getMessage(), e);
////			}
////		}
////	};
//	HeartbeatJob(HeartbeatJob.class,"*/10 * * * * ?"),
//	JustwebJob(JustwebJob.class,"*/5 * * * * ?");
//
//	private Class<? extends Job> jobClass = null;
//
//	private String name = null;
//	// 因應可能臨時更改
//	public String cronSchedule = null;
//
//	private ScheduleType(Class<? extends Job> jobClass, String cronSchedule) {
//		this.jobClass = jobClass;
//		this.name = jobClass.getSimpleName();
//		this.cronSchedule = cronSchedule;
//	}
//
//	public Class<? extends Job> getJobClass() {
//		return jobClass;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public String getTriggerName() {
//		return name + "Trigger";
//	}
//
//	public String getGroup() {
//		return "group1";
//	}
//
//	public String getCronSchedule() {
//		return cronSchedule;
//	}
//
//	public void execute() {
//		try {
//			CronScheduler.getInstance().createCronScheduleJob(this);
//		} catch (Exception e) {
//			LogUtils.coral.error(e.getMessage(), e);
//		}
//	}
//
//}
