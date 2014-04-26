package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

/**
 * Created by cael6 on 24/03/14.
 */
public class SpellCard extends Card{

    public int dragonBreathCost;
    public int functionId;
    public String effect;
    public int validTargets;

    public final static int TARGET_CHARACTER = 1;
    public final static int TARGET_EGG = 2;
    public final static int TARGET_HERO = 3;
    public final static int TARGET_DRAGON = 4;

    public final static int EFFECT_POISON_BLAST = 1;
    public final static int EFFECT_FIREBALL = 2;
    public final static int EFFECT_DESTROY_EGG = 3;
    public final static int EFFECT_ROCK_THROW = 4;
    public final static int EFFECT_DELAY_EGG = 5;
    public final static int EFFECT_UNHATCH = 6;
    public final static int EFFECT_EGG_SWEEP = 7;
    public final static int EFFECT_MINOR_HEAL = 8;
    public final static int EFFECT_MAJOR_HEAL = 9;
    public final static int EFFECT_MASS_HEAL = 10;

    public final static int POISON_BLAST_DAMAGE = 1;
    public final static int FIREBALL_DAMAGE = 2;
    public final static int ROCK_THROW_DAMAGE = 4;
    public final static int DELAY_EGG_AMOUNT = 2;
    public final static int MINOR_HEAL_AMOUNT = 3;
    public final static int MASS_HEAL_AMOUNT = 3;
    public final static int MAJOR_HEAL_AMOUNT = 7;

    public SpellCard(Context context) {
        super(context);
    }

    public SpellCard(Context context, AttributeSet set) {
        super(context, set);
    }

    public SpellCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public SpellCard(Context context, SpellCard card, Player owner) {
        super(context, card, owner);
        this.dragonBreathCost = card.dragonBreathCost;
        this.functionId = card.functionId;
        this.effect = card.effect;
        this.validTargets = card.validTargets;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        super.generateFromStyle(context, set, style);
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.SpellCard,
                style,
                0);

        final int taCount;
        if (ta != null) {
            taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.SpellCard_breathCost:
                        dragonBreathCost = ta.getInt(attr, -1);
                        break;
                    case R.styleable.SpellCard_function:
                        functionId = ta.getInt(attr, -1);
                        break;
                    case R.styleable.SpellCard_effect:
                        effect = ta.getString(attr);
                        break;
                    case R.styleable.SpellCard_validTargets:
                        validTargets = ta.getInt(attr, -1);
                        break;
                }
            }

        }
    }

    @Override
    protected void setCardChildrenValues(){
        super.setCardChildrenValues();
        LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
        LinearLayout costLL = (LinearLayout)findViewWithTag("costLL");
        TextView breathCostText = null;
        switch(cardSize){
            case LARGE_CARD:
                breathCostText = (TextView)inflater.inflate(R.layout.breath_cost, costLL, false);
                break;
            case SMALL_CARD:
                breathCostText = (TextView)inflater.inflate(R.layout.small_breath_cost, costLL, false);
                break;
        }

        TextView effectText = (TextView)this.findViewWithTag("effect");
        effectText.setText(effect);
        breathCostText.setText(Integer.toString(dragonBreathCost));
        costLL.addView(breathCostText);
    }

    @Override
    protected void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        switch (cardSize) {
            case SMALL_CARD:
                layoutResource = R.layout.small_spell_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.spell_card;
        }
        Card cardLayout = (Card) inflater.inflate(layoutResource, expectedParent, false);

        this.setLayoutParams(cardLayout.getLayoutParams());
        while (0 < cardLayout.getChildCount()) {
            View cardChild = cardLayout.getChildAt(0);
            if (cardChild != null) {
                ((ViewGroup) cardChild.getParent()).removeView(cardChild);
                this.addView(cardChild);
            }
        }
        setCardChildrenValues();
    }

    @Override
    public void setListeners() {
        setOnTouchListener(new CTouchListener(CTouchListener.TYPE_STATIC));
        final SpellCard thisCard = this;
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity)getContext()).previewCard(thisCard);
            }
        });
    }

    public class SpellOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    }


    public boolean isValidTarget(Card card){
        switch(validTargets){
            case TARGET_CHARACTER:
                return card instanceof CharacterCard;
            case TARGET_DRAGON:
                return card instanceof DragonCard;
            case TARGET_EGG:
                return card instanceof EggCard;
            case TARGET_HERO:
                return card instanceof HeroCard;
            default:
                return false;
        }
    }

    public void executeEffect(Card target){
        switch (functionId){
            case EFFECT_POISON_BLAST:
                poisonBlast((CharacterCard) target);
                break;
            case EFFECT_FIREBALL:
                fireball((CharacterCard) target);
                break;
            case EFFECT_DESTROY_EGG:
                destroyEgg((EggCard) target);
                break;
            case EFFECT_ROCK_THROW:
                rockThrow((CharacterCard) target);
                break;
            case EFFECT_DELAY_EGG:
                delayEgg((EggCard) target);
                break;
            case EFFECT_EGG_SWEEP:
                eggSweep((EggCard) target);
                break;
            case EFFECT_UNHATCH:
                unhatch((DragonCard) target);
                break;
            case EFFECT_MINOR_HEAL:
                minorHeal((CharacterCard) target);
                break;
            case EFFECT_MAJOR_HEAL:
                majorHeal((CharacterCard) target);
                break;
            case EFFECT_MASS_HEAL:
                massHeal((HeroCard) target);
                break;
        }
    }

    /**
     * Heal target hero and all owner's cards.
     * @param target
     */
    private void massHeal(HeroCard target) {
        target.heal(MASS_HEAL_AMOUNT);
        target.regenerateView();
        for(CharacterCard card : target.getOwner().dragonsOnBoard){
            card.heal(MASS_HEAL_AMOUNT);
            card.regenerateView();
        }
    }

    /**
     * Heal target character
     * @param target
     */
    private void majorHeal(CharacterCard target) {
        target.heal(MAJOR_HEAL_AMOUNT);
        target.regenerateView();
    }

    /**
     * Heal target character.
     * @param target
     */
    private void minorHeal(CharacterCard target) {
        target.heal(MINOR_HEAL_AMOUNT);
        target.regenerateView();
    }

    /**
     * return dragon to owner's hand and create a new egg at 0.
     * @param target
     */
    private void unhatch(DragonCard target) {
        Player dragonOwner = target.getOwner();
        ViewGroup boardZone = (ViewGroup)target.getParent();
        dragonOwner.dragonsOnBoard.remove(target);
        dragonOwner.cardsInHand.add(target);
        boardZone.removeView(target);
        dragonOwner.hand.addView(target);
        if(dragonOwner.cardsDefaultHidden){
            target.hideCard();
        }

        LayoutInflater infl = LayoutInflater.from(getContext());
        ViewGroup cards = (ViewGroup)infl.inflate(R.layout.cards, boardZone);

        EggCard egg = null;
        if (cards != null) {
            egg = (EggCard)cards.findViewById(R.id.eggCard);
        }
        ((ViewGroup)egg.getParent()).removeView(egg);

        egg.generateCardForView(boardZone);
        boardZone.addView(egg);
        dragonOwner.eggsOnBoard.add(egg);
    }

    /**
     * return egg to owner's hand.
     * @param target
     */
    private void eggSweep(EggCard target) {
        Player eggOwner = target.getOwner();
        eggOwner.eggsOnBoard.remove(target);
        eggOwner.cardsInHand.add(target);
        ((ViewGroup)target.getParent()).removeView(target);
        eggOwner.hand.addView(target);
        target.setHatchTimer(0);
        if(eggOwner.cardsDefaultHidden){
            target.hideCard();
        }
    }

    /**
     * delay egg by a certain amount
     * @param target
     */
    private void delayEgg(EggCard target) {
        target.subtractHatchTimer(DELAY_EGG_AMOUNT);
        target.regenerateView();
    }

    /**
     * Throw a rock at a target.
     * Deals indirect damage
     * @param target
     */
    private void rockThrow(CharacterCard target) {
        target.attacked(ROCK_THROW_DAMAGE, true);
        target.regenerateView();
    }

    /**
     * Destroy target egg
     * @param target
     */
    private void destroyEgg(EggCard target) {
        target.getOwner().eggsOnBoard.remove(target);
        ((ViewGroup)target.getParent()).removeView(target);
    }

    /**
     * Send a fireball at target.
     * Deals direct damage.
     * @param target
     */
    private void fireball(CharacterCard target) {
        target.damaged(FIREBALL_DAMAGE, true);
        regenerateView();
    }

    /**
     * Send a poison blast at target.
     * Deals direct damage
     * @param target
     */
    private void poisonBlast(CharacterCard target){
        target.damaged(POISON_BLAST_DAMAGE, true);
        regenerateView();
    }
}
