package com.tintin.mat.winecellar.bo;

/**
 * Created by Mat & Audrey on 16/10/2017.
 */

public class Cepage {

    private long id;
    private String nom;

    public Cepage(){
        this.nom = "";
    }

    public Cepage(String nom){
        this.nom = nom;
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

    /**
     * Pay attention here, you have to override the toString method as the
     * ArrayAdapter will reads the toString of the given object for the name
     *
     * @return nom
     */
    @Override
    public String toString() {
        return nom;
    }
}
