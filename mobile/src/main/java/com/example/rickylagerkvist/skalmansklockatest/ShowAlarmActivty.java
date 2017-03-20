package com.example.rickylagerkvist.skalmansklockatest;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rickylagerkvist.skalmansklockatest.AlarmFrag.AlarmReceiver;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModel;
import com.google.gson.Gson;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowAlarmActivty extends AppCompatActivity {

    @BindView(R.id.alarm_type_image) ImageView alarmTypeImage;
    @BindView(R.id.alarm_model_title) TextView alarmTitleText;
    @BindView(R.id.exit_app_button) FloatingActionButton fab;
    Vibrator mVibrator;
    AlarmModel alarmModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alarm_activty);

        ButterKnife.bind(this);

        setFullScreen();
        setScreenToBeOn();

        getAlarmModel();

        setUI();

        buildNotification();

        startVibrator();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVibrator.cancel();
    }


    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    private void setScreenToBeOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }


    private void getAlarmModel() {
        // get intent and convert Json back to AlarmModel
        Intent intent = getIntent();
        Gson gson = new Gson();
        alarmModel = gson.fromJson(intent.getStringExtra("alarmModel"), AlarmModel.class);
    }


    private void setUI() {
        switch (alarmModel.getAlarmType()){
            case FOOD:  alarmTypeImage.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_restaurant_white_48dp));
                break;
            case WAKE:  alarmTypeImage.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_wb_sunny_white_48dp));
                break;
            case SLEEP: alarmTypeImage.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_airline_seat_individual_suite_white_48dp));
                break;
            default:    alarmTypeImage.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_restaurant_white_48dp));
                break;
        }
        alarmTitleText.setText(alarmModel.getTitle());
    }


    private void buildNotification() {

        int alarmTypeImage;

        switch (alarmModel.getAlarmType()){
            case FOOD:  alarmTypeImage = R.drawable.ic_restaurant_white_48dp;
                break;
            case WAKE:  alarmTypeImage = R.drawable.ic_wb_sunny_white_48dp;
                break;
            case SLEEP: alarmTypeImage = R.drawable.ic_airline_seat_individual_suite_white_48dp;
                break;
            default:    alarmTypeImage = R.drawable.ic_restaurant_white_48dp;
                break;
        }

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                alarmTypeImage);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(alarmTypeImage)
                        .setContentTitle(alarmModel.getTitle())
                        .setContentText(alarmModel.getCalenderText());

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(intent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }


    private void startVibrator() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Start without a delay, Vibrate for 100 milliseconds, Sleep for 1000 milliseconds
        long[] pattern = {0, 1000, 1000};
        // repeat indefinitely
        mVibrator.vibrate(pattern, 0);
    }

}
