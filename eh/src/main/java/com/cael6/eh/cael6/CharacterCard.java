package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

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
    }

    public CharacterCard(Context context, int cardStyle) {
        super(context, cardStyle);
    }

    public CharacterCard(Context context, Card card, Player owner) {
        super(context, card, owner);
    }

    public static boolean checkAttack(CharacterCard card, CharacterCard targetCard) {
        return card.attack > targetCard.defense;
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
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
                            traits.add(new EnduranceTrait());
                        }
                        break;
                    case R.styleable.Card_battleReady:
                        boolean battleReady = ta.getBoolean(attr, false);
                        if (battleReady) {
                            traits.add(new BattleReadyTrait());
                        }
                        break;
                    case R.styleable.Card_enrage:
                        boolean enrage = ta.getBoolean(attr, false);
                        if (enrage) {
                            traits.add(new EnrageTrait());
                        }
                        break;
                    case R.styleable.Card_concentrate:
                        boolean concentrate = ta.getBoolean(attr, false);
                        if (concentrate) {
                            traits.add(new ConcentrateTrait());
                        }
                        break;
                    case R.styleable.Card_growth:
                        int growth = ta.getInt(attr, -1);
                        if (growth > 0) {
                            traits.add(new GrowthTrait(growth));
                        }
                        break;
                    case R.styleable.Card_lifetap:
                        int lifetap = ta.getInt(attr, -1);
                        if (lifetap > 0) {
                            traits.add(new LifetapTrait(lifetap));
                        }
                        break;
                    case R.styleable.Card_airShield:
                        int airShield = ta.getInt(attr, -1);
                        if (airShield > 0) {
                            traits.add(new AirShieldTrait(airShield));
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
}
