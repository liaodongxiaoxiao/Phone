package com.ldxx.connection;

/**
 * Created by LDXX on 2016/2/29.
 * company Ltd
 * liaodongxiaoxiao@gmail.com
 */
public interface ConnectionListener {
    void onConnectFailed();

    void onConnectLost();

    void onConnectSuccess();
}
