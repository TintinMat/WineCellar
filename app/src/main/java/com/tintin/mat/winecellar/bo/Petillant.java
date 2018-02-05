package com.tintin.mat.winecellar.bo;

import java.io.Serializable;

/**
 * Created by Mat & Audrey on 03/11/2017.
 */

public class Petillant implements Serializable {

    public static final int NON_PETILLANT = 0;
    public static final int PETILLANT = 1;
    public static final int PERLE = 2;

    public static final String NON_PETILLANT_NOM = "Non petillant";
    public static final String PETILLANT_NOM = "Petillant";
    public static final String PERLE_NOM = "Perle";

    private int id;
    private String nom;

    public Petillant(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public Petillant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static String getNom(int id){
        if (id==NON_PETILLANT){
            return NON_PETILLANT_NOM;
        }else if (id==PETILLANT){
            return PETILLANT_NOM;
        }else if (id==PERLE) {
            return PERLE_NOM;
        }
        return null;
    }

    @Override
    public String toString() {
        return nom;
    }
}
