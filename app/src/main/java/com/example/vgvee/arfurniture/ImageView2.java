package com.example.vgvee.arfurniture;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageView2 extends android.support.v7.widget.AppCompatImageView {
    public ImageView2(Context context) {
        super(context);
        setPadding(5,0,0,0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredHeight();
        setMeasuredDimension(width, width);
        setScaleType(ScaleType.CENTER_CROP);
    }
}
