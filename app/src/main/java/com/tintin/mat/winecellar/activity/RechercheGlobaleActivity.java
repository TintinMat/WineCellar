package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Millesime;
import com.tintin.mat.winecellar.bo.Region;
import com.tintin.mat.winecellar.dao.BouteilleDao;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class RechercheGlobaleActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.recherche_globale_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_globale);
        setTitle(R.string.toolbar_recherche_globale);
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



    public void lancerRecherche(View v) {

        // récupérer les options
        RadioButton dansLannee = (RadioButton)findViewById(R.id.boireDansLanneeRadioButton);
        RadioButton parCriteres = (RadioButton)findViewById(R.id.rechercheParCriteresRadioButton);
        EditText textToSearch = (EditText)findViewById(R.id.rechercheTexteEditText);
        RadioButton tousChamps = (RadioButton)findViewById(R.id.rechercheTousChampsRadioButton);
        RadioButton certainsChamps = (RadioButton)findViewById(R.id.rechercheCertainsChampsRadioButton);
        CheckBox domaine = (CheckBox)findViewById(R.id.rechercheDomaineCheckBox);
        CheckBox millesime = (CheckBox)findViewById(R.id.rechercheMillesimeCheckBox);
        CheckBox region = (CheckBox)findViewById(R.id.rechercheRegionCheckBox);
        CheckBox appellation = (CheckBox)findViewById(R.id.rechercheAppellationCheckBox);
        CheckBox commentaires = (CheckBox)findViewById(R.id.rechercheCommentairesCheckBox);

        ArrayList<Bouteille> listeBouteillesTrouvees = null;

        BouteilleDao bDao = new BouteilleDao(this, null);

        Bouteille bouteilleTemplate = new Bouteille();
        Appellation appTemplate = new Appellation();
        Region regionTemplate = new Region();
        Millesime millTemplate = new Millesime();

        boolean next = true;
        if (dansLannee.isChecked()){
            listeBouteillesTrouvees  = bDao.findToDrink();
        }else if (parCriteres.isChecked()){
            String text = textToSearch.getText().toString().trim();
            if (text.length() > 0){
                if (tousChamps.isChecked()){
                    bouteilleTemplate.setDomaine(text);
                    regionTemplate.setNom(text);
                    appTemplate.setNom(text);
                    bouteilleTemplate.setCommentaires(text);
                    try{
                        millTemplate.setAnnee(Integer.valueOf(text));
                    }catch(NumberFormatException e){
                        if (BuildConfig.DEBUG){
                            Log.w(TAG, "rechercherBouteille ",e );
                        }
                    }
                    listeBouteillesTrouvees  = bDao.findWhere(bouteilleTemplate, appTemplate, regionTemplate, millTemplate);

                }else if (certainsChamps.isChecked()){
                    boolean unChoix = false;
                    if (domaine.isChecked()){
                        bouteilleTemplate.setDomaine(text);
                        unChoix = true;
                    }
                    if (millesime.isChecked()){
                        try{
                            millTemplate.setAnnee(Integer.valueOf(text));
                        }catch(NumberFormatException e){
                            if (BuildConfig.DEBUG){
                                Log.w(TAG, "rechercherBouteille ",e );
                            }
                        }
                        unChoix = true;
                    }
                    if (region.isChecked()){
                        regionTemplate.setNom(text);
                        unChoix = true;
                    }
                    if (appellation.isChecked()){
                        appTemplate.setNom(text);
                        unChoix = true;
                    }
                    if (commentaires.isChecked()){
                        bouteilleTemplate.setCommentaires(text);
                        unChoix = true;
                    }
                    if (!unChoix){
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_recherche_choix_champs_ko, Toast.LENGTH_LONG);
                        toast.show();
                        next = false;
                    }else{
                        listeBouteillesTrouvees  = bDao.findWhere(bouteilleTemplate, appTemplate, regionTemplate, millTemplate);
                    }
                }else {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.message_recherche_choix_champs_ko, Toast.LENGTH_LONG);
                    toast.show();
                    next = false;
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_recherche_texte_ko, Toast.LENGTH_LONG);
                toast.show();
                next = false;
            }

        }else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_recherche_criteres_ko, Toast.LENGTH_LONG);
            toast.show();
            next = false;
        }

        if (next) {
            if (listeBouteillesTrouvees == null || listeBouteillesTrouvees.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.no_bouteille_recherche, Toast.LENGTH_LONG);
                toast.show();
            } else {
                // on a la liste des id dans listeBouteillesTrouvees
                // passer cette liste à la view
                Intent intent = new Intent(RechercheGlobaleActivity.this, VisualiserRechercheActivity.class);
                //based on item add info to intent
                intent.putExtra("Key", (Serializable) listeBouteillesTrouvees);
                startActivity(intent);
            }
        }

    }


}
