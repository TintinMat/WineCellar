package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Region;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class AppellationDao extends DAOBase {

    public static final String TABLE_NAME = "appellation";
    public static final String KEY = "id";
    public static final String NOM = "nom";
    public static final String FK_REGION = "id_region";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOM + " TEXT, " +
            FK_REGION + " INTEGER, FOREIGN KEY("+ FK_REGION +") REFERENCES "+ RegionDao.TABLE_NAME +"("+ RegionDao.KEY +")" +
            ");";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public AppellationDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param appellation la cave à ajouter à la base
     */
    public long ajouter(Appellation appellation) {
        long ret_value = -1;
        ContentValues values = new ContentValues();
        values.put(NOM, appellation.getNom());
        values.put(FK_REGION, appellation.getRegion().getId());
        open();
        ret_value = mDb.insert(TABLE_NAME, null, values);
        close();
        return ret_value;
    }

    public ArrayList<Appellation> getFromRegion(Region region){
        ArrayList<Appellation> listAppellations = new ArrayList<Appellation>();
        open();
        String whereClause = " WHERE "+TABLE_NAME+"."+FK_REGION+"="+region.getId();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME + whereClause, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Appellation appellation = new Appellation();
                appellation.setNom(cursor.getString(cursor.getColumnIndex(NOM)));
                appellation.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                listAppellations.add(appellation);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listAppellations;
    }
}
