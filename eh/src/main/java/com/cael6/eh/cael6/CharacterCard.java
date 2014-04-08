package com.cael6.eh.cael6;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import java.util.ArrayList;

/**
 * Class that represents a card that can attack, defend, has health, can do things.
 */
public class CharacterCard extends Card {
    public int attack;
    public int defense;
    public int health;
    public int turns = 0;
    public ArrayList<ITrait> traits;
    protected int maxTurns = 1;

    public CharacterCard(Context context) {
        super(context);
    }

    public CharacterCard(Context context, AttributeSet set) {
        super(context, set);
        init();
        int style = set.getStyleAttribute();
        if (0 != style) {
            generateFromStyle(context, set, style);
        }
    }

    public CharacterCard(Context context, int cardStyle) {
        super(context, cardStyle);
        init();
    }

    public CharacterCard(Context context, CharacterCard card, Player owner) {
        super(context, card, owner);
        this.attack = card.attack;
        this.defense = card.defense;
        this.health = card.health;
        this.turns = card.turns;
        this.traits = card.traits;
    }

    public static boolean checkAttack(CharacterCard card, CharacterCard targetCard) {
        return card.attack > targetCard.defense;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        super.generateFromStyle(context, set, style);
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        traits = new ArrayList<ITrait>();

        final int taCount;
        if (ta != null) {
            taCount = ta.getIndexCount();
            for (int i = 0; i < taCount; i++) {
                int attr = ta.getIndex(i);
                switch (attr) {
                    case R.styleable.Card_attack:
                        attack = ta.getInt(attr, -1);
                        break;
                    case R.styleable.Card_defense:
                        defense = ta.getInt(attr, -1);
                        break;
                    case R.styleable.Card_health:
                        health = ta.getInt(attr, -1);
                        break;
                    case R.styleable.Card_endurance:
                        boolean endurance = ta.getBoolean(attr, false);
                        if (endurance) {
                            traits.add(new Trait.EnduranceTrait());
                        }
                        break;
                    case R.styleable.Card_battleReady:
                        boolean battleReady = ta.getBoolean(attr, false);
                        if (battleReady) {
                            traits.add(new Trait.BattleReadyTrait());
                        }
                        break;
                    case R.styleable.Card_enrage:
                        boolean enrage = ta.getBoolean(attr, false);
                        if (enrage) {
                            traits.add(new Trait.EnrageTrait());
                        }
                        break;
                    case R.styleable.Card_concentrate:
                        boolean concentrate = ta.getBoolean(attr, false);
                        if (concentrate) {
                            traits.add(new Trait.ConcentrateTrait());
                        }
                        break;
                    case R.styleable.Card_growth:
                        int growth = ta.getInt(attr, -1);
                        if (growth > 0) {
                            traits.add(new Trait.GrowthTrait(growth));
                        }
                        break;
                    case R.styleable.Card_lifetap:
                        int lifetap = ta.getInt(attr, -1);
                        if (lifetap > 0) {
                            traits.add(new Trait.LifetapTrait(lifetap));
                        }
                        break;
                    case R.styleable.Card_airShield:
                        int airShield = ta.getInt(attr, -1);
                        if (airShield > 0) {
                            traits.add(new Trait.AirShieldTrait(airShield));
                        }
                        break;
                }
            }

        }
    }

    public void resetActions() {
        turns = maxTurns;
    }

    public void damageCard(int amount) {
        this.health -= amount;
        TextView healthTV = (TextView) this.findViewWithTag("health");
        if (healthTV != null) {
            healthTV.setText(String.valueOf(health));
            healthTV.setTextColor(0xFFAA1111);
            healthTV.invalidate();
        }
    }

    public void enterBattleField() {
        loopTraits(null, ITrait.TRIGGER_ENTER_BATTLEFIELD);
    }

    protected boolean loopTraits(ArrayList<Card> targets, int trigger) {
        for (ITrait trait : traits) {
            if (trait.checkTrigger(trigger)) {
                return trait.traitEffect(this, targets, ITrait.TRIGGER_KILL);
            }
        }
        return false;
    }

    public boolean spendAction() {
        if (turns > 0) {
            turns--;
            this.invalidate();
            return true;
        }
        return false;
    }

    /**
     * This card has killed the targetCard
     *
     * @param targetCard The card that was killed
     */
    public void kill(Card targetCard) {
        ArrayList<Card> targets = new ArrayList<Card>();
        targets.add(targetCard);
        loopTraits(targets, ITrait.TRIGGER_KILL);
    }

    protected void setCardChildrenValues() {
        super.setCardChildrenValues();

        TextView defenseText = (TextView) this.findViewWithTag("defense");
        if (defenseText != null) {
            defenseText.setText(String.valueOf(defense));
        }
        TextView healthText = (TextView) this.findViewWithTag("health");
        if (healthText != null) {
            healthText.setText(String.valueOf(health));
        }
    }

    @Override
    public void setListeners() {
        if(inHand) {
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assert view.getContext() != null;
                    ((GameActivity) view.getContext()).previewCard((Card) view);
                }
            });
        } else {
            setOnDragListener(new CharacterDragListener());
        }
    }

    @Override
    public boolean checkResources(Object extraResources) {
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class CharacterDragListener implements View.OnDragListener {

        private Drawable defaultBackground;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if(event.getLocalState() instanceof EggCard){
                return true;
            }
            CharacterCard card = (CharacterCard)event.getLocalState();
            GameActivity context = (GameActivity)v.getContext();
            int action = event.getAction();
            CharacterCard targetCard = (CharacterCard) v;
            if(!card.movable && !card.equals(targetCard)){
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        defaultBackground = v.getBackground();
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //set background target shape
                        //context.setBackground(v, context.creatureZoneEnterShape);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //set background back to default background
                        context.setBackground(v, defaultBackground);
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        if(card.turns > 0){
                            context.attackCard(card, targetCard);
                        }
                        context.setBackground(v, null);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        //set background back to default background
                        context.setBackground(v, defaultBackground);
                    default:
                        break;
                }
                return true;
            }else{
                return true;
            }
        }
    }
}
