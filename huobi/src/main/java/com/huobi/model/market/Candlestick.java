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
public class Candlestick {

    private Long id;

    private BigDecimal amount;

    private BigDecimal count;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private BigDecimal vol;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(final BigDecimal count) {
        this.count = count;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(final BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(final BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(final BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(final BigDecimal close) {
        this.close = close;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(final BigDecimal vol) {
        this.vol = vol;
    }
}
