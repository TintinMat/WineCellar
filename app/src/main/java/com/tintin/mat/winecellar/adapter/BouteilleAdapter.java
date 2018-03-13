package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.interfce.BouteilleInterface;
import com.tintin.mat.winecellar.utils.Utils;

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
        if (bouteille.getPhotoPath() != null) {
            //get bitmap from the Uri
            viewHolder.avatar.setImageBitmap(Utils.getImage(bouteille.getPhotoPath(), getContext()));
        }else{
            viewHolder.avatar.setImageResource(R.drawable.glasses1);
        }

        return convertView;
    }


    private class BouteilleViewHolder{
        public TextView nomDomaine;
        public TextView nomAppellation;
        public TextView milesime;
        public ImageView avatar;
    }
}
