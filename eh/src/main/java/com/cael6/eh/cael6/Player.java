package com.cael6.eh.cael6;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cael6.eh.GameActivity;
import com.cael6.eh.GameStatsActivity;
import com.cael6.eh.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Class that represents the players of the game
 */
public class Player {
    private final String TURN_TAG = "turn";
    private final String HEALTH_TAG = "health";
    private final String DB_DRAWING_TAG = "dragonbreath";
    public int currDragonBreathDrawn;
    public Deck deck;
    public LinearLayout statusBar, hand, board;
    public boolean cardsDefaultHidden;
    public boolean playerControl = false;
    public ArrayList<Card> cardsInHand;
    public ArrayList<DragonCard> dragonsOnBoard;
    public ArrayList<ViewGroup> boardPositions;
    public ArrayList<EggCard> eggsOnBoard;
    public Player enemy;
    public boolean isAi = false;
    public Deck graveyard;

    public Player(Context context, int deck, LinearLayout statusBar, LinearLayout hand, LinearLayout board) {
        this.deck = new Deck(context, deck, this);
        this.statusBar = statusBar;
        this.hand = hand;
        this.board = board;
        this.cardsInHand = new ArrayList<Card>();
        this.dragonsOnBoard = new ArrayList<DragonCard>();
        this.eggsOnBoard = new ArrayList<EggCard>();
        this.boardPositions = new ArrayList<ViewGroup>();
        createDragonBreath();
        drawDragonBreath();
        for (int j = 0; j < board.getChildCount(); j++) {
            RelativeLayout boardPosition = (RelativeLayout) board.getChildAt(j);
            boardPositions.add(boardPosition);
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
            GameActivity.setBackground(enTV, context.getResources().getDrawable(R.drawable.fire_status_icon));
            statusBar.addView(enTV);
        }
        //Actions
        TextView actionTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        if (actionTV != null) {
            actionTV.setText(String.valueOf(deck.hero.turns));
            actionTV.setTag(TURN_TAG);
            GameActivity.setBackground(actionTV, context.getResources().getDrawable(R.drawable.action_status_icon));
            statusBar.addView(actionTV);
        }

        //Health
        TextView lifeTV = (TextView) inflater.inflate(R.layout.status_text, statusBar, false);
        if (lifeTV != null) {
            lifeTV.setText(String.valueOf(deck.hero.turns));
            lifeTV.setTag(HEALTH_TAG);
            GameActivity.setBackground(lifeTV, context.getResources().getDrawable(R.drawable.life_status_icon));
            statusBar.addView(lifeTV);
        }
    }

    public void updatePlayerUiStatusBar() {

        for (int i = 0; i < statusBar.getChildCount(); i++) {
            View child = statusBar.getChildAt(i);
            if (child instanceof TextView) {
                TextView tv = (TextView) child;
                if (tv.getTag().equals(DB_DRAWING_TAG)) {
                    tv.setText(String.valueOf(currDragonBreathDrawn));
                }
                if (tv.getTag().equals(TURN_TAG)) {
                    tv.setText(String.valueOf(deck.hero.turns));
                }
                if (tv.getTag().equals(HEALTH_TAG)) {
                    tv.setText(String.valueOf(deck.hero.health));
                }
            }
        }
        statusBar.invalidate();
    }

    /**
     * initialize dragonBreathDrawn
     */
    private void createDragonBreath() {
        currDragonBreathDrawn = 0;
    }

    /**
     * Draw dragon breath each turn. Add to current count
     */
    public void drawDragonBreath() {
        drawDragonBreath(deck.hero.dragonBreathDrawing);
    }

    /**
     * Draws a specific amount of dragon breath
     *
     * @param amount the amount of breath to draw
     */
    public void drawDragonBreath(int amount) {
        currDragonBreathDrawn += amount;
    }

    /**
     * Draw card from deck and put it into the player's hand.
     *
     * @return returns the drawn card.
     */
    public Card drawCard() {
        Card drawnCard = deck.drawCard();
        drawnCard.setCardForView(Card.SMALL_CARD, this.hand);
        drawnCard.inHand = true;
        drawnCard.movable = true;
        if (cardsDefaultHidden) {
            drawnCard.hideCard();
        }
        int leftRightMargin = drawnCard.getContext().getResources().getDimensionPixelSize(R.dimen.hand_left_right_margin);
        cardsInHand.add(drawnCard);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(drawnCard.getWidth(), drawnCard.getHeight());
        params.setMargins(leftRightMargin, 0, leftRightMargin, 0);
        hand.addView(drawnCard);

        if (playerControl) {
            drawnCard.setListeners();
        }
        return drawnCard;
    }

    /**
     * Check to see if the player has the appropriate resources to play a card.
     *
     * @param card The card that the player is playing
     * @param egg  The egg to be sacrificed for dragon cards
     * @return Does the player have the appropriate resources.
     */
    public boolean checkResources(Card card, EggCard egg) {
        boolean haveResources = deck.hero.turns > 0;
        if (haveResources) {
            if (card instanceof SpellCard) {
                haveResources = currDragonBreathDrawn >= ((SpellCard) card).dragonBreathCost;
            } else if (card instanceof DragonCard && null != egg) {
                haveResources = ((DragonCard) card).getHatchCost() <= egg.getHatchTimer();
            }
        }
        return haveResources;
    }

    /**
     * Player attempts to play the dragon.
     *
     * @param dragon The dragon that the player is playing
     * @param egg  The egg to be sacrificed for dragon cards
     * @return true if the dragon is played.
     */
    public boolean attemptToPlayDragonCard(Card dragon, EggCard egg) {
        if (!checkResources(dragon, egg)) {
            return false;
        }
        spendResources(dragon, egg); //spend resources
        updatePlayerUiStatusBar(); //update ui
        return true;
    }

    /**
     * Player attempts to play the egg.
     *
     * @param egg The egg that the player is playing
     * @return true if the egg is played.
     */
    public boolean attemptToPlayEgg(EggCard egg) {
        if(!checkResources(egg, null)){
            return false;
        }
        spendResources(egg, null);
        updatePlayerUiStatusBar();
        return true;
    }

    /**
     * Player attempts to play spell card targeting target
     * @param spell The spell that the player is playing
     * @param target The target for the spell card.
     * @param destroyIfCast
     * @return true if the spell is played.
     */
    public boolean attemptToPlaySpellCard(SpellCard spell, Card target, boolean destroyIfCast) {
        if(!checkResources(spell, null)){
            return false;
        }
        spell.executeEffect(target);
        target.invalidate();
        spendResources(spell, null);
        if(destroyIfCast) {
            cardsInHand.remove(spell);
            spell.destroyCard();
        }
        updatePlayerUiStatusBar();
        enemy.updatePlayerUiStatusBar();
        return true;
    }

    /**
     * Spend the resources to play the card.
     *
     * @param card The card that the player is playing
     * @param egg  The egg to be sacrificed for dragon cards
     */
    public void spendResources(Card card, EggCard egg) {
        deck.hero.spendAction();
        if (card instanceof SpellCard) {
            currDragonBreathDrawn -= ((SpellCard) card).dragonBreathCost;
        } else if (card instanceof DragonCard && null != egg) {
            egg.destroyCard();
            eggsOnBoard.remove(egg);
        }
    }

    /**
     * reset the actions for every card on the board.
     */
    public void resetActions() {
        deck.hero.resetActions();
        for (int i = 0; i < board.getChildCount(); i++) {
            ViewGroup child = (ViewGroup) board.getChildAt(i);
            assert child != null;
            for (int j = 0; j < child.getChildCount(); j++) {
                View possibleCard = child.getChildAt(j);
                if (possibleCard instanceof CharacterCard) {
                    ((CharacterCard) possibleCard).resetActions();
                    possibleCard.invalidate();
                }
            }
        }
    }

    public void increaseHatch() {
        for (EggCard egg : eggsOnBoard) {
            egg.increaseHatch();
            egg.invalidate();
        }
    }

    /**
     * End the current player's turn and start the enemy's turn
     *
     * @param context game activity
     */
    public void endTurn(GameActivity context) {
        enemy.startTurn(context);
    }

    /**
     * Does all the necessary actions to start a player's turn.
     *
     * @param context Game Activity
     */
    public void startTurn(GameActivity context) {
        drawCard();
        drawDragonBreath();
        resetActions();
        increaseHatch();
        updatePlayerUiStatusBar();
        if (isAi) {
            aiTurn(context);
        }
    }

    /**
     * Run an ai's complete turn.
     *
     * @param context Current Game Activity
     */
    public void aiTurn(GameActivity context) {
        aiPlayCards(context);
        aiAttack(context);
        endTurn(context);
    }

    /**
     * Make attacks for the ai
     *
     * @param context Current Game Activity
     */
    private void aiAttack(GameActivity context) {
        for (DragonCard card : dragonsOnBoard) {
            Iterator<DragonCard> enemyCardIterator = enemy.dragonsOnBoard.iterator();
            while (enemyCardIterator.hasNext()) {
                CharacterCard enemyCard = enemyCardIterator.next();
                if (card.attack > enemyCard.defense) {
                    if (card.attack(enemyCard, false)) {
                        enemyCardIterator.remove();
                    }
                }
            }
            context.attackCard(card, enemy.deck.hero);
        }
        ListIterator<DragonCard> enemyCardIterator = enemy.dragonsOnBoard.listIterator();
        while (enemyCardIterator.hasNext()) {
            DragonCard enemyCard = enemyCardIterator.next();
            if (deck.hero.attack > enemyCard.defense) {
                if (context.attackCard(deck.hero, enemyCard)) {
                    enemyCardIterator.remove();
                }
            }
        }
        while (deck.hero.turns > 0) {
            context.attackCard(deck.hero, enemy.deck.hero);
        }
    }

    /**
     * Plays Egg card
     * @param context Current Game Activity
     * @param egg Card to attempt to play
     * @param cardIterator iterator going through player's hand
     */
    private void aiPlayEggs(GameActivity context, EggCard egg, ListIterator<Card> cardIterator) {
        for (ViewGroup boardPosition : boardPositions) {
            if (boardPosition.getChildCount() < 1) {
                if (context.dropEggCard(egg, boardPosition, this)) {
                    cardIterator.remove();
                    eggsOnBoard.add(egg);
                    GameActivity.setBackground(boardPosition, null);
                    break;
                }
            }
        }
    }

    /**
     * Plays dragon card
     * @param context Current Game Activity
     * @param dragon dragon to attempt to play
     * @param cardIterator iterator going through player's hand
     */
    private void aiPlayDragon(GameActivity context, DragonCard dragon, ListIterator<Card> cardIterator) {
        for (EggCard egg : eggsOnBoard) {
            if (context.dropDragonCard(dragon, egg, this)) {
                cardIterator.remove();
                dragonsOnBoard.add(dragon);
                break;
            }
        }
    }

    /**
     * play cards for the ai
     *
     * @param context Current Game Activity
     */
    private void aiPlayCards(GameActivity context) {
        ListIterator<Card> cardIterator = cardsInHand.listIterator();
        while (cardIterator.hasNext()) {
            Card card = cardIterator.next();
            if (card instanceof DragonCard) {
                aiPlayDragon(context, (DragonCard) card, cardIterator);
            } else if (card instanceof EggCard){
                aiPlayEggs(context, (EggCard) card, cardIterator );
            } else if(card instanceof SpellCard){
                aiPlaySpell(context, (SpellCard) card, cardIterator);
            }
        }
    }

    private void aiPlaySpell(GameActivity context, SpellCard card, ListIterator<Card> cardIterator) {
        switch(card.functionId) {
            case SpellCard.EFFECT_MASS_HEAL:
                if (deck.hero.health < deck.hero.maxHealth / 2
                        && attemptToPlaySpellCard(card, deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                }
                break;
            case SpellCard.EFFECT_MAJOR_HEAL:
                if (deck.hero.health < deck.hero.maxHealth - SpellCard.MAJOR_HEAL_AMOUNT
                        && attemptToPlaySpellCard(card, deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                } else {
                    for (DragonCard dragon : dragonsOnBoard) {
                        if (dragon.health < dragon.health - SpellCard.MAJOR_HEAL_AMOUNT / 2 + 1
                                && attemptToPlaySpellCard(card, dragon, false)) {
                            cardIterator.remove();
                            card.destroyCard();
                        }
                    }
                }
                break;
            case SpellCard.EFFECT_MINOR_HEAL:
                if (deck.hero.health < deck.hero.maxHealth - SpellCard.MINOR_HEAL_AMOUNT
                        && attemptToPlaySpellCard(card, deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                } else {
                    for (DragonCard dragon : dragonsOnBoard) {
                        if (dragon.health < dragon.health - SpellCard.MINOR_HEAL_AMOUNT / 2 + 1
                                && attemptToPlaySpellCard(card, dragon, false)) {
                            cardIterator.remove();
                            card.destroyCard();
                            break;
                        }
                    }
                }
                break;
            case SpellCard.EFFECT_FIREBALL:
                if (enemy.deck.hero.health <= SpellCard.FIREBALL_DAMAGE
                        && attemptToPlaySpellCard(card, enemy.deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                } else {
                    for (DragonCard dragon : enemy.dragonsOnBoard) {
                        if (dragon.health <= SpellCard.FIREBALL_DAMAGE
                                && attemptToPlaySpellCard(card, dragon, false)) {
                            cardIterator.remove();
                            card.destroyCard();
                            break;
                        }
                    }
                }
                break;
            case SpellCard.EFFECT_UNHATCH:
                DragonCard biggestDragon = null;
                int threatLevel = 0;
                for (DragonCard dragon : enemy.dragonsOnBoard) {
                    int currThreat = 0;
                    currThreat += dragon.getHatchCost();
                    currThreat += dragon.attack;
                    currThreat += dragon.defense;
                    currThreat *= dragon.health / dragon.maxHealth;
                    if (currThreat > threatLevel) {
                        threatLevel = currThreat;
                        biggestDragon = dragon;
                    }
                }
                if (null != biggestDragon
                        && attemptToPlaySpellCard(card, biggestDragon, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                }
                break;
            case SpellCard.EFFECT_EGG_SWEEP:
            case SpellCard.EFFECT_DELAY_EGG:
            case SpellCard.EFFECT_DESTROY_EGG:
                EggCard biggestEgg = null;
                for (EggCard egg : enemy.eggsOnBoard) {
                    if ((null == biggestEgg || egg.getHatchTimer() > biggestEgg.getHatchTimer())
                            && attemptToPlaySpellCard(card, egg, false)) {
                        cardIterator.remove();
                        card.destroyCard();
                        break;
                    }
                }
                break;
            case SpellCard.EFFECT_ROCK_THROW:
                if (enemy.deck.hero.health <= SpellCard.ROCK_THROW_DAMAGE
                        && attemptToPlaySpellCard(card, enemy.deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                } else {
                    for (DragonCard dragon : enemy.dragonsOnBoard) {
                        if (dragon.health + dragon.defense <= SpellCard.ROCK_THROW_DAMAGE
                                && attemptToPlaySpellCard(card, dragon, false)) {
                            cardIterator.remove();
                            card.destroyCard();
                            break;
                        }
                    }
                }
                break;
            case SpellCard.EFFECT_POISON_BLAST:
                if (enemy.deck.hero.health <= SpellCard.POISON_BLAST_DAMAGE
                        && attemptToPlaySpellCard(card, enemy.deck.hero, false)) {
                    cardIterator.remove();
                    card.destroyCard();
                } else {
                    for (DragonCard dragon : enemy.dragonsOnBoard) {
                        if (dragon.health <= SpellCard.POISON_BLAST_DAMAGE
                                && attemptToPlaySpellCard(card, dragon, false)) {
                            cardIterator.remove();
                            card.destroyCard();
                            break;
                        }
                    }
                }
                break;
        }
    }

    /**
     * This player lost and the other won. Finish GameActivity and go to stats screen
     * @param context Game Activity.
     */
    public void lose(GameActivity context){
        if(!context.isFinishing() && !context.isDestroyed()) {
            Intent intent = new Intent(context, GameStatsActivity.class);
            //if isAi the player won.
            String playerHeroId, enemyHeroId;
            if (isAi) {
                playerHeroId = enemy.deck.hero.getTag().toString();
                enemyHeroId = deck.hero.getTag().toString();
            } else {
                playerHeroId = deck.hero.getTag().toString();
                enemyHeroId = enemy.deck.hero.getTag().toString();
            }

            intent.putExtra("win", isAi);
            intent.putExtra("playerHeroId", playerHeroId);
            intent.putExtra("enemyHeroId", enemyHeroId);

            context.startActivity(intent);
            context.finish();
        }
    }
}
