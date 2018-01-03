package com.tintin.mat.winecellar.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Region;
import com.tintin.mat.winecellar.dao.AppellationDao;
import com.tintin.mat.winecellar.dao.PaysDao;
import com.tintin.mat.winecellar.dao.RegionDao;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    private PaysDao paysDao = null;
    private RegionDao regionDao = null;
    private AppellationDao appellationDao = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        setTitle(R.string.toolbar_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // pays

        if (paysDao == null){
            paysDao = new PaysDao(this, null);
        }
        ArrayList<Pays> listePays = paysDao.getAll();
        ArrayAdapter<Pays> adapter = new ArrayAdapter<Pays>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listePays);
        AutoCompleteTextView addPays = (AutoCompleteTextView) findViewById(R.id.ajouterPaysAutoCompleteTextView);
        addPays.setThreshold(1);//will start working from first character
        addPays.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        addPays.setTextColor(Color.RED);


        // region

        if (regionDao == null){
            regionDao = new RegionDao(this, null);
        }
        ArrayList<Region> listeRegions = regionDao.getAll();
        ArrayAdapter<Region> adapterRegion = new ArrayAdapter<Region>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeRegions);
        AutoCompleteTextView addRegions = (AutoCompleteTextView) findViewById(R.id.ajouterRegionAutoCompleteTextView);
        addRegions.setThreshold(1);//will start working from first character
        addRegions.setAdapter(adapterRegion);//setting the adapter data into the AutoCompleteTextView
        addRegions.setTextColor(Color.RED);


        // appellations

        if (appellationDao == null){
            appellationDao = new AppellationDao(this, null);
        }
        ArrayList<Appellation> listeAppellations = appellationDao.getAll();
        ArrayAdapter<Appellation> adapterAppellation = new ArrayAdapter<Appellation>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeAppellations);
        AutoCompleteTextView addAppellations = (AutoCompleteTextView) findViewById(R.id.ajouterAppellationAutoCompleteTextView);
        addAppellations.setThreshold(1);//will start working from first character
        addAppellations.setAdapter(adapterAppellation);//setting the adapter data into the AutoCompleteTextView
        addAppellations.setTextColor(Color.RED);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_options:
                ajouter_donnees();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ajouter_donnees(){

        Pays myPays = null;
        Region myRegion = null;
        Appellation myAppellation = null;
        // verifier si le pays existe
        AutoCompleteTextView addPays = (AutoCompleteTextView) findViewById(R.id.ajouterPaysAutoCompleteTextView);
        if (addPays != null && addPays.getText().length()>0){
            // ok il y a un pays
            // vérifier s'il existe
            if (paysDao == null){
                paysDao = new PaysDao(this, null);
            }
            ArrayList<Pays> listPays = paysDao.getByName(addPays.getText().toString());
            if (listPays != null && listPays.size()>0){
                myPays = listPays.get(0);
            }else {
                myPays = new Pays(addPays.getText().toString());
                long id = paysDao.ajouter(myPays);
                myPays.setId(id);
            }
        }

        // verifier si la region existe
        AutoCompleteTextView addRegion = (AutoCompleteTextView) findViewById(R.id.ajouterRegionAutoCompleteTextView);
        if (addRegion != null && addRegion.getText().length()>0){
            // ok il y a une région
            // vérifier si elle existe
            if (regionDao == null){
                regionDao = new RegionDao(this, null);
            }
            ArrayList<Region> listRegions = regionDao.getByName(addRegion.getText().toString());
            if (listRegions != null && listRegions.size()>0){
                myRegion = listRegions.get(0);
            }else {
                myRegion = new Region();
                myRegion.setNom(addRegion.getText().toString());
                myRegion.setPays(myPays);
                long id = regionDao.ajouter(myRegion);
                myRegion.setId(id);
            }
        }


        // verifier si l'appellation existe
        AutoCompleteTextView addAppellation = (AutoCompleteTextView) findViewById(R.id.ajouterAppellationAutoCompleteTextView);
        if (addAppellation != null && addAppellation.getText().length()>0){
            // ok il y a une région
            // vérifier si elle existe
            if (appellationDao == null){
                appellationDao = new AppellationDao(this, null);
            }
            ArrayList<Appellation> listAppellations = appellationDao.getByName(addAppellation.getText().toString());
            if (listAppellations != null && listAppellations.size()>0){
                myAppellation = listAppellations.get(0);
            }else {
                myAppellation = new Appellation();
                myAppellation.setNom(addAppellation.getText().toString());
                myAppellation.setRegion(myRegion);
                long id = appellationDao.ajouter(myAppellation);
                myAppellation.setId(id);
            }
        }

        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_donnees_ok, Toast.LENGTH_LONG);
        toast.show();
        finish();
    }
}
