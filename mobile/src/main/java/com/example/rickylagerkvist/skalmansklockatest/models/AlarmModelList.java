package com.example.rickylagerkvist.skalmansklockatest.models;

import java.util.List;

/**
 * Created by rickylagerkvist on 2017-02-12.
 */

public class AlarmModelList {

    private List<AlarmModel> list;

    public AlarmModelList(List<AlarmModel> list) {
        this.list = list;
    }

    public List<AlarmModel> getList() {
        return list;
    }

    public void setList(List<AlarmModel> list) {
        this.list = list;
    }
}
