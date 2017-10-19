package com.dosomething.commons.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.dosomething.commons.dto.DailyStake;
import com.dosomething.commons.dto.StockDailyTransact;
import com.dosomething.commons.model.database.DBQueryRunner;

public class StockDailyTransactDAO {

	public static CachedRowSet getThisMonthStockTransactDate(Connection conn, String stockId) throws SQLException {
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		String sql = "SELECT DAY(TRANSACT_DATE) AS DAY FROM STOCK_DAILY_TRANSACT WHERE STOCK_ID=? AND MONTH(TRANSACT_DATE)=? ";
		return DBQueryRunner.query(conn, sql, stockId, cal.get(Calendar.MONTH) + 1);
	}

	public static StockDailyTransact getStockDailyTransact(Connection conn, String stockId, Date transactDate) throws SQLException {
		String sql = "SELECT * FROM STOCK_DAILY_TRANSACT WHERE STOCK_ID=? AND TRANSACT_DATE=? ";
		return DBQueryRunner.getBean(conn, StockDailyTransact.class, sql, stockId, new Timestamp(transactDate.getTime()));
	}	
	
	public static int save(Connection conn, StockDailyTransact sdt) throws SQLException {
		String sql = "INSERT INTO STOCK_DAILY_TRANSACT "
				   + "(STOCK_ID ,TRANSACT_DATE, TRANSACT_VOLUME, TURNOVER ,OPEN, HIGH, LOW, CLOSE, GROSS_BALANCE, TRANSACT_TOTAL) "
				   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Object[] values = new Object[10];
		int i = 0;
		values[i++] = sdt.getStockId();
		values[i++] = sdt.getTransactDate();
		values[i++] = sdt.getTransactVolume();
		values[i++] = sdt.getTurnover();
		values[i++] = sdt.getOpen();
		values[i++] = sdt.getHigh();
		values[i++] = sdt.getLow();
		values[i++] = sdt.getClose();
		values[i++] = sdt.getGrossBalance();
		values[i++] = sdt.getTransactTotal();
		
		return DBQueryRunner.update(conn, sql, values);
		
	}	
}
