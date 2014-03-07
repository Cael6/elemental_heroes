package com.cael6.eh.cael6;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class ConcentrateTrait extends CardTrait implements ITrait {
    private final int trigger;

    public ConcentrateTrait(){
        super(R.drawable.concentrate, R.layout.concentrate);
        this.trigger = ITrait.TRIGGER_AFTER_RESET;
    }

    @Override
    public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
        Player player = card.getOwner();
        player.drawCard(card.getContext());
        player.deck.hero.turns++;
        return true;
    }

    @Override
    public boolean checkTrigger(int trigger) {
        return this.trigger == trigger;
    }
}
