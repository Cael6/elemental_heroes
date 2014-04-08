package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cael6.eh.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Object that represents a player's deck
 */
public class Deck {
    private ArrayList<Card> cards;
    public HeroCard hero;
    private Player owner;


    public Deck(Context context, int deck, Player owner){
        this.owner = owner;

        XmlResourceParser xrp = context.getResources().getXml(deck);
        cards = new ArrayList<Card>();
        assert xrp != null;
        try {
            xrp.next();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int eventType = 0;
        try {
            eventType = xrp.getEventType();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        LayoutInflater infl = LayoutInflater.from(context);
        ViewGroup cards = (ViewGroup)infl.inflate(R.layout.cards, owner.hand);
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("dragon")) {
                String cardId = xrp.getAttributeValue(null, "id");
                int quantity = xrp.getAttributeIntValue(null, "quantity", 1);

                DragonCard dragon = (DragonCard)cards.findViewById(
                        context.getResources().getIdentifier(
                                cardId, "id", context.getPackageName())
                );

                for(int i = 0; i < quantity; i++){
                    this.cards.add(new DragonCard(context, dragon, owner));
                }
            }else if(eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("hero")) {
                String cardId = xrp.getAttributeValue(null, "id");

                HeroCard card = (HeroCard)cards.findViewById(
                        context.getResources().getIdentifier(
                                cardId, "id", context.getPackageName())
                );
                hero = new HeroCard(context, card, owner);
            } else if(eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("egg")) {

                int quantity = xrp.getAttributeIntValue(null, "quantity", 1);

                EggCard egg = null;
                if (cards != null) {
                    egg = (EggCard)cards.findViewById(R.id.eggCard);
                }

                for(int i = 0; i < quantity; i++){
                    this.cards.add(new EggCard(context, egg, owner));
                }
            } else if(eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("spell")){
                String cardId = xrp.getAttributeValue(null, "id");
                int quantity = xrp.getAttributeIntValue(null, "quantity", 1);

                SpellCard spell = (SpellCard)cards.findViewById(
                        context.getResources().getIdentifier(
                                cardId, "id", context.getPackageName())
                );

                for(int i = 0; i < quantity; i++){
                    this.cards.add(new SpellCard(context, spell, owner));
                }
            }
            try {
                eventType = xrp.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        shuffleDeck();
    }

    public Card drawCard(){
        if(cards.size() > 0){
            return cards.remove(0);
        }else{
            return null;
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(cards, new Random(System.nanoTime()));
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public Player getOwner() {
        return owner;
    }
}
