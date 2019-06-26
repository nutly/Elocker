package com.feiyang.elocker.util;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

public class ImaginViewHelper {
    public static void setImaginViewColor(ImageView view, int colorResId) {
        Drawable modeDrawable = view.getDrawable().mutate();
        Drawable temp = DrawableCompat.wrap(modeDrawable);
        ColorStateList colorStateList = ColorStateList.valueOf(view.getResources().getColor(colorResId, null));
        DrawableCompat.setTintList(temp, colorStateList);
        view.setImageDrawable(temp);
    }
}
