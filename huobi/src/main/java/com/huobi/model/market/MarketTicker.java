package com.huobi.model.market;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MarketTicker {

  private String symbol;

  private BigDecimal open;

  private BigDecimal close;

  private BigDecimal low;

  private BigDecimal high;

  private BigDecimal amount;

  private Long count;

  private BigDecimal vol;

  private BigDecimal bid;

  private BigDecimal bidSize;

  private BigDecimal ask;

  private BigDecimal askSize;

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(final String symbol) {
    this.symbol = symbol;
  }

  public BigDecimal getOpen() {
    return open;
  }

  public void setOpen(final BigDecimal open) {
    this.open = open;
  }

  public BigDecimal getClose() {
    return close;
  }

  public void setClose(final BigDecimal close) {
    this.close = close;
  }

  public BigDecimal getLow() {
    return low;
  }

  public void setLow(final BigDecimal low) {
    this.low = low;
  }

  public BigDecimal getHigh() {
    return high;
  }

  public void setHigh(final BigDecimal high) {
    this.high = high;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(final BigDecimal amount) {
    this.amount = amount;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(final Long count) {
    this.count = count;
  }

  public BigDecimal getVol() {
    return vol;
  }

  public void setVol(final BigDecimal vol) {
    this.vol = vol;
  }

  public BigDecimal getBid() {
    return bid;
  }

  public void setBid(final BigDecimal bid) {
    this.bid = bid;
  }

  public BigDecimal getBidSize() {
    return bidSize;
  }

  public void setBidSize(final BigDecimal bidSize) {
    this.bidSize = bidSize;
  }

  public BigDecimal getAsk() {
    return ask;
  }

  public void setAsk(final BigDecimal ask) {
    this.ask = ask;
  }

  public BigDecimal getAskSize() {
    return askSize;
  }

  public void setAskSize(final BigDecimal askSize) {
    this.askSize = askSize;
  }
}
