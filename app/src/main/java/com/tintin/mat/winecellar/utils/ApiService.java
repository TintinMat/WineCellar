package com.tintin.mat.winecellar.utils;

/**
 * Created by Mat & Audrey on 04/01/2018.
 */


import com.tintin.mat.winecellar.ResponseModels.InsertCaveResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by putuguna on 1/24/2017.
 */

public interface ApiService {
    @FormUrlEncoded
    @POST("winecellar/InsertCave.php")
    Call<InsertCaveResponseModel> insertCave(@Field("nom") String nom, @Field("nb_bouteilles_theoriques") int nbBouteillesTheoriques);
}