package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tintin.mat.winecellar.bo.Cepage;
import com.tintin.mat.winecellar.bo.Pays;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class CepageDao extends DAOBase {

    public static final String TABLE_NAME = "cepage";
    public static final String KEY = "id";
    public static final String NOM = "nom";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOM + " TEXT );";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public CepageDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param cepage la cave à ajouter à la base
     */
    public long ajouter(Cepage cepage) {
        long ret_value = -1;

        ContentValues values = new ContentValues();
        values.put(NOM, cepage.getNom());
        open();
        ret_value = mDb.insert(TABLE_NAME, null, values);
        close();
        return ret_value;
    }

    public ArrayList<Cepage> getAll(){
        ArrayList<Cepage> listCepages = new ArrayList<Cepage>();
        open();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Cepage cepage = new Cepage(cursor.getString(cursor.getColumnIndex(NOM)));
                cepage.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                listCepages.add(cepage);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listCepages;
    }
}
