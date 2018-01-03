package com.tintin.mat.winecellar.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tintin.mat.winecellar.R;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class ModifierCaveActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.modifier_cave_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_cave);
        setTitle(R.string.toolbar_cave_modif);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.ajouter_bouteille:
                //TODO
                return true;
            case R.id.modifier_cave:
                return true;
            case R.id.rechercher_bouteille :
                //TODO
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
