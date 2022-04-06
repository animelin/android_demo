package com.example.broadcastreceiverdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private NetBroadcastReceiver netBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (netBroadcastReceiver == null) {
            netBroadcastReceiver = new NetBroadcastReceiver();
            intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netBroadcastReceiver, intentFilter);
            //设置监听
            //netBroadcastReceiver.setStatusMonitor(this);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(netBroadcastReceiver);

    }
}

class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        int statu = networkInfo.getType();
        NetworkInfo.State s1 =networkInfo.getState();
        boolean isWiFi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        String s2 =networkInfo.getTypeName();

        Log.d("internet", "网络链接:"+isConnected);
        Log.d("internet", "使用WIFI:"+isWiFi);
        Log.d("internet", "状态:"+s1);
        Log.d("internet", "网络类型:"+statu);
        Log.d("internet", "类型名字:"+s2);

    }
}