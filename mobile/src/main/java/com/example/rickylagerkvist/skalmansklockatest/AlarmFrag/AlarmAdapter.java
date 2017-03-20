package com.example.rickylagerkvist.skalmansklockatest.AlarmFrag;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rickylagerkvist.skalmansklockatest.utils.DialogHelpers;
import com.example.rickylagerkvist.skalmansklockatest.R;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModel;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModelList;
import com.github.zagum.switchicon.SwitchIconView;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rickylagerkvist on 2017-02-12.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

    private List<AlarmModel> alarmModelList;
    private Context context;
    private AlarmManager alarmManager;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.title) public TextView title;
        @BindView(R.id.time) public TextView time;
        @BindView(R.id.delete_image) ImageView deleteImage;
        @BindView(R.id.alarm_type_image) ImageView alarmTypeImage;
        @BindView(R.id.switch_alarm_icon) SwitchIconView isAlarmOnImage;

        public MyViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public AlarmAdapter(List<AlarmModel> alarmModelList, Context context) {
        this.alarmModelList = alarmModelList;
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public AlarmAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item_layout, parent, false);
        return new MyViewHolder(ItemView);
    }

    @Override
    public void onBindViewHolder(final AlarmAdapter.MyViewHolder holder, int position) {
        final AlarmModel alarmModel = alarmModelList.get(position);

        // isAlarmOn
        if(alarmModel.isAlarmOn()){
            holder.isAlarmOnImage.setIconEnabled(true);
        } else {
            holder.isAlarmOnImage.setIconEnabled(false);
        }

        // switch model IsAlarmOn true or false and cancel or add alarm
        holder.isAlarmOnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alarmModel.isAlarmOn()){
                    alarmModel.setAlarmOn(false);

                    // cancel alarm
                    Intent intent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmModel.getIntentNr(), intent, 0);

                    alarmManager.cancel(pendingIntent);

                } else {
                    alarmModel.setAlarmOn(true);

                    // add alarm
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, alarmModel.getCalendarHour());
                    calendar.set(Calendar.MINUTE, alarmModel.getCalendarMin());

                    Intent intent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmModel.getIntentNr(), intent, 0);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

//                    // set alarm as exact as possible based on build ver
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    } else {
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//                    }
                }
                AlarmAdapter.this.notifyDataSetChanged();
                saveListToPref();
            }
        });


        // title
        holder.title.setText(alarmModel.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelpers.AddOrUpdateAlarmModelDialog(context, alarmModel, AlarmAdapter.this, alarmModelList);
            }
        });

        // time
        holder.time.setText(alarmModel.getCalenderText());
        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelpers.AddOrUpdateAlarmModelDialog(context, alarmModel, AlarmAdapter.this, alarmModelList);
            }
        });

        // alarm type
        switch (alarmModel.getAlarmType()) {
            case FOOD:  holder.alarmTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
                break;
            case WAKE:  holder.alarmTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wb_sunny_black_24dp));
                break;
            case SLEEP: holder.alarmTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_airline_seat_individual_suite_black_24dp));
                break;
            default:    holder.alarmTypeImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_restaurant_black_24dp));
                break;
        }
        holder.alarmTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelpers.AddOrUpdateAlarmModelDialog(context, alarmModel, AlarmAdapter.this, alarmModelList);
            }
        });

        // delete model
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Ta bort " + alarmModel.getTitle() + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ta bort",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // cancel alarm
                                Intent intent = new Intent(context, AlarmReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmModel.getIntentNr(), intent, 0);

                                alarmManager.cancel(pendingIntent);

                                // remove model
                                alarmModelList.remove(alarmModel);
                                AlarmAdapter.this.notifyDataSetChanged();

                                // save updated list to sharedPref
                                saveListToPref();

                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "Avbryt",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmModelList.size();
    }


    private void saveListToPref(){
        Gson gson = new Gson();
        AlarmModelList list = new AlarmModelList(alarmModelList);
        String alarmListString = gson.toJson(list);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ALARMLIST", alarmListString).apply();
    }
}
