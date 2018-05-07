package com.tintin.mat.winecellar.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.tintin.mat.winecellar.BuildConfig;
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

public class ModifierBouteilleActivity extends StoragePermissions implements View.OnClickListener, View.OnFocusChangeListener {

    private static int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri = null;

    private EditText dateDachatEditText;
    private int dateDachatIntFormat;
    private EditText dateDegustationEditText;
    private int dateDegustationIntFormat;

    private Bouteille bouteille = null;
    private Appellation appellationChosen = null;
    private Millesime millesimeChosen = null;
    private Clayette clayetteChosen = null;
    private Couleur couleurChosen = null;
    private Petillant petillantChosen = null;
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
        getMenuInflater().inflate(R.menu.modifier_bouteille_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_bouteille);
        setTitle(R.string.toolbar_bouteille_modifier);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("Key") != null) {
            Bouteille b = (Bouteille) getIntent().getExtras().get("Key");
            bouteilleDao = new BouteilleDao(this,null);
            bouteille = bouteilleDao.getWithAllDependencies(b);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareUI();

        //((ScrollView)findViewById(R.id.scrollViewAddBottle).fullScroll(ScrollView.FOCUS_UP);

        if (bouteille != null) {
            setValues();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.modif_bouteille:
                modifierBouteille();
                return true;
            case R.id.del_bouteille:
                supprimerBouteille();
                return true;
            case R.id.back_home:
                onBackHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackHome() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }




    /* ============================================================================= */
    /* méthode principale d'ajout de la bouteille */

    private void modifierBouteille(){
        // récupérer les infos de l'ihm
        EditText nomDomaine = (EditText)findViewById(R.id.nomDomaineEditText);
        EditText prix = (EditText)findViewById(R.id.prixDachatEditText);
        EditText lieu = (EditText)findViewById(R.id.lieuDachatEditText);
        EditText comm = (EditText)findViewById(R.id.commentairesEditText);
        CheckBox bio = (CheckBox)findViewById(R.id.bioCheckBox);

        if (nomDomaine == null || nomDomaine.getText().length()==0){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_creation_bouteille_ko_nomDomaine, Toast.LENGTH_LONG);
            toast.show();
        } else {

            bouteille.setCommentaires(comm.getText().toString());
            bouteille.setDateDachat(dateDachatIntFormat);
            bouteille.setAnneeDegustation(dateDegustationIntFormat);
            bouteille.setDomaine(nomDomaine.getText().toString());
            bouteille.setLieuDachat(lieu.getText().toString());
            bouteille.setMillesime(millesimeChosen);
            if (prix.getText().length()>0) {
                bouteille.setPrix(new Float(prix.getText().toString()));
            }
            bouteille.setPetillant(petillantChosen);
            bouteille.setClayette(clayetteChosen);
            bouteille.setCouleur(couleurChosen);
            bouteille.setBio(bio.isChecked());
            bouteille.setAppellation(appellationChosen);
            // récupérer la photo si non vide
            if (imageUri != null && imageUri.toString().length() > 0) {
                bouteille.setPhotoPath(imageUri.toString());
            }
            bouteille.setApogeeMin(apogeeMinChosen);
            bouteille.setApogeeMax(apogeeMaxChosen);
            try{
                bouteilleDao.modifier(bouteille);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_modifier_bouteille_ok, Toast.LENGTH_LONG);
                toast.show();
                finish();
            }catch (Exception ex){
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "modifierBouteille ",ex );
                }
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_modifier_bouteille_ko, Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    /* ============================================================================= */






    /* ============================================================================= */
    /* méthode principale pour afficher les données  */

    public void supprimerBouteille(){
        try{
            int nb = bouteilleDao.supprimer(bouteille);
            if (nb > 0) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_bouteille_ok, Toast.LENGTH_LONG);
                toast.show();
            }
            finish();
        }catch (Exception ex){
            if (BuildConfig.DEBUG){
                Log.e(TAG, "supprimerBouteille ",ex );
            }
            Toast toast = Toast.makeText(getApplicationContext(), R.string.message_supprimer_bouteille_ko, Toast.LENGTH_LONG);
            toast.show();
        }
    }


    /* ============================================================================= */








    /* ============================================================================= */
    /* méthode principale pour afficher les données  */


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
        // positionner la valeur

        if (bouteille.getMillesime() != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (bouteille.getMillesime().getAnnee() == adapter.getItem(i).getAnnee()) {
                    spinnerMillesime.setSelection(i);
                    break;
                }
            }
        }

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
        populateApogees();

        dateDachatEditText=(EditText)findViewById(R.id.dateDachatEditText);
        dateDachatEditText.setInputType(InputType.TYPE_NULL);
        dateDachatEditText.setLongClickable(false);
        dateDachatEditText.setOnClickListener(this);
        dateDachatEditText.setOnFocusChangeListener(this);

        dateDegustationEditText=(EditText)findViewById(R.id.dateDegustationEditText);
        dateDegustationEditText.setInputType(InputType.TYPE_NULL);
        dateDegustationEditText.setLongClickable(false);
        dateDegustationEditText.setOnClickListener(this);
        dateDegustationEditText.setOnFocusChangeListener(this);

        dateDegustationIntFormat = bouteille.getAnneeDegustation();
        // les dates
        if (dateDegustationIntFormat>0) {
            try {
                // format de la date : dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                String yop = "" + dateDegustationIntFormat;
                String yyear = yop.substring(0, 4);
                String monthOfYear = yop.substring(4, 6);
                String dayOfMonth = yop.substring(6, 8);

                dateDegustationEditText.setText(dayOfMonth + "-" + monthOfYear + "-" + yyear);
            }catch(IndexOutOfBoundsException ioobe){
                dateDegustationEditText.setText("? - ? - ?");
            }
        }
        dateDachatIntFormat = bouteille.getDateDachat();
        if (dateDachatIntFormat>0) {
            try {
                // format de la date : dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                String yop = ""+dateDachatIntFormat;
                String yyear = yop.substring(0,4);
                String monthOfYear = yop.substring(4,6);
                String dayOfMonth = yop.substring(6,8);

                dateDachatEditText.setText(dayOfMonth+"-"+monthOfYear+"-"+yyear);
            }catch(IndexOutOfBoundsException ioobe){
                dateDachatEditText.setText("? - ? - ?");
            }
        }

        //charger la photo
        afficherPhoto();

    }


    /* ============================================================================= */
    /* méthode pour mettre à jour les données */

    private void setValues(){

        // récupérer les infos de l'ihm
        EditText nomDomaine = (EditText)findViewById(R.id.nomDomaineEditText);
        EditText prix = (EditText)findViewById(R.id.prixDachatEditText);
        EditText lieu = (EditText)findViewById(R.id.lieuDachatEditText);
        EditText comm = (EditText)findViewById(R.id.commentairesEditText);
        CheckBox bio = (CheckBox)findViewById(R.id.bioCheckBox);

        if (bouteille.getDomaine() != null){
            nomDomaine.setText(bouteille.getDomaine());
        }
        prix.setText(""+bouteille.getPrix());
        lieu.setText(bouteille.getLieuDachat());
        if (bouteille.getCommentaires() != null) {
            comm.setText(bouteille.getCommentaires());
        }
        bio.setChecked(bouteille.isBio());

    }

    /* ============================================================================= */


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
        // positionner la valeur
        try {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (bouteille.getAppellation().getRegion().getPays().getId() == adapter.getItem(i).getId()) {
                    spinnerPays.setSelection(i);
                    break;
                }
            }
        }catch (NullPointerException npe){
            //on fait rien
        }

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
        // positionner la valeur
        try{
            for(int i=0;i<adapter.getCount();i++){
                if (bouteille.getAppellation().getRegion().getId() == adapter.getItem(i).getId()){
                    spinnerRegions.setSelection(i);
                    break;
                }
            }
        }catch (NullPointerException npe){
            //on fait rien
        }

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
        // positionner la valeur
        try{
            for(int i=0;i<adapter.getCount();i++){
                if (bouteille.getAppellation().getId() == adapter.getItem(i).getId()){
                    spinnerAppellations.setSelection(i);
                    break;
                }
            }
        }catch (NullPointerException npe){
            //on fait rien
        }

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
                if (bouteille.getClayette().getCave().getId() == adapter.getItem(i).getId()){
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
        // positionner la valeur
        try{
            for(int i=0;i<adapter.getCount();i++){
                if (bouteille.getClayette().getId() == adapter.getItem(i).getId()){
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
        // positionner la valeur
        for(int i=0;i<adapter.getCount();i++){
            if (bouteille.getCouleur().getId() == adapter.getItem(i).getId()){
                spinnerCouleurs.setSelection(i);
                break;
            }
        }

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
        // positionner la valeur
        for(int i=0;i<adapter.getCount();i++){
            if (bouteille.getPetillant().getId() == adapter.getItem(i).getId()){
                spinnerPetillants.setSelection(i);
                break;
            }
        }

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

        // positionner la valeur
        for(int i=0;i<adapter.getCount();i++){
            if (bouteille.getApogeeMin() == adapter.getItem(i)){
                spinnerApogeeMin.setSelection(i);
                break;
            }
        }
        // positionner la valeur
        for(int i=0;i<adapter.getCount();i++){
            if (bouteille.getApogeeMax() == adapter.getItem(i)){
                spinnerApogeeMax.setSelection(i);
                break;
            }
        }
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
    /* méthode pour récupérer la photo */

    private void afficherPhoto(){
        if (bouteille.getPhotoPath() != null && bouteille.getPhotoPath().toString().length() > 0) {

            ImageButton imageCaveButton = (ImageButton)findViewById(R.id.bouteillePhotoImageButton);
            imageCaveButton.setImageBitmap(Utils.getImage(bouteille.getPhotoPath(), this));
        }
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
                GrantPermissionsForWriting();
                imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ImageButton imageCaveButton = (ImageButton)findViewById(R.id.bouteillePhotoImageButton);
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

    /* ============================================================================= */


    /* ============================================================================= */
    /* méthodes pour choisir la date */


    @Override
    public void onClick(View v) {

        if (v == dateDachatEditText) {

            afficherDatePickerForDateDachat();
        }
        else if (v == dateDegustationEditText) {

            afficherDatePickerForDegustationDachat();
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == dateDachatEditText && hasFocus) {
            afficherDatePickerForDateDachat();
        }
        else if (v == dateDegustationEditText && hasFocus) {
            afficherDatePickerForDegustationDachat();
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
                            String monthString = ""+ (monthOfYear + 1);
                            String dayString = ""+ dayOfMonth;

                            if (monthOfYear + 1 <10 ){
                                monthString = "0"+(monthOfYear+1);
                            }
                            if (dayOfMonth <10 ){
                                dayString = "0"+dayOfMonth;
                            }
                            dateDachatIntFormat = new Integer("" + year + monthString + dayString);
                            dateDachatEditText.setText(dayString + "-" + monthString + "-" + year);

                            /*dateDachatEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            dateDachatIntFormat = new Integer("" + year + "" + (monthOfYear + 1) + "" + dayOfMonth);*/
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

    private void afficherDatePickerForDegustationDachat(){
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
                            String monthString = ""+ (monthOfYear + 1);
                            String dayString = ""+ dayOfMonth;
                            if (monthOfYear + 1 <10 ){
                                monthString = "0"+(monthOfYear+1);
                            }
                            if (dayOfMonth <10 ){
                                dayString = "0"+dayOfMonth;
                            }
                            dateDegustationEditText.setText(dayString + "-" + monthString + "-" + year);
                            dateDegustationIntFormat = new Integer("" + year + monthString + dayString);
                        }catch (NumberFormatException nfe){
                            dateDegustationEditText.setText("");
                            dateDegustationIntFormat = 0;
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dateDegustationEditText.setText("");
                dateDegustationIntFormat = 0;
            }
        });
        datePickerDialog.show();
    }

}
