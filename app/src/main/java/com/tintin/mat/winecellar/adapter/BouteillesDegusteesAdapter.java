package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class BouteillesDegusteesAdapter extends ArrayAdapter<Bouteille> {

    public BouteillesDegusteesAdapter(Context context, List<Bouteille> bouteilles) {
        super(context, 0, bouteilles);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_bouteille_degustee,parent, false);
        }

        BouteilleViewHolder viewHolder = (BouteilleViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new BouteilleViewHolder();
            viewHolder.nomDomaine = (TextView) convertView.findViewById(R.id.nomDomaineRowTextView);
            viewHolder.nomAppellation = (TextView) convertView.findViewById(R.id.nomAppellationRowTextView);
            viewHolder.milesime = (TextView) convertView.findViewById(R.id.millesimeRowTextView);
            viewHolder.dateDegustation = (TextView) convertView.findViewById(R.id.dateDegustationRowTextView);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatarBouteille);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Cave> caves
        Bouteille bouteille = getItem(position);

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
        if (bouteille.getAnneeDegustation() > 0) {
            try {
                // format de la date : dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                String yop = "" + bouteille.getAnneeDegustation();
                String yyear = yop.substring(0, 4);
                String monthOfYear = yop.substring(4, 6);
                String dayOfMonth = yop.substring(6, 8);

                String messageDateDegustation = getContext().getString(R.string.text_view_row_date_degustation, dayOfMonth + "-" + monthOfYear + "-" + yyear);
                viewHolder.dateDegustation.setText(messageDateDegustation);
            }catch(IndexOutOfBoundsException ioobe){
                String messageDateDegustation = getContext().getString(R.string.text_view_row_date_degustation, "? - ? - ?");
                viewHolder.dateDegustation.setText(messageDateDegustation);
            }
        }else{
            String messageDateDegustation = getContext().getString(R.string.text_view_row_date_degustation, "? - ? - ?");
            viewHolder.dateDegustation.setText(messageDateDegustation);
        }
        if (bouteille.getVignettePath() != null && bouteille.getVignettePath().length()>0) {
            //get bitmap from the Uri
            viewHolder.avatar.setImageBitmap(BitmapFactory.decodeFile(Uri.parse(bouteille.getVignettePath()).getPath()));
            //viewHolder.avatar.setImageBitmap(Utils.getImage(bouteille.getVignetteBitmap()));
        }else{
            viewHolder.avatar.setImageResource(R.drawable.glasses1);
        }

        return convertView;
    }


    private class BouteilleViewHolder{
        public TextView nomDomaine;
        public TextView nomAppellation;
        public TextView milesime;
        public TextView dateDegustation;
        public ImageView avatar;
    }
}
