package com.stpi.campus.entity.partTimeJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/8/11.
 */
public class PartTimeJobHelper {
    private List<PartTimeJobInfo> result;
    private String state;

    public PartTimeJobHelper() {
        result = new ArrayList<PartTimeJobInfo>();
    }

    public List<PartTimeJobInfo> getResult() {
        return result;
    }

    public void setResult(List<PartTimeJobInfo> result) {
        this.result = result;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
