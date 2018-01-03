package com.tintin.mat.winecellar.dao;

import com.tintin.mat.winecellar.bo.Petillant;

import java.util.ArrayList;

/**
 * Created by Mat & Audrey on 03/11/2017.
 */

public class PetillantDao {


    public ArrayList<Petillant> getAll(){
        ArrayList<Petillant> petillants = new ArrayList<Petillant>(3);
        petillants.add(new Petillant(Petillant.NON_PETILLANT, Petillant.NON_PETILLANT_NOM));
        petillants.add(new Petillant(Petillant.PETILLANT, Petillant.PETILLANT_NOM));
        petillants.add(new Petillant(Petillant.PERLE, Petillant.PERLE_NOM));

        return petillants;
    }
}
