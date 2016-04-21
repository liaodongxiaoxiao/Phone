package com.ldxx.connection;

/**
 * Created by LDXX on 2016/2/29.
 * company Ltd
 * liaodongxiaoxiao@gmail.com
 */
public interface Connection {
    void start();

    void stop();

    int getStatus();

    void send(byte[] out);

    void send(String msg);

    void setConnectionListener(ConnectionListener listener);
}
