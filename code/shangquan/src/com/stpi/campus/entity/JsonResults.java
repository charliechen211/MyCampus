package com.stpi.campus.entity;

import java.util.ArrayList;
import java.util.List;

public class JsonResults {
    List<Node> resultList;

    public JsonResults() {
        resultList = new ArrayList<Node>();
    }

    public List<Node> getResultList() {
        return resultList;
    }

    public void setResultList(List<Node> resultList) {
        this.resultList = resultList;
    }

}
