package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Clayette;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class ClayetteDao extends DAOBase {

    public static final String TABLE_NAME = "clayette";
    public static final String KEY = "id";
    public static final String NOM = "nom";
    public static final String FK_CAVE = "id_cave";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOM + " TEXT, " +
            FK_CAVE + " INTEGER, FOREIGN KEY("+ FK_CAVE +") REFERENCES "+CaveDao.TABLE_NAME+"("+ CaveDao.KEY +")" +
            " );";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public ClayetteDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param clayette la cave à ajouter à la base
     */
    public boolean ajouter(Clayette clayette) {

        boolean ret_value = false;
        ContentValues values = new ContentValues();
        values.put(NOM, clayette.getNom());
        values.put(FK_CAVE, clayette.getCave().getId());
        open();
        ret_value = (mDb.insert(TABLE_NAME, null, values) != -1);
        close();
        return ret_value;
    }

    public long nbClayettes(){
        long cnt = 0;
        open();
        cnt  = DatabaseUtils.queryNumEntries(mDb, TABLE_NAME);
        close();
        return cnt;
    }

    public ArrayList<Clayette> getFromCave(Cave cave){
        ArrayList<Clayette> listClayettes = new ArrayList<Clayette>();
        open();
        String whereClause = " WHERE "+TABLE_NAME+"."+FK_CAVE+"="+cave.getId();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME + whereClause, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Clayette clayette = new Clayette();
                clayette.setNom(cursor.getString(cursor.getColumnIndex(NOM)));
                clayette.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                listClayettes.add(clayette);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listClayettes;
    }

    public int supprimer(Clayette clayette){
        open();
        int ret = mDb.delete(TABLE_NAME, " "+KEY+"=?", new String[]{new Long(clayette.getId()).toString()});
        close();
        return ret;
    }

    public int modifier(Clayette clayette){
        int ret_value = -1;

        ContentValues values = new ContentValues();
        values.put(NOM, clayette.getNom());
        open();
        ret_value = mDb.update(TABLE_NAME, values, KEY  + " = ?", new String[] {String.valueOf(clayette.getId())});
        close();
        return ret_value;

    }

}
