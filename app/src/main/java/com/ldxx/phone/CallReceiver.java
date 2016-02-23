package com.ldxx.phone;

import android.content.Context;
import android.util.Log;

import java.util.Date;

/**
 * Created by LDXX on 2016/2/22.
 * company Ltd
 * liaodongxiaoxiao@gmail.com
 */
public class CallReceiver extends PhonecallReceiver {
    private static final String TAG = "CallReceiver";

    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start) {
        Log.e(TAG, "onIncomingCallReceived: " + number + " " + start);
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Log.e(TAG, "onIncomingCallAnswered: " + number + " " + start);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e(TAG, "onIncomingCallEnded: " + number + " " + end);
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.e(TAG, "onOutgoingCallStarted: " + number + " " + start);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.e(TAG, "onOutgoingCallEnded: " + number + " " + end);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.e(TAG, "onMissedCall: " + number + " " + start);
    }
}
