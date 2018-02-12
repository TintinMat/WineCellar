package com.tintin.mat.winecellar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.ResponseModels.InsertCaveResponseModel;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.dao.CaveDao;
import com.tintin.mat.winecellar.dao.ClayetteDao;
import com.tintin.mat.winecellar.utils.ApiClient;
import com.tintin.mat.winecellar.utils.ApiService;
import com.tintin.mat.winecellar.utils.Utils;

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

public class ModifierCaveActivity extends AppCompatActivity {

    private CaveDao caveDao = null;
    private Cave cave;

    private static int RESULT_LOAD_IMAGE = 1;
    private byte[] inputDataForPhoto = null;

    private ProgressDialog progressDialog;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.modifier_cave_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_cave);
        setTitle(R.string.toolbar_cave_modif);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Inserting");
        progressDialog.setMessage("Please wait ....");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null && getIntent().getExtras().get("Key") != null) {
            cave = (Cave) getIntent().getExtras().get("Key");
            setValues();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.create_cave:
                modifierCave();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* ============================================================================= */
    /* méthode pour mettre à jour les données */

    private void setValues(){

        // récupérer les infos de l'ihm
        EditText nomCave = (EditText)findViewById(R.id.nomCaveModifierEditText);
        EditText nbBouteilles = (EditText)findViewById(R.id.nbBouteillesModifierEditText);

        if (cave.getNom() != null){
            nomCave.setText(cave.getNom());
        }
        nbBouteilles.setText(""+cave.getNbBouteillesTheoriques());


    }

    /* ============================================================================= */



    private void modifierCave(){
        // récupérer les infos de l'ihm
        EditText nomCave = (EditText)findViewById(R.id.nomCaveModifierEditText);
        EditText nbBouteilles = (EditText)findViewById(R.id.nbBouteillesModifierEditText);

        if (nomCave != null && nbBouteilles != null && nomCave.getText().length()>0 && nbBouteilles.getText().length()>0 ){
            CaveDao caveDao = new CaveDao(this, null);
            try{
                // récupérer la photo si non vide
                if (inputDataForPhoto != null && inputDataForPhoto.length > 0) {
                    cave.setPhoto(inputDataForPhoto);
                }
                cave.setNom(nomCave.getText().toString());
                cave.setNbBouteillesTheoriques(new Integer(nbBouteilles.getText().toString()));
                long idCave = caveDao.modifier(cave);
                cave.setId(idCave);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_modifier_cave_ok, Toast.LENGTH_LONG);
                toast.show();
                finish();
            }catch (Exception ex){
                Log.e(TAG, "modifierCave ",ex );
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_modifier_cave_ko, Toast.LENGTH_LONG);
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
                final Uri imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                // on sauve l'image en byte[] pour l'ajouter ensuite en base (methode ajouterCave)
                InputStream imageStream2 = getContentResolver().openInputStream(imageUri);
                inputDataForPhoto = Utils.getBytes(imageStream2);
                ImageButton imageCaveButton = (ImageButton)findViewById(R.id.cavePhotoModifierImageButton);
                imageCaveButton.setImageBitmap(selectedImage);
                if (imageStream != null) { imageStream.close();}
                if (imageStream2 != null) { imageStream2.close();}

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (IOException ioe) {
                Log.e(TAG, "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
                Toast.makeText(this, "Impossible de sauver l'image", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
