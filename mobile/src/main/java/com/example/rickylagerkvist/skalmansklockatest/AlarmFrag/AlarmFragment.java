package com.example.rickylagerkvist.skalmansklockatest.AlarmFrag;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rickylagerkvist.skalmansklockatest.utils.DialogHelpers;
import com.example.rickylagerkvist.skalmansklockatest.R;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModel;
import com.example.rickylagerkvist.skalmansklockatest.models.AlarmModelList;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment {

    @BindView(R.id.alarm_recycler_view) RecyclerView mAlarmRecyclerView;
    @BindView(R.id.add_alarm_fab) FloatingActionButton addAlarmFab;
    private AlarmAdapter mAlarmAdapter;
    private List<AlarmModel> alarmModelList = new ArrayList<>();

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, rootView);
        // set up model
        GetAlarmsFromSharedPref();

        // RecyclerView
        mAlarmAdapter = new AlarmAdapter(alarmModelList, getContext());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mAlarmRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);

        // Fab add new alarm);
        addAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelpers.AddOrUpdateAlarmModelDialog(getContext(), null, mAlarmAdapter, alarmModelList);
            }
        });


        // show/hide fab on scroll
        mAlarmRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 || dy<0 && addAlarmFab.isShown()){
                    addAlarmFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    addAlarmFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void GetAlarmsFromSharedPref() {

        // if key exists, convert string to object and set to alarmModelList
        if(PreferenceManager.getDefaultSharedPreferences(getContext()).contains("ALARMLIST")){
            try {
                Gson gson = new Gson();
                String alarmLstString = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("ALARMLIST", "");
                AlarmModelList list = gson.fromJson(alarmLstString, AlarmModelList.class);
                alarmModelList = list.getList();
            } catch (Exception e) {

            }
        }
    }
}
