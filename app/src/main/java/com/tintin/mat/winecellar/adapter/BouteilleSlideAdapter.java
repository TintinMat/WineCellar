package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 13/11/2018.
 */

public class BouteilleSlideAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private BouteilleDao bouteilleDao = null;

    private ArrayList<Bouteille> listeBouteilles = null;

    public BouteilleSlideAdapter(Context context, ArrayList<Bouteille> listeBouteilles){
        this.context = context;
        this.listeBouteilles = listeBouteilles;
        if (bouteilleDao == null){
            bouteilleDao = new BouteilleDao(this.context,null);
        }
    }

    public void updateListeBouteilles(ArrayList<Bouteille> listeBouteilles){
        this.listeBouteilles = listeBouteilles;
    }

    @Override
    public  int getItemPosition(Object object){
        // force la vue à se rafraichir
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if (listeBouteilles != null) {
            return listeBouteilles.size();
        }else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(ScrollView)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_bouteille, container, false);
        //ScrollView layoutSlide = (ScrollView)view.findViewById(R.id.scrollViewShowBottle);

        setValues(view, position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView)object);
    }


    /* ============================================================================= */
    /* méthode principale pour afficher les données  */

    private void setValues(View view, int position){

        // récupérer les infos de l'ihm
        TextView nomDomaine = (TextView)view.findViewById(R.id.nomDomaineText);
        TextView prix = (TextView)view.findViewById(R.id.prixDachatText);
        TextView lieu = (TextView)view.findViewById(R.id.lieuDachatText);
        EditText comm = (EditText)view.findViewById(R.id.commentairesText);
        CheckBox bio = (CheckBox)view.findViewById(R.id.bioCheckBox);
        RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar);

        if (listeBouteilles != null && position < listeBouteilles.size()) {
            // on recherche la bouteille avece tous les attributs pour l'afficher correctement
            Bouteille bouteille = bouteilleDao.getWithAllDependencies(listeBouteilles.get(position));


            if (bouteille.getDomaine() != null) {
                nomDomaine.setText(bouteille.getDomaine());
            }
            prix.setText("" + bouteille.getPrix());
            lieu.setText(bouteille.getLieuDachat());
            comm.setText(bouteille.getCommentaires());
            bio.setChecked(bouteille.isBio());
            if (bouteille.getDomaine() != null) {
                //setTitle(bouteille.getDomaine());
            }

            // millesime
            TextView spinnerMillesime = (TextView) view.findViewById(R.id.millesimeText);
            if (bouteille.getMillesime() != null) {
                spinnerMillesime.setText("" + bouteille.getMillesime().getAnnee());
            }

            //pays - région - appellation
            TextView spinnerPays = (TextView) view.findViewById(R.id.paysText);
            try {
                spinnerPays.setText(bouteille.getAppellation().getRegion().getPays().getNom());
            } catch (NullPointerException npe) {
                spinnerPays.setText("");
            }
            TextView spinnerRegions = (TextView) view.findViewById(R.id.regionText);
            try {
                spinnerRegions.setText(bouteille.getAppellation().getRegion().getNom());
            } catch (NullPointerException npe) {
                spinnerRegions.setText("");
            }
            TextView spinnerAppellations = (TextView) view.findViewById(R.id.appellationText);
            try {
                spinnerAppellations.setText(bouteille.getAppellation().getNom());
            } catch (NullPointerException npe) {
                spinnerAppellations.setText("");
            }

            //cave - clayette
            TextView spinnerCaves = (TextView) view.findViewById(R.id.caveText);
            try {
                spinnerCaves.setText(bouteille.getClayette().getCave().getNom());
            } catch (NullPointerException npe) {
                spinnerCaves.setText("");
            }
            TextView spinnerClayettes = (TextView) view.findViewById(R.id.clayetteText);
            try {
                spinnerClayettes.setText(bouteille.getClayette().getNom());
            } catch (NullPointerException npe) {
                spinnerClayettes.setText("");
            }

            //couleur
            TextView spinnerCouleurs = (TextView) view.findViewById(R.id.couleurText);
            try {
                spinnerCouleurs.setText(bouteille.getCouleur().getNom());
            } catch (NullPointerException npe) {
                spinnerCouleurs.setText("");
            }

            //pétillant
            TextView spinnerPetillants = (TextView) view.findViewById(R.id.petillantText);
            try {
                spinnerPetillants.setText(bouteille.getPetillant().getNom());
            } catch (NullPointerException npe) {
                spinnerPetillants.setText("");
            }

            //apogees
            TextView spinnerApogeeMin = (TextView) view.findViewById(R.id.apogeeMinText);
            TextView spinnerApogeeMax = (TextView) view.findViewById(R.id.apogeeMaxText);
            spinnerApogeeMin.setText("" + bouteille.getApogeeMin());
            spinnerApogeeMax.setText("" + bouteille.getApogeeMax());

            TextView dateDegustationEditText = (TextView) view.findViewById(R.id.dateDegustationText);
            int dateDegustationIntFormat = bouteille.getAnneeDegustation();
            // les dates
            if (dateDegustationIntFormat > 0) {
                try {
                    // format de la date : dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                    String yop = "" + dateDegustationIntFormat;
                    String yyear = yop.substring(0, 4);
                    String monthOfYear = yop.substring(4, 6);
                    String dayOfMonth = yop.substring(6, 8);

                    dateDegustationEditText.setText(dayOfMonth + "-" + monthOfYear + "-" + yyear);
                } catch (IndexOutOfBoundsException ioobe) {
                    dateDegustationEditText.setText("? - ? - ?");
                }
            } else {
                dateDegustationEditText.setText("");
            }

            TextView dateDachatEditText = (TextView) view.findViewById(R.id.dateDachatText);
            int dateDachatIntFormat = bouteille.getDateDachat();
            if (dateDachatIntFormat > 0) {
                try {
                    // format de la date : dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                    String yop = "" + dateDachatIntFormat;
                    String yyear = yop.substring(0, 4);
                    String monthOfYear = yop.substring(4, 6);
                    String dayOfMonth = yop.substring(6, 8);

                    dateDachatEditText.setText(dayOfMonth + "-" + monthOfYear + "-" + yyear);
                } catch (IndexOutOfBoundsException ioobe) {
                    dateDachatEditText.setText("? - ? - ?");
                }
            } else {
                dateDachatEditText.setText("");
            }

            ratingBar.setRating(bouteille.getRating());

            //charger la photo
            afficherPhoto(view, bouteille);
        }

    }

    /* ============================================================================= */


    /* ============================================================================= */
    /* méthode pour récupérer la photo */

    private void afficherPhoto(View view, Bouteille bouteille){
        if (bouteille.getPhotoPath() != null && bouteille.getPhotoPath().toString().length() > 0) {

            try {
                ImageButton imageCaveButton = (ImageButton) view.findViewById(R.id.bouteillePhotoImageButton);
                imageCaveButton.setImageBitmap(Utils.getImage(bouteille.getPhotoPath(), context));
            }catch (OutOfMemoryError memoryError){
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "afficherPhoto ", memoryError);
                }
            }
        }
    }

}
