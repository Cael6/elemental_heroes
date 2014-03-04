package com.cael6.eh.cael6;

import android.graphics.Color;

/**
 * Created by cael6 on 16/02/14.
 */
public class ColorMod extends Color{

    /**
     * Modifies the saturation of color by the amnt. Saturation is on a scale from 0 to 1.
     * If the saturation is increased above 1 it is set to 1. If it is decreased below 0 it is
     * set to 0.
     * @param color the color to be modified.
     * @param amnt The amount the saturation will be modified. + for increase. - for decrease.
     * @return
     */
    public static int modifySaturation(int color, float amnt){
        float[] hsv = new float[3];
        colorToHSV(color, hsv);
        //Get value of the modified brightness between 0 and 1
        hsv[1] = Math.max(0, Math.min(hsv[1] + amnt, 1));
        color = HSVToColor(hsv);

        return color;
    }

    /**
     * Modifies the brightness of color by the amnt. Brightness is on a scale from 0 to 1.
     * If the brightness is increased above 1 it is set to 1. If it is decreased below 0 it is
     * set to 0.
     * @param color the color to be modified.
     * @param amnt the amount the brightness will be modified. + for increase. - for decrease.
     * @return
     */
    public static int modifyBrightness(int color, float amnt){
        float[] hsv = new float[3];
        colorToHSV(color, hsv);
        //Get value of the modified brightness between 0 and 1
        hsv[2] = Math.max(0, Math.min(hsv[2] + amnt, 1));
        color = HSVToColor(hsv);
        return color;
    }
}
