package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tintin.mat.winecellar.bo.Cave;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class CaveDao extends DAOBase {

    public static final String TABLE_NAME = "cave";
    public static final String KEY = "id";
    public static final String NOM = "nom";
    public static final String NBBOUTEILLES = "nb_bouteilles_theoriques";
    public static final String PHOTO = "photo";

    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY, " + NOM + " TEXT, " +
            NBBOUTEILLES + " INTEGER, " + PHOTO + " BLOB);";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public CaveDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param cave la cave à ajouter à la base
     */
    public long ajouter(Cave cave) {
        // CODE
        long ret_value = -1;

        try {
            ContentValues values = new ContentValues();
            values.clear();
            if (cave.getNom()!= null) {
                values.put(NOM, cave.getNom());
            }
            values.put(NBBOUTEILLES, cave.getNbBouteillesTheoriques());
            if (cave.getPhoto() != null) {
                values.put(PHOTO, cave.getPhoto());
            }
            open();
            ret_value = mDb.insert(TABLE_NAME, null, values);
            close();
        }catch(Exception e){
            Log.e(TAG, "CaveDao.ajouter ", e);
        }
        return ret_value;

    }

    public boolean supprimer(Cave cave) {
        // CODE
        open();
        boolean ret_value = false;
        // d'abord supprimer les clayettes associées
        if ( mDb.delete(ClayetteDao.TABLE_NAME, " "+ClayetteDao.FK_CAVE+"="+cave.getId(), null) != -1){
            ret_value = (mDb.delete(TABLE_NAME, " "+KEY+"="+cave.getId(), null) > 0) ;
        }
        close();
        return ret_value;

    }

    public long nbCave(){
        long cnt = 0;
        open();
        cnt  = DatabaseUtils.queryNumEntries(mDb, TABLE_NAME);
        close();
        return cnt;
    }

    public ArrayList<Cave> getAll(){
        ArrayList<Cave> listCaves = new ArrayList<Cave>();
        open();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME, null);
        try{
            if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
                do {
                    Cave cave = new Cave(cursor.getString(cursor.getColumnIndex(NOM)), cursor.getInt(cursor.getColumnIndex(NBBOUTEILLES)));
                    cave.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                    cave.setPhoto(cursor.getBlob(cursor.getColumnIndex(PHOTO)));
                    listCaves.add(cave);
                }
                while (cursor.moveToNext());
            }
        }catch(Exception e){
            Log.e(TAG, "CaveDao.getAll ",e );
        }
        cursor.close();
        close();
        return listCaves;
    }

}
