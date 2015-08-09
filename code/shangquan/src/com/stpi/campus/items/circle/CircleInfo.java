package com.stpi.campus.items.circle;

/**
 * Created by cyc on 14-8-31.
 */
public class CircleInfo {
    private int circleId;
    private String circleName;

    public CircleInfo() {
        this.circleId = 0;
        this.circleName = "";
    }

    public CircleInfo(int circleId, String circleName) {
        this.circleId = circleId;
        this.circleName = circleName;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }
}
