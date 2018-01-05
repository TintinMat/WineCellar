package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Preferences;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 04/01/2018.
 */

public class PreferencesDao extends DAOBase {

    public static final String TABLE_NAME = "preferences";
    public static final String KEY = "cle";
    public static final String VALUE = "valeur";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " TEXT PRIMARY KEY, " + VALUE + " TEXT );";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public PreferencesDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    public long ajouter(Preferences preferences) {
        long ret_value = -1;

        ContentValues values = new ContentValues();
        values.put(KEY, preferences.getCle());
        values.put(VALUE, preferences.getValeur());
        open();
        ret_value = mDb.insert(TABLE_NAME, null, values);
        close();
        return ret_value;
    }


    public ArrayList<Preferences> getByCle(String cle){
        ArrayList<Preferences> listPrefs = new ArrayList<Preferences>();
        open();
        String whereClause = " WHERE LOWER("+TABLE_NAME+"."+KEY+")='"+cle.toLowerCase()+"'";
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME + whereClause, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Preferences p = new Preferences();
                p.setCle(cursor.getString(cursor.getColumnIndex(KEY)));
                p.setValeur(cursor.getString(cursor.getColumnIndex(VALUE)));
                listPrefs.add(p);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listPrefs;
    }

    public long ajouterOuModifier(Preferences preferences){
        // check si elle existe déjà
        if (getByCle(preferences.getCle()).isEmpty()){
            return ajouter(preferences);
        }else{
            ContentValues values = new ContentValues();
            values.put(VALUE, preferences.getValeur());
            open();
            long ret_value = mDb.update(TABLE_NAME, values, KEY  + " = ?", new String[] {preferences.getCle()});
            close();
            return ret_value;
        }
    }
}
