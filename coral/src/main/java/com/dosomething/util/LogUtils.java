package com.dosomething.util;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;

public class LogUtils {

	public static Logger coral = Logger.getLogger("Coral");

	private LogUtils() {
		throw new AssertionError();
	}
	
	

	
	
//	public static String pendingExpiredLog(Pending pending) {
//		StringWriter out = new StringWriter();
//		JsonGenerator jGenerator = null;
//
//		try {
//
//			jGenerator = JSONUtils.getFactory().createGenerator(out);
//			// generate json
//			jGenerator.writeStartObject();			
//			jGenerator.writeObjectField("pendingID", pending.getId());
//			jGenerator.writeObjectField("userID", pending.getUserId());
//			jGenerator.writeObjectField("gameSessionId", pending.getGameSessionId());
//			jGenerator.writeObjectField("gameDate", FormatUtils.dateFormat(pending.getGameDate().getTime(), FormatUtils.DATE_PATTERN_SLASH_MM_DD_YYYY));
//			jGenerator.writeObjectField("stake", pending.getStake());
//			jGenerator.writeObjectField("status", pending.getStatus());
//			jGenerator.writeObjectField("odds", pending.getOdds());
//			jGenerator.writeObjectField("resultType", GameResultType.getInstanceOf(pending.getResultType()).getName());
//			
//			jGenerator.writeEndObject();
//		} catch (IOException e) {
//			LogUtils.weatherForecast.error(e.getMessage(), e);
//		} finally {
//			JSONUtils.close(jGenerator);
//		}		
//		return out.toString();
//	}
}
