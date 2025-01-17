package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.adapter.CaveAdapter;
import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Preferences;
import com.tintin.mat.winecellar.bo.Region;
import com.tintin.mat.winecellar.dao.AppellationDao;
import com.tintin.mat.winecellar.dao.CaveDao;
import com.tintin.mat.winecellar.dao.DatabaseHandler;
import com.tintin.mat.winecellar.dao.PaysDao;
import com.tintin.mat.winecellar.dao.PreferencesDao;
import com.tintin.mat.winecellar.dao.RegionDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView text;
    private AppellationDao appellationDao;
    private RegionDao regionDao;
    private PaysDao paysDao;
    private CaveDao caveDao;

    protected DatabaseHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new DatabaseHandler(this);
        mHandler.getWritableDatabase();

        appellationDao = new AppellationDao(this, mHandler);
        regionDao = new RegionDao(this, mHandler);
        paysDao = new PaysDao(this, mHandler);
        caveDao = new CaveDao(this, mHandler);

        if (mHandler.getMode() == DatabaseHandler.ON_CREATE /*|| mHandler.getMode() == DatabaseHandler.ON_UPDATE*/){
            // ne pas remplir la bdd si on est en update vu qu'on ne vide plus la table
            populateBdd();
        }
        resetPreferences();

        setContentView(R.layout.activity_main);
        // modifier le titre de l'action bar
        setTitle(R.string.toolbar_home);

        /* pour le menu slide */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_bottle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                ajouterBouteille();
            }
        });

        FloatingActionButton fab_caveplus = (FloatingActionButton) findViewById(R.id.fab_add_cellar);
        fab_caveplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                ajouterCave();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onResume(){
        afficherInfosCave();
        super.onResume();

    }

    /* ----------------------------------------- */
    /* pour le slide menu */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.ajouter_cave){
            ajouterCave();
        }else if (id == R.id.ajouter_bouteille){
            ajouterBouteille();
        //}else if (id == R.id.modifier_cave){
           // modifierCave();
        }else if (id == R.id.rechercher_bouteille){
            rechercheGlobale();
        }else if (id == R.id.bouteilles_degustees){
            bouteillesDegustees();
        }else if (id == R.id.nav_map){
            carteVinsFrance();
        }else if (id == R.id.nav_millesimes){
            millesimesVinsFrance();
        }else if (id == R.id.nav_manage){
            options();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* ----------------------------------------- */



    private void afficherInfosCave(){
        // vérifier s'il y a déjà une cave
        ArrayList<Cave> listCaves = caveDao.getAll();
        TextView textCaveNb = (TextView) findViewById(R.id.textCaveNb);
        if (listCaves == null || listCaves.size() == 0){
            textCaveNb.setText(R.string.no_cave);
            findViewById(R.id.imageAddCave).setVisibility(View.VISIBLE);
            findViewById(R.id.listeCaves).setVisibility(View.INVISIBLE);
            findViewById(R.id.fab_add_bottle).setVisibility(View.INVISIBLE);
            findViewById(R.id.fab_add_cellar).setVisibility(View.VISIBLE);
        }else {
            // on change le texte ...
            textCaveNb.setText(R.string.choose_cave);
            // ... et on enlève le bouton pour ajouter une cave
            findViewById(R.id.imageAddCave).setVisibility(View.INVISIBLE);

            // la listView pour afficher les différentes caves
            ListView listeViewCaves = (ListView)findViewById(R.id.listeCaves);
            CaveAdapter adapter = new CaveAdapter(MainActivity.this, listCaves);
            listeViewCaves.setAdapter(adapter);

            // on rend les items de la liste cliquables
            listeViewCaves.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?>adapter,View v, int position, long id){
                    try {
                        Cave cave = (Cave) adapter.getItemAtPosition(position);

                        Intent intent = new Intent(MainActivity.this, VisualiserCaveActivity.class);
                        //based on item add info to intent
                        intent.putExtra("Key", (Serializable) cave);
                        startActivity(intent);
                    }catch(Exception e){
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "isteViewCaves.setOnItemClickListener ",e );
                        }
                    }
                }
            });

            // http://tutos-android-france.com/listview-afficher-une-liste-delements/
            findViewById(R.id.listeCaves).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_add_bottle).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_add_cellar).setVisibility(View.INVISIBLE);
        }

    }


    /*
    * lancée quand le bouton/image id/imageAddCave est cliquée*/
    public void addNewCave(View view){
        //getLayoutInflater().inflate(R.layout.content_main, null);
        ajouterCave();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void ajouterCave(){
        Intent appel = new Intent(MainActivity.this, AjouterCaveActivity.class);
        startActivity(appel);
    }

    private void ajouterBouteille(){
        Intent appel = new Intent(MainActivity.this, AjouterBouteilleActivity.class);
        startActivity(appel);
    }

    private void bouteillesDegustees(){
        Intent appel = new Intent(MainActivity.this, BouteillesDegusteesActivity.class);
        startActivity(appel);
    }

    private void carteVinsFrance(){
        Intent appel = new Intent(MainActivity.this, CarteVinsFranceActivity.class);
        startActivity(appel);
    }

    private void millesimesVinsFrance(){
        Intent appel = new Intent(MainActivity.this, MillesimesVinsFranceActivity.class);
        startActivity(appel);
    }

    private void options(){
        Intent appel = new Intent(MainActivity.this, OptionsActivity.class);
        startActivity(appel);
    }

    private void rechercheGlobale(){
        Intent appel = new Intent(MainActivity.this, RechercheGlobaleActivity.class);
        startActivity(appel);
    }


    private void resetPreferences(){
        PreferencesDao pDao = new PreferencesDao(this, mHandler);
        Preferences sauvegardeCloupdPref = null;
        List<Preferences> pp = pDao.getByCle(Preferences.SAUVEGARDE_CLOUD);
        if (pp == null || pp.isEmpty()){
            sauvegardeCloupdPref = new Preferences(Preferences.SAUVEGARDE_CLOUD, Preferences.YES);
            pDao.ajouter(sauvegardeCloupdPref);
        }
        pp = pDao.getByCle(Preferences.SAUVEGARDE_PHOTOS);
        if (pp == null || pp.isEmpty()){
            sauvegardeCloupdPref = new Preferences(Preferences.SAUVEGARDE_PHOTOS, Preferences.NO);
            pDao.ajouter(sauvegardeCloupdPref);
        }
        pp = pDao.getByCle(Preferences.LOGIN_CONNEXION);
        if (pp == null || pp.isEmpty()){
            sauvegardeCloupdPref = new Preferences(Preferences.LOGIN_CONNEXION, "");
            pDao.ajouter(sauvegardeCloupdPref);
        }

    }

    private void populateBdd(){
        Pays france = new Pays("France");
        long id = paysDao.ajouter(france);
        france.setId(id);
        Pays italie = new Pays("Italie");
        id = paysDao.ajouter(italie);
        italie.setId(id);

        // régions
        Region alsace = new Region();
        alsace.setNom("Alsace");
        alsace.setPays(france);
        id = regionDao.ajouter(alsace);
        alsace.setId(id);
        Region beaujolais = new Region();
        beaujolais.setNom("Beaujolais");
        beaujolais.setPays(france);
        id = regionDao.ajouter(beaujolais);
        beaujolais.setId(id);
        Region bordeaux = new Region();
        bordeaux.setNom("Bordeaux");
        bordeaux.setPays(france);
        id = regionDao.ajouter(bordeaux);
        bordeaux.setId(id);
        Region bourgogne = new Region();
        bourgogne.setNom("Bourgogne");
        bourgogne.setPays(france);
        id = regionDao.ajouter(bourgogne);
        bourgogne.setId(id);
        Region bugey = new Region();
        bugey.setNom("Bugey");
        bugey.setPays(france);
        id = regionDao.ajouter(bugey);
        bugey.setId(id);
        Region champagne = new Region();
        champagne.setNom("Champagne");
        champagne.setPays(france);
        id = regionDao.ajouter(champagne);
        champagne.setId(id);
        Region corse = new Region();
        corse.setNom("Corse");
        corse.setPays(france);
        id = regionDao.ajouter(corse);
        corse.setId(id);
        Region jura = new Region();
        jura.setNom("Jura");
        jura.setPays(france);
        id = regionDao.ajouter(jura);
        jura.setId(id);
        Region languedoc = new Region();
        languedoc.setNom("Languedoc");
        languedoc.setPays(france);
        id = regionDao.ajouter(languedoc);
        languedoc.setId(id);
        Region languedocR = new Region();
        languedocR.setNom("Languedoc-Roussilon");
        languedocR.setPays(france);
        id = regionDao.ajouter(languedocR);
        languedocR.setId(id);
        Region lorraine = new Region();
        lorraine.setNom("Lorraine");
        lorraine.setPays(france);
        id = regionDao.ajouter(lorraine);
        lorraine.setId(id);
        Region lyonnais = new Region();
        lyonnais.setNom("Lyonnais");
        lyonnais.setPays(france);
        id = regionDao.ajouter(lyonnais);
        lyonnais.setId(id);
        Region provence = new Region();
        provence.setNom("Provence");
        provence.setPays(france);
        id = regionDao.ajouter(provence);
        provence.setId(id);
        Region roussilon = new Region();
        roussilon.setNom("Roussilon");
        roussilon.setPays(france);
        id = regionDao.ajouter(roussilon);
        roussilon.setId(id);
        Region savoie = new Region();
        savoie.setNom("Savoie");
        savoie.setPays(france);
        id = regionDao.ajouter(savoie);
        savoie.setId(id);
        Region sudouest = new Region();
        sudouest.setNom("Sud-Ouest");
        sudouest.setPays(france);
        id = regionDao.ajouter(sudouest);
        sudouest.setId(id);
        Region valleeloire = new Region();
        valleeloire.setNom("Vallée de la Loire");
        valleeloire.setPays(france);
        id = regionDao.ajouter(valleeloire);
        valleeloire.setId(id);
        Region valleerhone = new Region();
        valleerhone.setNom("Vallée du Rhone");
        valleerhone.setPays(france);
        id = regionDao.ajouter(valleerhone);
        valleerhone.setId(id);

        //appellation
        Appellation aalsace = new Appellation();
        aalsace.setNom("Alsace");
        aalsace.setRegion(alsace);
        id = appellationDao.ajouter(aalsace);
        aalsace.setId(id);
        Appellation alsaceGdCru = new Appellation();
        alsaceGdCru.setNom("Alsace Grand Cru");
        alsaceGdCru.setRegion(alsace);
        id = appellationDao.ajouter(alsaceGdCru);
        alsaceGdCru.setId(id);
        Appellation cremantDalsace = new Appellation();
        cremantDalsace.setNom("Crémant d'Alsace");
        cremantDalsace.setRegion(alsace);
        id = appellationDao.ajouter(cremantDalsace);
        cremantDalsace.setId(id);

        Appellation abeaujolais = new Appellation();
        abeaujolais.setNom("Beaujolais");
        abeaujolais.setRegion(beaujolais);
        id = appellationDao.ajouter(abeaujolais);
        abeaujolais.setId(id);
        Appellation beaujolaisV = new Appellation();
        beaujolaisV.setNom("Beaujolais Villages");
        beaujolaisV.setRegion(beaujolais);
        id = appellationDao.ajouter(beaujolaisV);
        beaujolaisV.setId(id);
        Appellation brouilly = new Appellation();
        brouilly.setNom("Brouilly");
        brouilly.setRegion(beaujolais);
        id = appellationDao.ajouter(brouilly);
        brouilly.setId(id);
        Appellation chenas = new Appellation();
        chenas.setNom("Chénas");
        chenas.setRegion(beaujolais);
        id = appellationDao.ajouter(chenas);
        chenas.setId(id);
        Appellation chiroubles = new Appellation();
        chiroubles.setNom("Chiroubles");
        chiroubles.setRegion(beaujolais);
        id = appellationDao.ajouter(chiroubles);
        chiroubles.setId(id);
        Appellation coteBrouilly = new Appellation();
        coteBrouilly.setNom("Côte-De-Brouilly");
        coteBrouilly.setRegion(beaujolais);
        id = appellationDao.ajouter(coteBrouilly);
        coteBrouilly.setId(id);
        Appellation fleury = new Appellation();
        fleury.setNom("Fleury");
        fleury.setRegion(beaujolais);
        id = appellationDao.ajouter(fleury);
        fleury.setId(id);
        Appellation julienas = new Appellation();
        julienas.setNom("Julienas");
        julienas.setRegion(beaujolais);
        id = appellationDao.ajouter(julienas);
        julienas.setId(id);
        Appellation morgon = new Appellation();
        morgon.setNom("Morgon");
        morgon.setRegion(beaujolais);
        id = appellationDao.ajouter(morgon);
        morgon.setId(id);
        Appellation moulin = new Appellation();
        moulin.setNom("Moulin");
        moulin.setRegion(beaujolais);
        id = appellationDao.ajouter(moulin);
        moulin.setId(id);
        Appellation regnie = new Appellation();
        regnie.setNom("Regnie");
        regnie.setRegion(beaujolais);
        id = appellationDao.ajouter(regnie);
        regnie.setId(id);
        Appellation stamour = new Appellation();
        stamour.setNom("St Amour");
        stamour.setRegion(beaujolais);
        id = appellationDao.ajouter(stamour);
        stamour.setId(id);

        Appellation barsac = new Appellation();
        barsac.setNom("Barsac");
        barsac.setRegion(bordeaux);
        id = appellationDao.ajouter(barsac);
        barsac.setId(id);
        Appellation blaye = new Appellation();
        blaye.setNom("Blaye");
        blaye.setRegion(bordeaux);
        id = appellationDao.ajouter(blaye);
        blaye.setId(id);
        Appellation abordeaux = new Appellation();
        abordeaux.setNom("Bordeaux");
        abordeaux.setRegion(bordeaux);
        id = appellationDao.ajouter(abordeaux);
        abordeaux.setId(id);
        Appellation bordeauxsup = new Appellation();
        bordeauxsup.setNom("Bordeaux Supérieur");
        bordeauxsup.setRegion(bordeaux);
        id = appellationDao.ajouter(bordeauxsup);
        bordeauxsup.setId(id);
        Appellation cadillac = new Appellation();
        cadillac.setNom("Cadillac");
        cadillac.setRegion(bordeaux);
        id = appellationDao.ajouter(cadillac);
        cadillac.setId(id);
        Appellation canon = new Appellation();
        canon.setNom("Canon-Fronsac");
        canon.setRegion(bordeaux);
        id = appellationDao.ajouter(canon);
        canon.setId(id);
        Appellation cerons = new Appellation();
        cerons.setNom("Cérons");
        cerons.setRegion(bordeaux);
        id = appellationDao.ajouter(cerons);
        cerons.setId(id);
        Appellation coteblaye = new Appellation();
        coteblaye.setNom("Côte-de-Blaye");
        coteblaye.setRegion(bordeaux);
        id = appellationDao.ajouter(coteblaye);
        coteblaye.setId(id);
        Appellation coteBx = new Appellation();
        coteBx.setNom("Côte-de-Bordeaux");
        coteBx.setRegion(bordeaux);
        id = appellationDao.ajouter(coteBx);
        coteBx.setId(id);
        Appellation coteBxStMacaire = new Appellation();
        coteBxStMacaire.setNom("Côte-de-Bordeaux-Saint-Macaire");
        coteBxStMacaire.setRegion(bordeaux);
        id = appellationDao.ajouter(coteBxStMacaire);
        coteBxStMacaire.setId(id);
        Appellation coteBourg = new Appellation();
        coteBourg.setNom("Côte-De-Bourg");
        coteBourg.setRegion(bordeaux);
        id = appellationDao.ajouter(coteBourg);
        coteBourg.setId(id);
        Appellation cremantBx = new Appellation();
        cremantBx.setNom("Crémant De Bordeaux");
        cremantBx.setRegion(bordeaux);
        id = appellationDao.ajouter(cremantBx);
        cremantBx.setId(id);
        Appellation entre2mers = new Appellation();
        entre2mers.setNom("Entre-Deux-Mers");
        entre2mers.setRegion(bordeaux);
        id = appellationDao.ajouter(entre2mers);
        entre2mers.setId(id);
        Appellation fronsac = new Appellation();
        fronsac.setNom("Fronsac");
        fronsac.setRegion(bordeaux);
        id = appellationDao.ajouter(fronsac);
        fronsac.setId(id);
        Appellation graves = new Appellation();
        graves.setNom("Graves");
        graves.setRegion(bordeaux);
        id = appellationDao.ajouter(graves);
        graves.setId(id);
        Appellation gravesDeVayre = new Appellation();
        gravesDeVayre.setNom("Graves-De-Vayre");
        gravesDeVayre.setRegion(bordeaux);
        id = appellationDao.ajouter(gravesDeVayre);
        gravesDeVayre.setId(id);
        Appellation gravesSup = new Appellation();
        gravesSup.setNom("Graves Supérieures");
        gravesSup.setRegion(bordeaux);
        id = appellationDao.ajouter(gravesSup);
        gravesSup.setId(id);
        Appellation Htmedoc = new Appellation();
        Htmedoc.setNom("Haut-Médoc");
        Htmedoc.setRegion(bordeaux);
        id = appellationDao.ajouter(Htmedoc);
        Htmedoc.setId(id);
        Appellation lalande = new Appellation();
        lalande.setNom("Lalande-de-Pomerol");
        lalande.setRegion(bordeaux);
        id = appellationDao.ajouter(lalande);
        lalande.setId(id);
        Appellation listrac = new Appellation();
        listrac.setNom("Listrac-Médoc");
        listrac.setRegion(bordeaux);
        id = appellationDao.ajouter(listrac);
        listrac.setId(id);
        Appellation loupiac = new Appellation();
        loupiac.setNom("loupiac");
        loupiac.setRegion(bordeaux);
        id = appellationDao.ajouter(loupiac);
        loupiac.setId(id);
        Appellation lussac = new Appellation();
        lussac.setNom("Lussac-St-Emilion");
        lussac.setRegion(bordeaux);
        id = appellationDao.ajouter(lussac);
        lussac.setId(id);
        Appellation margaux = new Appellation();
        margaux.setNom("Margaux");
        margaux.setRegion(bordeaux);
        id = appellationDao.ajouter(margaux);
        margaux.setId(id);
        Appellation medoc = new Appellation();
        medoc.setNom("Médoc");
        medoc.setRegion(bordeaux);
        id = appellationDao.ajouter(medoc);
        medoc.setId(id);
        Appellation montagne = new Appellation();
        montagne.setNom("Montagne-Saint-Emilion");
        montagne.setRegion(bordeaux);
        id = appellationDao.ajouter(montagne);
        montagne.setId(id);
        Appellation moulis = new Appellation();
        moulis.setNom("Moulis");
        moulis.setRegion(bordeaux);
        id = appellationDao.ajouter(moulis);
        moulis.setId(id);
        Appellation neac = new Appellation();
        neac.setNom("Néac");
        neac.setRegion(bordeaux);
        id = appellationDao.ajouter(neac);
        neac.setId(id);
        Appellation pauillac = new Appellation();
        pauillac.setNom("Pauillac");
        pauillac.setRegion(bordeaux);
        id = appellationDao.ajouter(pauillac);
        pauillac.setId(id);
        Appellation pessac = new Appellation();
        pessac.setNom("Pessac-Léognan");
        pessac.setRegion(bordeaux);
        id = appellationDao.ajouter(pessac);
        pessac.setId(id);
        Appellation pomerol = new Appellation();
        pomerol.setNom("Pomerol");
        pomerol.setRegion(bordeaux);
        id = appellationDao.ajouter(pomerol);
        pomerol.setId(id);
        Appellation premCoteBx = new Appellation();
        premCoteBx.setNom("Premières-Côtes-de-Bordeaux");
        premCoteBx.setRegion(bordeaux);
        id = appellationDao.ajouter(premCoteBx);
        premCoteBx.setId(id);
        Appellation puisseguin = new Appellation();
        puisseguin.setNom("Puisseguin-Saint-Emilion");
        puisseguin.setRegion(bordeaux);
        id = appellationDao.ajouter(puisseguin);
        puisseguin.setId(id);
        Appellation stEmilion = new Appellation();
        stEmilion.setNom("Saint-Emilion");
        stEmilion.setRegion(bordeaux);
        id = appellationDao.ajouter(stEmilion);
        stEmilion.setId(id);
        Appellation stEmilionGdCru = new Appellation();
        stEmilionGdCru.setNom("Saint-Emilion Grand Cru");
        stEmilionGdCru.setRegion(bordeaux);
        id = appellationDao.ajouter(stEmilionGdCru);
        stEmilionGdCru.setId(id);
        Appellation stEstephe = new Appellation();
        stEstephe.setNom("Saint-Estèphe");
        stEstephe.setRegion(bordeaux);
        id = appellationDao.ajouter(stEstephe);
        stEstephe.setId(id);
        Appellation stG = new Appellation();
        stG.setNom("Saint-Georges-Saint-Emilion");
        stG.setRegion(bordeaux);
        id = appellationDao.ajouter(stG);
        stG.setId(id);
        Appellation stJulien = new Appellation();
        stJulien.setNom("Saint-Julien");
        stJulien.setRegion(bordeaux);
        id = appellationDao.ajouter(stJulien);
        stJulien.setId(id);
        Appellation stCroix = new Appellation();
        stCroix.setNom("Sainte-Croix-du-Mont");
        stCroix.setRegion(bordeaux);
        id = appellationDao.ajouter(stCroix);
        stCroix.setId(id);
        Appellation steFoy = new Appellation();
        steFoy.setNom("Sainte-Foy-Bordeaux");
        steFoy.setRegion(bordeaux);
        id = appellationDao.ajouter(steFoy);
        steFoy.setId(id);
        Appellation sauternes = new Appellation();
        sauternes.setNom("Sauternes");
        sauternes.setRegion(bordeaux);
        id = appellationDao.ajouter(sauternes);
        sauternes.setId(id);


        Appellation aloxe = new Appellation();
        aloxe.setNom("Aloxe-Corton");
        aloxe.setRegion(bourgogne);
        id = appellationDao.ajouter(aloxe);
        aloxe.setId(id);
        Appellation auxey = new Appellation();
        auxey.setNom("Auxey-Duresses");
        auxey.setRegion(bourgogne);
        id = appellationDao.ajouter(auxey);
        auxey.setId(id);
        Appellation batard = new Appellation();
        batard.setNom("Bâtard-Montrachet");
        batard.setRegion(bourgogne);
        id = appellationDao.ajouter(batard);
        batard.setId(id);
        Appellation beaune = new Appellation();
        beaune.setNom("Beaune");
        beaune.setRegion(bourgogne);
        id = appellationDao.ajouter(beaune);
        beaune.setId(id);
        Appellation bienvenue = new Appellation();
        bienvenue.setNom("Bienvenues-Bâtard-Montrachet");
        bienvenue.setRegion(bourgogne);
        id = appellationDao.ajouter(bienvenue);
        bienvenue.setId(id);
        Appellation blagny = new Appellation();
        blagny.setNom("Blagny");
        blagny.setRegion(bourgogne);
        id = appellationDao.ajouter(blagny);
        blagny.setId(id);
        Appellation bonnes = new Appellation();
        bonnes.setNom("Bonnes-Mares");
        bonnes.setRegion(bourgogne);
        id = appellationDao.ajouter(bonnes);
        bonnes.setId(id);
        Appellation abourgogne = new Appellation();
        abourgogne.setNom("Bourgogne");
        abourgogne.setRegion(bourgogne);
        id = appellationDao.ajouter(abourgogne);
        abourgogne.setId(id);
        Appellation bourAli = new Appellation();
        bourAli.setNom("Bourgogne Aligoté");
        bourAli.setRegion(bourgogne);
        id = appellationDao.ajouter(bourAli);
        bourAli.setId(id);
        Appellation bourOrdi = new Appellation();
        bourOrdi.setNom("Bourgogne-Ordinaire");
        bourOrdi.setRegion(bourgogne);
        id = appellationDao.ajouter(bourOrdi);
        bourOrdi.setId(id);
        Appellation bourMousseux = new Appellation();
        bourMousseux.setNom("Bourgogne Mousseux");
        bourMousseux.setRegion(bourgogne);
        id = appellationDao.ajouter(bourMousseux);
        bourMousseux.setId(id);
        Appellation passeTout = new Appellation();
        passeTout.setNom("Bourgogne-PAsse-Tout-Grains");
        passeTout.setRegion(bourgogne);
        id = appellationDao.ajouter(passeTout);
        passeTout.setId(id);
        Appellation bouzeron = new Appellation();
        bouzeron.setNom("Bouzeron");
        bouzeron.setRegion(bourgogne);
        id = appellationDao.ajouter(bouzeron);
        bouzeron.setId(id);
        Appellation chablis = new Appellation();
        chablis.setNom("Chablis");
        chablis.setRegion(bourgogne);
        id = appellationDao.ajouter(chablis);
        chablis.setId(id);
        Appellation chaGdCru = new Appellation();
        chaGdCru.setNom("Chablis Grand Cru");
        chaGdCru.setRegion(bourgogne);
        id = appellationDao.ajouter(chaGdCru);
        chaGdCru.setId(id);
        Appellation cha1erCru = new Appellation();
        cha1erCru.setNom("Chablis 1er Cru");
        cha1erCru.setRegion(bourgogne);
        id = appellationDao.ajouter(cha1erCru);
        cha1erCru.setId(id);
        Appellation chambertin = new Appellation();
        chambertin.setNom("Chambertin");
        chambertin.setRegion(bourgogne);
        id = appellationDao.ajouter(chambertin);
        chambertin.setId(id);
        Appellation chamClos = new Appellation();
        chamClos.setNom("Chambertin-Clos-De-Bèze");
        chamClos.setRegion(bourgogne);
        id = appellationDao.ajouter(chamClos);
        chamClos.setId(id);
        Appellation chambolle = new Appellation();
        chambolle.setNom("Chambolle-Musigny");
        chambolle.setRegion(bourgogne);
        id = appellationDao.ajouter(chambolle);
        chambolle.setId(id);
        Appellation chapelle = new Appellation();
        chapelle.setNom("Chapelle-Chambertin");
        chapelle.setRegion(bourgogne);
        id = appellationDao.ajouter(chapelle);
        chapelle.setId(id);
        Appellation charlemagne = new Appellation();
        charlemagne.setNom("Charlemagne");
        charlemagne.setRegion(bourgogne);
        id = appellationDao.ajouter(charlemagne);
        charlemagne.setId(id);
        Appellation charmes = new Appellation();
        charmes.setNom("Charmes-Chambertin");
        charmes.setRegion(bourgogne);
        id = appellationDao.ajouter(charmes);
        charmes.setId(id);
        Appellation chassagne = new Appellation();
        chassagne.setNom("Chassagne-Montrachet");
        chassagne.setRegion(bourgogne);
        id = appellationDao.ajouter(chassagne);
        chassagne.setId(id);
        Appellation chevalier = new Appellation();
        chevalier.setNom("Chevalier-Montrachet");
        chevalier.setRegion(bourgogne);
        id = appellationDao.ajouter(chevalier);
        chevalier.setId(id);
        Appellation chorey = new Appellation();
        chorey.setNom("Chorey-Lès-Beaune");
        chorey.setRegion(bourgogne);
        id = appellationDao.ajouter(chorey);
        chorey.setId(id);
        Appellation closRoche = new Appellation();
        closRoche.setNom("Clos-De-La-Roche");
        closRoche.setRegion(bourgogne);
        id = appellationDao.ajouter(closRoche);
        closRoche.setId(id);
        Appellation closTart = new Appellation();
        closTart.setNom("Clos-De-Tart");
        closTart.setRegion(bourgogne);
        id = appellationDao.ajouter(closTart);
        closTart.setId(id);
        Appellation closVougeot = new Appellation();
        closVougeot.setNom("Clos-Vougeot");
        closVougeot.setRegion(bourgogne);
        id = appellationDao.ajouter(closVougeot);
        closVougeot.setId(id);
        Appellation closLambrays = new Appellation();
        closLambrays.setNom("Clos-Des-Lambrays");
        closLambrays.setRegion(bourgogne);
        id = appellationDao.ajouter(closLambrays);
        closLambrays.setId(id);
        Appellation closStDenis = new Appellation();
        closStDenis.setNom("Clos-St-Denis");
        closStDenis.setRegion(bourgogne);
        id = appellationDao.ajouter(closStDenis);
        closStDenis.setId(id);
        Appellation corton = new Appellation();
        corton.setNom("Corton");
        corton.setRegion(bourgogne);
        id = appellationDao.ajouter(corton);
        corton.setId(id);
        Appellation cortonCh = new Appellation();
        cortonCh.setNom("Corton-Charlemagne");
        cortonCh.setRegion(bourgogne);
        id = appellationDao.ajouter(cortonCh);
        cortonCh.setId(id);
        Appellation coteBeaune = new Appellation();
        coteBeaune.setNom("Côte-De-Beaune");
        coteBeaune.setRegion(bourgogne);
        id = appellationDao.ajouter(coteBeaune);
        coteBeaune.setId(id);
        Appellation coteBeauneVill = new Appellation();
        coteBeauneVill.setNom("Côte-De-Beaune-Villages");
        coteBeauneVill.setRegion(bourgogne);
        id = appellationDao.ajouter(coteBeauneVill);
        coteBeauneVill.setId(id);
        Appellation coteNuits = new Appellation();
        coteNuits.setNom("Côte-De-Nuits-Vilages");
        coteNuits.setRegion(bourgogne);
        id = appellationDao.ajouter(coteNuits);
        coteNuits.setId(id);
        Appellation cremantBourg = new Appellation();
        cremantBourg.setNom("Crémant De Bourgogne");
        cremantBourg.setRegion(bourgogne);
        id = appellationDao.ajouter(cremantBourg);
        cremantBourg.setId(id);
        Appellation criots = new Appellation();
        criots.setNom("Criots-Bâtard-Montrachet");
        criots.setRegion(bourgogne);
        id = appellationDao.ajouter(criots);
        criots.setId(id);
        Appellation echezeaux = new Appellation();
        echezeaux.setNom("Echezeaux");
        echezeaux.setRegion(bourgogne);
        id = appellationDao.ajouter(echezeaux);
        echezeaux.setId(id);
        Appellation fixin = new Appellation();
        fixin.setNom("Fixin");
        fixin.setRegion(bourgogne);
        id = appellationDao.ajouter(fixin);
        fixin.setId(id);
        Appellation gevrey = new Appellation();
        gevrey.setNom("Gevrey-Chambertin");
        gevrey.setRegion(bourgogne);
        id = appellationDao.ajouter(gevrey);
        gevrey.setId(id);
        Appellation givry = new Appellation();
        givry.setNom("Givry");
        givry.setRegion(bourgogne);
        id = appellationDao.ajouter(givry);
        givry.setId(id);
        Appellation gdEchezeaux = new Appellation();
        gdEchezeaux.setNom("Grands-Echezeaux");
        gdEchezeaux.setRegion(bourgogne);
        id = appellationDao.ajouter(gdEchezeaux);
        gdEchezeaux.setId(id);
        Appellation griotte = new Appellation();
        griotte.setNom("Griotte-Chambertin");
        griotte.setRegion(bourgogne);
        id = appellationDao.ajouter(griotte);
        griotte.setId(id);
        Appellation hteCoteBeaune = new Appellation();
        hteCoteBeaune.setNom("Hautes-Côte-De-Beaune");
        hteCoteBeaune.setRegion(bourgogne);
        id = appellationDao.ajouter(hteCoteBeaune);
        hteCoteBeaune.setId(id);
        Appellation irancy = new Appellation();
        irancy.setNom("Irancy");
        irancy.setRegion(bourgogne);
        id = appellationDao.ajouter(irancy);
        irancy.setId(id);
        Appellation laGdeRue = new Appellation();
        laGdeRue.setNom("La-Grande-Rue");
        laGdeRue.setRegion(bourgogne);
        id = appellationDao.ajouter(laGdeRue);
        laGdeRue.setId(id);
        Appellation laRomanee = new Appellation();
        laRomanee.setNom("La-Romanée");
        laRomanee.setRegion(bourgogne);
        id = appellationDao.ajouter(laRomanee);
        laRomanee.setId(id);
        Appellation laTache = new Appellation();
        laTache.setNom("La-Tâche");
        laTache.setRegion(bourgogne);
        id = appellationDao.ajouter(laTache);
        laTache.setId(id);
        Appellation ladoix = new Appellation();
        ladoix.setNom("adoix");
        ladoix.setRegion(bourgogne);
        id = appellationDao.ajouter(ladoix);
        ladoix.setId(id);
        Appellation latricieres = new Appellation();
        latricieres.setNom("Latricières-Chambertin");
        latricieres.setRegion(bourgogne);
        id = appellationDao.ajouter(latricieres);
        latricieres.setId(id);
        Appellation macon = new Appellation();
        macon.setNom("Mâcon");
        macon.setRegion(bourgogne);
        id = appellationDao.ajouter(macon);
        macon.setId(id);
        Appellation maranges = new Appellation();
        maranges.setNom("Maranges");
        maranges.setRegion(bourgogne);
        id = appellationDao.ajouter(maranges);
        maranges.setId(id);
        Appellation marsannay = new Appellation();
        marsannay.setNom("Marsannay");
        marsannay.setRegion(bourgogne);
        id = appellationDao.ajouter(marsannay);
        marsannay.setId(id);
        Appellation mazis = new Appellation();
        mazis.setNom("Mazis-Chambertin");
        mazis.setRegion(bourgogne);
        id = appellationDao.ajouter(mazis);
        mazis.setId(id);
        Appellation mazoyeres = new Appellation();
        mazoyeres.setNom("Mazoyères-Chambertin");
        mazoyeres.setRegion(bourgogne);
        id = appellationDao.ajouter(mazoyeres);
        mazoyeres.setId(id);
        Appellation mercurey = new Appellation();
        mercurey.setNom("Mercurey");
        mercurey.setRegion(bourgogne);
        id = appellationDao.ajouter(mercurey);
        mercurey.setId(id);
        Appellation meurseult = new Appellation();
        meurseult.setNom("Meurseult");
        meurseult.setRegion(bourgogne);
        id = appellationDao.ajouter(meurseult);
        meurseult.setId(id);
        Appellation montagny = new Appellation();
        montagny.setNom("Montagny");
        montagny.setRegion(bourgogne);
        id = appellationDao.ajouter(montagny);
        montagny.setId(id);
        Appellation monthelie = new Appellation();
        monthelie.setNom("Monthélie");
        monthelie.setRegion(bourgogne);
        id = appellationDao.ajouter(monthelie);
        monthelie.setId(id);
        Appellation montrachet = new Appellation();
        montrachet.setNom("Montrachet");
        montrachet.setRegion(bourgogne);
        id = appellationDao.ajouter(montrachet);
        montrachet.setId(id);
        Appellation morey = new Appellation();
        morey.setNom("Morey-Saint-Denis");
        morey.setRegion(bourgogne);
        id = appellationDao.ajouter(morey);
        morey.setId(id);
        Appellation musigny = new Appellation();
        musigny.setNom("Musigny");
        musigny.setRegion(bourgogne);
        id = appellationDao.ajouter(musigny);
        musigny.setId(id);
        Appellation nuitsStG = new Appellation();
        nuitsStG.setNom("Nuits-St-Georges");
        nuitsStG.setRegion(bourgogne);
        id = appellationDao.ajouter(nuitsStG);
        nuitsStG.setId(id);
        Appellation pernand = new Appellation();
        pernand.setNom("Pernand-Vergelesses");
        pernand.setRegion(bourgogne);
        id = appellationDao.ajouter(pernand);
        pernand.setId(id);
        Appellation ptChablis = new Appellation();
        ptChablis.setNom("Petit-Chablis");
        ptChablis.setRegion(bourgogne);
        id = appellationDao.ajouter(ptChablis);
        ptChablis.setId(id);
        Appellation pommard = new Appellation();
        pommard.setNom("Pommard");
        pommard.setRegion(bourgogne);
        id = appellationDao.ajouter(pommard);
        pommard.setId(id);
        Appellation pouilly = new Appellation();
        pouilly.setNom("Pouilly-Fuissé");
        pouilly.setRegion(bourgogne);
        id = appellationDao.ajouter(pouilly);
        pouilly.setId(id);
        Appellation pouillyL = new Appellation();
        pouillyL.setNom("Pouilly-Loché");
        pouillyL.setRegion(bourgogne);
        id = appellationDao.ajouter(pouillyL);
        pouillyL.setId(id);
        Appellation pouillyV = new Appellation();
        pouillyV.setNom("Pouilly-Vinzelles");
        pouillyV.setRegion(bourgogne);
        id = appellationDao.ajouter(pouillyV);
        pouillyV.setId(id);
        Appellation puligny = new Appellation();
        puligny.setNom("Puligny-Montrachet");
        puligny.setRegion(bourgogne);
        id = appellationDao.ajouter(puligny);
        puligny.setId(id);
        Appellation richebourg = new Appellation();
        richebourg.setNom("Richebourg");
        richebourg.setRegion(bourgogne);
        id = appellationDao.ajouter(richebourg);
        richebourg.setId(id);
        Appellation romanee = new Appellation();
        romanee.setNom("Romanée-Conti");
        romanee.setRegion(bourgogne);
        id = appellationDao.ajouter(romanee);
        romanee.setId(id);
        Appellation romaneeStV = new Appellation();
        romaneeStV.setNom("Romanée-Saint-Vivant");
        romaneeStV.setRegion(bourgogne);
        id = appellationDao.ajouter(romaneeStV);
        romaneeStV.setId(id);
        Appellation ruchottes = new Appellation();
        ruchottes.setNom("Ruchottes-Chambertin");
        ruchottes.setRegion(bourgogne);
        id = appellationDao.ajouter(ruchottes);
        ruchottes.setId(id);
        Appellation rully = new Appellation();
        rully.setNom("Rully");
        rully.setRegion(bourgogne);
        id = appellationDao.ajouter(rully);
        rully.setId(id);
        Appellation stAubin = new Appellation();
        stAubin.setNom("Saint-Aubin");
        stAubin.setRegion(bourgogne);
        id = appellationDao.ajouter(stAubin);
        stAubin.setId(id);
        Appellation stBris = new Appellation();
        stBris.setNom("Saint-Bris");
        stBris.setRegion(bourgogne);
        id = appellationDao.ajouter(stBris);
        stBris.setId(id);
        Appellation stRomain = new Appellation();
        stRomain.setNom("Saint-Romain");
        stRomain.setRegion(bourgogne);
        id = appellationDao.ajouter(stRomain);
        stRomain.setId(id);
        Appellation stVeran = new Appellation();
        stVeran.setNom("Saint-Véran");
        stVeran.setRegion(bourgogne);
        id = appellationDao.ajouter(stVeran);
        stVeran.setId(id);
        Appellation santenay = new Appellation();
        santenay.setNom("Santenay");
        santenay.setRegion(bourgogne);
        id = appellationDao.ajouter(santenay);
        santenay.setId(id);
        Appellation savigny = new Appellation();
        savigny.setNom("Savigny-Lès-Beaune");
        savigny.setRegion(bourgogne);
        id = appellationDao.ajouter(savigny);
        savigny.setId(id);
        Appellation vire = new Appellation();
        vire.setNom("Viré-Clessé");
        vire.setRegion(bourgogne);
        id = appellationDao.ajouter(vire);
        vire.setId(id);
        Appellation volnay = new Appellation();
        volnay.setNom("Volnay");
        volnay.setRegion(bourgogne);
        id = appellationDao.ajouter(volnay);
        volnay.setId(id);
        Appellation vosne = new Appellation();
        vosne.setNom("Vosne-Romanée");
        vosne.setRegion(bourgogne);
        id = appellationDao.ajouter(vosne);
        vosne.setId(id);
        Appellation vougeot = new Appellation();
        vougeot.setNom("Vougeot");
        vougeot.setRegion(bourgogne);
        id = appellationDao.ajouter(vougeot);
        vougeot.setId(id);

        Appellation abugey = new Appellation();
        abugey.setNom("Bugey");
        abugey.setRegion(bugey);
        id = appellationDao.ajouter(abugey);
        abugey.setId(id);
        Appellation roussette = new Appellation();
        roussette.setNom("Roussette Du Bugey");
        roussette.setRegion(bugey);
        id = appellationDao.ajouter(roussette);
        roussette.setId(id);

        Appellation achampagne = new Appellation();
        achampagne.setNom("Champagne");
        achampagne.setRegion(champagne);
        id = appellationDao.ajouter(achampagne);
        achampagne.setId(id);
        Appellation coteauxChamp = new Appellation();
        coteauxChamp.setNom("Côteaux-Champenois");
        coteauxChamp.setRegion(champagne);
        id = appellationDao.ajouter(coteauxChamp);
        coteauxChamp.setId(id);
        Appellation rose = new Appellation();
        rose.setNom("Rosé Des Riceys");
        rose.setRegion(champagne);
        id = appellationDao.ajouter(rose);
        rose.setId(id);

        Appellation ajaccio = new Appellation();
        ajaccio.setNom("Ajaccio");
        ajaccio.setRegion(corse);
        id = appellationDao.ajouter(ajaccio);
        ajaccio.setId(id);
        Appellation acorse = new Appellation();
        acorse.setNom("Corse");
        acorse.setRegion(corse);
        id = appellationDao.ajouter(acorse);
        acorse.setId(id);
        Appellation muscat = new Appellation();
        muscat.setNom("Muscat Du Cap-Corse");
        muscat.setRegion(corse);
        id = appellationDao.ajouter(muscat);
        muscat.setId(id);
        Appellation patrimonio = new Appellation();
        patrimonio.setNom("Patrimonio");
        patrimonio.setRegion(corse);
        id = appellationDao.ajouter(patrimonio);
        patrimonio.setId(id);


        Appellation arbois = new Appellation();
        arbois.setNom("Arbois");
        arbois.setRegion(jura);
        id = appellationDao.ajouter(arbois);
        arbois.setId(id);
        Appellation chateauChalon = new Appellation();
        chateauChalon.setNom("Château-Chalon");
        chateauChalon.setRegion(jura);
        id = appellationDao.ajouter(chateauChalon);
        chateauChalon.setId(id);
        Appellation coteJura = new Appellation();
        coteJura.setNom("Côtes-Du-Jura");
        coteJura.setRegion(jura);
        id = appellationDao.ajouter(coteJura);
        coteJura.setId(id);
        Appellation cremantJura = new Appellation();
        cremantJura.setNom("Crémant Du Jura");
        cremantJura.setRegion(jura);
        id = appellationDao.ajouter(cremantJura);
        cremantJura.setId(id);
        Appellation letoile = new Appellation();
        letoile.setNom("L'étoile");
        letoile.setRegion(jura);
        id = appellationDao.ajouter(letoile);
        letoile.setId(id);


        Appellation banyulsGdCru = new Appellation();
        banyulsGdCru.setNom("Banyuls Grand Cru");
        banyulsGdCru.setRegion(languedoc);
        id = appellationDao.ajouter(banyulsGdCru);
        banyulsGdCru.setId(id);
        Appellation cabardes = new Appellation();
        cabardes.setNom("Cabardès");
        cabardes.setRegion(languedoc);
        id = appellationDao.ajouter(cabardes);
        cabardes.setId(id);
        Appellation clairetteLanguedoc = new Appellation();
        clairetteLanguedoc.setNom("Clairette Du Languedoc");
        clairetteLanguedoc.setRegion(languedoc);
        id = appellationDao.ajouter(clairetteLanguedoc);
        clairetteLanguedoc.setId(id);
        Appellation corbieres = new Appellation();
        corbieres.setNom("Corbières");
        corbieres.setRegion(languedoc);
        id = appellationDao.ajouter(corbieres);
        corbieres.setId(id);
        Appellation corbieresB = new Appellation();
        corbieresB.setNom("Corbières-Boutenac");
        corbieresB.setRegion(languedoc);
        id = appellationDao.ajouter(corbieresB);
        corbieresB.setId(id);
        Appellation cremantLimoux = new Appellation();
        cremantLimoux.setNom("Crémant De Limoux");
        cremantLimoux.setRegion(languedoc);
        id = appellationDao.ajouter(cremantLimoux);
        cremantLimoux.setId(id);
        Appellation faugeres = new Appellation();
        faugeres.setNom("Faugères");
        faugeres.setRegion(languedoc);
        id = appellationDao.ajouter(faugeres);
        faugeres.setId(id);
        Appellation fitou = new Appellation();
        fitou.setNom("Fitou");
        fitou.setRegion(languedoc);
        id = appellationDao.ajouter(fitou);
        fitou.setId(id);
        Appellation alanguedoc = new Appellation();
        alanguedoc.setNom("Languedoc");
        alanguedoc.setRegion(languedoc);
        id = appellationDao.ajouter(alanguedoc);
        alanguedoc.setId(id);
        Appellation limoux = new Appellation();
        limoux.setNom("Limoux");
        limoux.setRegion(languedoc);
        id = appellationDao.ajouter(limoux);
        limoux.setId(id);
        Appellation malepere = new Appellation();
        malepere.setNom("Malepère");
        malepere.setRegion(languedoc);
        id = appellationDao.ajouter(malepere);
        malepere.setId(id);
        Appellation minervois = new Appellation();
        minervois.setNom("Minervois");
        minervois.setRegion(languedoc);
        id = appellationDao.ajouter(minervois);
        minervois.setId(id);
        Appellation minervoisLa = new Appellation();
        minervoisLa.setNom("Minervois-La-Livinière");
        minervoisLa.setRegion(languedoc);
        id = appellationDao.ajouter(minervoisLa);
        minervoisLa.setId(id);
        Appellation muscatFront = new Appellation();
        muscatFront.setNom("Muscat De Frontignan");
        muscatFront.setRegion(languedoc);
        id = appellationDao.ajouter(muscatFront);
        muscatFront.setId(id);
        Appellation muscatLunel = new Appellation();
        muscatLunel.setNom("Muscat De Lunel");
        muscatLunel.setRegion(languedoc);
        id = appellationDao.ajouter(muscatLunel);
        muscatLunel.setId(id);
        Appellation muscatMireval = new Appellation();
        muscatMireval.setNom("Muscat De Mireval");
        muscatMireval.setRegion(languedoc);
        id = appellationDao.ajouter(muscatMireval);
        muscatMireval.setId(id);
        Appellation muscatStJean = new Appellation();
        muscatStJean.setNom("Muscat De Saint-Jean-De-Minervois");
        muscatStJean.setRegion(languedoc);
        id = appellationDao.ajouter(muscatStJean);
        muscatStJean.setId(id);
        Appellation stChinian = new Appellation();
        stChinian.setNom("Saint-Chinian");
        stChinian.setRegion(languedoc);
        id = appellationDao.ajouter(stChinian);
        stChinian.setId(id);

        Appellation banyuls = new Appellation();
        banyuls.setNom("Banyuls");
        banyuls.setRegion(languedocR);
        id = appellationDao.ajouter(banyuls);
        banyuls.setId(id);

        Appellation toul = new Appellation();
        toul.setNom("Côtes-De-Toul");
        toul.setRegion(lorraine);
        id = appellationDao.ajouter(toul);
        toul.setId(id);
        Appellation moselle = new Appellation();
        moselle.setNom("Moselle");
        moselle.setRegion(lorraine);
        id = appellationDao.ajouter(moselle);
        moselle.setId(id);

        Appellation coteauxLyonnais = new Appellation();
        coteauxLyonnais.setNom("Côteaux-Du-Lyonnais");
        coteauxLyonnais.setRegion(lyonnais);
        id = appellationDao.ajouter(coteauxLyonnais);
        coteauxLyonnais.setId(id);

        Appellation bandol = new Appellation();
        bandol.setNom("Bandol");
        bandol.setRegion(provence);
        id = appellationDao.ajouter(bandol);
        bandol.setId(id);
        Appellation bellet = new Appellation();
        bellet.setNom("Bellet");
        bellet.setRegion(provence);
        id = appellationDao.ajouter(bellet);
        bellet.setId(id);
        Appellation cassis = new Appellation();
        cassis.setNom("Cassis");
        cassis.setRegion(provence);
        id = appellationDao.ajouter(cassis);
        cassis.setId(id);
        Appellation coteauxAix = new Appellation();
        coteauxAix.setNom("Côteaux-D'Aix-En-Provence");
        coteauxAix.setRegion(provence);
        id = appellationDao.ajouter(coteauxAix);
        coteauxAix.setId(id);
        Appellation coteauxVarois = new Appellation();
        coteauxVarois.setNom("Côteaux-Varois-En-Provence");
        coteauxVarois.setRegion(provence);
        id = appellationDao.ajouter(coteauxVarois);
        coteauxVarois.setId(id);
        Appellation cotesProvence = new Appellation();
        cotesProvence.setNom("Côtes-De-Provence");
        cotesProvence.setRegion(provence);
        id = appellationDao.ajouter(cotesProvence);
        cotesProvence.setId(id);
        Appellation bauxProvence = new Appellation();
        bauxProvence.setNom("Les-Baux-De-Provence");
        bauxProvence.setRegion(provence);
        id = appellationDao.ajouter(bauxProvence);
        bauxProvence.setId(id);
        Appellation palette = new Appellation();
        palette.setNom("Palette");
        palette.setRegion(provence);
        id = appellationDao.ajouter(palette);
        palette.setId(id);
        Appellation pierrevert = new Appellation();
        pierrevert.setNom("Pierrevert");
        pierrevert.setRegion(provence);
        id = appellationDao.ajouter(pierrevert);
        pierrevert.setId(id);

        Appellation collioure = new Appellation();
        collioure.setNom("Collioure");
        collioure.setRegion(roussilon);
        id = appellationDao.ajouter(collioure);
        collioure.setId(id);
        Appellation coteRoussillon = new Appellation();
        coteRoussillon.setNom("Côtes-Du-Roussillon");
        coteRoussillon.setRegion(roussilon);
        id = appellationDao.ajouter(coteRoussillon);
        coteRoussillon.setId(id);
        Appellation coteRoussillonVill = new Appellation();
        coteRoussillonVill.setNom("Côtes-Du-Roussillon Villages");
        coteRoussillonVill.setRegion(roussilon);
        id = appellationDao.ajouter(coteRoussillonVill);
        coteRoussillonVill.setId(id);
        Appellation maury = new Appellation();
        maury.setNom("Maury");
        maury.setRegion(roussilon);
        id = appellationDao.ajouter(maury);
        maury.setId(id);
        Appellation muscatRiv = new Appellation();
        muscatRiv.setNom("Muscat De Rivesaltes");
        muscatRiv.setRegion(roussilon);
        id = appellationDao.ajouter(muscatRiv);
        muscatRiv.setId(id);
        Appellation rivesaltes = new Appellation();
        rivesaltes.setNom("Rivesaltes");
        rivesaltes.setRegion(roussilon);
        id = appellationDao.ajouter(rivesaltes);
        rivesaltes.setId(id);

        Appellation roussetteSavoie = new Appellation();
        roussetteSavoie.setNom("Roussette De Savoie");
        roussetteSavoie.setRegion(savoie);
        id = appellationDao.ajouter(roussetteSavoie);
        roussetteSavoie.setId(id);
        Appellation vinSavoie = new Appellation();
        vinSavoie.setNom("Vin De Savoie");
        vinSavoie.setRegion(savoie);
        id = appellationDao.ajouter(vinSavoie);
        vinSavoie.setId(id);

        Appellation bearn = new Appellation();
        bearn.setNom("Béarn");
        bearn.setRegion(sudouest);
        id = appellationDao.ajouter(bearn);
        bearn.setId(id);
        Appellation bergerac = new Appellation();
        bergerac.setNom("Bergerac");
        bergerac.setRegion(sudouest);
        id = appellationDao.ajouter(bergerac);
        bergerac.setId(id);
        Appellation buzet = new Appellation();
        buzet.setNom("Buzet");
        buzet.setRegion(sudouest);
        id = appellationDao.ajouter(buzet);
        buzet.setId(id);
        Appellation cahors = new Appellation();
        cahors.setNom("Cahors");
        cahors.setRegion(sudouest);
        id = appellationDao.ajouter(cahors);
        cahors.setId(id);
        Appellation cotesBergerac = new Appellation();
        cotesBergerac.setNom("Côtes-De-Bergerac");
        cotesBergerac.setRegion(sudouest);
        id = appellationDao.ajouter(cotesBergerac);
        cotesBergerac.setId(id);
        Appellation cotesDuras = new Appellation();
        cotesDuras.setNom("Côtes-De-Duras");
        cotesDuras.setRegion(sudouest);
        id = appellationDao.ajouter(cotesDuras);
        cotesDuras.setId(id);
        Appellation cotesMontravel = new Appellation();
        cotesMontravel.setNom("cotes-De-Montravel");
        cotesMontravel.setRegion(sudouest);
        id = appellationDao.ajouter(cotesMontravel);
        cotesMontravel.setId(id);
        Appellation cotesMarmandais = new Appellation();
        cotesMarmandais.setNom("Côtes-Du-Marmandais");
        cotesMarmandais.setRegion(sudouest);
        id = appellationDao.ajouter(cotesMarmandais);
        cotesMarmandais.setId(id);
        Appellation fronton = new Appellation();
        fronton.setNom("Fronton");
        fronton.setRegion(sudouest);
        id = appellationDao.ajouter(fronton);
        fronton.setId(id);
        Appellation gaillac = new Appellation();
        gaillac.setNom("Gaillac");
        gaillac.setRegion(sudouest);
        id = appellationDao.ajouter(gaillac);
        gaillac.setId(id);
        Appellation gaillacPrem = new Appellation();
        gaillacPrem.setNom("Gaillac-Premières-Côtes");
        gaillacPrem.setRegion(sudouest);
        id = appellationDao.ajouter(gaillacPrem);
        gaillacPrem.setId(id);
        Appellation htMontravel = new Appellation();
        htMontravel.setNom("Haut-Montravel");
        htMontravel.setRegion(sudouest);
        id = appellationDao.ajouter(htMontravel);
        htMontravel.setId(id);
        Appellation irouleguy = new Appellation();
        irouleguy.setNom("Irouleguy");
        irouleguy.setRegion(sudouest);
        id = appellationDao.ajouter(irouleguy);
        irouleguy.setId(id);
        Appellation jurançon = new Appellation();
        jurançon.setNom("Jurançon");
        jurançon.setRegion(sudouest);
        id = appellationDao.ajouter(jurançon);
        jurançon.setId(id);
        Appellation madiran = new Appellation();
        madiran.setNom("Madiran");
        madiran.setRegion(sudouest);
        id = appellationDao.ajouter(madiran);
        madiran.setId(id);
        Appellation marcillac = new Appellation();
        marcillac.setNom("Marcillac");
        marcillac.setRegion(sudouest);
        id = appellationDao.ajouter(marcillac);
        marcillac.setId(id);
        Appellation monbazilac = new Appellation();
        monbazilac.setNom("Monbazilac");
        monbazilac.setRegion(sudouest);
        id = appellationDao.ajouter(monbazilac);
        monbazilac.setId(id);
        Appellation montravel = new Appellation();
        montravel.setNom("Montravel");
        montravel.setRegion(sudouest);
        id = appellationDao.ajouter(montravel);
        montravel.setId(id);
        Appellation pacherenc = new Appellation();
        pacherenc.setNom("Pacherenc-Du-Vic-Bilh");
        pacherenc.setRegion(sudouest);
        id = appellationDao.ajouter(pacherenc);
        pacherenc.setId(id);
        Appellation pecharmant = new Appellation();
        pecharmant.setNom("Pécharmant");
        pecharmant.setRegion(sudouest);
        id = appellationDao.ajouter(pecharmant);
        pecharmant.setId(id);
        Appellation rosette = new Appellation();
        rosette.setNom("Rosette");
        rosette.setRegion(sudouest);
        id = appellationDao.ajouter(rosette);
        rosette.setId(id);
        Appellation saussignac = new Appellation();
        saussignac.setNom("Saussignac");
        saussignac.setRegion(sudouest);
        id = appellationDao.ajouter(saussignac);
        saussignac.setId(id);



        Appellation anjou = new Appellation();
        anjou.setNom("Anjou");
        anjou.setRegion(valleeloire);
        id = appellationDao.ajouter(anjou);
        anjou.setId(id);
        Appellation anjouCoteaux = new Appellation();
        anjouCoteaux.setNom("Anjou-Coteaux-De-La-Loire");
        anjouCoteaux.setRegion(valleeloire);
        id = appellationDao.ajouter(anjouCoteaux);
        anjouCoteaux.setId(id);
        Appellation anjouVill = new Appellation();
        anjouVill.setNom("Anjou Villages");
        anjouVill.setRegion(valleeloire);
        id = appellationDao.ajouter(anjouVill);
        anjouVill.setId(id);
        Appellation anjouVillBrisac = new Appellation();
        anjouVillBrisac.setNom("Anjou Villages Brissac");
        anjouVillBrisac.setRegion(valleeloire);
        id = appellationDao.ajouter(anjouVillBrisac);
        anjouVillBrisac.setId(id);
        Appellation bonnezeaux = new Appellation();
        bonnezeaux.setNom("Bonnezeaux");
        bonnezeaux.setRegion(valleeloire);
        id = appellationDao.ajouter(bonnezeaux);
        bonnezeaux.setId(id);
        Appellation bourgueil = new Appellation();
        bourgueil.setNom("Bourgueil");
        bourgueil.setRegion(valleeloire);
        id = appellationDao.ajouter(bourgueil);
        bourgueil.setId(id);
        Appellation cabernet = new Appellation();
        cabernet.setNom("Cabernet D'anjou");
        cabernet.setRegion(valleeloire);
        id = appellationDao.ajouter(cabernet);
        cabernet.setId(id);
        Appellation cabernetS = new Appellation();
        cabernetS.setNom("Cabernet De Saumur");
        cabernetS.setRegion(valleeloire);
        id = appellationDao.ajouter(cabernetS);
        cabernetS.setId(id);
        Appellation chateauMeillant = new Appellation();
        chateauMeillant.setNom("Châteaumeillant");
        chateauMeillant.setRegion(valleeloire);
        id = appellationDao.ajouter(chateauMeillant);
        chateauMeillant.setId(id);
        Appellation cheverny = new Appellation();
        cheverny.setNom("Cheverny");
        cheverny.setRegion(valleeloire);
        id = appellationDao.ajouter(cheverny);
        cheverny.setId(id);
        Appellation chinon = new Appellation();
        chinon.setNom("Chinon");
        chinon.setRegion(valleeloire);
        id = appellationDao.ajouter(chinon);
        chinon.setId(id);
        Appellation coteRoannaise = new Appellation();
        coteRoannaise.setNom("Côte-Roannaise");
        coteRoannaise.setRegion(valleeloire);
        id = appellationDao.ajouter(coteRoannaise);
        coteRoannaise.setId(id);
        Appellation coteauxAubance = new Appellation();
        coteauxAubance.setNom("Côteaux-De-L'Aubance");
        coteauxAubance.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxAubance);
        coteauxAubance.setId(id);
        Appellation coteauxSaumur = new Appellation();
        coteauxSaumur.setNom("Côteaux-De-Saumur");
        coteauxSaumur.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxSaumur);
        coteauxSaumur.setId(id);
        Appellation coteauxGiennois = new Appellation();
        coteauxGiennois.setNom("Côteaux-Du-Giennois");
        coteauxGiennois.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxGiennois);
        coteauxGiennois.setId(id);
        Appellation coteauxLayon = new Appellation();
        coteauxLayon.setNom("Côteaux-Du-Layon");
        coteauxLayon.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxLayon);
        coteauxLayon.setId(id);
        Appellation coteauxLoir = new Appellation();
        coteauxLoir.setNom("Côteaux-Du-Loir");
        coteauxLoir.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxLoir);
        coteauxLoir.setId(id);
        Appellation coteauxVendomois = new Appellation();
        coteauxVendomois.setNom("Côteaux-Du-Vendomois");
        coteauxVendomois.setRegion(valleeloire);
        id = appellationDao.ajouter(coteauxVendomois);
        coteauxVendomois.setId(id);
        Appellation cotesForez = new Appellation();
        cotesForez.setNom("Côtes-Du-Forez");
        cotesForez.setRegion(valleeloire);
        id = appellationDao.ajouter(cotesForez);
        cotesForez.setId(id);
        Appellation courCheverny = new Appellation();
        courCheverny.setNom("Cour-Cheverny");
        courCheverny.setRegion(valleeloire);
        id = appellationDao.ajouter(courCheverny);
        courCheverny.setId(id);
        Appellation cremantLoire = new Appellation();
        cremantLoire.setNom("Crémant De Loire");
        cremantLoire.setRegion(valleeloire);
        id = appellationDao.ajouter(cremantLoire);
        cremantLoire.setId(id);
        Appellation fiefsVendeens = new Appellation();
        fiefsVendeens.setNom("Fiefs-Vendéens");
        fiefsVendeens.setRegion(valleeloire);
        id = appellationDao.ajouter(fiefsVendeens);
        fiefsVendeens.setId(id);
        Appellation HtPoitou = new Appellation();
        HtPoitou.setNom("Haut-Poitou");
        HtPoitou.setRegion(valleeloire);
        id = appellationDao.ajouter(HtPoitou);
        HtPoitou.setId(id);
        Appellation jasnieres = new Appellation();
        jasnieres.setNom("Jasnières");
        jasnieres.setRegion(valleeloire);
        id = appellationDao.ajouter(jasnieres);
        jasnieres.setId(id);
        Appellation menetou = new Appellation();
        menetou.setNom("Menetou-Salon");
        menetou.setRegion(valleeloire);
        id = appellationDao.ajouter(menetou);
        menetou.setId(id);
        Appellation montlouis = new Appellation();
        montlouis.setNom("Montlouis-Sur-Loire");
        montlouis.setRegion(valleeloire);
        id = appellationDao.ajouter(montlouis);
        montlouis.setId(id);
        Appellation muscadet = new Appellation();
        muscadet.setNom("Muscadet");
        muscadet.setRegion(valleeloire);
        id = appellationDao.ajouter(muscadet);
        muscadet.setId(id);
        Appellation muscadetCoteaux = new Appellation();
        muscadetCoteaux.setNom("Muscadet-Coteaux-De-La-Loire");
        muscadetCoteaux.setRegion(valleeloire);
        id = appellationDao.ajouter(muscadetCoteaux);
        muscadetCoteaux.setId(id);
        Appellation muscadetCote = new Appellation();
        muscadetCote.setNom("Muscadet-Côtes-De-Grandlieu");
        muscadetCote.setRegion(valleeloire);
        id = appellationDao.ajouter(muscadetCote);
        muscadetCote.setId(id);
        Appellation muscadetSevre = new Appellation();
        muscadetSevre.setNom("Muscadet-Sèvre-Et-Maine");
        muscadetSevre.setRegion(valleeloire);
        id = appellationDao.ajouter(muscadetSevre);
        muscadetSevre.setId(id);
        Appellation orleans = new Appellation();
        orleans.setNom("Orléans");
        orleans.setRegion(valleeloire);
        id = appellationDao.ajouter(orleans);
        orleans.setId(id);
        Appellation orleansCl = new Appellation();
        orleansCl.setNom("Orléans-Cléry");
        orleansCl.setRegion(valleeloire);
        id = appellationDao.ajouter(orleansCl);
        orleansCl.setId(id);
        Appellation pouillyFume = new Appellation();
        pouillyFume.setNom("Pouilly-Fumé");
        pouillyFume.setRegion(valleeloire);
        id = appellationDao.ajouter(pouillyFume);
        pouillyFume.setId(id);
        Appellation pouillyLoire = new Appellation();
        pouillyLoire.setNom("Pouilly-Sur-Loire");
        pouillyLoire.setRegion(valleeloire);
        id = appellationDao.ajouter(pouillyLoire);
        pouillyLoire.setId(id);
        Appellation quart = new Appellation();
        quart.setNom("Quart-De-Chaume");
        quart.setRegion(valleeloire);
        id = appellationDao.ajouter(quart);
        quart.setId(id);
        Appellation quincy = new Appellation();
        quincy.setNom("Quincy");
        quincy.setRegion(valleeloire);
        id = appellationDao.ajouter(quincy);
        quincy.setId(id);
        Appellation reuilly = new Appellation();
        reuilly.setNom("Reuilly");
        reuilly.setRegion(valleeloire);
        id = appellationDao.ajouter(reuilly);
        reuilly.setId(id);
        Appellation roseAnjou = new Appellation();
        roseAnjou.setNom("Rosé D'Anjou");
        roseAnjou.setRegion(valleeloire);
        id = appellationDao.ajouter(roseAnjou);
        roseAnjou.setId(id);
        Appellation roseLoire = new Appellation();
        roseLoire.setNom("Rosé De Loire");
        roseLoire.setRegion(valleeloire);
        id = appellationDao.ajouter(roseLoire);
        roseLoire.setId(id);
        Appellation stNicolasBourgueil = new Appellation();
        stNicolasBourgueil.setNom("Saint-Nicolas-De-Bourgueil");
        stNicolasBourgueil.setRegion(valleeloire);
        id = appellationDao.ajouter(stNicolasBourgueil);
        stNicolasBourgueil.setId(id);
        Appellation stPourcain = new Appellation();
        stPourcain.setNom("Saint-Pourçain");
        stPourcain.setRegion(valleeloire);
        id = appellationDao.ajouter(stPourcain);
        stPourcain.setId(id);
        Appellation sancerre = new Appellation();
        sancerre.setNom("Sancerre");
        sancerre.setRegion(valleeloire);
        id = appellationDao.ajouter(sancerre);
        sancerre.setId(id);
        Appellation saumur = new Appellation();
        saumur.setNom("Saumur");
        saumur.setRegion(valleeloire);
        id = appellationDao.ajouter(saumur);
        saumur.setId(id);
        Appellation saumurCh = new Appellation();
        saumurCh.setNom("Saumur-Champigny");
        saumurCh.setRegion(valleeloire);
        id = appellationDao.ajouter(saumurCh);
        saumurCh.setId(id);
        Appellation savennieres = new Appellation();
        savennieres.setNom("Savennières");
        savennieres.setRegion(valleeloire);
        id = appellationDao.ajouter(savennieres);
        savennieres.setId(id);
        Appellation touraine = new Appellation();
        touraine.setNom("Touraine");
        touraine.setRegion(valleeloire);
        id = appellationDao.ajouter(touraine);
        touraine.setId(id);
        Appellation touraineNoble = new Appellation();
        touraineNoble.setNom("Touraine-Noble-Joué");
        touraineNoble.setRegion(valleeloire);
        id = appellationDao.ajouter(touraineNoble);
        touraineNoble.setId(id);
        Appellation valencay = new Appellation();
        valencay.setNom("Valençay");
        valencay.setRegion(valleeloire);
        id = appellationDao.ajouter(valencay);
        valencay.setId(id);
        Appellation vouvray = new Appellation();
        vouvray.setNom("Vouvray");
        vouvray.setRegion(valleeloire);
        id = appellationDao.ajouter(vouvray);
        vouvray.setId(id);


        Appellation beaumesVenise = new Appellation();
        beaumesVenise.setNom("Beaumes-De-Venise");
        beaumesVenise.setRegion(valleerhone);
        id = appellationDao.ajouter(beaumesVenise);
        beaumesVenise.setId(id);
        Appellation chateauGrillet = new Appellation();
        chateauGrillet.setNom("Château-Grillet");
        chateauGrillet.setRegion(valleerhone);
        id = appellationDao.ajouter(chateauGrillet);
        chateauGrillet.setId(id);
        Appellation chateauneufPape = new Appellation();
        chateauneufPape.setNom("Châteauneuf-Du-Pape");
        chateauneufPape.setRegion(valleerhone);
        id = appellationDao.ajouter(chateauneufPape);
        chateauneufPape.setId(id);
        Appellation chatillon = new Appellation();
        chatillon.setNom("Châtillon-En-Dioid");
        chatillon.setRegion(valleerhone);
        id = appellationDao.ajouter(chatillon);
        chatillon.setId(id);
        Appellation clairette = new Appellation();
        clairette.setNom("Clairette De Die");
        clairette.setRegion(valleerhone);
        id = appellationDao.ajouter(clairette);
        clairette.setId(id);
        Appellation condrieu = new Appellation();
        condrieu.setNom("Condrieu");
        condrieu.setRegion(valleerhone);
        id = appellationDao.ajouter(condrieu);
        condrieu.setId(id);
        Appellation cornas = new Appellation();
        cornas.setNom("Cornas");
        cornas.setRegion(valleerhone);
        id = appellationDao.ajouter(cornas);
        cornas.setId(id);
        Appellation costieres = new Appellation();
        costieres.setNom("Costières-De-Nîmes");
        costieres.setRegion(valleerhone);
        id = appellationDao.ajouter(costieres);
        costieres.setId(id);
        Appellation coteRotie = new Appellation();
        coteRotie.setNom("Côte-Rôtie");
        coteRotie.setRegion(valleerhone);
        id = appellationDao.ajouter(coteRotie);
        coteRotie.setId(id);
        Appellation coteauxDie = new Appellation();
        coteauxDie.setNom("Coteaux-De-Die");
        coteauxDie.setRegion(valleerhone);
        id = appellationDao.ajouter(coteauxDie);
        coteauxDie.setId(id);
        Appellation cotesRhones = new Appellation();
        cotesRhones.setNom("Côtes-Du-Rhone");
        cotesRhones.setRegion(valleerhone);
        id = appellationDao.ajouter(cotesRhones);
        cotesRhones.setId(id);
        Appellation cotesRhoneVill = new Appellation();
        cotesRhoneVill.setNom("Côtes-Du-Rhône Villages");
        cotesRhoneVill.setRegion(valleerhone);
        id = appellationDao.ajouter(cotesRhoneVill);
        cotesRhoneVill.setId(id);
        Appellation cotesViravais = new Appellation();
        cotesViravais.setNom("Côtes-Du-Viravais");
        cotesViravais.setRegion(valleerhone);
        id = appellationDao.ajouter(cotesViravais);
        cotesViravais.setId(id);
        Appellation cremantDie = new Appellation();
        cremantDie.setNom("Crémant De Die");
        cremantDie.setRegion(valleerhone);
        id = appellationDao.ajouter(cremantDie);
        cremantDie.setId(id);
        Appellation crozes = new Appellation();
        crozes.setNom("Crozes-Hermitage");
        crozes.setRegion(valleerhone);
        id = appellationDao.ajouter(crozes);
        crozes.setId(id);
        Appellation gigondas = new Appellation();
        gigondas.setNom("Gigondas");
        gigondas.setRegion(valleerhone);
        id = appellationDao.ajouter(gigondas);
        gigondas.setId(id);
        Appellation grignan = new Appellation();
        grignan.setNom("Grignan-Les-Adhémar");
        grignan.setRegion(valleerhone);
        id = appellationDao.ajouter(grignan);
        grignan.setId(id);
        Appellation hermitage = new Appellation();
        hermitage.setNom("Hermitage");
        hermitage.setRegion(valleerhone);
        id = appellationDao.ajouter(hermitage);
        hermitage.setId(id);
        Appellation lirac = new Appellation();
        lirac.setNom("Lirac");
        lirac.setRegion(valleerhone);
        id = appellationDao.ajouter(lirac);
        lirac.setId(id);
        Appellation luberon = new Appellation();
        luberon.setNom("Luberon");
        luberon.setRegion(valleerhone);
        id = appellationDao.ajouter(luberon);
        luberon.setId(id);
        Appellation muscatBeaumeVen = new Appellation();
        muscatBeaumeVen.setNom("Muscat De Beaumes-De-Venise");
        muscatBeaumeVen.setRegion(valleerhone);
        id = appellationDao.ajouter(muscatBeaumeVen);
        muscatBeaumeVen.setId(id);
        Appellation rasteau = new Appellation();
        rasteau.setNom("Rasteau");
        rasteau.setRegion(valleerhone);
        id = appellationDao.ajouter(rasteau);
        rasteau.setId(id);
        Appellation stJoseph = new Appellation();
        stJoseph.setNom("Saint-Joseph");
        stJoseph.setRegion(valleerhone);
        id = appellationDao.ajouter(stJoseph);
        stJoseph.setId(id);
        Appellation stPeray = new Appellation();
        stPeray.setNom("Saint-Péray");
        stPeray.setRegion(valleerhone);
        id = appellationDao.ajouter(stPeray);
        stPeray.setId(id);
        Appellation tavel = new Appellation();
        tavel.setNom("Tavel");
        tavel.setRegion(valleerhone);
        id = appellationDao.ajouter(tavel);
        tavel.setId(id);
        Appellation vacqueyras = new Appellation();
        vacqueyras.setNom("Vacqueyras");
        vacqueyras.setRegion(valleerhone);
        id = appellationDao.ajouter(vacqueyras);
        vacqueyras.setId(id);
        Appellation ventoux = new Appellation();
        ventoux.setNom("Ventoux");
        ventoux.setRegion(valleerhone);
        id = appellationDao.ajouter(ventoux);
        ventoux.setId(id);Appellation vinsobres = new Appellation();
        vinsobres.setNom("Vinsobres");
        vinsobres.setRegion(valleerhone);
        id = appellationDao.ajouter(vinsobres);
        vinsobres.setId(id);


    }
}
