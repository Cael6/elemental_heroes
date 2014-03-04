package com.cael6.eh.cael6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.cael6.eh.R;

/**
 * Created by cael6 on 15/02/14.
 */
public class Energy {
    public static final int FIRE_ID = 0;
    public static final int WATER_ID = 1;
    public static final int WIND_ID = 2;
    public static final int EARTH_ID = 3;
    public static final int COLORLESS_ID = 4;

    public String name;
    public int level;
    public int id;
    public Drawable image;

    public Energy(Context context, int id, int level){
        this.id = id;
        this.name = getNameById(id);
        this.level = level;
        this.image = getImageById(context, id);
    }

    private Drawable getImageById(Context context, int id) {
        switch(id){
            case FIRE_ID:
                return context.getResources().getDrawable(R.drawable.fire);
            case WATER_ID:
                return  context.getResources().getDrawable(R.drawable.water);
            case EARTH_ID:
                return  context.getResources().getDrawable(R.drawable.earth);
            case WIND_ID:
                return  context.getResources().getDrawable(R.drawable.wind);
            case COLORLESS_ID:
                return context.getResources().getDrawable(R.drawable.colorless);
        }
        return null;
    }

    public String getNameById(int id){
        switch(id){
            case FIRE_ID:
                return "Fire";
            case WATER_ID:
                return "Water";
            case EARTH_ID:
                return "Earth";
            case WIND_ID:
                return "Wind";
            default:
                return "Colorless";
        }
    }

    /**
     * Fire > Earth > Wind > Water > Fire
     * @param e1
     * @param e2
     * @return
     */
    public static float getAttackMultiplier(Energy e1, Energy e2){
        float multiplier = 1;
        switch(e1.id){
            case FIRE_ID:
                switch(e2.id){
                    case WATER_ID:
                        multiplier *= 1/2;
                        break;
                    case EARTH_ID:
                        multiplier *= 2;
                        break;
                }
                break;
            case WATER_ID:
                switch(e2.id){
                    case WIND_ID:
                        multiplier *= 1/2;
                        break;
                    case FIRE_ID:
                        multiplier *= 2;
                        break;
                }
                break;
            case EARTH_ID:
                switch(e2.id){
                    case FIRE_ID:
                        multiplier *= 1/2;
                        break;
                    case WIND_ID:
                        multiplier *= 2;
                        break;
                }
                break;
            case WIND_ID:
                switch(e2.id){
                    case EARTH_ID:
                        multiplier *= 1/2;
                        break;
                    case WATER_ID:
                        multiplier *= 2;
                        break;
                }
                break;
        }
        return multiplier;
    }
}
