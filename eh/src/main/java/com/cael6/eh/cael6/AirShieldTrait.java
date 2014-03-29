package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class AirShieldTrait extends CardTrait implements ITrait {
    private final int trigger;
    private int airShield;
    private boolean shieldConsumed = false;

    public AirShieldTrait(int airShield) {
        super(R.drawable.air_shield, R.layout.air_shield);
        this.trigger = ITrait.TRIGGER_ATTACKED;
        this.airShield = airShield;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        CharacterCard charCard = (CharacterCard)card;
        if(shieldConsumed && charCard.health <= airShield){
            charCard.health = airShield;
            shieldConsumed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
