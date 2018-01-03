package com.tintin.mat.winecellar.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Region;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class RegionDao extends DAOBase {

    public static final String TABLE_NAME = "region";
    public static final String KEY = "id";
    public static final String NOM = "nom";
    public static final String FK_PAYS = "id_pays";


    public static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NOM + " TEXT, " +
            FK_PAYS + " INTEGER, FOREIGN KEY("+ FK_PAYS +") REFERENCES "+ PaysDao.TABLE_NAME +"("+ PaysDao.KEY +")" +
            ");";

    public static final String TABLE_DROP =  "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    public RegionDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /**
     * @param region la cave à ajouter à la base
     */
    public long ajouter(Region region) {
        long ret_value = -1;
        ContentValues values = new ContentValues();
        values.put(NOM, region.getNom());
        values.put(FK_PAYS, region.getPays().getId());
        open();
        ret_value = mDb.insert(TABLE_NAME, null, values);
        close();
        return ret_value;
    }

    public ArrayList<Region> getFromPays(Pays pays){
        ArrayList<Region> listRegions = new ArrayList<Region>();
        open();
        String whereClause = " WHERE "+TABLE_NAME+"."+FK_PAYS+"="+pays.getId();
        Cursor cursor = mDb.rawQuery( "SELECT * FROM " + TABLE_NAME + whereClause, null);
        if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()) {
            do {
                Region region = new Region();
                region.setNom(cursor.getString(cursor.getColumnIndex(NOM)));
                region.setId(cursor.getInt(cursor.getColumnIndex(KEY)));
                listRegions.add(region);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return listRegions;
    }


}
