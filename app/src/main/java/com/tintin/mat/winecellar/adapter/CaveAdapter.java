package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class CaveAdapter extends ArrayAdapter<Cave> {

    //tweets est la liste des models à afficher
    public CaveAdapter(Context context, List<Cave> caves) {
        super(context, 0, caves);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_cave,parent, false);
        }

        CaveViewHolder viewHolder = (CaveViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new CaveViewHolder();
            viewHolder.nomCave = (TextView) convertView.findViewById(R.id.nomCaveRowTextView);
            viewHolder.nbBouteilles = (TextView) convertView.findViewById(R.id.nbBouteillesRowTextView);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Cave> caves
        Cave cave = getItem(position);

        // recuperer le nombre de bouteilles déjà insérées dans la cave
        BouteilleDao bouteilleDao = new BouteilleDao(this.getContext(), null);
        ArrayList<Bouteille> listeBouteilles = bouteilleDao.getAllNotDegustedAssociatedWithCave(cave);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.nomCave.setText("cave : " + cave.getNom());
        viewHolder.nbBouteilles.setText("nombre de bouteilles : "+listeBouteilles.size() +"/"+ cave.getNbBouteillesTheoriques());
        if (cave.getVignettePath() != null) {
            //get bitmap from the Uri
            viewHolder.avatar.setImageBitmap(Utils.getImage(cave.getVignettePath(), getContext()));
        }else{
            viewHolder.avatar.setImageResource(R.drawable.tonneau2);
        }
        //viewHolder.avatar.setImageDrawable(new ColorDrawable(Color.GREEN));

        return convertView;
    }

    private class CaveViewHolder{
        public TextView nomCave;
        public TextView nbBouteilles;
        public ImageView avatar;
    }
}
