package com.cael6.eh.cael6;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by cael6 on 23/02/14.
 */
public class Player {
    public ArrayList<Energy> currEnergy;
    public Deck deck;
    public LinearLayout statusBar, hand, board;
    public boolean cardsDefaultHidden;
    public boolean playerControl = false;
    public ArrayList<Card> cardsInHand;
    public ArrayList<Card> cardsOnBoard;
    public ArrayList<ViewGroup> creatureBoardPositions;
    public Player enemy;
    public boolean isAi = false;

    private final String TURN_TAG = "turn";
    private final String HEALTH_TAG = "health";

    public Player(Context context, int deck, LinearLayout statusBar, LinearLayout hand, LinearLayout board){
        this.deck = new Deck(context, deck, this);
        this.statusBar = statusBar;
        this.hand = hand;
        this.board = board;
        this.cardsInHand = new ArrayList<Card>();
        this.cardsOnBoard = new ArrayList<Card>();
        this.creatureBoardPositions = new ArrayList<ViewGroup>();
        createElements(context);
        generateElements();
        for(int j = 1; j < board.getChildCount(); j++){
            RelativeLayout boardPosition = (RelativeLayout) board.getChildAt(j);
            creatureBoardPositions.add(boardPosition);
        }
        createPlayerUiStatusBar(context);
        updatePlayerUiStatusBar();
    }

    private void createPlayerUiStatusBar(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (Energy en : deck.hero.energyGen) {
            TextView enTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
            enTV.setText(String.valueOf(en.level));
            enTV.setTag(en.name);
            ((GameActivity)context).setBackground(enTV, en.image);
            statusBar.addView(enTV);
        }
        //Actions
        TextView actionTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        actionTV.setText(String.valueOf(deck.hero.turns));
        actionTV.setTag(TURN_TAG);
        ((GameActivity)context).setBackground(actionTV, context.getResources().getDrawable(R.drawable.turn));
        statusBar.addView(actionTV);

        //Health
        TextView lifeTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        lifeTV.setText(String.valueOf(deck.hero.turns));
        lifeTV.setTag(HEALTH_TAG);
        ((GameActivity)context).setBackground(lifeTV, context.getResources().getDrawable(R.drawable.life_status_icon));
        statusBar.addView(lifeTV);
    }

    protected TextView getStatusBarTextView(Context context, String value, String tag){
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int)((GameActivity)context).pxFromDp(10);
        params.topMargin = (int)((GameActivity)context).pxFromDp(-4);
        params.rightMargin = (int)((GameActivity)context).pxFromDp(5);
        tv.setText(value);
        tv.setTag(tag);
        tv.setTextColor(0xFFFFFFFF);
        tv.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.status_text_size));
        tv.setLayoutParams(params);
        return tv;
    }

    protected ImageView getStatusBarImageView(Context context, Drawable drawable){

        ImageView iv = new ImageView(context);
        iv.setImageDrawable(drawable);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.status_icon_size),
                context.getResources().getDimensionPixelSize(R.dimen.status_icon_size));
        iv.setLayoutParams(params);
        return iv;
    }

    public void updatePlayerUiStatusBar() {

        for(int i = 0; i < statusBar.getChildCount(); i++){
            View child = statusBar.getChildAt(i);
            if(child instanceof TextView){
                TextView tv = (TextView) child;
                for (Energy currEn : currEnergy) {
                    if (currEn.name.equals(tv.getTag())) {
                        tv.setText(String.valueOf(currEn.level));
                        break;
                    }
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

    private void createElements(Context context){
        currEnergy = new ArrayList<Energy>();
        for (Energy en : deck.hero.energyGen) {
            currEnergy.add(new Energy(context, en.id, 0));
        }
    }

    public void generateElements(){
        for(Energy en : deck.hero.energyGen){
            for(Energy currEn : currEnergy){
                if(en.id == currEn.id){
                    currEn.level += en.level;
                }
            }
        }
    }

    public Card drawCard(Context context){
        Card drawnCard = deck.drawCard();
        drawnCard.setCardForView(Card.SMALL_CARD, this.hand);
        drawnCard.inHand = true;
        drawnCard.movable = true;
        if(cardsDefaultHidden){
            drawnCard.hideCard();
        }
        cardsInHand.add(drawnCard);
        assert hand != null;
        hand.addView(drawnCard);

        if(playerControl){
            drawnCard.setOnTouchListener(new MovableCardOnTouchListener((GameActivity)drawnCard.getContext()));
            drawnCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assert ((GameActivity)view.getContext()) != null;
                    ((GameActivity)view.getContext()).previewCard((Card) view);
                }
            });
        }
        return drawnCard;
    }

    public boolean checkResources(Card card) {
        boolean haveResources = deck.hero.turns > 0;
        if(haveResources){
            for(Energy en : card.energy){
                for (Energy currEn : currEnergy) {
                    if (en.id == currEn.id) {
                        haveResources &= currEn.level >= en.level;
                    }
                }
            }
        }
        return haveResources;
    }

    public boolean attemptToPlayCard(Card card) {
        if(!checkResources(card)){
            return false;
        }
        spendResources(card); //spend resources
        updatePlayerUiStatusBar(); //update ui
        return true;
    }

    public void spendResources(Card card) {
        deck.hero.spendAction();
        Iterator<Energy> it = card.energy.iterator();
        while(it.hasNext()){
            Energy en = it.next();

            Iterator<Energy> currEnIt = currEnergy.iterator();
            while(currEnIt.hasNext()){
                Energy currEn = currEnIt.next();
                if(en.id == currEn.id){
                    currEn.level -= en.level;
                }
            }
        }
    }

    public void resetActions(){
        deck.hero.resetActions();
        for(int i = 0; i < board.getChildCount(); i++){
            ViewGroup child = (ViewGroup) board.getChildAt(i);
            assert child != null;
            for(int j = 0; j < child.getChildCount(); j++){
                View possibleCard = child.getChildAt(j);
                if(possibleCard instanceof Card){
                    ((Card) possibleCard).resetActions();
                    possibleCard.invalidate();
                }
            }
        }
    }

    public void endTurn(GameActivity context){
        enemy.startTurn(context);
    }

    public void startTurn(GameActivity context){
        drawCard(context);
        generateElements();
        resetActions();
        updatePlayerUiStatusBar();
        if(isAi){
            aiTurn(context);
        }
    }

    public void aiTurn(GameActivity context){
        aiPlayCards(context);
        aiAttack(context);
        endTurn(context);
    }

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

    public void generateElement(Energy energy) {
        for(Energy en : deck.hero.energyGen){
            if(en.id == energy.id){
                energy.level += en.level;
            }
        }
    }
}
