package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tintin.mat.winecellar.bo.Pays;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class PaysDao extends DAOBase {

    public static final String TABLE_NAME = "pays";
    public static final String KEY = "id";
    public static final String NOM = "nom";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOM + " TEXT );";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public PaysDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param pays la cave à ajouter à la base
     */
    public long ajouter(Pays pays) {
        long ret_value = -1;

        ContentValues values = new ContentValues();
        values.put(NOM, pays.getNom());
        open();
        ret_value = mDb.insert(TABLE_NAME, null, values);
        close();
        return ret_value;
    }

    public ArrayList<Pays> getAll(){
        ArrayList<Pays> listPays = new ArrayList<Pays>();
        open();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Pays pays = new Pays(cursor.getString(cursor.getColumnIndex(NOM)));
                pays.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                listPays.add(pays);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listPays;
    }
}
