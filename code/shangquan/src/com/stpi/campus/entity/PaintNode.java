package com.stpi.campus.entity;


public class PaintNode {
    private Node node;
    private float posX;
    private float posY;
    private int color;
    private float radius;
    private float weight;
    private boolean rightDirection;

    public PaintNode() {

    }

    public PaintNode(Node node, float posX, float posY, int color, float radius, float weight, boolean rightDirection) {
        super();
        this.node = node;
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.radius = radius;
        this.weight = weight;
        this.rightDirection = rightDirection;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isRightDirection() {
        return rightDirection;
    }

    public void setRightDirection(boolean rightDirection) {
        this.rightDirection = rightDirection;
    }
}
