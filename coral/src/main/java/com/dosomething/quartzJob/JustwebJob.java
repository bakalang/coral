//package com.dosomething.quartzJob;
//
//import java.sql.Connection;
//
//import org.quartz.DisallowConcurrentExecution;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.dosomething.commons.model.database.DBPool;
//import com.dosomething.util.DbUtils;
//import com.dosomething.util.LogUtils;
//
//@DisallowConcurrentExecution
//public class JustwebJob implements Job {
//	         
//	public JustwebJob() {
//	}
//
//	public void execute(JobExecutionContext ctx) throws JobExecutionException {
//		try {
//			
////			System.out.println("JustwebJob");
////			Connection conn = null;
////			try {
////				conn = DBPool.getInstance().getReadConnection();				
////				System.out.println(SystemSettingDAO.findEntityByKey(conn));
////			} finally {
////				DbUtils.close(conn);
////			}
//			
//			
//			//執行權限的控管交由ScheduleType負責			
////			WeatherHistoryBO.saveHistory();
//		} catch (Exception e) {
//			LogUtils.coral.error(e.getMessage(), e);
//		}
//	}
//}
