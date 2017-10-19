package com.dosomething.commons.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.dosomething.commons.dto.DailyStake;
import com.dosomething.commons.model.database.DBQueryRunner;


public class DailyStakeDAO {
	
//	public static Integer findEntityByKey(final Connection conn) throws Exception {
//
//		String sql = "SELECT count(*) from DAILY_STAKE ";
//
//		return DBQueryRunner.getNumber(conn, sql).intValue();
//	}
	
	public static int save(final Connection conn, DailyStake ds) throws SQLException {
		String sql = "INSERT INTO DAILY_STAKE (SECURITY_ID ,STOCK_ID, BUY_STAKE, SELL_STAKE, CLOSE,CREATED_DATE) VALUES (?, ?, ?, ?, ?, ?)";
		return DBQueryRunner.update(conn, sql, ds.getSecurityId(), ds.getStockId(), ds.getBuyStake(), ds.getSellStake(), ds.getClose(), ds.getCreatedDate());
	}
	
	public static CachedRowSet getTopBuySecurityTrade(Connection conn, String securityId, int top) throws SQLException {
		String sql = "SELECT SUM( BUY_STAKE - SELL_STAKE ) AS COUNT, SECURITY_ID, STOCK_ID "
				   + "FROM DAILY_STAKE WHERE SECURITY_ID = ? "
				   + "GROUP BY SECURITY_ID, STOCK_ID ORDER BY COUNT DESC LIMIT ?";
		return DBQueryRunner.query(conn, sql, securityId, top);
	}
	
	public static CachedRowSet getTopSellSecurityTrade(Connection conn, String securityId, int top) throws SQLException {
		String sql = "SELECT SUM( SELL_STAKE - BUY_STAKE ) AS COUNT, SECURITY_ID, STOCK_ID "
				   + "FROM DAILY_STAKE WHERE SECURITY_ID = ? "
				   + "GROUP BY SECURITY_ID, STOCK_ID ORDER BY COUNT DESC LIMIT ?";
		return DBQueryRunner.query(conn, sql, securityId, top);
	}

	public static List<DailyStake> getSecurityTradeByStockIdAndSecurityId(Connection conn, String stockId, String securityId) throws SQLException {
		String sql = "SELECT * FROM DAILY_STAKE WHERE STOCK_ID=? AND SECURITY_ID=? ORDER BY CREATED_DATE ASC";
		return DBQueryRunner.getBeanList(conn, DailyStake.class, sql, stockId, securityId);
	}	
}