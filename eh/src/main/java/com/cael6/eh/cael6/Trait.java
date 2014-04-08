package com.cael6.eh.cael6;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Created by cael6 on 04/03/14.
 */
public class Trait {
    private final int imageResource;
    private final int layoutResource;

    public Trait(int imageResource, int layoutResource){
        this.imageResource = imageResource;
        this.layoutResource = layoutResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getLayoutResource() { return layoutResource;}

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class LifetapTrait extends Trait implements ITrait {
        private final int trigger;
        private final int lifetap;

        public LifetapTrait(int lifetap) {
            super(R.drawable.lifetap, R.layout.lifetap);
            this.trigger = ITrait.TRIGGER_START_TURN;
            this.lifetap = lifetap;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            DragonCard dragon = (DragonCard) card;
            dragon.health += lifetap;
            dragon.getOwner().enemy.deck.hero.health -= lifetap;
            return false;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class AirShieldTrait extends Trait implements ITrait {
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

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class BattleReadyTrait extends Trait implements ITrait {

        private final int trigger;

        public BattleReadyTrait(){
            super(R.drawable.battle_ready, R.layout.battle_ready);
            this.trigger = ITrait.TRIGGER_ENTER_BATTLEFIELD;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            CharacterCard character = (CharacterCard) card;
            character.resetActions();
            return false;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    /**
     * Trait for concentrait
     */
    public static class ConcentrateTrait extends Trait implements ITrait {
        private final int trigger;

        public ConcentrateTrait(){
            super(R.drawable.concentrate, R.layout.concentrate);
            this.trigger = ITrait.TRIGGER_AFTER_RESET;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            Player player = card.getOwner();
            player.drawCard();
            player.deck.hero.turns++;
            return true;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class DrawDragonBreathTrait extends Trait implements ITrait{
        int trigger;
        int dragonBreathDrawing;
        public DrawDragonBreathTrait(int dragonBreathDrawing){
            super(R.drawable.fire, R.layout.draw_dragon_breath_trait);
            this.dragonBreathDrawing = dragonBreathDrawing;
            this.trigger = ITrait.TRIGGER_START_TURN;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            card.getOwner().drawDragonBreath(dragonBreathDrawing);
            return true;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class EnduranceTrait extends Trait implements ITrait{

        private final int trigger;

        public EnduranceTrait(){
            super(R.drawable.endurance, R.layout.endurance);
            this.trigger = ITrait.TRIGGER_AFTER_ATTACK;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            //TODO: make card defender
            return false;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    public static class EnrageTrait extends Trait implements ITrait{

        private int trigger;

        public EnrageTrait(){
            super(R.drawable.enrage, R.layout.enrage);
            trigger = ITrait.TRIGGER_KILL;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger){
            DragonCard dragon = (DragonCard) card;
            dragon.attack++;
            TextView attackTV = (TextView)card.findViewWithTag("attack");
            attackTV.setText(String.valueOf(dragon.attack));
            attackTV.setTextColor(0xFF22CC22);
            return true;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

    /**
     * Created by cael6 on 04/03/14.
     */
    public static class GrowthTrait extends Trait implements ITrait {
        private final int maxGrowth;
        private final int trigger;
        private int growthCount = 0;

        public GrowthTrait(int growth) {
            super(R.drawable.growth, R.layout.growth);
            this.trigger = ITrait.TRIGGER_START_TURN;
            this.maxGrowth = growth;
        }

        @Override
        public boolean traitEffect(Card card, ArrayList<Card> targets, int trigger) {
            if(growthCount < maxGrowth){
                DragonCard dragon = (DragonCard) card;
                dragon.health++;
                dragon.defense++;
                growthCount++;
                return true;
            }
            return false;
        }

        @Override
        public boolean checkTrigger(int trigger) {
            return this.trigger == trigger;
        }
    }

}
