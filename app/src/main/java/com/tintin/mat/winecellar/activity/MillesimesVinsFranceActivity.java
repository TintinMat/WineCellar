package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tintin.mat.winecellar.R;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class MillesimesVinsFranceActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_millesimes_vins);
        setTitle(R.string.toolbar_millesimes_vins);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.back_home:
                onBackHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackHome() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }



}
