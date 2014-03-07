package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class BattleReadyTrait extends CardTrait implements ITrait {

    private final int trigger;

    public BattleReadyTrait(){
        super(R.drawable.battle_ready, R.layout.battle_ready);
        this.trigger = ITrait.TRIGGER_ENTER_BATTLEFIELD;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        card.resetActions();
        return false;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
