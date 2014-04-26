package com.cael6.eh.cael6;

import java.util.ArrayList;

/**
 * Interface for card traits.
 */
public interface ITrait {
    public final static int TRIGGER_KILL = 1;
    public final static int TRIGGER_DRAGON_ENTER_BATTLEFIELD = 2;
    public final static int TRIGGER_START_TURN = 3;
    public final static int TRIGGER_AFTER_RESET = 5;
    public final static int TRIGGER_AFTER_ATTACK = 4;
    public final static int TRIGGER_ATTACKED = 6;
    public final static int TRIGGER_BEFORE_KILLED = 7;
    public final static int TRIGGER_AFTER_KILLED = 8;

    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger);

    public boolean checkTrigger(int trigger);

    public int getLayoutResource();

}
