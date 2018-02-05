package com.tintin.mat.winecellar.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Appellation;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.bo.Couleur;
import com.tintin.mat.winecellar.bo.Millesime;
import com.tintin.mat.winecellar.bo.Pays;
import com.tintin.mat.winecellar.bo.Petillant;
import com.tintin.mat.winecellar.bo.Region;
import com.tintin.mat.winecellar.dao.AppellationDao;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.dao.CaveDao;
import com.tintin.mat.winecellar.dao.ClayetteDao;
import com.tintin.mat.winecellar.dao.CouleurDao;
import com.tintin.mat.winecellar.dao.PaysDao;
import com.tintin.mat.winecellar.dao.PetillantDao;
import com.tintin.mat.winecellar.dao.RegionDao;
import com.tintin.mat.winecellar.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 18/10/2017.
 */

public class AjouterBouteilleActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private byte[] inputDataForPhoto = null;
    private EditText dateDachatEditText;
    private int dateDachatIntFormat;

    private Clayette clayette = null;
    private Appellation appellationChosen = null;
    private Millesime millesimeChosen = null;
    private Clayette clayetteChosen = null;
    private Couleur couleurChosen = null;
    private Petillant petillantChosen = null;
    private Integer qtyChosen = 0;
    private Integer apogeeMinChosen = 0;
    private Integer apogeeMaxChosen = 0;

    private PaysDao paysDao = null;
    private RegionDao regionDao = null;
    private AppellationDao appellationDao = null;
    private BouteilleDao bouteilleDao = null;
    private CaveDao caveDao = null;
    private ClayetteDao clayetteDao = null;
    private CouleurDao couleurDao = null;
    private PetillantDao petillantDao = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.ajouter_bouteille_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_bouteille);
        setTitle(R.string.toolbar_bouteille_nouvelle);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("KeyClayette") != null) {
            clayette = (Clayette) getIntent().getExtras().get("KeyClayette");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.create_bouteille:
                ajouterBouteille();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /* ============================================================================= */
    /* méthode principale d'ajout de la bouteille */

    private void ajouterBouteille(){
        // récupérer les infos de l'ihm
        EditText nomDomaine = (EditText)findViewById(R.id.nomDomaineEditText);
        EditText prix = (EditText)findViewById(R.id.prixDachatEditText);
        EditText lieu = (EditText)findViewById(R.id.lieuDachatEditText);
        EditText comm = (EditText)findViewById(R.id.commentairesEditText);
        CheckBox bio = (CheckBox)findViewById(R.id.bioCheckBox);

        if (nomDomaine == null || nomDomaine.getText().length()==0){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_bouteille_ko_nomDomaine, Toast.LENGTH_LONG);
            toast.show();
        } else if (clayetteChosen == null || clayetteChosen.toString() == null || clayetteChosen.toString().length()==0){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_bouteille_ko_clayette, Toast.LENGTH_LONG);
            toast.show();
        } else {
            bouteilleDao = new BouteilleDao(this,null);
            for (int i=0;i<qtyChosen;i++){
                Bouteille b = new Bouteille();
                b.setCommentaires(comm.getText().toString());
                b.setDateDachat(dateDachatIntFormat);
                b.setDomaine(nomDomaine.getText().toString());
                b.setLieuDachat(lieu.getText().toString());
                b.setMillesime(millesimeChosen);
                if (prix.getText().length()>0) {
                    b.setPrix(new Float(prix.getText().toString()));
                }
                b.setPetillant(petillantChosen);
                //b.setAnneeDegustation();
                b.setClayette(clayetteChosen);
                b.setCouleur(couleurChosen);
                b.setBio(bio.isChecked());
                b.setAppellation(appellationChosen);
                b.setPhoto(inputDataForPhoto);
                b.setApogeeMin(apogeeMinChosen);
                b.setApogeeMax(apogeeMaxChosen);
                try{
                    bouteilleDao.ajouter(b);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_bouteille_ok, Toast.LENGTH_LONG);
                    toast.show();
                    finish();
                }catch (Exception ex){
                    Log.e(TAG, "ajouterBouteille ",ex );
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_bouteille_ko, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }

    }


    private void prepareUI(){

        // millesime
        int year = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<Millesime> listeAnnees = new ArrayList<Millesime>();
        listeAnnees.add(new Millesime(0));
        for (int y=year+1;y>=1900;y--){
            listeAnnees.add(new Millesime(y));
        }
        Spinner spinnerMillesime = (Spinner) findViewById(R.id.millesimeSpinner);
        ArrayAdapter<Millesime> adapter =
                new ArrayAdapter<Millesime>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeAnnees);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerMillesime.setAdapter(adapter);
        spinnerMillesime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                millesimeChosen  = (Millesime)parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //populer les spinner pays/region/appellation
        populatePays();
        populateCave();
        populateCouleur();
        populatePetillant();
        populateQty();
        populateApogees();

        dateDachatEditText=(EditText)findViewById(R.id.dateDachatEditText);
        dateDachatEditText.setInputType(InputType.TYPE_NULL);
        dateDachatEditText.setLongClickable(false);
        dateDachatEditText.setOnClickListener(this);
        dateDachatEditText.setOnFocusChangeListener(this);

    }

    /* ============================================================================= */
    /* méthodes pour populer les spinner */

    private void populatePays(){

        if (paysDao == null){
            paysDao = new PaysDao(this, null);
        }
        ArrayList<Pays> listePays = paysDao.getAll();
        Spinner spinnerPays = (Spinner) findViewById(R.id.paysSpinner);
        ArrayAdapter<Pays> adapter =
                new ArrayAdapter<Pays>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listePays);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerPays.setAdapter(adapter);

        spinnerPays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                /*Toast toast = Toast.makeText(getApplicationContext(), ""+((Pays)parentView.getItemAtPosition(position)).getId(), Toast.LENGTH_LONG);
                toast.show();*/
                populateRegion((Pays)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void populateRegion(Pays pays){

        if (regionDao == null){
            regionDao = new RegionDao(this, null);
        }
        ArrayList<Region> listeRegions = regionDao.getFromPays(pays);
        Spinner spinnerRegions = (Spinner) findViewById(R.id.regionSpinner);
        ArrayAdapter<Region> adapter =
                new ArrayAdapter<Region>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeRegions);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerRegions.setAdapter(adapter);

        spinnerRegions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                populateAppellation((Region)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void populateAppellation(Region region){

        if (appellationDao == null){
            appellationDao = new AppellationDao(this, null);
        }
        ArrayList<Appellation> listeAppellations = appellationDao.getFromRegion(region);
        Spinner spinnerAppellations = (Spinner) findViewById(R.id.appellationSpinner);
        ArrayAdapter<Appellation> adapter =
                new ArrayAdapter<Appellation>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeAppellations);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAppellations.setAdapter(adapter);

        spinnerAppellations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                appellationChosen = (Appellation)parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    private void populateCave(){

        if (caveDao == null){
            caveDao = new CaveDao(this, null);
        }
        ArrayList<Cave> listeCaves = caveDao.getAll();
        Spinner spinnerCaves = (Spinner) findViewById(R.id.caveSpinner);
        ArrayAdapter<Cave> adapter =
                new ArrayAdapter<Cave>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeCaves);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCaves.setAdapter(adapter);

        // positionner la valeur
        try{
            for(int i=0;i<adapter.getCount();i++){
                if (clayette.getCave().getId() == adapter.getItem(i).getId()){
                    spinnerCaves.setSelection(i);
                    break;
                }
            }
        }catch (NullPointerException npe){
            //on fait rien
        }

        spinnerCaves.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                populateClayette((Cave)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void populateClayette(Cave c){

        if (clayetteDao == null){
            clayetteDao = new ClayetteDao(this, null);
        }
        ArrayList<Clayette> listeClayettes = clayetteDao.getFromCave(c);
        Spinner spinnerClayettes = (Spinner) findViewById(R.id.clayetteSpinner);
        ArrayAdapter<Clayette> adapter =
                new ArrayAdapter<Clayette>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeClayettes);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerClayettes.setAdapter(adapter);

        try{
            for(int i=0;i<adapter.getCount();i++){
                if (clayette.getId() == adapter.getItem(i).getId()){
                    spinnerClayettes.setSelection(i);
                    break;
                }
            }
        }catch (NullPointerException npe){
            //on fait rien
        }

        spinnerClayettes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                clayetteChosen = (Clayette)parentView.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    private void populateCouleur(){

        if (couleurDao == null){
            couleurDao = new CouleurDao();
        }
        ArrayList<Couleur> listeCouleurs = couleurDao.getAll();
        Spinner spinnerCouleurs = (Spinner) findViewById(R.id.couleurSpinner);
        ArrayAdapter<Couleur> adapter =
                new ArrayAdapter<Couleur>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeCouleurs);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCouleurs.setAdapter(adapter);

        spinnerCouleurs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                couleurChosen = ((Couleur)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void populatePetillant(){

        if (petillantDao == null){
            petillantDao = new PetillantDao();
        }
        ArrayList<Petillant> listePetillants = petillantDao.getAll();
        Spinner spinnerPetillants = (Spinner) findViewById(R.id.petillantSpinner);
        ArrayAdapter<Petillant> adapter =
                new ArrayAdapter<Petillant>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listePetillants);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerPetillants.setAdapter(adapter);

        spinnerPetillants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                petillantChosen = ((Petillant)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    private void populateQty(){

        ArrayList<Integer> listeQty = new ArrayList<>(36);
        for (Integer i=1;i<=36;i++){listeQty.add(i);}
        Spinner spinnerQty = (Spinner) findViewById(R.id.quantiteSpinner);
        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<Integer>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeQty);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerQty.setAdapter(adapter);

        spinnerQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                qtyChosen = ((Integer)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    private void populateApogees(){

        ArrayList<Integer> listeApogees = new ArrayList<>(102);
        listeApogees.add(new Integer(0));
        for (Integer i=2000;i<=2100;i++){listeApogees.add(i);}
        Spinner spinnerApogeeMin = (Spinner) findViewById(R.id.apogeeMinSpinner);
        Spinner spinnerApogeeMax = (Spinner) findViewById(R.id.apogeeMaxSpinner);
        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<Integer>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, listeApogees);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerApogeeMin.setAdapter(adapter);
        spinnerApogeeMax.setAdapter(adapter);

        spinnerApogeeMin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                apogeeMinChosen = ((Integer)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        spinnerApogeeMax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                apogeeMaxChosen = ((Integer)parentView.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }

    /* ============================================================================= */



    /* ============================================================================= */
    /* méthodes pour ajouter une photo */

    public void takePhoto(View view){
        //Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //startActivityForResult(i, RESULT_LOAD_IMAGE);
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
                // on sauve l'image en byte[] pour l'ajouter ensuite en base (methode ajouterBouteille)
                InputStream imageStream2 = getContentResolver().openInputStream(imageUri);
                inputDataForPhoto = Utils.getBytes(imageStream2);
                ImageButton imageCaveButton = (ImageButton)findViewById(R.id.bouteillePhotoImageButton);
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

    /* ============================================================================= */


    /* ============================================================================= */
    /* méthodes pour choisir la date */


    @Override
    public void onClick(View v) {

        if (v == dateDachatEditText) {

            afficherDatePickerForDateDachat();
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == dateDachatEditText && hasFocus) {
            afficherDatePickerForDateDachat();
        }
    }

    private void afficherDatePickerForDateDachat(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        try {
                            dateDachatEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            dateDachatIntFormat = new Integer("" + year + "" + (monthOfYear + 1) + "" + dayOfMonth);
                        }catch (NumberFormatException nfe){
                            dateDachatEditText.setText("");
                            dateDachatIntFormat = 0;
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateDachatEditText.setText("");
                dateDachatIntFormat = 0;
            }
        });
        datePickerDialog.show();
    }
}
