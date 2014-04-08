package com.cael6.eh.cael6;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cael6.eh.GameActivity;

/**
 * Created by cael6 on 24/03/14.
 */
public class SpellCard extends Card{

    public int dragonBreathCost;

    public SpellCard(Context context) {
        super(context);
    }

    public SpellCard(Context context, AttributeSet set) {
        super(context, set);
    }

    public SpellCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public SpellCard(Context context, Card card, Player owner) {
        super(context, card, owner);
    }
    @Override
    public void setListeners() {
        setOnTouchListener(new SpellOnTouchListener());
    }

    public class SpellOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }
}
