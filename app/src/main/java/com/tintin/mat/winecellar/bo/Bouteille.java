package com.tintin.mat.winecellar.bo;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Mat & Audrey on 16/10/2017.
 */

public class Bouteille implements Serializable, Comparable<Bouteille> {

    private long id;
    private String domaine;
    private Appellation appellation;
    private Millesime millesime;
    private Couleur couleur;
    private Petillant petillant;

    private float prix;
    private String lieuDachat;
    private int dateDachat;
    private int anneeDegustation;
    private String commentaires;
    private boolean bio;
    private int apogeeMin;
    private int apogeeMax;

    private Clayette clayette;
    private String photoPath = null;
    private String vignettePath = null;
    private byte[] vignetteBitmap = null;

    private float rating;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public Appellation getAppellation() {
        return appellation;
    }

    public void setAppellation(Appellation appellation) {
        this.appellation = appellation;
    }

    public Millesime getMillesime() {
        return millesime;
    }

    public void setMillesime(Millesime millesime) {
        this.millesime = millesime;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public void setCouleur(Couleur couleur) {
        this.couleur = couleur;
    }

    public Petillant getPetillant() {
        return petillant;
    }

    public void setPetillant(Petillant petillant) {
        this.petillant = petillant;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getLieuDachat() {
        return lieuDachat;
    }

    public void setLieuDachat(String lieuDachat) {
        this.lieuDachat = lieuDachat;
    }

    public int getAnneeDegustation() {
        return anneeDegustation;
    }

    public void setAnneeDegustation(int anneeDegustation) {
        this.anneeDegustation = anneeDegustation;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public Clayette getClayette() {
        return clayette;
    }

    public void setClayette(Clayette clayette) {
        this.clayette = clayette;
    }

    public int getDateDachat() {
        return dateDachat;
    }

    public void setDateDachat(int dateDachat) {
        this.dateDachat = dateDachat;
    }

    public boolean isBio() {
        return bio;
    }

    public void setBio(boolean bio) {
        this.bio = bio;
    }

    public String getPhotoPath() {
        return photoPath;
    }
    public String getVignettePath() {
        return vignettePath;
    }
/*
    public byte[] getVignetteBitmap() {
        return vignetteBitmap;
    }

    public void setVignetteBitmap(byte[] vignetteBitmap) {
        this.vignetteBitmap = vignetteBitmap;
    }
*/
    public void setVignettePath(String vignettePath) {
        this.vignettePath = vignettePath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getApogeeMin() {
        return apogeeMin;
    }

    public void setApogeeMin(int apogeeMin) {
        this.apogeeMin = apogeeMin;
    }

    public int getApogeeMax() {
        return apogeeMax;
    }

    public void setApogeeMax(int apogeeMax) {
        this.apogeeMax = apogeeMax;
    }

    public float getRating(){ return rating;}

    public void setRating(float rating){ this.rating = rating; }

    @Override
    public int compareTo(Bouteille b) {
        return this.anneeDegustation > b.anneeDegustation ? -1 : 1;
    }

}
