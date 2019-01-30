package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.adapter.BouteilleSlideAdapter;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.dao.BouteilleDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class AfficherBouteilleActivity extends AppCompatActivity {

    private Bouteille bouteille = null;
    private BouteilleDao bouteilleDao = null;
    private ArrayList<Bouteille> listeBouteilles = null;

    private ViewPager viewPager;
    private BouteilleSlideAdapter slideAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.afficher_bouteille_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_bouteille);
        setTitle(R.string.toolbar_bouteille_afficher);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("Key") != null) {
            Bouteille b = (Bouteille) getIntent().getExtras().get("Key");
            if (bouteilleDao == null){
                bouteilleDao = new BouteilleDao(this,null);
            }
            bouteille = bouteilleDao.getWithAllDependencies(b);
        }
        // récupérer la liste des bouteilles
        if (getIntent().getExtras() != null && getIntent().getExtras().get("listeBouteilles") != null) {
            listeBouteilles = (ArrayList<Bouteille>)  getIntent().getExtras().get("listeBouteilles");
        }
        // récupérer la position de la bouteille dans la liste
        int position = 0;
        if (getIntent().getExtras() != null && getIntent().getExtras().get("position") != null) {
            position = (int) getIntent().getExtras().get("position");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        slideAdapter = new BouteilleSlideAdapter(this, listeBouteilles);
        viewPager.setAdapter(slideAdapter);
        viewPager.setCurrentItem(position);

    }

    @Override
    public void onResume(){
        updateBouteille();
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.to_modif_bouteille:
                if (bouteilleDao == null){
                    bouteilleDao = new BouteilleDao(this,null);
                }
                bouteille = bouteilleDao.getWithAllDependencies(listeBouteilles.get(viewPager.getCurrentItem()));
                Intent intent = new Intent(AfficherBouteilleActivity.this, ModifierBouteilleActivity.class);
                intent.putExtra("Key", (Serializable) bouteille);
                startActivity(intent);
                return true;
            case R.id.to_open_bouteille:
                if (bouteilleDao == null){
                    bouteilleDao = new BouteilleDao(this,null);
                }
                bouteille = bouteilleDao.getWithAllDependencies(listeBouteilles.get(viewPager.getCurrentItem()));
                degusterBouteille(bouteille);
                return true;
            case R.id.del_bouteille:
                if (bouteilleDao == null){
                    bouteilleDao = new BouteilleDao(this,null);
                }
                bouteille = bouteilleDao.getWithAllDependencies(listeBouteilles.get(viewPager.getCurrentItem()));
                supprimerBouteille(bouteille);
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

    /* ============================================================================= */


    private void updateBouteille(){
        // met à jour la bouteille dans la liste des bouteilles
        // 1. on reprend les infos mises à jour de la base
        if (bouteilleDao == null){
            bouteilleDao = new BouteilleDao(this,null);
        }
        // garder l'index de la bouteille si jamais on la supprime
        //int indexOfBottle =listeBouteilles.indexOf(bouteille);
        bouteille = bouteilleDao.getWithAllDependencies(listeBouteilles.get(viewPager.getCurrentItem()));
        if (bouteille == null){
            // on supprime la bouteille de la liste
            listeBouteilles.remove(viewPager.getCurrentItem());
        }else{
            listeBouteilles.set(viewPager.getCurrentItem(), bouteille);
           /* // 2. on parcourt la liste pour mettre à jour la bouteille qui nous interesse
            for (Bouteille b : listeBouteilles) {
                if (b.getId()==bouteille.getId()){
                    // on remplace
                    listeBouteilles.set(listeBouteilles.indexOf(b), bouteille);
                    break;
                }
            }*/
        }
        // on le dit à la view
        slideAdapter.updateListeBouteilles(listeBouteilles);
        slideAdapter.notifyDataSetChanged();
    }


    /* ============================================================================= */
    /* méthode pour supprimer la bouteille  */

    public void supprimerBouteille(Bouteille b){
        try{
            int nb = bouteilleDao.supprimer(b);
            if (nb > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_bouteille_ok, Toast.LENGTH_LONG);
                toast.show();
            }
            finish();
        }catch (Exception ex){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "supprimerBouteille ",ex );
            }
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_bouteille_ko, Toast.LENGTH_LONG);
            toast.show();
        }
    }



    /* ============================================================================= */



    /* ============================================================================= */
    /* méthode pour supprimer la bouteille  */


    public void degusterBouteille(Bouteille bouteille) {

        Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // don't forget this if date is arbitrary e.g. 01-01-2014
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); // 17
        int monthOfYear = cal.get(Calendar.MONTH); // 5
        int year = cal.get(Calendar.YEAR); // 2016

        String monthString = ""+ (monthOfYear + 1);
        String dayString = ""+ dayOfMonth;
        if (monthOfYear + 1 <10 ){
            monthString = "0"+(monthOfYear+1);
        }
        if (dayOfMonth <10 ){
            dayString = "0"+dayOfMonth;
        }
        int dateDegustationIntFormat = new Integer("" + year + monthString + dayString);

        bouteille.setAnneeDegustation(dateDegustationIntFormat);
        BouteilleDao bouteilleDao = new BouteilleDao(this,null);
        bouteilleDao.modifierAnneeDegustation(bouteille);

        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_date_degustation_ok, Toast.LENGTH_LONG);
        toast.show();

        finish();
    }

    /* ============================================================================= */
}
