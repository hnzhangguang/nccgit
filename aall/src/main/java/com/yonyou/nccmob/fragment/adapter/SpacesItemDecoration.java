package com.yonyou.nccmob.fragment.adapter;

import android.graphics.Rect;
import android.view.View;

import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private HashMap<String, Integer> spaceValue;

    public static final String TOP_SPACE = "top_space";
    public static final String BOTTOM_SPACE = "bottom_space";
    public static final String LEFT_SPACE = "left_space";
    public static final String RIGHT_SPACE = "right_space";

    public SpacesItemDecoration(HashMap spaceValue) {
        this.spaceValue = spaceValue;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (spaceValue.get(LEFT_SPACE) != null)
            outRect.left = spaceValue.get(LEFT_SPACE);
        if (spaceValue.get(RIGHT_SPACE) != null)
            outRect.right = spaceValue.get(RIGHT_SPACE);
        if (spaceValue.get(BOTTOM_SPACE) != null)
            outRect.bottom = spaceValue.get(BOTTOM_SPACE);
        if (spaceValue.get(TOP_SPACE) != null)
            outRect.top = spaceValue.get(TOP_SPACE);
    }
}