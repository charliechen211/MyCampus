package com.stpi.campus.entity;


public class Node {
    private String name;
    private String id;
    private float weight;
    private int recommended;

    public Node() {
    }

    public Node(String name, String id, float weight, int recommended) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.recommended = recommended;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getRecommeded() {
        return recommended;
    }

    public void setRecommeded(int recommeded) {
        this.recommended = recommeded;
    }

}
