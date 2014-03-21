package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
public class Card extends FrameLayout {

    //<editor-fold desc="Private variables">
    private int maxTurns = 1;
    private boolean hidden = false;
    private Player owner;
    private int cardSize;
    //</editor-fold>

    //<editor-fold desc="Public variables">
    public final static int SMALL_CARD = 0;
    public final static int LARGE_CARD = 1;
    public Drawable image;
    public float mRotation = 0;
    public boolean inHand;
    public int attack;
    public int defense;
    public int health;
    public int turns = 0;
    public ArrayList<ITrait> traits;

    public ArrayList<Energy> energy;
    public ArrayList<Energy> energyGen;
    public boolean movable;
    //</editor-fold>


    Paint mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float mShadowDepth;
    Bitmap mShadowBitmap;
    static final int BLUR_RADIUS = 4;
    static final RectF sShadowRectF = new RectF(0, 0, 200, 200);
    static final Rect sShadowRect = new Rect(0, 0, 200 + 2 * BLUR_RADIUS, 200 + 2 * BLUR_RADIUS);
    static RectF tempShadowRectF = new RectF(0, 0, 0, 0);

    public Card(Context context) {
        super(context);
        init();
    }

    public Card(Context context, AttributeSet set) {
        super(context, set);
        init();
        int style = set.getStyleAttribute();
        if (0 != style) {
            generateFromStyle(context, set, style);
        }
    }

    public Card(Context context, int cardStyle) {
        super(context);
        init();
        generateFromStyle(context, null, cardStyle);
    }

    public Card(Context context, Card card, Player owner) {
        super(context);
        setOwner(owner);
        init();
        this.energy = card.energy;
        this.inHand = card.inHand;
        this.attack = card.attack;
        this.defense = card.defense;
        this.health = card.health;
        this.mRotation = card.mRotation;
        this.turns = card.turns;

        this.traits = card.traits;

        this.energyGen = card.energyGen;
        this.image = card.image;
    }

    private void init() {
        mShadowPaint.setColor(0x80fcfc33);
        mShadowPaint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false);
        mShadowBitmap = Bitmap.createBitmap(sShadowRect.width(),
                sShadowRect.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mShadowBitmap);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));
        c.translate(BLUR_RADIUS, BLUR_RADIUS);
        c.drawRoundRect(sShadowRectF, sShadowRectF.width() / 40,
                sShadowRectF.height() / 40, mShadowPaint);

        this.inHand = false;
    }

    /**
     * The "depth" factor determines the offset distance and opacity of the shadow (shadows that
     * are further away from the source are offset greater and are more translucent).
     * @param depth
     */
    public void setShadowDepth(float depth) {
        if (depth != mShadowDepth) {
            mShadowDepth = depth;
            mShadowPaint.setAlpha((int) (100 + 150 * (1 - mShadowDepth)));
            invalidate(); // We need to redraw when the shadow attributes change
        }
    }

    protected void generateFromStyle(Context context, AttributeSet set, int style) {
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.Card,
                style,
                0);

        traits = new ArrayList<ITrait>();
        energy = new ArrayList<Energy>();
        energyGen = new ArrayList<Energy>();

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
                case R.styleable.Card_fireCost:
                    fireCost = ta.getInt(attr, -1);
                    if(fireCost > 0){
                        Energy fireEnergy = new Energy(context, Energy.FIRE_ID, fireCost);
                        energy.add(fireEnergy);
                    }
                    break;
                case R.styleable.Card_waterCost:
                    waterCost = ta.getInt(attr, -1);
                    if(waterCost > 0){
                        Energy waterEnergy = new Energy(context, Energy.WATER_ID, waterCost);
                        energy.add(waterEnergy);
                    }
                    break;
                case R.styleable.Card_earthCost:
                    earthCost = ta.getInt(attr, -1);
                    if(earthCost > 0){
                        Energy earthEnergy = new Energy(context, Energy.EARTH_ID, earthCost);
                        energy.add(earthEnergy);
                    }
                    break;
                case R.styleable.Card_windCost:
                    windCost = ta.getInt(attr, -1);
                    if(windCost > 0){
                        Energy windEnergy = new Energy(context, Energy.WIND_ID, windCost);
                        energy.add(windEnergy);
                    }
                    break;
                case R.styleable.Card_colorlessCost:
                    colorlessCost = ta.getInt(attr, -1);
                    if(colorlessCost > 0){
                        Energy colorlessEnergy = new Energy(context, Energy.COLORLESS_ID, colorlessCost);
                        energy.add(colorlessEnergy);
                    }
                    break;
                case R.styleable.Card_fireGen:
                    fireGen = ta.getInt(attr, -1);
                    if(fireGen > 0){
                        Energy fireEnergy = new Energy(context, Energy.FIRE_ID, fireGen);
                        energyGen.add(fireEnergy);
                        GenerateTrait generateFireTrait = new GenerateTrait(fireEnergy);
                    }
                    break;
                case R.styleable.Card_waterGen:
                    waterGen = ta.getInt(attr, -1);
                    if(waterGen > 0){
                        Energy waterEnergy = new Energy(context, Energy.FIRE_ID, waterGen);
                        energyGen.add(waterEnergy);
                        GenerateTrait generateFireTrait = new GenerateTrait(waterEnergy);
                    }
                    break;
                case R.styleable.Card_earthGen:
                    earthGen = ta.getInt(attr, -1);
                    if(earthGen > 0){
                        Energy earthEnergy = new Energy(context, Energy.WATER_ID, earthGen);
                        energyGen.add(earthEnergy);
                        GenerateTrait generateFireTrait = new GenerateTrait(earthEnergy);
                    }
                    break;
                case R.styleable.Card_windGen:
                    windGen = ta.getInt(attr, -1);
                    if(windGen > 0){
                        Energy windEnergy = new Energy(context, Energy.WIND_ID, windGen);
                        energyGen.add(windEnergy);
                        GenerateTrait generateFireTrait = new GenerateTrait(windEnergy);
                    }
                    break;
                case R.styleable.Card_colorlessGen:
                    colorlessGen = ta.getInt(attr, -1);
                    if(colorlessGen > 0){
                        Energy colorlessEnergy = new Energy(context, Energy.COLORLESS_ID, colorlessGen);
                        energyGen.add(colorlessEnergy);
                        GenerateTrait generateFireTrait = new GenerateTrait(colorlessEnergy);
                    }
                    break;
                case R.styleable.Card_endurance:
                    boolean endurance = ta.getBoolean(attr, false);
                    if(endurance){
                        traits.add(new EnduranceTrait());
                    }
                    break;
                case R.styleable.Card_battleReady:
                    boolean battleReady = ta.getBoolean(attr, false);
                    if(battleReady){
                        traits.add(new BattleReadyTrait());
                    }
                    break;
                case R.styleable.Card_enrage:
                    boolean enrage = ta.getBoolean(attr, false);
                    if(enrage){
                        traits.add(new EnrageTrait());
                    }
                    break;
                case R.styleable.Card_concentrate:
                    boolean concentrate = ta.getBoolean(attr, false);
                    if(concentrate){
                        traits.add(new ConcentrateTrait());
                    }
                    break;
                case R.styleable.Card_growth:
                    int growth = ta.getInt(attr, -1);
                    if(growth > 0){
                        traits.add(new GrowthTrait(growth));
                    }
                    break;
                case R.styleable.Card_lifetap:
                    int lifetap = ta.getInt(attr, -1);
                    if(lifetap > 0){
                        traits.add(new LifetapTrait(lifetap));
                    }
                    break;
                case R.styleable.Card_airShield:
                    int airShield = ta.getInt(attr, -1);
                    if(airShield > 0){
                        traits.add(new AirShieldTrait(airShield));
                    }
                    break;
                case R.styleable.Card_image:
                    int srcId = ta.getResourceId(attr, -1);
                    image = getResources().getDrawable(srcId);
                    break;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(turns > 0){
            int depthFactor = (int) (80 * mShadowDepth);
            canvas.save();
            canvas.translate(this.getLeft() + depthFactor,
                    this.getTop() + depthFactor);
            canvas.concat(this.getMatrix());
            tempShadowRectF.right = this.getWidth();
            tempShadowRectF.bottom = this.getHeight();
            canvas.drawBitmap(mShadowBitmap, sShadowRect, tempShadowRectF, mShadowPaint);
            canvas.restore();
        }
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
        loopTraits(null, ITrait.TRIGGER_ENTER_BATTLEFIELD);
    }

    /**
     * this card has been killed;
     */
    public void killed() {
        loopTraits(null, ITrait.TRIGGER_BEFORE_KILLED);
        ((GameActivity)getContext()).setBackground((ViewGroup) getParent(), ((GameActivity) getContext()).creatureZoneNormalShape);
        ((ViewGroup)getParent()).removeView(this);
        loopTraits(null, ITrait.TRIGGER_AFTER_KILLED);
    }

    /**
     * This card has killed the targetCard
     * @param targetCard
     */
    public void kill(Card targetCard) {
        ArrayList<Card> targets = new ArrayList<Card>();
        targets.add(targetCard);
        loopTraits(targets, ITrait.TRIGGER_KILL);
    }

    private void setCardChildrenValues() {
        ((ImageView)this.findViewWithTag("cardImage")).setImageDrawable(image);

        int spacer = 0;
        int energyIconSize = 0;
        int colorlessEnergyTextSize = 0;
        int energyLayout = 0;
        LinearLayout traitLL = (LinearLayout)this.findViewWithTag("traitLL");
        switch(cardSize){
            case SMALL_CARD:
                spacer = getResources().getDimensionPixelSize(R.dimen.small_card_spacer);
                energyIconSize = getResources().getDimensionPixelSize(R.dimen.small_card_energy_icon_dim);
                colorlessEnergyTextSize = getResources().getDimensionPixelSize(R.dimen.small_card_energy_text_size)/2;
                energyLayout = R.layout.small_energy_cost;

                for(ITrait trait : traits){
                    LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
                    ImageView traitLayout = (ImageView)inflater.inflate(R.layout.small_trait_image, traitLL, false);
                    traitLayout.setImageDrawable(getResources().getDrawable(trait.getImageResource()));
                    traitLL.addView(traitLayout);
                }
                break;
            case LARGE_CARD:
                spacer = getResources().getDimensionPixelSize(R.dimen.large_card_spacer);
                energyIconSize = getResources().getDimensionPixelSize(R.dimen.large_card_energy_icon_dim);
                colorlessEnergyTextSize = getResources().getDimensionPixelSize(R.dimen.large_card_colorless_energy_text_size)/2;
                energyLayout = R.layout.energy_cost;

                for(ITrait trait : traits){
                    LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();
                    RelativeLayout traitLayout = (RelativeLayout)inflater.inflate(trait.getLayoutResource(), traitLL, false);
                    traitLL.addView(traitLayout);
                }
                break;
        }

        LinearLayout costLL = (LinearLayout)this.findViewWithTag("costLL");
        if(0 < costLL.getChildCount()){
            costLL.removeAllViews();
        }

        LayoutInflater inflater = ((GameActivity) getContext()).getLayoutInflater();

        for(Energy en : energy){
            TextView energyCost = (TextView) inflater.inflate(energyLayout, costLL, false);
            energyCost.setText(String.valueOf(en.level));
            ((GameActivity)getContext()).setBackground(energyCost, en.image);
            energyCost.setPadding(
                    energyCost.getPaddingLeft(),
                    en.getPadding(cardSize),  //set custom top padding
                    energyCost.getPaddingRight(),
                    energyCost.getPaddingBottom());
            costLL.addView(energyCost);
        }

        TextView attackText = (TextView)this.findViewWithTag("attack");
        attackText.setText(String.valueOf(attack));
        TextView defenseText = (TextView)this.findViewWithTag("defense");
        defenseText.setText(String.valueOf(defense));
        TextView healthText = (TextView)this.findViewWithTag("health");
        healthText.setText(String.valueOf(health));
    }

    private void generateCardForView(ViewGroup expectedParent) {
        Context context = getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResource = 0;
        int backgroundResource = 0;
        switch(cardSize){
            case SMALL_CARD:
                layoutResource = R.layout.small_card;
                backgroundResource = R.drawable.small_card_background;
                break;
            case LARGE_CARD:
                layoutResource = R.layout.card;
                backgroundResource = R.drawable.card_background;
        }
        Card cardLayout = (Card)inflater.inflate(layoutResource, expectedParent, false);

        this.setLayoutParams(cardLayout.getLayoutParams());
        ((GameActivity)context).setBackground(this, context.getResources().getDrawable(backgroundResource));
        while(0 < cardLayout.getChildCount()){
            View cardChild = cardLayout.getChildAt(0);
            if(cardChild != null){
                ((ViewGroup)cardChild.getParent()).removeView(cardChild);
                this.addView(cardChild);
            }
        }
        setCardChildrenValues();
    }

    public void damageCard(int amount){
        this.health -= amount;
        TextView healthTV = (TextView)this.findViewWithTag("health");
        healthTV.setText(String.valueOf(health));
        healthTV.setTextColor(0xFFAA1111);
        healthTV.invalidate();
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
        ((GameActivity)getContext()).setBackground(this, getResources().getDrawable(R.drawable.card_background));
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    private boolean loopTraits(ArrayList<Card> targets, int trigger){
        for(ITrait trait : traits){
            if(trait.checkTrigger(trigger)){
                return trait.traitEffect(this, targets, ITrait.TRIGGER_KILL);
            }
        }
        return false;
    }

    public void setCardForView(int size, ViewGroup expectedParent) {
        cardSize = size;
        generateCardForView(expectedParent);
    }

}
