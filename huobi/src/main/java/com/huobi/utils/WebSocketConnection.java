package com.huobi.utils;

import com.huobi.constant.enums.ConnectionStateEnum;

public interface WebSocketConnection {

    ConnectionStateEnum getState();

    Long getConnectionId();

    void reConnect();

    void reConnect(int delayInSecond);

    void close();

    long getLastReceivedTime();

    void send(String str);
}
