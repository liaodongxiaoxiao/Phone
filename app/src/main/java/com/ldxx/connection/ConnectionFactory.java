package com.ldxx.connection;

import com.ldxx.connection.imlp.BluetoothConnection;

/**
 * Created by LDXX on 2016/2/29.
 * company Ltd
 * liaodongxiaoxiao@gmail.com
 */
public class ConnectionFactory {
    public static final String TYPE_BLUETOOTH="bluetooth";
    public Connection getConnection(String type) {
        if (TYPE_BLUETOOTH.equals(type)) {
            return new BluetoothConnection();
        }
        return null;
    }
}
