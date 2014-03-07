package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class LifetapTrait extends CardTrait implements ITrait {
    private final int trigger;
    private final int lifetap;

    public LifetapTrait(int lifetap) {
        super(R.drawable.lifetap, R.layout.lifetap);
        this.trigger = ITrait.TRIGGER_START_TURN;
        this.lifetap = lifetap;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        card.health += lifetap;
        card.getOwner().deck.hero.health += lifetap;
        return false;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
