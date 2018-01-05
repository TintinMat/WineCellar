package com.tintin.mat.winecellar.bo;

/**
 * Created by Mat & Audrey on 04/01/2018.
 */

public class Preferences {

    public final static String SAUVEGARDE_CLOUD = "sauvegardeCloud";
    public final static String SAUVEGARDE_PHOTOS = "sauvegardePhotos";
    public final static String LOGIN_CONNEXION = "loginDeConnexion";

    public final static String YES = "Y";
    public final static String NO  = "N";

    private String cle;
    private String valeur;


    public Preferences() {
    }

    public Preferences(String cle, String valeur) {
        this.cle = cle;
        this.valeur = valeur;
    }

    public String getCle() {
        return cle;
    }

    public void setCle(String cle) {
        this.cle = cle;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
