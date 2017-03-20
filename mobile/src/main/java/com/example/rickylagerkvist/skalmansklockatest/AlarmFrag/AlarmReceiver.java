package com.example.rickylagerkvist.skalmansklockatest.AlarmFrag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.Window;
import android.view.WindowManager;

import com.example.rickylagerkvist.skalmansklockatest.ShowAlarmActivty;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        String alarmModelJson = intent.getStringExtra("alarmModel");

        Intent showAlarmActivityMobile = new Intent(context, ShowAlarmActivty.class);
        showAlarmActivityMobile.putExtra("alarmModel", alarmModelJson);
        showAlarmActivityMobile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(showAlarmActivityMobile);
    }
}
