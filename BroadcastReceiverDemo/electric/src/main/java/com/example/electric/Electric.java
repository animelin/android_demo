package com.example.electric;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.BatteryState;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

public class Electric extends AppCompatActivity {

    private String TAG="Electric.this";
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private PowerConnectionReceiver powerConnectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric);

        //设置意图过滤器
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //注册广播接收者，因为BatteryManager发送的是sticky形式的intent，所以接收者可以为空
        Intent batteryStatus = Electric.this.registerReceiver(null, ifilter);
        //得到电池当前的状态（共有5种，包括unkonwn、charging、discharging、not charging、full）
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        //是否处于充电状态
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        //充电方式（共有两种，分别是通过AC充电和通过USB端口进行充电）
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        //判断当前电池电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        //打印相关信息
        Log.i("BatteryLevelReceiver", "isCharging:" + isCharging + "；" + "usbCharge:" + usbCharge + "；" + "acCharge:" + acCharge);
        if(isCharging==true){
            Log.d("BatteryLevelReceiver","正在充电" );
        }
        Log.d("BatteryLevelReceiver", "电量池："+level);
        Log.d("BatteryLevelReceiver", "电量总池："+scale);
        Log.i("BatteryLevelReceiver","电量："+batteryPct);
        if(batteryPct<=0.2){
            Log.d("BatteryLevelReceiver","电量较低" );
        }
        if(batteryPct>0.2){
            Log.d("BatteryLevelReceiver","电量充足" );
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        broadcastReceiver = new BatteryLevelReceiver();
        powerConnectionReceiver =new PowerConnectionReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
        registerReceiver(powerConnectionReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(powerConnectionReceiver);
    }

}


class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG ="Electric.this";

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Log.d("PowerConnectionReceiver", "状态:"+status );
        Log.d("PowerConnectionReceiver", "是否充电:"+isCharging );
        Log.d("PowerConnectionReceiver", "插口:"+chargePlug );
        Log.d("PowerConnectionReceiver", "usb插口:"+usbCharge );
        Log.d("PowerConnectionReceiver", "ac插口:"+acCharge );
    }
}

class BatteryLevelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        float batteryPct = level / (float)scale;
        //电池温度温度
        int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);

        Log.d("BatteryLevelReceiver", "温度:"+temperature );
        Log.d("BatteryLevelReceiver", "当前电量:"+level);
        Log.d("BatteryLevelReceiver", "总量:"+scale);
        Log.d("BatteryLevelReceiver", "电量:"+batteryPct);
//        Log.d("BatteryLevelReceiver:onReceive", "batteryPct: "+batteryPct);
//        Log.d("BatteryLevelReceiver:onReceive", "level: "+level);
//        Log.d("BatteryLevelReceiver:onReceive", "scale："+scale);
    }
}