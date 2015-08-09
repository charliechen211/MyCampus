package com.stpi.campus.entity.interactionWall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyl on 2014/7/27.
 */
public class InteractionTopicHelper {
    private List<InteractionTopicInfo> result;
    private String state;

    public InteractionTopicHelper() {
        result = new ArrayList<InteractionTopicInfo>();
        state = "fail";
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<InteractionTopicInfo> getResult() {
        return result;
    }

    public void setResult(List<InteractionTopicInfo> result) {
        this.result = result;
    }
}

