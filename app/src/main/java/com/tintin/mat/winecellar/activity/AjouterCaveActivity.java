package com.tintin.mat.winecellar.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.ResponseModels.InsertCaveResponseModel;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.dao.CaveDao;
import com.tintin.mat.winecellar.dao.ClayetteDao;
import com.tintin.mat.winecellar.utils.ApiClient;
import com.tintin.mat.winecellar.utils.ApiService;
import com.tintin.mat.winecellar.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class AjouterCaveActivity extends StoragePermissions {

    private static int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri = null;

    private ProgressDialog progressDialog;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.ajouter_cave_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_cave);
        setTitle(R.string.toolbar_cave_nouvelle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Inserting");
        progressDialog.setMessage("Please wait ....");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.create_cave:
                ajouterCave();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ajouterCave(){
        // récupérer les infos de l'ihm
        EditText nomCave = (EditText)findViewById(R.id.nomCaveEditText);
        EditText nbClayettesCave = (EditText)findViewById(R.id.nbClayettesEditText);
        EditText nbBouteilles = (EditText)findViewById(R.id.nbBouteillesEditText);

        if (nomCave != null && nbBouteilles != null && nbClayettesCave != null && nomCave.getText().length()>0 && nbBouteilles.getText().length()>0 && nbClayettesCave.getText().length()>0){
            Cave cave = new Cave(nomCave.getText().toString(), new Integer(nbBouteilles.getText().toString()));
            CaveDao caveDao = new CaveDao(this, null);
            try{
                // récupérer la photo
                if (imageUri != null && imageUri.toString().length() > 0) {
                    cave.setPhotoPath(imageUri.toString());
                }
                long idCave = caveDao.ajouter(cave);
                cave.setId(idCave);
                ClayetteDao clayetteDao = new ClayetteDao(this, null);
                for (int i=0;i<new Integer(nbClayettesCave.getText().toString());i++){
                    clayetteDao.ajouter(new Clayette(cave));
                }
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_cave_ok, Toast.LENGTH_LONG);
                toast.show();
                finish();
            }catch (Exception ex){
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "ajouterCave ",ex );
                }
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_cave_ko, Toast.LENGTH_LONG);
                toast.show();
            }

        }else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_cave_arg_manquant, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public void takePhoto(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)  {
            try {
                GrantPermissionsForWriting();
                imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageButton imageCaveButton = (ImageButton)findViewById(R.id.cavePhotoImageButton);
                imageCaveButton.setImageBitmap(selectedImage);
                if (imageStream != null) { imageStream.close();}

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
                }
                Toast.makeText(this, "Impossible de sauver l'image", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }













    /***************************************************/

    /**
     * this method used to send data to server or our local server
     * @param cave
     */
    private void insertData(Cave cave){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<InsertCaveResponseModel> call = apiService.insertCave(cave.getNom(), cave.getNbBouteillesTheoriques());
        call.enqueue(new Callback<InsertCaveResponseModel>() {
            @Override
            public void onResponse(Call<InsertCaveResponseModel> call, Response<InsertCaveResponseModel> response) {

                InsertCaveResponseModel insertCaveResponseModel = response.body();

                //check the status code
                if(insertCaveResponseModel.getStatus()==1){
                    Toast.makeText(AjouterCaveActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else{
                    Toast.makeText(AjouterCaveActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<InsertCaveResponseModel> call, Throwable t) {
                Toast.makeText(AjouterCaveActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    /***************************************************/

}
