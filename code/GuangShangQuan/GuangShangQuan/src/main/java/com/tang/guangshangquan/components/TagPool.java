package com.tang.guangshangquan.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TagPool extends ViewGroup {

    private final static int VIEW_MARGIN = 2;

    public TagPool(Context context) {
        super(context);
    }

    public TagPool(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        final int count = getChildCount();
        int row = 0;// which row lay you view relative to parent
        int lengthX = arg1; // right position of child relative to parent
        int lengthY = arg2; // bottom position of child relative to parent
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            lengthX += width + VIEW_MARGIN;
            lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
            //if it can't drawing on a same line , skip to next line
            if (lengthX > arg3) {
                lengthX = width + VIEW_MARGIN + arg1;
                row++;
                lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
            }
            child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            if (view instanceof TagBar) {
                TagBar bar = (TagBar) view;
                if (bar.isSelected())
                    sb.append(bar.toString()).append(';');
            }
        }
        return sb.toString();
    }

    public void removeUnselected() {
        List<TagBar> toRemove = new ArrayList<TagBar>();
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            if (view instanceof TagBar) {
                TagBar bar = (TagBar) view;
                if (!bar.isSelected())
                    toRemove.add(bar);
            }
        }
        for (TagBar bar : toRemove)
            bar.destroy();
    }

    public List<Integer> getUnselectedIds() {
        List<Integer> res = new ArrayList<Integer>();
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            if (view instanceof TagBar) {
                TagBar bar = (TagBar) view;
                if (!bar.isSelected() && bar.getTagId() > 0)
                    res.add(bar.getTagId());
            }
        }
        return res;
    }
}
