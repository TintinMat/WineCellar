package com.tintin.mat.winecellar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.adapter.ClayetteAdapter;
import com.tintin.mat.winecellar.adapter.ClayetteSwipeAdapter;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.dao.CaveDao;
import com.tintin.mat.winecellar.dao.ClayetteDao;
import com.tintin.mat.winecellar.interfce.ClayetteInterface;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class VisualiserCaveActivity extends AppCompatActivity implements ClayetteInterface{

    private Cave cave;
    private ClayetteDao clayetteDao = null;
    private ListView listeViewClayettes;
    private ClayetteSwipeAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.visualiser_cave_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualiser_cave);
        // récupération de la cave passée en paramètre
        cave = (Cave) getIntent().getExtras().get("Key");
        // mettre ici le nom de la cave passée en paramètre
        setTitle(cave.getNom());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_clayette);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterClayette();
            }
        });
    }

    @Override
    public void onResume(){
        CaveDao caveDao = new CaveDao(this, null);
        cave = caveDao.get(cave.getId());
        setTitle(cave.getNom());
        afficherListeClayettes();
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.ajouter_bouteille:
                ajouterBouteille();
                return true;
            case R.id.modify_cave:
                modifierCave();
                return true;
            case R.id.delete_cave:
                deleteCave();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ajouterBouteille(){
        Intent appel = new Intent(this, AjouterBouteilleActivity.class);
        appel.putExtra("Key", (Serializable) cave);
        if (cave != null) {
            appel.putExtra("idCave", cave.getId());
        }
        startActivity(appel);
    }

    private void modifierCave(){
        Intent appel = new Intent(this, ModifierCaveActivity.class);
        appel.putExtra("Key", (Serializable) cave);
        startActivity(appel);
    }

    private void deleteCave(){
        // récupérer les infos de l'ihm
        BouteilleDao btlleDao = new BouteilleDao(this, null);
        int nbBtllesDansCave = btlleDao.getAllNotDegustedAssociatedWithCave(cave).size();
        if ( nbBtllesDansCave > 0) {
            Toast.makeText(getApplicationContext(), R.string.message_supprimer_cave_ko_bouteille, Toast.LENGTH_SHORT).show();
        } else {
            CaveDao caveDao = new CaveDao(this, null);
            try {
                caveDao.supprimer(cave);
                finish();
            } catch (Exception ex) {
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "deleteCave ", ex);
                }
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_cave_ko, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void afficherListeClayettes(){

        // récupérer les bouteilles
        clayetteDao = new ClayetteDao(this,null);
        ArrayList<Clayette> listeClayettes = clayetteDao.getFromCave(cave);

        listeViewClayettes = (ListView)findViewById(R.id.listeClayettes);
        TextView textClayetteNb = (TextView) findViewById(R.id.textClayetteNb);

        if (listeClayettes == null || listeClayettes.size() == 0){
            textClayetteNb.setText(R.string.no_bouteille);
            textClayetteNb.setVisibility(View.VISIBLE);
            listeViewClayettes.setVisibility(View.INVISIBLE);
        }else {

            BouteilleDao bouteilleDao = new BouteilleDao(this,null);
            ArrayList<Bouteille> listeBouteilles = null;
            for(Clayette clayette:listeClayettes){
                listeBouteilles = bouteilleDao.getAllNotDegustedAssociatedWithClayette(clayette);
                clayette.setBouteilles(listeBouteilles);
            }

            adapter = new ClayetteSwipeAdapter(VisualiserCaveActivity.this, listeClayettes, this);
            listeViewClayettes.setAdapter(adapter);
            adapter.setMode(Attributes.Mode.Single);

            listeViewClayettes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Clayette c = (Clayette) adapter.getItem(position);

                    Intent intent = new Intent(VisualiserCaveActivity.this, VisualiserClayetteActivity.class);
                    intent.putExtra("Key", (Serializable) c);
                    if (cave != null) {
                        intent.putExtra("idCave", cave.getId());
                    }
                    startActivity(intent);
                    //Log.e("ListView", "onItemClick : position "+position);

                }
            });
            listeViewClayettes.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Log.e("ListView", "OnTouch");
                    return false;
                }
            });
            listeViewClayettes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(), "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            listeViewClayettes.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //Log.e("ListView", "onScrollStateChanged");
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //Log.e("ListView", "onScroll : firstVisibleItem "+firstVisibleItem+" ; visibleItemCount "+ visibleItemCount+" ; totalItemCount "+totalItemCount);
                }
            });

            listeViewClayettes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Log.e("ListView", "onItemSelected:" + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Log.e("ListView", "onNothingSelected:");
                }
            });

            textClayetteNb.setVisibility(View.INVISIBLE);
            listeViewClayettes.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void editClayette(int position) {

        // position dans la liste visible
        int ihmPosition = position - listeViewClayettes.getFirstVisiblePosition();
        // selon les cas, l'ihm ne voit pas correctement la position (il renvoie alors 0)
        if (ihmPosition>=0) {
            ((SwipeLayout) (listeViewClayettes.getChildAt(ihmPosition))).close(true);
            final Clayette clayette = (Clayette) adapter.getItem(position);

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText edittext = new EditText(this);
            edittext.setText(clayette.toString());
            alert.setMessage(cave.getNom() + " - modifier la clayette");
            alert.setTitle("Entrer le nom pour cette clayette");

            alert.setView(edittext);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    try {
                        Editable youEditTextValue = edittext.getText();
                        ClayetteDao clayetteDao = new ClayetteDao(VisualiserCaveActivity.this, null);
                        clayette.setNom(youEditTextValue.toString());
                        clayetteDao.modifier(clayette);
                    } catch (Exception ex) {
                        if (BuildConfig.DEBUG){
                            Log.e(TAG, "editClayette ", ex);
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_modifier_clayette_ko, Toast.LENGTH_LONG);
                        toast.show();
                    }

                    ((ClayetteSwipeAdapter) listeViewClayettes.getAdapter()).notifyDataSetChanged();
                }
            });

            alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });

            alert.show();
        }

    }

    @Override
    public void supprimerClayette(int position) {

        // position dans la liste visible
        int ihmPosition = position - listeViewClayettes.getFirstVisiblePosition();
        // selon les cas, l'ihm ne voit pas correctement la position (il renvoie alors 0)
        if (ihmPosition>=0) {
            Clayette clayette = (Clayette) adapter.getItem(position);

            if (clayette.listeBouteilles() != null && clayette.listeBouteilles().size() > 0) {
                Toast.makeText(getApplicationContext(), R.string.message_supprimer_clayette_ko_bouteille, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    ClayetteDao clayetteDao = new ClayetteDao(this, null);
                    int nb = clayetteDao.supprimer(clayette);
                    if (nb > 0) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_clayette_ok, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    onResume();
                } catch (Exception ex) {
                    if (BuildConfig.DEBUG){
                        Log.e(TAG, "supprimerClayette ", ex);
                    }
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_clayette_ko, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    private void ajouterClayette(){
        ClayetteDao clayetteDao = new ClayetteDao(this, null);
        clayetteDao.ajouter(new Clayette(cave));
        onResume();
    }
}
