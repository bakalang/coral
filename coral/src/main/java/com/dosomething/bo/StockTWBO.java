package com.dosomething.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.dosomething.commons.dao.DailyStakeDAO;
import com.dosomething.commons.dao.SecuritysDAO;
import com.dosomething.commons.dao.StockDailyTransactDAO;
import com.dosomething.commons.dto.DailyStake;
import com.dosomething.commons.dto.Securitys;
import com.dosomething.commons.model.database.DBPool;
import com.dosomething.util.DbUtils;


public class StockTWBO {
	
	public static int addSecurity(String securityId, String url) throws SQLException {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getWriteConnection();
			conn.setAutoCommit(false);

			int result = SecuritysDAO.save(conn, securityId, url);
			conn.commit();
			return result;
		} catch (Exception e) {
			DbUtils.rollback(conn);
			throw e;
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static List<Securitys> queryNewsByID() throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			return SecuritysDAO.findAllSecuritys(conn);
		} finally {
			DbUtils.close(conn);
		}
	}
	
	public static List<String> getTopSecurityTrade(String securityId, String type, int top) throws SQLException {
		Connection conn = null;
		CachedRowSet crs = null;
		List<String> rsList = new ArrayList<String>();
		try {
			conn = DBPool.getInstance().getReadConnection();
			if(type.equals("b")) {
				crs = DailyStakeDAO.getTopBuySecurityTrade(conn, securityId, top);
			} else if(type.equals("s")) {
				crs = DailyStakeDAO.getTopSellSecurityTrade(conn, securityId, top);
			}
			
			while (crs.next()) {
				rsList.add(crs.getString("STOCK_ID"));
			}
			return rsList;
		}finally {
			DbUtils.close(crs);
			DbUtils.close(conn);
		}		
	}
	
	public static List<Integer> getThisMonthStockTransactDate(String stockId) throws SQLException {
		Connection conn = null;
		CachedRowSet crs = null;
		List<Integer> rsList = new ArrayList<Integer>();
		try {
			conn = DBPool.getInstance().getReadConnection();
			crs = StockDailyTransactDAO.getThisMonthStockTransactDate(conn, stockId);
			
			while (crs.next()) {
				rsList.add(crs.getInt("DAY"));
			}
			return rsList;
		}finally {
			DbUtils.close(crs);
			DbUtils.close(conn);
		}		
	}

	public static List<DailyStake> getSecurityTradeByStockIdAndSecurityId(String stockId, String securityId) throws Exception {
		Connection conn = null;
		try {
			conn = DBPool.getInstance().getReadConnection();
			return DailyStakeDAO.getSecurityTradeByStockIdAndSecurityId(conn, stockId, securityId);
		} finally {
			DbUtils.close(conn);
		}
	}
}
