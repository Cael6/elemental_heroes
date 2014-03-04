package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by cael6 on 12/02/14.
 */
public class Card extends RelativeLayout {

    //<editor-fold desc="Private variables">
    private Paint paint;
    private Drawable cardBackImage;
    private int cardSize;
    private int maxTurns = 1;
    private boolean hidden = false;
    //</editor-fold>

    //<editor-fold desc="Public variables">
    public final static int SMALL_CARD = 0;
    public final static int LARGE_CARD = 1;
    public Drawable image;
    public float mRotation = 0;
    public boolean inHand;
    public String name;
    public int attack;
    public int defense;
    public int health;
    public int turns = 0;
    public boolean endurance = false;
    public boolean battleReady = false;
    public int growth = -1;
    public boolean enrage = false;
    public boolean concentrate = false;
    public int lifetap = -1;
    public int airShield = -1;

    public ArrayList<Energy> energy;
    public ArrayList<Energy> energyGen;
    public boolean movable;
    //</editor-fold>

    public Card(Context context) {
        super(context);
        init(context);
    }

    public Card(Context context, AttributeSet set) {
        super(context, set);
        init(context);
        int style = set.getStyleAttribute();
        if (0 != style) {
            generateFromStyle(context, set, style);
        }
    }

    public Card(Context context, int cardStyle) {
        super(context);
        init(context);
        generateFromStyle(context, null, cardStyle);
    }

    public Card(Context context, AttributeSet set, int defStyleAttr) {
        super(context, set, defStyleAttr);
        init(context);
    }

    public Card(Context context, Card card) {
        super(context);
        init(context);
        this.energy = card.energy;
        this.inHand = card.inHand;
        this.name = card.name;
        this.attack = card.attack;
        this.defense = card.defense;
        this.health = card.health;
        this.mRotation = card.mRotation;
        this.turns = card.turns;

        this.endurance = card.endurance;
        this.battleReady = card.battleReady;
        this.growth = card.growth;
        this.enrage = card.enrage;
        this.concentrate = card.concentrate;
        this.lifetap = card.lifetap;
        this.airShield = card.airShield;
        this.energyGen = card.energyGen;
        this.image = card.image;
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);

        this.inHand = false;
        cardBackImage = getResources().getDrawable(R.drawable.card_back);
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        final int taCount = ta.getIndexCount();
        int fireCost = -1,
                waterCost = -1,
                earthCost = -1,
                windCost = -1,
                colorlessCost = -1,
                fireGen = -1,
                waterGen = -1,
                earthGen = -1,
                windGen = -1,
                colorlessGen = -1;
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
                case R.styleable.Card_name:
                    name = ta.getString(attr);
                    break;
                case R.styleable.Card_fireCost:
                    fireCost = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_waterCost:
                    waterCost = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_earthCost:
                    earthCost = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_windCost:
                    windCost = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_colorlessCost:
                    colorlessCost = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_fireGen:
                    fireGen = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_waterGen:
                    waterGen = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_earthGen:
                    earthGen = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_windGen:
                    windGen = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_colorlessGen:
                    colorlessGen = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_endurance:
                    endurance = ta.getBoolean(attr, false);
                    break;
                case R.styleable.Card_battleReady:
                    battleReady = ta.getBoolean(attr, false);
                    break;
                case R.styleable.Card_enrage:
                    enrage = ta.getBoolean(attr, false);
                    break;
                case R.styleable.Card_concentrate:
                    concentrate = ta.getBoolean(attr, false);
                    break;
                case R.styleable.Card_growth:
                    growth = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_lifetap:
                    lifetap = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_airShield:
                    airShield = ta.getInt(attr, -1);
                    break;
                case R.styleable.Card_image:
                    int srcId = ta.getResourceId(attr, -1);
                    image = getResources().getDrawable(srcId);
                    break;
            }
        }

        energy = generateEnergy(context, fireCost, waterCost, earthCost, windCost, colorlessCost);
        energyGen = generateEnergy(context, fireGen, waterGen, earthGen, windGen, colorlessGen);
    }

    protected ArrayList<Energy> generateEnergy(Context context, int fire, int water, int earth, int wind, int colorless) {
        ArrayList<Energy> energies = new ArrayList<Energy>();
        if (fire >= 0) {
            energies.add(new Energy(context, Energy.FIRE_ID, fire));
        }
        if (water >= 0) {
            energies.add(new Energy(context, Energy.WATER_ID, water));
        }
        if (earth >= 0) {
            energies.add(new Energy(context, Energy.EARTH_ID, earth));
        }
        if (wind >= 0) {
            energies.add(new Energy(context, Energy.WIND_ID, wind));
        }
        if (colorless >= 0) {
            energies.add(new Energy(context, Energy.COLORLESS_ID, colorless));
        }
        return energies;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void resetActions() {
        turns = maxTurns;
    }

    public static boolean checkAttack(Card card, Card targetCard) {
        float attackMultiplier = 1;
        if (!(targetCard instanceof HeroCard) && !(card instanceof HeroCard)) {
            Iterator<Energy> cardIt = card.energy.iterator();
            while (cardIt.hasNext()) {
                Energy cardEnergy = cardIt.next();
                Iterator<Energy> targetIt = targetCard.energy.iterator();
                while (targetIt.hasNext()) {
                    attackMultiplier *= Energy.getAttackMultiplier(cardEnergy, targetIt.next());
                }
            }
        }
        return card.attack * attackMultiplier > targetCard.defense;
    }

    public boolean spendAction() {
        if (turns > 0) {
            turns--;
            this.invalidate();
            return true;
        }
        return false;
    }

    public void enterBattleField(){
        if(battleReady){
            resetActions();
        }
    }

    public void destroy() {
        ((GameActivity)getContext()).setBackground((ViewGroup) getParent(), ((GameActivity) getContext()).normalShape);
        ((ViewGroup)getParent()).removeView(this);
    }

    public void destroyed(Card targetCard) {
        enrage();
    }

    public int getCardSize() {
        return cardSize;
    }

    public void setCardSize(int cardSize) {
        this.cardSize = cardSize;
        generateCardChildren();
        setCardChildrenValues();
    }

    private void setCardChildrenValues() {
        ((GameActivity)getContext()).setBackground(this.findViewWithTag("cardImage"), image);

        int spacer = 0;
        int energyIconSize = 0;
        int colorlessEnergyTextSize = 0;
        switch(cardSize){
            case SMALL_CARD:
                spacer = getResources().getDimensionPixelSize(R.dimen.small_card_spacer);
                energyIconSize = getResources().getDimensionPixelSize(R.dimen.small_card_energy_icon_dim);
                colorlessEnergyTextSize = getResources().getDimensionPixelSize(R.dimen.small_card_colorless_energy_text_size)/2;
                break;
            case LARGE_CARD:
                spacer = getResources().getDimensionPixelSize(R.dimen.large_card_spacer);
                energyIconSize = getResources().getDimensionPixelSize(R.dimen.large_card_energy_icon_dim);
                colorlessEnergyTextSize = getResources().getDimensionPixelSize(R.dimen.large_card_colorless_energy_text_size)/2;
                break;
        }

        LinearLayout costLL = (LinearLayout)this.findViewWithTag("costLL");
        if(0 < costLL.getChildCount()){
            costLL.removeAllViews();
        }
        for(Energy en : energy){
            if(Energy.COLORLESS_ID == en.id){
                FrameLayout colorlessFrame = new FrameLayout(getContext());
                LinearLayout.LayoutParams frameParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                frameParams.bottomMargin = spacer;
                colorlessFrame.setLayoutParams(frameParams);

                ImageView colorlessImage = new ImageView(getContext());
                FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(energyIconSize, energyIconSize);
                colorlessImage.setLayoutParams(imageParams);
                colorlessImage.setImageDrawable(getResources().getDrawable(R.drawable.colorless));

                TextView colorlessText = new TextView(getContext());
                colorlessText.setTextSize(colorlessEnergyTextSize);
                colorlessText.setTextColor(getResources().getColor(R.color.colorless_cost_text_color));
                colorlessText.setIncludeFontPadding(false);
                colorlessText.setTypeface(null, Typeface.BOLD);
                FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                textParams.gravity = Gravity.CENTER;
                colorlessText.setLayoutParams(textParams);
                colorlessText.setText(String.valueOf(en.level));

                colorlessFrame.addView(colorlessImage);
                colorlessFrame.addView(colorlessText);

                costLL.addView(colorlessFrame);
            } else {
                for(int i = 0; i < en.level; i++){
                    ImageView elementalEn = new ImageView(getContext());
                    LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(energyIconSize, energyIconSize);
                    imageParams.bottomMargin = spacer;
                    elementalEn.setLayoutParams(imageParams);
                    elementalEn.setImageDrawable(en.image);

                    costLL.addView(elementalEn);
                }
            }
        }

        TextView attackText = (TextView)this.findViewWithTag("attack");
        attackText.setText(String.valueOf(attack));
        TextView defenseText = (TextView)this.findViewWithTag("defense");
        defenseText.setText(String.valueOf(defense));
        TextView healthText = (TextView)this.findViewWithTag("health");
        healthText.setText(String.valueOf(health));
    }

    private void generateCardChildren() {
        Context context = getContext();

        int layoutResource = 0;
        switch(cardSize){
            case SMALL_CARD:
                layoutResource = R.layout.small_card;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.card;
        }
        RelativeLayout cardLayout = (RelativeLayout)((ViewGroup)inflate(context, layoutResource, null)).findViewWithTag("card");

        switch(cardSize){
            case SMALL_CARD:
                ((GameActivity)getContext()).setBackground(this,getResources().getDrawable(R.drawable.small_card_background));
                break;
            case LARGE_CARD:
                ((GameActivity)getContext()).setBackground(this,getResources().getDrawable(R.drawable.card_background));
        }

        while(0 < cardLayout.getChildCount()){
            View cardChild = cardLayout.getChildAt(0);
            if(cardChild != null){
                ((ViewGroup)cardChild.getParent()).removeView(cardChild);
                this.addView(cardChild);
            }
        }
    }

    private void getCardElement(){
        for(Energy en : energy){

        }
    }

    public void damageCard(int amount){
        this.health -= amount;
        TextView healthTV = (TextView)this.findViewWithTag("health");
        healthTV.setText(String.valueOf(health));
        healthTV.setTextColor(0xFFAA1111);
        healthTV.invalidate();
    }

    private void enrage(){
        if(enrage){
            attack++;
            TextView attackTV = (TextView)this.findViewWithTag("attack");
            attackTV.setText(String.valueOf(attack));
            attackTV.setTextColor(0xFF33AA33);
            attackTV.invalidate();
        }
    }

    public void hideCard(){
        this.hidden = true;
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).setVisibility(INVISIBLE);
        }
        ((GameActivity)getContext()).setBackground(this, getResources().getDrawable(R.drawable.card_back));
    }

    public void showCard(){
        this.hidden = false;
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).setVisibility(VISIBLE);
        }
        int backgroundResourceId = 0;
        switch(cardSize){
            case SMALL_CARD:
                backgroundResourceId = R.drawable.small_card_background;
                break;
            case LARGE_CARD:
                backgroundResourceId = R.drawable.card_background;
                break;
        }
        ((GameActivity)getContext()).setBackground(this, getResources().getDrawable(backgroundResourceId));
    }
}
