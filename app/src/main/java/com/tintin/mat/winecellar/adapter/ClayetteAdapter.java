package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.interfce.BouteilleInterface;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class ClayetteAdapter extends ArrayAdapter<Clayette> {

    Context context;
    //private BouteilleInterface listener;

    public ClayetteAdapter(Context context, List<Clayette> clayettes/*, BouteilleInterface listener*/) {
        super(context, 0, clayettes);
        this.context = context;
        //this.listener=listener;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_clayette,parent, false);
        }

        ClayetteViewHolder viewHolder = (ClayetteViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ClayetteViewHolder();
            viewHolder.nom = (TextView) convertView.findViewById(R.id.nomClayetteRowTextView);
            viewHolder.nombreBouteilles = (TextView) convertView.findViewById(R.id.nombreBouteillesRowTextView);
            convertView.setTag(viewHolder);
        }

        final Clayette clayette = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        if (clayette.getNom() != null && clayette.getNom().length()>0) {
            viewHolder.nom.setText(clayette.getNom());
        }else{
            viewHolder.nom.setText(clayette.toString());
        }
        String messageNbBouteillesMillesime = "";
        if (clayette.listeBouteilles() != null && clayette.listeBouteilles().size()>0) {
            messageNbBouteillesMillesime = getContext().getString(R.string.text_view_row_nb_bouteilles, clayette.listeBouteilles().size());
        }else {
            messageNbBouteillesMillesime = getContext().getString(R.string.text_view_row_nb_bouteilles, 0);
        }
        viewHolder.nombreBouteilles.setText(messageNbBouteillesMillesime);

/*
        // afficher le bouton pour déguster
        Button btn = convertView.findViewById(R.id.deguster_btn);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });*/

        return convertView;
    }


    private class ClayetteViewHolder{
        public TextView nom;
        public TextView nombreBouteilles;
    }
}
