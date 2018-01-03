package com.tintin.mat.winecellar.dao;

import com.tintin.mat.winecellar.bo.Couleur;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 03/11/2017.
 */

public class CouleurDao {


    public ArrayList<Couleur> getAll(){
        ArrayList<Couleur> couleurs = new ArrayList<Couleur>(3);
        couleurs.add(new Couleur(Couleur.ROUGE, Couleur.ROUGE_NOM));
        couleurs.add(new Couleur(Couleur.BLANC, Couleur.BLANC_NOM));
        couleurs.add(new Couleur(Couleur.ROSE, Couleur.ROSE_NOM));

        return couleurs;
    }
}
