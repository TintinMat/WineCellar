package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.adapter.BouteilleAdapter;
import com.tintin.mat.winecellar.adapter.BouteillesDegusteesAdapter;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.dao.CaveDao;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

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
        }
        return super.onOptionsItemSelected(item);
    }


    private void afficherBouteillesDegustees(){

        // récupérer les bouteilles
        bouteilleDao = new BouteilleDao(this,null);
        ArrayList<Bouteille> listeBouteilles = bouteilleDao.getAllDegusted();

        ListView listeViewBouteilles = (ListView)findViewById(R.id.listeBouteilles);
        TextView textBouteilleNb = (TextView) findViewById(R.id.textBouteilleNb);

        if (listeBouteilles == null || listeBouteilles.size() == 0){
            textBouteilleNb.setText(R.string.no_drunk_bouteille);
            textBouteilleNb.setVisibility(View.VISIBLE);
            listeViewBouteilles.setVisibility(View.INVISIBLE);
        }else {
            // la listView pour afficher les différentes caves
            BouteillesDegusteesAdapter adapter = new BouteillesDegusteesAdapter(BouteillesDegusteesActivity.this, listeBouteilles);
            listeViewBouteilles.setAdapter(adapter);

            // on rend les items de la liste cliquables
            listeViewBouteilles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    Bouteille b = (Bouteille) adapter.getItemAtPosition(position);

                    Intent intent = new Intent(BouteillesDegusteesActivity.this, ModifierBouteilleActivity.class);
                    //based on item add info to intent
                    intent.putExtra("Key", (Serializable) b);
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
