package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.adapter.BouteillesDegusteesAdapter;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.dao.BouteilleDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.sort;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class BouteillesDegusteesActivity extends AppCompatActivity {

    private BouteilleDao bouteilleDao = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.bouteilles_degustees_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouteilles_degustees);
        setTitle(R.string.toolbar_bouteilles_degustees);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onResume(){
        afficherBouteillesDegustees();
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.rechercher_bouteille :
                //TODO
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




    private void afficherBouteillesDegustees(){

        // récupérer les bouteilles
        bouteilleDao = new BouteilleDao(this,null);
        final ArrayList<Bouteille> listeBouteilles = bouteilleDao.getAllDegusted();

        ListView listeViewBouteilles = (ListView)findViewById(R.id.listeBouteilles);
        TextView textBouteilleNb = (TextView) findViewById(R.id.textBouteilleNb);

        if (listeBouteilles == null || listeBouteilles.size() == 0){
            textBouteilleNb.setText(R.string.no_drunk_bouteille);
            textBouteilleNb.setVisibility(View.VISIBLE);
            listeViewBouteilles.setVisibility(View.INVISIBLE);
        }else {
            // on trie la liste
            Collections.sort(listeBouteilles);
            // la listView pour afficher les différentes caves
            BouteillesDegusteesAdapter adapter = new BouteillesDegusteesAdapter(BouteillesDegusteesActivity.this, listeBouteilles);
            listeViewBouteilles.setAdapter(adapter);

            // on rend les items de la liste cliquables
            listeViewBouteilles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    Bouteille b = (Bouteille) adapter.getItemAtPosition(position);

                    Intent intent = new Intent(BouteillesDegusteesActivity.this, AfficherBouteilleDegusteeActivity.class);
                    //based on item add info to intent
                    intent.putExtra("Key", (Serializable) b);
                    intent.putExtra("position", position);
                    intent.putExtra("listeBouteilles", (Serializable) listeBouteilles);
                    startActivity(intent);

                }
            });

            listeViewBouteilles.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Bouteille b = (Bouteille) parent.getItemAtPosition(position);

                    Intent intent = new Intent(BouteillesDegusteesActivity.this, ModifierBouteilleActivity.class);
                    //based on item add info to intent
                    intent.putExtra("Key", (Serializable) b);
                    startActivity(intent);
                    return true;
                }
            });

            textBouteilleNb.setVisibility(View.INVISIBLE);
            listeViewBouteilles.setVisibility(View.VISIBLE);
        }
    }
}
