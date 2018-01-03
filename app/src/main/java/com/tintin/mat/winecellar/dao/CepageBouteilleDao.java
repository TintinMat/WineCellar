package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Cepage;
import com.tintin.mat.winecellar.bo.Clayette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tintin.mat.winecellar.dao.ClayetteDao.FK_CAVE;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class CepageBouteilleDao extends DAOBase {

    public static final String TABLE_NAME = "lien_cepage_bouteille";
    public static final String KEY = "id";
    public static final String POURCENTAGE = "pourcentage";
    public static final String FK_BOUTEILLE = "id_bouteille";
    public static final String FK_CEPAGE = "id_cepage";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + POURCENTAGE + " TEXT, " +
            FK_BOUTEILLE + " INTEGER, " + FK_CEPAGE + " INTEGER, FOREIGN KEY("+ FK_BOUTEILLE +") REFERENCES "+BouteilleDao.TABLE_NAME+"("+ BouteilleDao.KEY +"), " +
            "FOREIGN KEY("+ FK_CEPAGE +") REFERENCES "+CepageDao.TABLE_NAME+"("+ CepageDao.KEY +")" +
            " );";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public CepageBouteilleDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     */
    public boolean ajouter(Bouteille bouteille, Cepage cepage, int pourcent) {

        boolean ret_value = false;
        ContentValues values = new ContentValues();
        values.put(FK_BOUTEILLE, bouteille.getId());
        values.put(FK_CEPAGE, cepage.getId());
        values.put(POURCENTAGE, pourcent);
        open();
        ret_value = (mDb.insert(TABLE_NAME, null, values) != -1);
        close();
        return ret_value;
    }

    /* return Map de pourcentage pour chaque cepage*/
    public Map<Cepage, Integer> get(Bouteille bouteille){
        Map<Cepage, Integer> listCepage = new HashMap<Cepage, Integer>();
        open();
        String whereClause = " WHERE "+TABLE_NAME+"."+FK_BOUTEILLE+"="+bouteille.getId();
        Cursor cursor = mDb.rawQuery( "SELECT "+TABLE_NAME+".*, "+CepageDao.TABLE_NAME+"."+CepageDao.NOM+" FROM " + TABLE_NAME +", "+ CepageDao.TABLE_NAME + whereClause, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Cepage c = new Cepage();
                c.setNom(cursor.getString(cursor.getColumnIndex(CepageDao.NOM)));
                c.setId(cursor.getInt(cursor.getColumnIndex(FK_CEPAGE)));
                listCepage.put(c, cursor.getInt(cursor.getColumnIndex(POURCENTAGE)));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listCepage;
    }

    public int supprimer(Bouteille b){
        open();
        int ret = mDb.delete(TABLE_NAME, " "+FK_BOUTEILLE+"=?", new String[]{new Long(b.getId()).toString()});
        close();
        return ret;
    }


}
