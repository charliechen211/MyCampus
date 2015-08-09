package com.stpi.campus.activity.otherService;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import com.stpi.campus.entity.PaintNode;
import com.stpi.campus.entity.Node;
import com.stpi.campus.util.Constants;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cyc on 14-2-24.
 */
public class SearchStartActivity extends PApplet {
    private final PVector ref = new PVector();
    private final PVector trans = new PVector();
    private RelativeLayout surfaceLayout;
    private List<PaintNode> nodes = new ArrayList<PaintNode>();
    private boolean inited = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.stpi.campus.R.layout.activity_searchstart);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (inited == false) {
            initTypeNodes();
            inited = true;
        }
    }

    private void initTypeNodes() {
        List<Node> valueNodes = new ArrayList<Node>();

        Node type1 = new Node("美食", "1", 1.0f, 0);
        valueNodes.add(type1);
        Node type2 = new Node("购物", "2", 1.0f, 0);
        valueNodes.add(type2);
        Node type3 = new Node("娱乐", "3", 1.0f, 0);
        valueNodes.add(type3);
        Node type4 = new Node("ATM", "4", 1.0f, 0);
        valueNodes.add(type4);
        Node type5 = new Node("其他", "5", 1.0f, 0);
        valueNodes.add(type5);


        if (valueNodes.size() > 0) {
            PaintNode node;
            for (int i = 0; i < valueNodes.size(); i++) {
                node = new PaintNode(valueNodes.get(i),
                        (float) (((Math.random() - 0.5) * width * 0.8 + width / 2)),
                        (float) (((Math.random() - 0.5) * height * 0.8 + height / 2)),
                        (int) (Math.random() * Color.BLACK),
                        (float) (Math.random() * 80 + 40), (float) (Math.random() * 3 + 3),
                        Math.random() > 0.5f ? true : false);
                nodes.add(node);
            }
        }
    }

    private void init() {
        surfaceLayout = (RelativeLayout) findViewById(com.stpi.campus.R.id.layout_surface_recommend);
        surfaceLayout.addView(surfaceView, sketchWidth(), sketchHeight());
        width = sketchWidth();
        height = sketchHeight();
        Log.i("width", String.valueOf(width));
    }

    @Override
    public void setup() {
        super.setup();
        background(Color.WHITE);
        smooth();
        loop();
        ellipseMode(RADIUS);
        textAlign(CENTER, CENTER);
    }

    @Override
    public void draw() {
        background(Color.WHITE);
        drawContents();
    }

    private void drawContents() {

        for (int i = 0; i < nodes.size(); i++) {
            // draw nodes
            fill(nodes.get(i).getColor());
            noStroke();
            ellipse(nodes.get(i).getPosX(), nodes.get(i).getPosY(), nodes
                    .get(i).getRadius(), nodes.get(i).getRadius());
            // Log.i("node", i+ "X: " + nodes.get(i).getPosX() + " Y: " +
            // nodes.get(i).getPosY());

            // draw title
            fill(Color.WHITE);
            textSize(nodes.get(i).getRadius() / 60 * 20);
            text(nodes.get(i).getNode().getName(), nodes.get(i).getPosX()
                            - nodes.get(i).getRadius() / sqrt(2) + 2, nodes.get(i)
                            .getPosY() - nodes.get(i).getRadius() / sqrt(2) + 2,
                    sqrt(2) * nodes.get(i).getRadius() - 4, sqrt(2)
                            * nodes.get(i).getRadius() - 4
            );

        }
    }

    @Override
    public void mouseClicked() {
        Log.i("Clicked", "X: " + mouseX + " Y: " + mouseY);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (isInCircle(mouseX, mouseY, nodes.get(i).getPosX(), nodes.get(i)
                    .getPosY(), nodes.get(i).getRadius())) {
                Log.i("Click Node", String.valueOf(i));

                Constants.type = nodes.get(i).getNode().getId();
                Intent intent = new Intent(this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("loadSearch", true);
                bundle.putString("type", "one");
                bundle.putString("keyword", nodes.get(i).getNode().getName());
                bundle.putString("recommented", "0");
                bundle.putString("id", "");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void mouseDragged() {
        //Log.i("Dragged", "X: " + mouseX + " Y: " + mouseY);
        trans.set(mouseX, mouseY, 0);
        trans.sub(ref);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (isInCircle(mouseX, mouseY, nodes.get(i).getPosX(),
                    nodes.get(i).getPosY(), nodes.get(i).getRadius())) {
                //	Log.i("Drag Node", String.valueOf(i));
                PVector temp = new PVector(nodes.get(i).getPosX(), nodes.get(i).getPosY());
                temp.add(trans);
                nodes.get(i).setPosX(temp.x);
                nodes.get(i).setPosY(temp.y);
                break;
            }
        }
        ref.set(mouseX, mouseY, 0);
        redraw();
    }

    @Override
    public void mousePressed() {
        //Log.i("Pressed", "X: " + mouseX + " Y: " + mouseY);
        ref.set(mouseX, mouseY, 0);
        redraw();
    }

    @Override
    public void mouseReleased() {
        //Log.i("Released", "X: " + mouseX + " Y: " + mouseY);
        redraw();
    }

    private boolean isInCircle(float x, float y, float centerX, float centerY,
                               float radius) {
        if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) < radius
                * radius) {
            return true;
        }
        return false;
    }

}