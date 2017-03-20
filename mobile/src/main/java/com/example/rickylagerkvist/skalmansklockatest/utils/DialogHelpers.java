package com.example.rickylagerkvist.skalmansklockatest.utils;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rickylagerkvist.skalmansklockatest.AlarmFrag.AlarmAdapter;
import com.example.rickylagerkvist.skalmansklockatest.AlarmFrag.AlarmReceiver;
import com.example.rickylagerkvist.skalmansklockatest.R;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModel;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModelList;
import com.example.rickylagerkvist.skalmansklockatest.models.enums.AlarmType;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

/**
 * Created by rickylagerkvist on 2017-02-12.
 */

public class DialogHelpers {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void AddOrUpdateAlarmModelDialog(final Context context, final AlarmModel model, final AlarmAdapter alarmAdapter, final List<AlarmModel> alarmModels){

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alarm_dialog);

        // title
        final EditText text = (EditText) dialog.findViewById(R.id.title_edittext);

        // spinner for AlarmType
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.alarm_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.alarm_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        final AlarmType[] alarmType = {AlarmType.FOOD};

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:  alarmType[0] = AlarmType.FOOD;
                        break;
                    case 1:  alarmType[0] = AlarmType.SLEEP;
                        break;
                    case 2:  alarmType[0] = AlarmType.WAKE;
                        break;
                    default: alarmType[0] = AlarmType.FOOD;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // TimePicker
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);

        // dismiss dialog
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // add/update alarm button
        Button addButton = (Button) dialog.findViewById(R.id.add_alarm_button);

        // update mode, set model values to views
        if(model != null){
            text.setText(model.getTitle());

            if (Build.VERSION.SDK_INT >= 23 ) {
                timePicker.setHour(model.getCalendarHour());
                timePicker.setMinute(model.getCalendarMin());
            } else {
                timePicker.setCurrentHour(model.getCalendarHour());
                timePicker.setCurrentMinute(model.getCalendarMin());
            }

            switch (model.getAlarmType()){
                case FOOD: spinner.setSelection(0);
                    break;
                case SLEEP: spinner.setSelection(1);
                    break;
                case WAKE: spinner.setSelection(2);
                    break;
                default: spinner.setSelection(0);
                    break;
            }

            addButton.setText(R.string.update_alarm);
        }

        // alarm button action
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!text.getText().toString().trim().isEmpty()){

                    Intent intent = new Intent(context, AlarmReceiver.class);
                    Gson gson = new Gson();
                    Calendar calendar = Calendar.getInstance();
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    int currentHour = calendar.get(Calendar.HOUR);
                    int currentMin = calendar.get(Calendar.MINUTE);

                    // update model
                    if(model != null) {

                        model.setTitle(text.getText().toString());
                        model.setCalendarHour(timePicker.getHour());
                        model.setCalendarMin(timePicker.getMinute());
                        model.setAlarmType(alarmType[0]);

                        // put model as String extra
                        String AlarmModelJson = gson.toJson(model);
                        intent.putExtra("alarmModel", AlarmModelJson);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, model.getIntentNr(), intent, 0);

                        // selected time is before current time
                        if(currentHour > model.getCalendarHour() || currentMin > model.getCalendarMin()){
                            calendar.add(Calendar.DATE, 1);
                            calendar.set(Calendar.HOUR_OF_DAY, model.getCalendarHour());
                            calendar.set(Calendar.MINUTE, model.getCalendarMin());
                        } else {
                            calendar.set(Calendar.HOUR_OF_DAY, model.getCalendarHour());
                            calendar.set(Calendar.MINUTE, model.getCalendarMin());
                        }

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                    // create new model
                    } else {

                        // get nr to use in PendingIntent and model
                        int intentNr = PreferenceManager.getDefaultSharedPreferences(context).getInt("PendingIntenNr", 0) + 1;
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("PendingIntenNr", intentNr).apply();

                        // create model
                        AlarmModel alarmModel = new AlarmModel(text.getText().toString(), timePicker.getHour(), timePicker.getMinute(), alarmType[0], intentNr);
                        alarmModels.add(alarmModel);

                        // put model as String extra
                        String AlarmModelJson = gson.toJson(alarmModel);
                        intent.putExtra("alarmModel", AlarmModelJson);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, intentNr, intent, 0);

                        // selected time is before current time
                        if(currentHour > alarmModel.getCalendarHour() || currentMin > alarmModel.getCalendarMin()){
                            calendar.add(Calendar.DATE, 1);
                            calendar.set(Calendar.HOUR_OF_DAY, alarmModel.getCalendarHour());
                            calendar.set(Calendar.MINUTE, alarmModel.getCalendarMin());
                        } else {
                            calendar.set(Calendar.HOUR_OF_DAY, alarmModel.getCalendarHour());
                            calendar.set(Calendar.MINUTE, alarmModel.getCalendarMin());
                        }

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                    alarmAdapter.notifyDataSetChanged();

                    // save list to sharedPref
                    Gson gsonList = new Gson();
                    AlarmModelList list = new AlarmModelList(alarmModels);
                    String alarmListString = gsonList.toJson(list);
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("ALARMLIST", alarmListString).apply();

                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Lägg til en titel", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }
}
