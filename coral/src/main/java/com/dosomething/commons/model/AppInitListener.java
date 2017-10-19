package com.dosomething.commons.model;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//import com.dosomething.commons.cron.CronScheduler;
//import com.dosomething.commons.type.ScheduleType;
import com.dosomething.util.DateUtil;
import com.dosomething.util.LogUtils;



@WebListener
public class AppInitListener  implements ServletContextListener {
	
	// 程式執行的時區，避免設定錯誤導致不可預期的問題
	private String SERVER_TIME_ZONE = "GMT+8:00";
    /**
     * Default constructor. 
     */
    public AppInitListener() {
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
	public void contextInitialized(ServletContextEvent contextEvent) {
		try {			
			// 檢查目前server的時區是否正確
			checkTimeZone();

			
			//run quartz job
//			for(ScheduleType scheduleType : ScheduleType.values()) {
//				scheduleType.execute();
//			}
			
			// initialize JS File Version. Reset when restarting server.
			Setting.JS_FILE_VERSION = Integer.valueOf(DateUtil.toString(new java.util.Date(), "yyyyMMdd"));
		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage(), e);
		}
    }
	
	private void checkTimeZone() {
		String serverTimeZone = displayTimeZone(TimeZone.getDefault());
		if(!SERVER_TIME_ZONE.equals(serverTimeZone)) {
			throw new RuntimeException("Server Time Zone Error !! : " + serverTimeZone + " != " + SERVER_TIME_ZONE);
		}
	}
	
	private String displayTimeZone(TimeZone timeZone) {
		long hours = TimeUnit.MILLISECONDS.toHours(timeZone.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeZone.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		if (hours > 0) {
			return String.format("GMT+%d:%02d", hours, minutes);
		} else {
			return String.format("GMT%d:%02d", hours, minutes);
		}
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		/** Shutdown CronScheduler */
//		CronScheduler.getInstance().shutdown();
	}
	

}
