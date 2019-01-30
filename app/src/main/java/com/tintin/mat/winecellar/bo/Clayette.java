package com.tintin.mat.winecellar.bo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 16/10/2017.
 */

public class Clayette implements Serializable {

    private long id;
    private String nom;
    private ArrayList<Bouteille> listeBouteilles;
    private Cave cave;

    public void addBouteille(Bouteille bouteille){
        if (bouteille != null){
            if (listeBouteilles == null){
                listeBouteilles = new ArrayList<Bouteille>();
            }
            listeBouteilles.add(bouteille);
        }
    }

    public void delBouteille(Bouteille bouteille){
        if (bouteille != null){
            if (listeBouteilles != null){
                listeBouteilles.remove(bouteille);
            }
        }
    }

    public Clayette(){
    }

    public Clayette(Cave cave) {
        this.nom = "";
        this.cave = cave;
    }

    public Clayette(String nom, ArrayList<Bouteille> listeBouteilles) {
        this.nom = nom;
        this.listeBouteilles = listeBouteilles;
    }

    public ArrayList<Bouteille> listeBouteilles() {
        return listeBouteilles;
    }

    public void setBouteilles(ArrayList<Bouteille> listeBouteilles) {
        this.listeBouteilles = listeBouteilles;
    }

    public String getNom() {
        return toString();
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Cave getCave() {
        return cave;
    }

    public void setCave(Cave cave) {
        this.cave = cave;
    }

    @Override
    public String toString() {
        if (nom != null && nom.length()>0) {
            return nom;
        }
        else{
            return "Clayette n°"+id;
        }
    }
}
