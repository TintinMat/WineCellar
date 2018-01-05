package com.tintin.mat.winecellar.bo;

import java.io.Serializable;

/**
 * Created by Mat & Audrey on 15/10/2017.
 */

public class Cave implements Serializable {

    private long id;
    private String nom;
    private int nbBouteillesTheoriques;
    private byte[] photo;

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

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return nom;
    }
}
