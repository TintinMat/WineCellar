package com.tintin.mat.winecellar.bo;

import java.io.Serializable;

/**
 * Created by Mat & Audrey on 01/11/2017.
 */

public class Millesime implements Serializable {

    public final String NO_MILLESIME = "<aucun>";
    private int annee;

    public Millesime(int annee) {
        this.annee = annee;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    @Override
    public String toString() {
        if (getAnnee()!=0) {
            return ""+annee;
        }else{
            return NO_MILLESIME;
        }
    }
}
