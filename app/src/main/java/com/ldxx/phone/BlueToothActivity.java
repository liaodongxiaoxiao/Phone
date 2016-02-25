package com.ldxx.phone;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlueToothActivity extends AppCompatActivity {
    private static final String TAG = "BlueToothActivity";

    public static final int REQUEST_ENABLE_BT = 1100;
    private BluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        ButterKnife.bind(this);


        registerReceiver(stateChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(foundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        IntentFilter in = new IntentFilter();
        in.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        in.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        in.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(connectionReceiver, in);
        //1. get bluetooth adapter
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(BlueToothActivity.this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            //2.
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                queryDevices();
            }
        }
        Log.e(TAG, "onCreate: " + adapter);

    }

    private void queryDevices() {
        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.e(TAG, "onReceive: " + device.getName() + " " //+ device.getType() + " "
                    + device.getAddress() + " " + device.getBondState());
        }
    }


    @OnClick(R.id.discovery)
    void discoveringDevices() {
        //扫描蓝牙设备
        adapter.startDiscovery();
        //取消扫描
        //adapter.cancelDiscovery();
    }

    @OnClick(R.id.enabling)
    void enablingDiscoverability() {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //0~3600 seconds
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1200);
        startActivity(discoverableIntent);
    }


    private final BroadcastReceiver foundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, "onReceive: " + device.getName() + "\n" + device.getAddress());
            }
        }
    };
    private final BroadcastReceiver stateChangedReceiver = new BroadcastReceiver() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.e(TAG, "onReceive: STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.e(TAG, "onReceive: STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.e(TAG, "onReceive: STATE_ON");
                        queryDevices();

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.e(TAG, "onReceive: STATE_TURNING_ON");
                        break;
                }

            }
        }
    };

    public BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();            // Get intent's action string
            Bundle extras = intent.getExtras();            // Get all the Intent's extras
            if (extras == null) return;                    // All intents of interest have extras.

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Log.e(TAG, "onReceive ACTION_ACL_CONNECTED: " + intent.getData());
                //Do something with bluetooth device connection
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Log.e(TAG, "onReceive ACTION_ACL_DISCONNECTED: " + intent.getData());
                //Do something with bluetooth device disconnection
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.e(TAG, "onReceive: ACTION_BOND_STATE_CHANGED" );
                BluetoothDevice device =  (BluetoothDevice) extras.get("android.bluetooth.device.extra.DEVICE");
                if(device!=null){
                    Log.e(TAG, "onReceive: "+device.getName()+" "+device.getAddress() );
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateChangedReceiver);
        unregisterReceiver(foundReceiver);
        unregisterReceiver(connectionReceiver);
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);

                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
