package com.dosomething.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dosomething.api.JsoupConnect;
import com.dosomething.api.TwseConnect;
import com.dosomething.commons.dao.DailyStakeDAO;
import com.dosomething.commons.dao.StockDailyTransactDAO;
import com.dosomething.commons.dto.DailyStake;
import com.dosomething.commons.dto.Securitys;
import com.dosomething.commons.dto.StockDailyTransact;
import com.dosomething.commons.model.database.DBPool;
import com.dosomething.util.BigDecimalUtils;
import com.dosomething.util.DateUtil;
import com.dosomething.util.DbUtils;
import com.dosomething.util.FormatUtils;
import com.dosomething.util.LogUtils;
import com.dosomething.util.ParseUtils;
import com.dosomething.util.StockSecurityUtils;
import com.dosomething.util.Validator;


public class JustwebBO {
	
	
	
	public static void grebAllSecurityStake() throws Exception {
		
		 List<Securitys> allList = StockTWBO.queryNewsByID();
		 if(allList != null) {
			 for(Securitys s : allList) {
				 Date d = ParseUtils.parseDate(FormatUtils.DATE_PATTERN_YYYYMMDD, "20171017");
				 grebSecurityDailyStake(s, d);
			 }
		 }
	}
	
	public static void grebSecurityDailyStake(Securitys s, Date date) {
		try {
			Document doc = JsoupConnect.jsoupRetry(StockSecurityUtils.getURLParameters(s.getUrl(), date));
			if(doc == null){
				LogUtils.coral.info("webpage not found.");
                return;
			}
			 
			Elements mainTable = doc.select("#oMainTable");
			String tmpDate = mainTable.select(".t11").text();
			if(tmpDate.length() > 10) {
				String pageDate = tmpDate.substring(tmpDate.length() - 8, tmpDate.length());
				if(!Validator.isPositiveInteger(pageDate)) {
					System.out.println(s+" new data need greb.");
					return;
				}
				if(DateUtil.isToday(DateUtil.toDate(tmpDate.substring(tmpDate.length() - 8, tmpDate.length()), FormatUtils.DATE_PATTERN_YYYYMMDD))) {
					System.out.println(s+" new data need greb.");
				}
	 		}
			 
			Elements mainTRs = mainTable.select("tbody > tr");
			fetchDetailTR(s.getSecurityId(), date, mainTRs.get(2).children().get(0));
			fetchDetailTR(s.getSecurityId(), date, mainTRs.get(2).children().get(1));
			 
		} catch (Exception e) {
			LogUtils.coral.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private static void fetchDetailTR(String securityId, Date date, Element datailTR) throws Exception {
		String dateString = DateUtil.toString(date, FormatUtils.DATE_PATTERN_YYYYMMDD); 
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
		
			Iterator<Element> rr = datailTR.select("tr").iterator();
	        while (rr.hasNext()) {
	        	Iterator<Element> dd = rr.next().select("td").iterator();
	        	DailyStake ds = null;
	    		int t3n1Flag = 0;
	        	while (dd.hasNext()) {
	        		Element element = dd.next();
	        		if(element.hasClass("t4t1")) {
	        			ds = new DailyStake();
	        			Elements aHref = element.select("a[href]");
	        			if(aHref.size() > 0) {
	        				String hrefText = aHref.attr("href");
	        				// <a href="javascript:Link2Stk('00637L');">00637L元大滬深300正2</a>
	        				ds.setStockId(hrefText.substring(hrefText.indexOf("'") + 1, hrefText.lastIndexOf("'")));
	        				ds.setSecurityId(securityId);
	        				ds.setCreatedDate(new Timestamp(date.getTime()));
	        			}else {
	        				// <script language="javascript"> GenLink2stk('AS5871','中租-KY'); </script>
	        				String htmlText = element.html().replace("AS", "");
	        				ds.setStockId(htmlText.substring(htmlText.indexOf("'") + 1, htmlText.indexOf("',")));
	        				ds.setSecurityId(securityId);
	        				ds.setCreatedDate(new Timestamp(date.getTime()));
	        			}
	        		}
	        		if(element.hasClass("t3n1")) {
	        			if(t3n1Flag == 0) {
	        				ds.setBuyStake(getBigDecimal(Jsoup.parse(element.html()).text(), 0));
	        			} else if (t3n1Flag == 1) {
	        				ds.setSellStake(getBigDecimal(Jsoup.parse(element.html()).text(), 0));
	        			} else {
	        				continue;
	        			}        			
	        			t3n1Flag++;        			
	        		}
	        	}
	        	
	        	if(ds != null) {
	        		TwseConnect twseConnect = new TwseConnect(dateString, ds.getStockId());
	    	    	twseConnect.run();
	    	    	BigDecimal dayClose = getStockMonthTransaction(date, twseConnect.getRespList(), ds.getStockId());
	    	    	ds.setClose(dayClose);

	        		System.out.println(ds.toString());
	        		DailyStakeDAO.save(conn, ds);
	        		Thread.sleep(500);
	        	}        	
	        }	

			conn.commit();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getStockMonthTransaction(Date today, List<Object> respList, String stockId) throws SQLException {
		if(respList == null) {
			return BigDecimalUtils.ZERO;
		}

        BigDecimal rtnClose = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);
		
			List<Integer> s = StockTWBO.getThisMonthStockTransactDate(stockId);
			for(Object o : respList) {
				String date = ((List<String>) o).get(0);
				int day = Integer.parseInt(date.substring(date.lastIndexOf("/") + 1));
				if(!s.contains(day)) {
					StockDailyTransact sdt = new StockDailyTransact();
					sdt.setStockId(stockId);
					cal.set(Calendar.DAY_OF_MONTH, day);
					sdt.setTransactDate(new Timestamp(cal.getTime().getTime()));
					sdt.setTransactVolume(getBigDecimal(((List<String>) o).get(1), 0));
					sdt.setTurnover(getBigDecimal(((List<String>) o).get(2), 0));
					sdt.setOpen(getBigDecimal(((List<String>) o).get(3), 2));
					sdt.setHigh(getBigDecimal(((List<String>) o).get(4), 2));
					sdt.setLow(getBigDecimal(((List<String>) o).get(5), 2));
					sdt.setClose(getBigDecimal(((List<String>) o).get(6), 2));
					sdt.setGrossBalance(getBigDecimal(((List<String>) o).get(7), 2));
					sdt.setTransactTotal(getBigDecimal(((List<String>) o).get(8), 0));
					System.out.println(sdt.toString());
					StockDailyTransactDAO.save(conn, sdt);
				} else {
					//rtnClose = StockDailyTransactDAO.getStockDailyTransact(conn, stockId, today).getClose();
				}
				
				if(DateUtils.isSameDay(today, cal.getTime())) {
					rtnClose = getBigDecimal(((List<String>) o).get(6), 2);
				}
			}
			
			conn.commit();
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
		
		return rtnClose;
	}

	private static BigDecimal getBigDecimal(String tmp, int scale) {
		tmp = tmp.replace(String.valueOf((char) 160), " ");
        tmp = tmp.replace("%", "");
        tmp = tmp.replace(",", "");
        tmp = tmp.replace("+", "");
        tmp = tmp.replace("X", "");
        BigDecimal b = null;
        try {
            b = BigDecimal.valueOf(Double.valueOf(tmp));
            b.setScale(scale);
        }catch (NumberFormatException nfe){
            return BigDecimalUtils.ZERO;
        }
        return b;
    }	
}
