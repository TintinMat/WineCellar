package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.activity.AjouterBouteilleActivity;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.interfce.BouteilleInterface;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class BouteilleAdapter extends ArrayAdapter<Bouteille> {

    Context context;
    private BouteilleInterface listener;

    public BouteilleAdapter(Context context, List<Bouteille> bouteilles, BouteilleInterface listener) {
        super(context, 0, bouteilles);
        this.context = context;
        this.listener=listener;

    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bouteille,parent, false);
        }

        BouteilleViewHolder viewHolder = (BouteilleViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BouteilleViewHolder();
            viewHolder.nomDomaine = (TextView) convertView.findViewById(R.id.nomDomaineRowTextView);
            viewHolder.nomAppellation = (TextView) convertView.findViewById(R.id.nomAppellationRowTextView);
            viewHolder.milesime = (TextView) convertView.findViewById(R.id.millesimeRowTextView);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatarBouteille);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Cave> caves
        final Bouteille bouteille = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        String messageNomDomaine = getContext().getString(R.string.text_view_nom_domaine, bouteille.getDomaine());
        viewHolder.nomDomaine.setText(messageNomDomaine);
        if (bouteille.getAppellation() != null) {
            viewHolder.nomAppellation.setText(bouteille.getAppellation().getNom());
        }else{
            viewHolder.nomAppellation.setText("");
        }
        if (bouteille.getMillesime() != null && bouteille.getMillesime().getAnnee()>0) {
            String messageDateMillesime = getContext().getString(R.string.text_view_row_millesime, bouteille.getMillesime().getAnnee());
            viewHolder.milesime.setText(messageDateMillesime);
        }else{
            String messageDateMillesime = getContext().getString(R.string.text_view_row_no_millesime, "-");
            viewHolder.milesime.setText(messageDateMillesime);
        }
        if (bouteille.getPhoto() != null) {
            viewHolder.avatar.setImageBitmap(Utils.getImage(bouteille.getPhoto()));
        }else{
            viewHolder.avatar.setImageResource(R.drawable.glasses1);
        }
/*
        // afficher le bouton pour déguster
        Button btn = convertView.findViewById(R.id.deguster_btn);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Date today = new Date(); // Fri Jun 17 14:54:28 PDT 2016
                Calendar cal = Calendar.getInstance();
                cal.setTime(today); // don't forget this if date is arbitrary e.g. 01-01-2014
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH); // 17
                int monthOfYear = cal.get(Calendar.MONTH); // 5
                int year = cal.get(Calendar.YEAR); // 2016

                String monthString = ""+ (monthOfYear + 1);
                String dayString = ""+ dayOfMonth;
                if (monthOfYear + 1 <10 ){
                    monthString = "0"+(monthOfYear+1);
                }
                if (dayOfMonth <10 ){
                    dayString = "0"+dayOfMonth;
                }
                int dateDegustationIntFormat = new Integer("" + year + monthString + dayString);

                bouteille.setAnneeDegustation(dateDegustationIntFormat);
                BouteilleDao bouteilleDao = new BouteilleDao(context,null);
                bouteilleDao.modifierAnneeDegustation(bouteille);

                listener.refresh();

                /*notifyDataSetChanged();
                Intent appel = new Intent(this, AjouterBouteilleActivity.class);
                convertView.getContext().startActivity();
            }
        });*/

        return convertView;
    }


    private class BouteilleViewHolder{
        public TextView nomDomaine;
        public TextView nomAppellation;
        public TextView milesime;
        public ImageView avatar;
    }
}
