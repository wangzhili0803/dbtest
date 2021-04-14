package com.huobi.model.market;

import java.util.List;

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
public class CandlestickReq {

  private String ch;

  private Long ts;

  private List<Candlestick> candlestickList;

  public String getCh() {
    return ch;
  }

  public void setCh(final String ch) {
    this.ch = ch;
  }

  public Long getTs() {
    return ts;
  }

  public void setTs(final Long ts) {
    this.ts = ts;
  }

  public List<Candlestick> getCandlestickList() {
    return candlestickList;
  }

  public void setCandlestickList(final List<Candlestick> candlestickList) {
    this.candlestickList = candlestickList;
  }
}
