package com.dosomething.commons.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.dosomething.commons.annotation.Column;
import com.dosomething.util.JSONUtils;

public class StockDailyTransact {
	
	@Column(name = "STOCK_ID")
	private String stockId;
	@Column(name = "TRANSACT_DATE")
	private Timestamp transactDate;
	@Column(name = "TRANSACT_VOLUME")
	private BigDecimal transactVolume;
	@Column(name = "TURNOVER")
	private BigDecimal turnover;
	@Column(name = "OPEN")
	private BigDecimal open;
	@Column(name = "CLOSE")
	private BigDecimal close;
	@Column(name = "HIGH")
	private BigDecimal high;
	@Column(name = "LOW")
	private BigDecimal low;
	@Column(name = "GROSS_BALANCE")
	private BigDecimal grossBalance ;
	@Column(name = "TRANSACT_TOTAL")
	private BigDecimal transactTotal;
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public Timestamp getTransactDate() {
		return transactDate;
	}
	public void setTransactDate(Timestamp transactDate) {
		this.transactDate = transactDate;
	}
	public BigDecimal getTransactVolume() {
		return transactVolume;
	}
	public void setTransactVolume(BigDecimal transactVolume) {
		this.transactVolume = transactVolume;
	}
	public BigDecimal getTurnover() {
		return turnover;
	}
	public void setTurnover(BigDecimal turnover) {
		this.turnover = turnover;
	}
	public BigDecimal getOpen() {
		return open;
	}
	public void setOpen(BigDecimal open) {
		this.open = open;
	}
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	public BigDecimal getHigh() {
		return high;
	}
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
	public BigDecimal getLow() {
		return low;
	}
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	public BigDecimal getGrossBalance() {
		return grossBalance;
	}
	public void setGrossBalance(BigDecimal grossBalance) {
		this.grossBalance = grossBalance;
	}
	public BigDecimal getTransactTotal() {
		return transactTotal;
	}
	public void setTransactTotal(BigDecimal transactTotal) {
		this.transactTotal = transactTotal;
	}
	
	@Override
	public String toString() {
		return JSONUtils.toJsonString(this);
	}
}
