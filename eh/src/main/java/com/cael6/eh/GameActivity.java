package com.cael6.eh;

import com.cael6.eh.cael6.Card;
import com.cael6.eh.cael6.HeroCard;
import com.cael6.eh.cael6.Player;
import com.cael6.eh.cael6.StaticCardOnTouchListener;
import com.cael6.eh.cael6.TargetDragListener;
import com.cael6.eh.cael6.ZoneDragListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends Activity {

    //<editor-fold desc="Privates">
    public Player player;
    private Player enemy;
    public boolean previewOpen = false;

    //</editor-fold>
    //<editor-fold desc="Views">
    //Top to hand
    private LinearLayout enemyStatusBar;

    private RelativeLayout enemyHero;
    private LinearLayout enemyHand;

    private LinearLayout enemyBoard;

    private LinearLayout playerBoard;

    private RelativeLayout playerHero;
    private LinearLayout playerHand;

    private LinearLayout playerStatusBar;

    private LinearLayout preview;

    //</editor-fold>

    public Drawable enterShape;
    public Drawable normalShape;

    public int startingHandSize = 5;

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

        int cardSpacing = (int)pxFromDp(20);

        player = new Player(this, R.xml.deck1, playerStatusBar, playerHand, playerBoard);
        player.cardsDefaultHidden = false;
        player.playerControl = true;
        enemy = new Player(this, R.xml.deck1, enemyStatusBar, enemyHand, enemyBoard);
        enemy.cardsDefaultHidden = true;
        enemy.isAi = true;

        player.enemy = enemy;
        enemy.enemy = player;

        playerHero = (RelativeLayout)player.board.findViewWithTag("hero");
        player.deck.hero.setCardForView(Card.SMALL_CARD, playerHero);
        playerHero.addView(player.deck.hero);

        player.deck.hero.setOnTouchListener(new StaticCardOnTouchListener(this));
        player.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(player.deck.hero);
            }
        });
        player.deck.hero.setOnDragListener(new TargetDragListener(this));

        enemyHero = (RelativeLayout)enemy.board.findViewWithTag("hero");
        enemy.deck.hero.setCardForView(Card.SMALL_CARD, enemyHero);
        enemyHero.addView(enemy.deck.hero);

        enemy.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(enemy.deck.hero);
            }
        });
        enemy.deck.hero.setOnDragListener(new TargetDragListener(this));

        for(int i = 0; i <player.board.getChildCount(); i++){
            View v = player.board.getChildAt(i);
            if(v.getTag().equals("creatureZone")){
                v.setOnDragListener(new ZoneDragListener(this));
            }
        }
        for(int i = 0; i < startingHandSize; i++){
            enemy.drawCard(this);
            player.drawCard(this);

        }
    }

    private void initViews(){
        enemyStatusBar = (LinearLayout) findViewById(R.id.enemyStatusBar);
        enemyHand = (LinearLayout) findViewById(R.id.enemyHand).findViewWithTag("hand");
        enemyBoard = (LinearLayout) findViewById(R.id.enemyBoard);

        playerBoard = (LinearLayout) findViewById(R.id.playerBoard);
        playerHand = (LinearLayout) findViewById(R.id.playerHand).findViewWithTag("hand");
        playerStatusBar = (LinearLayout) findViewById(R.id.playerStatusBar);

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
        RelativeLayout preview = (RelativeLayout)this.preview.findViewWithTag("card");
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
            previewCard = new HeroCard(this, (HeroCard)card, card.getOwner());;
        }
        else{
            previewCard = new Card(this, card, card.getOwner());
        }
        previewCard.setCardForView(Card.LARGE_CARD, preview);
        preview.addView(previewCard, 0, params);

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

    public boolean dropCard(Card card, ViewGroup container, Player player){
        //TODO: check resource count and spend/play if possible.
        if(player.attemptToPlayCard(card)){
            ViewGroup owner = (ViewGroup) card.getParent();
            owner.removeView(card);
            card.setOnTouchListener(new StaticCardOnTouchListener(this));
            card.setOnDragListener(new TargetDragListener(this));
            container.addView(card);
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
     * @return true if the targetCard is kill
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
                    targetCard.killed();
                    card.kill(targetCard);
                    return true;
                }
                player.updatePlayerUiStatusBar();
                enemy.updatePlayerUiStatusBar();
            } else {
                //no damage
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