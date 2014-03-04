package com.cael6.eh.cael6;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cael6.eh.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by cael6 on 17/02/14.
 */
public class Deck {
    private ArrayList<Card> cards;
    public HeroCard hero;


    public Deck(Context context, int deck){
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
        ViewGroup cards = (ViewGroup)infl.inflate(R.layout.cards, null);
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("card")) {
                String cardId = xrp.getAttributeValue(null, "id");
                int quantity = xrp.getAttributeIntValue(null, "quantity", 1);

                Card card = (Card)cards.findViewById(
                        context.getResources().getIdentifier(
                                cardId, "id", context.getPackageName())
                );

                for(int i = 0; i < quantity; i++){
                    this.cards.add(new Card(context, card));
                }
            }else if(eventType == XmlPullParser.START_TAG
                    && xrp.getName().equalsIgnoreCase("hero")) {
                String cardId = xrp.getAttributeValue(null, "id");

                HeroCard card = (HeroCard)cards.findViewById(
                        context.getResources().getIdentifier(
                                cardId, "id", context.getPackageName())
                );
                hero = new HeroCard(context, card);
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
            Card card = cards.remove(0);
            return cards.remove(0);
        }else{
            return null;
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(cards, new Random(System.nanoTime()));
    }
}
