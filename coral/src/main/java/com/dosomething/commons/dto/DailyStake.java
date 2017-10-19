package com.dosomething.commons.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.dosomething.commons.annotation.Column;
import com.dosomething.util.JSONUtils;

public class DailyStake {
	
	@Column(name = "SECURITY_ID")
	private String securityId;
	@Column(name = "STOCK_ID")
	private String stockId;
	@Column(name = "BUY_STAKE")
	private BigDecimal buyStake;
	@Column(name = "SELL_STAKE")
	private BigDecimal sellStake;
	@Column(name = "CLOSE")
	private BigDecimal close;
	@Column(name = "CREATED_DATE")
	private Timestamp createdDate;
	
	private long tradeDateMinSec;
	
	public String getSecurityId() {
		return securityId;
	}
	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public BigDecimal getBuyStake() {
		return buyStake;
	}
	public void setBuyStake(BigDecimal buyStake) {
		this.buyStake = buyStake;
	}
	public BigDecimal getSellStake() {
		return sellStake;
	}
	public void setSellStake(BigDecimal sellStake) {
		this.sellStake = sellStake;
	}	
	public Timestamp getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}	
	public long getTradeDateMinSec() {
		return tradeDateMinSec;
	}
	public void setTradeDateMinSec(long tradeDateMinSec) {
		this.tradeDateMinSec = tradeDateMinSec;
	}	
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	
	@Override
	public String toString() {
		return JSONUtils.toJsonString(this);
	}
}
