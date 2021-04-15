package com.huobi.client.req.market;

import com.huobi.constant.enums.CandlestickIntervalEnum;

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
public class SubCandlestickRequest {

    private String symbol;

    private CandlestickIntervalEnum interval;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public CandlestickIntervalEnum getInterval() {
        return interval;
    }

    public void setInterval(final CandlestickIntervalEnum interval) {
        this.interval = interval;
    }
}
