package com.cael6.eh.cael6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Class that represents the players of the game
 */
public class Player {
    public int currDragonBreathDrawn;
    public Deck deck;
    public LinearLayout statusBar, hand, board;
    public boolean cardsDefaultHidden;
    public boolean playerControl = false;
    public ArrayList<Card> cardsInHand;
    public ArrayList<Card> cardsOnBoard;
    public ArrayList<ViewGroup> creatureBoardPositions;
    public Player enemy;
    public boolean isAi = false;
    public Deck graveyard;

    private final String TURN_TAG = "turn";
    private final String HEALTH_TAG = "health";
    private final String DB_DRAWING_TAG = "dragonbreath";

    public Player(Context context, int deck, LinearLayout statusBar, LinearLayout hand, LinearLayout board){
        this.deck = new Deck(context, deck, this);
        this.statusBar = statusBar;
        this.hand = hand;
        this.board = board;
        this.cardsInHand = new ArrayList<Card>();
        this.cardsOnBoard = new ArrayList<Card>();
        this.creatureBoardPositions = new ArrayList<ViewGroup>();
        createDragonBreath();
        drawDragonBreath();
        for(int j = 1; j < board.getChildCount(); j++){
            RelativeLayout boardPosition = (RelativeLayout) board.getChildAt(j);
            creatureBoardPositions.add(boardPosition);
        }
        createPlayerUiStatusBar(context);
        updatePlayerUiStatusBar();
    }

    private void createPlayerUiStatusBar(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //Dragon Breath Drawing
        TextView enTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        if (enTV != null) {
            enTV.setText(String.valueOf(deck.hero.dragonBreathDrawing));
            enTV.setTag(DB_DRAWING_TAG);
            ((GameActivity)context).setBackground(enTV, context.getResources().getDrawable(R.drawable.fire_status_icon));
            statusBar.addView(enTV);
        }
        //Actions
        TextView actionTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        if (actionTV != null) {
            actionTV.setText(String.valueOf(deck.hero.turns));
            actionTV.setTag(TURN_TAG);
            ((GameActivity)context).setBackground(actionTV, context.getResources().getDrawable(R.drawable.action_status_icon));
            statusBar.addView(actionTV);
        }

        //Health
        TextView lifeTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        if (lifeTV != null) {
            lifeTV.setText(String.valueOf(deck.hero.turns));
            lifeTV.setTag(HEALTH_TAG);
            ((GameActivity)context).setBackground(lifeTV, context.getResources().getDrawable(R.drawable.life_status_icon));
            statusBar.addView(lifeTV);
        }
    }

    public void updatePlayerUiStatusBar() {

        for(int i = 0; i < statusBar.getChildCount(); i++){
            View child = statusBar.getChildAt(i);
            if(child instanceof TextView){
                TextView tv = (TextView) child;
                if(tv.getTag().equals(DB_DRAWING_TAG)){
                    tv.setText(String.valueOf(currDragonBreathDrawn));
                }
                if(tv.getTag().equals(TURN_TAG)){
                   tv.setText(String.valueOf(deck.hero.turns));
                }
                if(tv.getTag().equals(HEALTH_TAG)){
                    tv.setText(String.valueOf(deck.hero.health));
                }
            }
        }
        statusBar.invalidate();
    }

    /**
     * initialize dragonBreathDrawn
     */
    private void createDragonBreath(){
        currDragonBreathDrawn = 0;
    }

    /**
     * Draw dragon breath each turn. Add to current count
     */
    public void drawDragonBreath(){
        drawDragonBreath(deck.hero.dragonBreathDrawing);
    }

    /**
     * Draws a specific amount of dragon breath
     * @param amount the amount of breath to draw
     */
    public void drawDragonBreath(int amount){
        currDragonBreathDrawn += amount;
    }

    /**
     * Draw card from deck and put it into the player's hand.
     * @return returns the drawn card.
     */
    public Card drawCard(){
        Card drawnCard = deck.drawCard();
        drawnCard.setCardForView(Card.SMALL_CARD, this.hand);
        drawnCard.inHand = true;
        drawnCard.movable = true;
        if(cardsDefaultHidden){
            drawnCard.hideCard();
        }
        int leftRightMargin = drawnCard.getContext().getResources().getDimensionPixelSize(R.dimen.hand_left_right_margin);
        cardsInHand.add(drawnCard);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(drawnCard.getWidth(), drawnCard.getHeight());
        params.setMargins(leftRightMargin, 0, leftRightMargin, 0);
        hand.addView(drawnCard);

        if(playerControl){
            drawnCard.setOnTouchListener(new CardOnTouchListener());
            drawnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assert view.getContext() != null;
                    ((GameActivity)view.getContext()).previewCard((Card) view);
                }
            });
        }
        return drawnCard;
    }

    /**
     * Check to see if the player has the appropriate resources to play a card.
     * @param card The card that the player is playing
     * @param egg The egg to be sacrificed for dragon cards
     * @return Does the player have the appropriate resources.
     */
    public boolean checkResources(Card card, EggCard egg) {
        boolean haveResources = deck.hero.turns > 0;
        if(haveResources){
            if(card instanceof SpellCard){
                haveResources &= currDragonBreathDrawn >= ((SpellCard)card).dragonBreathCost;
            }else if(card instanceof DragonCard && null != egg){
                haveResources &= ((DragonCard)card).getHatchCost() <= egg.getHatchTimer();
            }
        }
        return haveResources;
    }

    /**
     * Player attempts to play the card.
     * @param card The card that the player is playing
     * @param egg The egg to be sacrificed for dragon cards
     * @return true if the card is played.
     */
    public boolean attemptToPlayCard(Card card, EggCard egg) {
        if(!checkResources(card, egg)){
            return false;
        }
        spendResources(card, egg); //spend resources
        updatePlayerUiStatusBar(); //update ui
        return true;
    }

    /**
     * Spend the resources to play the card.
     * @param card The card that the player is playing
     * @param egg The egg to be sacrificed for dragon cards
     */
    public void spendResources(Card card, EggCard egg) {
        deck.hero.spendAction();
        if(card instanceof SpellCard){
            currDragonBreathDrawn -= ((SpellCard)card).dragonBreathCost;
        }else if(card instanceof DragonCard && null != egg){
            egg.destroyCard();
        }
    }

    /**
     * reset the actions for every card on the board.
     */
    public void resetActions(){
        deck.hero.resetActions();
        for(int i = 0; i < board.getChildCount(); i++){
            ViewGroup child = (ViewGroup) board.getChildAt(i);
            assert child != null;
            for(int j = 0; j < child.getChildCount(); j++){
                View possibleCard = child.getChildAt(j);
                if(possibleCard instanceof Card){
                    ((CharacterCard) possibleCard).resetActions();
                    possibleCard.invalidate();
                }
            }
        }
    }

    /**
     * End the current player's turn and start the enemy's turn
     * @param context game activity
     */
    public void endTurn(GameActivity context){
        enemy.startTurn(context);
    }

    /**
     * Does all the necessary actions to start a player's turn.
     * @param context Game Activity
     */
    public void startTurn(GameActivity context){
        drawCard();
        drawDragonBreath();
        resetActions();
        updatePlayerUiStatusBar();
        if(isAi){
            aiTurn(context);
        }
    }

    /**
     * Run an ai's complete turn.
     * @param context
     */
    public void aiTurn(GameActivity context){
        aiPlayCards(context);
        aiAttack(context);
        endTurn(context);
    }

    /**
     * Make attacks for the ai
     * @param context
     */
    private void aiAttack(GameActivity context) {
        for (Card card : cardsOnBoard) {
            Iterator<Card> enemyCardIterator = enemy.cardsOnBoard.iterator();
            while (enemyCardIterator.hasNext()) {
                Card enemyCard = enemyCardIterator.next();
                if (card.attack > enemyCard.defense) {
                    if (context.attackCard(card, enemyCard)) {
                        enemyCardIterator.remove();
                    }
                }
            }
            context.attackCard(card, enemy.deck.hero);
        }
        ListIterator<Card> enemyCardIterator = enemy.cardsOnBoard.listIterator();
        while(enemyCardIterator.hasNext()){
            Card enemyCard = enemyCardIterator.next();
            if(deck.hero.attack > enemyCard.defense){
                if(context.attackCard(deck.hero, enemyCard)){
                    enemyCardIterator.remove();
                }
            }
        }
        while(deck.hero.turns > 0){
            context.attackCard(deck.hero, enemy.deck.hero);
        }
    }

    /**
     * play cards for the ai
     * @param context
     */
    private void aiPlayCards(GameActivity context) {
        ListIterator<Card> cardIterator = cardsInHand.listIterator();
        while(cardIterator.hasNext()){
            Card card = cardIterator.next();
            for (ViewGroup boardPosition : creatureBoardPositions) {
                if (boardPosition.getChildCount() < 1) {
                    if (context.dropCard(card, boardPosition, this)) {
                        cardIterator.remove();
                        cardsOnBoard.add(card);
                        context.setBackground(boardPosition, null);
                        break;
                    }
                }
            }
        }
    }

}
