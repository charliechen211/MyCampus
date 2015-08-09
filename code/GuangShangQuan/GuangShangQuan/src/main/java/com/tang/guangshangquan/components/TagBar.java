package com.tang.guangshangquan.components;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tang.guangshangquan.R;

public class TagBar extends LinearLayout {
    private TagPool parent = null;
    private TextView textView = null;
    private boolean selected = false;
    private int tagId = -1;

    public TagBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tag_bar_layout, this);
        textView = (TextView) this.findViewById(R.id.tag_content);
        this.setClickable(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selected = !selected;
                changeWithState();
            }
        });
    }

    public void setParent(TagPool parent) {
        this.parent = parent;
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setSelected(boolean val) {
        selected = val;
        changeWithState();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getTagId() {
        return tagId;
    }

    @Override
    public String toString() {
        return textView.getText().toString();
    }

    public void destroy() {
        if (parent == null)
            return;
        parent.removeView(this);
    }

    // private functions
    private void changeWithState() {
        if (selected)
            textView.setBackgroundColor(this.getResources().getColor(R.color.tsuyukusa));
        else
            textView.setBackgroundColor(this.getResources().getColor(R.color.white));
    }


}
