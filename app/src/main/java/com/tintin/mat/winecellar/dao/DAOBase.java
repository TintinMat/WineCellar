package com.tintin.mat.winecellar.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public abstract class DAOBase {
    // Nous sommes à la première version de la base
    // Si je décide de la mettre à jour, il faudra changer cet attribut
    protected final static int VERSION = 6;
    // Le nom du fichier qui représente ma base
    protected final static String NOM = "database.db";

    protected SQLiteDatabase mDb = null;
    protected DatabaseHandler mHandler;


    public DAOBase(Context pContext, DatabaseHandler databaseHandler) {
        if (databaseHandler != null){
            this.mHandler = databaseHandler;
        }
        else {
            this.mHandler = new DatabaseHandler(pContext, NOM, null, VERSION);
        }
    }

    public SQLiteDatabase open() {
        // Pas besoin de fermer la dernière base puisque getWritableDatabase s'en charge
        /*if (getDb() != null){
            return getDb();
        }else {*/
            mDb = mHandler.getWritableDatabase();
            return mDb;
        //}
    }

    public void close() {
        if (getDb() != null) {
            mDb.close();
        }
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }


}

