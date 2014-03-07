package com.cael6.eh.cael6;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 23/02/14.
 */
public class HeroCard extends Card {

    private Paint paint;
    public ArrayList<Energy> energy;

    private int maxTurns = 2;

    public HeroCard(Context context, AttributeSet set){
        super(context, set);
        init();
    }

    public HeroCard(Context context, HeroCard card, Player owner){
        super(context, card, owner);
        setOwner(owner);
        init();
        this.health = card.health;
    }

    private void init(){
        paint = new Paint();
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);

        turns = maxTurns;
    }

    public void resetActions(){
        turns = maxTurns;
        invalidate();
    }
}
