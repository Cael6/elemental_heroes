package com.cael6.eh.cael6;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public interface ITrait {
    public final static int TRIGGER_KILL = 1;
    public final static int TRIGGER_ENTER_BATTLEFIELD = 2;
    public final static int TRIGGER_START_TURN = 3;
    public final static int TRIGGER_AFTER_RESET = 5;
    public final static int TRIGGER_AFTER_ATTACK = 4;
    public final static int TRIGGER_ATTACKED = 6;
    public final static int TRIGGER_BEFORE_KILLED = 7;
    public final static int TRIGGER_AFTER_KILLED = 8;

    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger);

    public boolean checkTrigger(int trigger);

    public int getImageResource();
    public int getLayoutResource();

}
