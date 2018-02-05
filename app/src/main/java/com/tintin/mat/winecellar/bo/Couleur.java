package com.tintin.mat.winecellar.bo;

import java.io.Serializable;

/**
 * Created by Mat & Audrey on 03/11/2017.
 */

public class Couleur implements Serializable {

    public static final int ROUGE = 1;
    public static final int ROSE = 3;
    public static final int BLANC = 2;

    public static final String ROUGE_NOM = "Rouge";
    public static final String ROSE_NOM = "Rose";
    public static final String BLANC_NOM = "Blanc";

    private int id;
    private String nom;


    public Couleur(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }
    public Couleur() {
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
        if (id==ROUGE){
            return ROUGE_NOM;
        }else if (id==ROSE){
            return ROSE_NOM;
        }else if (id==BLANC) {
            return BLANC_NOM;
        }
        return null;
    }

    @Override
    public String toString() {
        return nom;
    }
}
