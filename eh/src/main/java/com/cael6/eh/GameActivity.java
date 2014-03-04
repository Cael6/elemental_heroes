package com.cael6.eh;

import com.cael6.eh.cael6.Card;
import com.cael6.eh.cael6.HeroCard;
import com.cael6.eh.cael6.Player;
import com.cael6.eh.cael6.StaticCardOnTouchListener;
import com.cael6.eh.cael6.TargetDragListener;
import com.cael6.eh.cael6.ZoneDragListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

    //<editor-fold desc="Privates">
    /**
     * Width of each card retrieved from attrs
     */
    private float cardWidth;
    /**
     * Height of each card retrieved from attrs
     */
    private float cardHeight;
    public Player player;
    private Player enemy;
    public boolean previewOpen = false;

    //</editor-fold>
    //<editor-fold desc="Views">
    //Top to playerCardZone
    private LinearLayout enemyStatsBar;

    private LinearLayout enemyCardZone;
    private RelativeLayout enemyHero;
    private HorizontalScrollView enemyHandScroll;
    private LinearLayout enemyHand;

    private LinearLayout enemyBoard;
    private RelativeLayout enemyTriggerZone;
    private RelativeLayout enemyCreatureZone1;
    private RelativeLayout enemyCreatureZone2;
    private RelativeLayout enemyCreatureZone3;
    private RelativeLayout enemyCreatureZone4;

    private LinearLayout playerBoard;
    private RelativeLayout triggerZone;
    private RelativeLayout creatureZone1;
    private RelativeLayout creatureZone2;
    private RelativeLayout creatureZone3;
    private RelativeLayout creatureZone4;

    private LinearLayout playerCardZone;
    private RelativeLayout hero;
    private HorizontalScrollView handScroll;
    private LinearLayout playerHand;

    private LinearLayout playerStatusBar;

    private LinearLayout preview;

    //</editor-fold>

    public Drawable enterShape;
    public Drawable normalShape;

    public int startingHandSize = 5;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        initViews();
        enterShape = getResources().getDrawable(R.drawable.board_area_hover);
        normalShape = getResources().getDrawable(R.drawable.board_area);

        //Set card sizes.
        cardHeight = getResources().getDimensionPixelSize(R.dimen.small_card_height);
        cardWidth = getResources().getDimensionPixelSize(R.dimen.small_card_width);
        int cardSpacing = (int)pxFromDp(20);

        player = new Player(this, R.xml.deck1, playerStatusBar, playerCardZone, playerBoard, (int)cardWidth, (int)cardHeight, cardSpacing);
        player.cardsDefaultHidden = false;
        player.playerControl = true;
        enemy = new Player(this, R.xml.deck1, enemyStatsBar, enemyCardZone, enemyBoard, (int)cardWidth, (int)cardHeight, cardSpacing);
        enemy.cardsDefaultHidden = true;
        enemy.isAi = true;

        player.enemy = enemy;
        enemy.enemy = player;

        hero = (RelativeLayout)findViewById(R.id.hero);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)cardWidth, (int)cardHeight);
        player.deck.hero.setCardSize(Card.SMALL_CARD);
        int spacer = getResources().getDimensionPixelSize(R.dimen.small_card_spacer);
        player.deck.hero.setPadding(spacer, spacer, 0, 0);
        hero.addView(player.deck.hero, params);

        player.deck.hero.setOnTouchListener(new StaticCardOnTouchListener(this));
        player.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(player.deck.hero);
            }
        });
        player.deck.hero.setOnDragListener(new TargetDragListener(this));

        enemyHero = (RelativeLayout)findViewById(R.id.enemyHero);
        enemy.deck.hero.setCardSize(Card.SMALL_CARD);
        enemyHero.addView(enemy.deck.hero, params);

        enemy.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(enemy.deck.hero);
            }
        });
        enemy.deck.hero.setOnDragListener(new TargetDragListener(this));

        creatureZone1.setOnDragListener(new ZoneDragListener(this));
        creatureZone2.setOnDragListener(new ZoneDragListener(this));
        creatureZone3.setOnDragListener(new ZoneDragListener(this));
        creatureZone4.setOnDragListener(new ZoneDragListener(this));

        for(int i = 0; i < startingHandSize; i++){
            enemy.drawCard(this);
            player.drawCard(this);

        }
    }

    private void initViews(){
        enemyStatsBar = (LinearLayout) findViewById(R.id.enemyEnergy);

        enemyCardZone = (LinearLayout) findViewById(R.id.enemyCardZone);
        enemyHero = (RelativeLayout) findViewById(R.id.enemyHero);
        enemyHandScroll = (HorizontalScrollView) findViewById(R.id.enemyHandScroll);
        enemyHand = (LinearLayout) findViewById(R.id.enemyHand);

        enemyBoard = (LinearLayout) findViewById(R.id.enemyBoard);
        enemyTriggerZone = (RelativeLayout) findViewById(R.id.enemyTriggerZone);
        enemyCreatureZone1 = (RelativeLayout) findViewById(R.id.enemyCreatureZone1);
        enemyCreatureZone2 = (RelativeLayout) findViewById(R.id.enemyCreatureZone2);
        enemyCreatureZone3 = (RelativeLayout) findViewById(R.id.enemyCreatureZone3);
        enemyCreatureZone4 = (RelativeLayout) findViewById(R.id.enemyCreatureZone4);

        playerBoard = (LinearLayout) findViewById(R.id.playerBoard);
        triggerZone = (RelativeLayout) findViewById(R.id.triggerZone);
        creatureZone1 = (RelativeLayout) findViewById(R.id.creatureZone1);
        creatureZone2 = (RelativeLayout) findViewById(R.id.creatureZone2);
        creatureZone3 = (RelativeLayout) findViewById(R.id.creatureZone3);
        creatureZone4 = (RelativeLayout) findViewById(R.id.creatureZone4);

        playerCardZone = (LinearLayout) findViewById(R.id.playerCardZone);
        hero = (RelativeLayout) findViewById(R.id.hero);
        handScroll = (HorizontalScrollView) findViewById(R.id.handScroll);
        playerHand = (LinearLayout) findViewById(R.id.playerHand);

        playerStatusBar = (LinearLayout) findViewById(R.id.statusBar);

        preview = (LinearLayout) findViewById(R.id.preview);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(previewOpen){
            //if preview open close it with cancelCardSelect
            cancelCardSelect(new View(this));
        } else {
            //Do default
            super.onBackPressed();
        }
    }

    public float pxFromDp(float dp){
        return dp / this.getResources().getDisplayMetrics().density;
    }

    public void previewCard(Card card){
        RelativeLayout preview = (RelativeLayout)findViewById(R.id.previewCardZone);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.large_card_width),
                        getResources().getDimensionPixelSize(R.dimen.large_card_height));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        if(preview.getChildAt(0) instanceof Card){
            preview.removeViewAt(0);
        }

        Card previewCard;
        if(card instanceof HeroCard){
            previewCard = new HeroCard(this, (HeroCard)card);;
        }
        else{
            previewCard = new Card(this, card);
        }
        previewCard.setCardSize(Card.LARGE_CARD);
        preview.addView(previewCard, 0, params);
        int spacer = getResources().getDimensionPixelSize(R.dimen.large_card_spacer);
        previewCard.setPadding(spacer, spacer, 0, 0);

        ((ViewGroup)preview.getParent()).setVisibility(View.VISIBLE);
        previewOpen = true;
    }

    public void cancelCardSelect(View view){
        LinearLayout preview = (LinearLayout)findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        previewOpen = false;
    }

    public void endTurn(View view){
        player.endTurn(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean dropCard(Card card, ViewGroup container, Player player){
        //TODO: check resource count and spend/play if possible.
        if(player.attemptToPlayCard(card)){
            ViewGroup owner = (ViewGroup) card.getParent();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)cardWidth,(int) cardHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.width = (int)cardWidth;
            owner.removeView(card);
            card.setOnTouchListener(new StaticCardOnTouchListener(this));
            card.setOnDragListener(new TargetDragListener(this));
            container.addView(card, params);
            player.updatePlayerUiStatusBar();
            card.showCard();
            card.enterBattleField();
            return true;
        }
        return false;
    }

    /**
     *
     * @param card
     * @param targetCard
     * @return true if the targetCard is destroyed
     */
    public boolean attackCard(Card card, Card targetCard) {
        if(card.spendAction()){
            if(Card.checkAttack(card, targetCard)){
                targetCard.damageCard(card.attack - targetCard.defense);
                if(targetCard.health < 1){
                    if(targetCard instanceof HeroCard){
                        //player loses.
                        finish();
                        return true;
                    }
                    targetCard.destroy();
                    card.destroyed(targetCard);
                    return true;
                }
                player.updatePlayerUiStatusBar();
                enemy.updatePlayerUiStatusBar();
            } else {
                //TODO: no damage
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void setBackground(View view, Drawable background){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }
}