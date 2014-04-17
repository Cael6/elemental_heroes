package com.cael6.eh;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cael6.eh.cael6.Card;
import com.cael6.eh.cael6.HeroCard;

import java.util.zip.Inflater;


public class GameStatsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_stats);

        boolean win = getIntent().getBooleanExtra("win", true);
        String playerHeroId = getIntent().getStringExtra("playerHeroId");
        String enemyHeroId = getIntent().getStringExtra("enemyHeroId");

        System.out.println(playerHeroId);
        System.out.println(enemyHeroId);

        String winloseString = win ? "You Win!" : "You Lose!";
        ((TextView)findViewById(R.id.winlose)).setText(winloseString);

        LayoutInflater infl = LayoutInflater.from(this);
        ViewGroup cards = (ViewGroup) infl.inflate(R.layout.cards, null);
        HeroCard playerHero = (HeroCard)cards.findViewWithTag(playerHeroId);
        HeroCard enemyHero = (HeroCard)cards.findViewWithTag(enemyHeroId);

        playerHero = new HeroCard(this, playerHero, null);
        enemyHero = new HeroCard(this, enemyHero, null);

        LinearLayout playerLL = ((LinearLayout)findViewById(R.id.player));
        LinearLayout enemyLL = ((LinearLayout)findViewById(R.id.enemy));

        playerHero.setCardForView(Card.SMALL_CARD, playerLL);
        enemyHero.setCardForView(Card.SMALL_CARD, enemyLL);

        playerLL.addView(playerHero);
        enemyLL.addView(enemyHero);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void backToMain(View view) {
        finish();
    }
}
