package com.tintin.mat.winecellar.bo;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class Cave implements Serializable {

    private long id;
    private String nom;
    private int nbBouteillesTheoriques;
    private String photoPath = null;
    private String vignettePath = null;

    public Cave() {
    }

    public Cave(String nom, int nbBouteillesTheoriques) {
        this.nom = nom;
        this.nbBouteillesTheoriques = nbBouteillesTheoriques;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNbBouteillesTheoriques() {
        return nbBouteillesTheoriques;
    }

    public void setNbBouteillesTheoriques(int nbBouteillesTheoriques) {
        this.nbBouteillesTheoriques = nbBouteillesTheoriques;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getVignettePath() {
        return vignettePath;
    }

    public void setVignettePath(String vignettePath) {
        this.vignettePath = vignettePath;
    }

    @Override
    public String toString() {
        return nom;
    }
}
