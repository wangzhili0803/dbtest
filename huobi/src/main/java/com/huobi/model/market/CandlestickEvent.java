package com.huobi.model.market;

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
public class CandlestickEvent {

    private String ch;

    private long ts;

    private Candlestick candlestick;

    public String getCh() {
        return ch;
    }

    public void setCh(final String ch) {
        this.ch = ch;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(final long ts) {
        this.ts = ts;
    }

    public Candlestick getCandlestick() {
        return candlestick;
    }

    public void setCandlestick(final Candlestick candlestick) {
        this.candlestick = candlestick;
    }
}
