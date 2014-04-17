package com.cael6.eh;

import com.cael6.eh.cael6.CTouchListener;
import com.cael6.eh.cael6.Card;
import com.cael6.eh.cael6.CharacterCard;
import com.cael6.eh.cael6.CharacterCard.CharacterDragListener;
import com.cael6.eh.cael6.CreatureZoneDragListener;
import com.cael6.eh.cael6.EggCard.EggDragListener;
import com.cael6.eh.cael6.DragonCard;
import com.cael6.eh.cael6.EggCard;
import com.cael6.eh.cael6.HeroCard;
import com.cael6.eh.cael6.Player;

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

    private LinearLayout enemyHand;

    private LinearLayout enemyBoard;

    private LinearLayout playerBoard;

    private LinearLayout playerHand;

    private LinearLayout playerStatusBar;

    private LinearLayout preview;

    //</editor-fold>

    public Drawable creatureZoneEnterShape;
    public Drawable creatureZoneNormalShape;

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
        creatureZoneEnterShape = getResources().getDrawable(R.drawable.board_area_hover);
        creatureZoneNormalShape = getResources().getDrawable(R.drawable.board_area);

        player = new Player(this, R.xml.deck1, playerStatusBar, playerHand, playerBoard);
        player.cardsDefaultHidden = false;
        player.playerControl = true;
        enemy = new Player(this, R.xml.deck1, enemyStatusBar, enemyHand, enemyBoard);
        enemy.cardsDefaultHidden = true;
        enemy.isAi = true;

        player.enemy = enemy;
        enemy.enemy = player;

        RelativeLayout playerHero = (RelativeLayout) findViewById(R.id.playerHero);
        player.deck.hero.setCardForView(Card.SMALL_CARD, playerHero);
        playerHero.addView(player.deck.hero);

        player.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(player.deck.hero);
            }
        });
        player.deck.hero.setOnDragListener(new CharacterCard.CharacterDragListener());

        RelativeLayout enemyHero = (RelativeLayout) findViewById(R.id.enemyHero);
        enemy.deck.hero.setCardForView(Card.SMALL_CARD, enemyHero);
        enemyHero.addView(enemy.deck.hero);

        enemy.deck.hero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewCard(enemy.deck.hero);
            }
        });
        enemy.deck.hero.setOnDragListener(new CharacterDragListener());

        for(int i = 0; i <player.board.getChildCount(); i++){
            View v = player.board.getChildAt(i);
            Object tag;
            if (v != null) {
                tag = v.getTag();
                if(tag !=null && tag.equals("creatureZone")){
                    v.setOnDragListener(new CreatureZoneDragListener(this));
                }
            }
        }
        for(int i = 0; i < startingHandSize; i++){
            enemy.drawCard();
            player.drawCard();

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
            cancelCardSelect(null);
        } else {
            //Do default
            super.onBackPressed();
        }
    }

    public void previewCard(Card card){

        RelativeLayout preview = (RelativeLayout)this.preview.findViewWithTag("card");
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.large_card_width),
                        getResources().getDimensionPixelSize(R.dimen.large_card_height));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        if (preview != null) {
            if(preview.getChildAt(0) instanceof Card){
                preview.removeViewAt(0);
            }
        }

        Card previewCard;
        if(card instanceof HeroCard){
            previewCard = new HeroCard(this, (HeroCard)card, card.getOwner());
        }
        else if(card instanceof DragonCard){
            previewCard = new DragonCard(this, (DragonCard)card, card.getOwner());
        }else{
            previewCard = null;
        }
        previewCard.setCardForView(Card.LARGE_CARD, preview);
        if (preview != null) {
            preview.addView(previewCard, 0, params);
        }

        if (preview != null) {
            if ((preview.getParent()) != null) {
                ((ViewGroup)preview.getParent()).setVisibility(View.VISIBLE);
            }
        }
        previewOpen = true;
        disableBackgroundActions();
    }

    public void cancelCardSelect(View view){
        LinearLayout preview = (LinearLayout)findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        previewOpen = false;
        enableBackgroundActions();
    }

    public void endTurn(View view){
        player.endTurn(this);
    }

    public boolean dropDragonCard(DragonCard dragon, EggCard egg, Player player){
        ViewGroup container = (ViewGroup)egg.getParent();
        if(player.attemptToPlayDragonCard(dragon, egg)){
            ViewGroup owner = (ViewGroup) dragon.getParent();
            if (owner != null) {
                owner.removeView(dragon);
            }
            dragon.setOnTouchListener(new CTouchListener(CTouchListener.TYPE_STATIC));
            dragon.setOnDragListener(new CharacterDragListener());
            if (container != null) {
                container.addView(dragon);
            }
            player.updatePlayerUiStatusBar();
            dragon.showCard();
            dragon.enterBattleField();
            return true;
        }
        return false;
    }

    /**
     *
     * @param card attacking card
     * @param targetCard card being attacked
     * @return true if the targetCard is kill
     */
    public boolean attackCard(CharacterCard card, CharacterCard targetCard) {
        if(card.spendAction()){
            if(CharacterCard.checkAttack(card, targetCard)){
                targetCard.damageCard(card.attack - targetCard.defense);
                if(targetCard.health < 1){
                    if(targetCard instanceof HeroCard){
                        //player loses.
                        targetCard.getOwner().lose(this);
                        return true;
                    }else if(targetCard instanceof DragonCard){
                        DragonCard targetDragonCard = (DragonCard) targetCard;
                        targetDragonCard.killed();
                        card.kill(targetCard);
                    }
                    return true;
                }
                player.updatePlayerUiStatusBar();
                enemy.updatePlayerUiStatusBar();
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static void setBackground(View view, Drawable background){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(background);
        } else {
            view.setBackground(background);
        }
    }

    public boolean dropEggCard(EggCard egg, ViewGroup container, Player player) {
        if(player.attemptToPlayEgg(egg)){
            ViewGroup owner = (ViewGroup) egg.getParent();
            if (owner != null) {
                owner.removeView(egg);
            }
            egg.setOnDragListener(new EggDragListener());
            if (container != null) {
                container.addView(egg);
            }
            player.updatePlayerUiStatusBar();
            egg.showCard();
            return true;
        }
        return false;
    }

    /**
     * disables the user from being able to do things while the preview is open.
     */
    public void disableBackgroundActions() {
        
    }

    public void enableBackgroundActions() {

    }
}