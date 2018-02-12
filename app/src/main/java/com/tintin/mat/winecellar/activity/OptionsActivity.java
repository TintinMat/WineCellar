package com.tintin.mat.winecellar.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Preferences;
import com.tintin.mat.winecellar.bo.Region;
import com.tintin.mat.winecellar.dao.AppellationDao;
import com.tintin.mat.winecellar.dao.PaysDao;
import com.tintin.mat.winecellar.dao.PreferencesDao;
import com.tintin.mat.winecellar.dao.RegionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class OptionsActivity extends AppCompatActivity {

    private PaysDao paysDao = null;
    private RegionDao regionDao = null;
    private AppellationDao appellationDao = null;
    private PreferencesDao pDao = null;

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


        // positionner correctement les switch en fonction des choix utilisateurs
        Switch prefPhoto = (Switch) findViewById(R.id.sauvegardePhotoSwitch);
        final Switch prefCloud = (Switch) findViewById(R.id.sauvegardeCloudSwitch);
        EditText prefLogin = (EditText) findViewById(R.id.loginConnexionEditText);

        if (pDao == null) {
            pDao = new PreferencesDao(this, null);
        }
        List<Preferences> pList = pDao.getByCle(Preferences.SAUVEGARDE_CLOUD);
        if (pList != null && !pList.isEmpty()) {
            Preferences p = pList.get(0);
            switch (p.getValeur()){
                case "Y" :
                    prefCloud.setChecked(true);
                    break;
                case "N" :
                    prefCloud.setChecked(false);
                    prefPhoto.setEnabled(false);
                    prefLogin.setEnabled(false);
                    break;
            }
        }
        pList = pDao.getByCle(Preferences.SAUVEGARDE_PHOTOS);
        if (pList != null && !pList.isEmpty()) {
            Preferences p = pList.get(0);
            switch (p.getValeur()){
                case "Y" :
                    prefPhoto.setChecked(true);
                    break;
                case "N" :
                    prefPhoto.setChecked(false);
                    break;
            }
        }
        pList = pDao.getByCle(Preferences.LOGIN_CONNEXION);
        if (pList != null && !pList.isEmpty()) {
            Preferences p = pList.get(0);
            prefLogin.setText(p.getValeur());
        }

        prefCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch prefPhoto = (Switch) findViewById(R.id.sauvegardePhotoSwitch);
                EditText prefLogin = (EditText) findViewById(R.id.loginConnexionEditText);
                if (prefCloud.isChecked()){
                    prefPhoto.setEnabled(true);
                    prefLogin.setEnabled(true);
                }else{
                    prefPhoto.setEnabled(false);
                    prefLogin.setEnabled(false);
                }
            }
        });
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

        boolean ok = false;

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
            ok = true;
        }

        // verifier si la region existe
        AutoCompleteTextView addRegion = (AutoCompleteTextView) findViewById(R.id.ajouterRegionAutoCompleteTextView);
        if (addRegion != null && addRegion.getText().length()>0){
            if (!ok){
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_region_ko, Toast.LENGTH_LONG);
                toast.show();
            }else{
                // ok il y a une région et un pays a été renseigné
                // vérifier si la région existe
                if (regionDao == null){
                    regionDao = new RegionDao(this, null);
                }
                ArrayList<Region> listRegions = regionDao.getByName(addRegion.getText().toString());
                if (listRegions != null && listRegions.size()>0){
                    myRegion = listRegions.get(0);
                }else {
                    if (myPays != null && myPays.getId() > 0) {
                        myRegion = new Region();
                        myRegion.setNom(addRegion.getText().toString());
                        myRegion.setPays(myPays);
                        long id = regionDao.ajouter(myRegion);
                        myRegion.setId(id);
                    } /*else {
                        ok = false;
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_region_ko, Toast.LENGTH_LONG);
                        toast.show();
                    }*/
                }
            }
        }


        // verifier si l'appellation existe
        AutoCompleteTextView addAppellation = (AutoCompleteTextView) findViewById(R.id.ajouterAppellationAutoCompleteTextView);
        if (addAppellation != null && addAppellation.getText().length()>0){
            if (!ok){
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_appellation_ko, Toast.LENGTH_LONG);
                toast.show();
            }else{
                // ok il y a une appellation et une région (et un pays)
                // vérifier si elle existe
                if (appellationDao == null){
                    appellationDao = new AppellationDao(this, null);
                }
                ArrayList<Appellation> listAppellations = appellationDao.getByName(addAppellation.getText().toString());
                if (listAppellations != null && listAppellations.size()>0){
                    myAppellation = listAppellations.get(0);
                }else {
                    if (myRegion != null && myRegion.getId() > 0) {
                        myAppellation = new Appellation();
                        myAppellation.setNom(addAppellation.getText().toString());
                        myAppellation.setRegion(myRegion);
                        long id = appellationDao.ajouter(myAppellation);
                        myAppellation.setId(id);
                    }/* else {
                        ok = false;
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_appellation_ko, Toast.LENGTH_LONG);
                        toast.show();
                    }*/
                }
            }
        }

        if (ok) {
            // passons maintenant aux preferences
            Switch prefCloud = (Switch) findViewById(R.id.sauvegardeCloudSwitch);
            Switch prefPhoto = (Switch) findViewById(R.id.sauvegardePhotoSwitch);
            EditText prefLogin = (EditText) findViewById(R.id.loginConnexionEditText);

            if (pDao == null) {
                pDao = new PreferencesDao(this, null);
            }
            Preferences pref1 = null;
            Preferences pref2 = null;
            Preferences pref3 = null;
            if (prefCloud.isChecked()) {
                pref1 = new Preferences(Preferences.SAUVEGARDE_CLOUD, Preferences.YES);
            } else {
                pref1 = new Preferences(Preferences.SAUVEGARDE_CLOUD, Preferences.NO);
            }
            if (prefPhoto.isChecked()) {
                pref2 = new Preferences(Preferences.SAUVEGARDE_PHOTOS, Preferences.YES);
            } else {
                pref2 = new Preferences(Preferences.SAUVEGARDE_PHOTOS, Preferences.NO);
            }
            pref3 = new Preferences(Preferences.LOGIN_CONNEXION, prefLogin.getText().toString());

            pDao.ajouterOuModifier(pref1);
            pDao.ajouterOuModifier(pref2);
            pDao.ajouterOuModifier(pref3);

            if (ok) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_ajouter_donnees_ok, Toast.LENGTH_LONG);
                toast.show();
            }
            finish();
        }
    }
}
