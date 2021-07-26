package com.j6crypto.to;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Candlestick {

	private BigDecimal open;
	private BigDecimal close;
	private BigDecimal high;
	private BigDecimal low;
	private boolean closed = false;
	private LocalDateTime date;
	private BigDecimal vol;
	private BigDecimal maxBidVol;
	private BigDecimal maxAskVol;
	private BigDecimal startVol;
	
	public Candlestick() {
	}
	public Candlestick(BigDecimal start, LocalDateTime date) {
		open = start;
		close=start;
		high=start;
		low=start;
		this.date = date;
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

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public BigDecimal getVol() {
		return vol;
	}

	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}

	public BigDecimal getMaxBidVol() {
		return maxBidVol;
	}

	public void setMaxBidVol(BigDecimal maxBidVol) {
		this.maxBidVol = maxBidVol;
	}

	public BigDecimal getMaxAskVol() {
		return maxAskVol;
	}

	public void setMaxAskVol(BigDecimal maxAskVol) {
		this.maxAskVol = maxAskVol;
	}

	public BigDecimal getStartVol() {
		return startVol;
	}

	public void setStartVol(BigDecimal startVol) {
		this.startVol = startVol;
	}
}
