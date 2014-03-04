package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by cael6 on 16/02/14.
 */
public class CreatureCard extends Card{

    private int attack;
    private int defense;


    public CreatureCard(Context context, AttributeSet set){
        super(context, set);
        init();
    }

    public CreatureCard(Context context, int cardStyle){
        super(context, cardStyle);
        init();
    }

    public void init(){

    }


}
