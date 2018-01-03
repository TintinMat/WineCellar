package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.interfce.BouteilleInterface;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class BouteilleSwipeAdapter extends BaseSwipeAdapter {

    private Context context;
    private BouteilleInterface listener;
    private List<Bouteille> bouteilles;

    public BouteilleSwipeAdapter(Context context, List<Bouteille> bouteilles, BouteilleInterface listener) {
        this.context = context;
        this.listener=listener;
        this.bouteilles = bouteilles;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_bouteille, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.tire_bouchon));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                //Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        final Bouteille bouteille = (Bouteille)getItem(position);
        v.findViewById(R.id.drunk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "click delete", Toast.LENGTH_SHORT).show();
                listener.degusterBouteille(bouteille);
            }
        });*/
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {

        TextView nomDomaine = (TextView) convertView.findViewById(R.id.nomDomaineRowTextView);
        TextView nomAppellation = (TextView) convertView.findViewById(R.id.nomAppellationRowTextView);
        TextView milesime = (TextView) convertView.findViewById(R.id.millesimeRowTextView);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatarBouteille);

        //getItem(position) va récupérer l'item [position] de la List<Bouteilles>
        final Bouteille bouteille = (Bouteille)getItem(position);

        //il ne reste plus qu'à remplir notre vue
        String messageNomDomaine = context.getString(R.string.text_view_nom_domaine, bouteille.getDomaine());
        nomDomaine.setText(messageNomDomaine);
        if (bouteille.getAppellation() != null) {
            nomAppellation.setText(bouteille.getAppellation().getNom());
        }else{
            nomAppellation.setText("");
        }
        if (bouteille.getMillesime() != null && bouteille.getMillesime().getAnnee()>0) {
            String messageDateMillesime = context.getString(R.string.text_view_row_millesime, bouteille.getMillesime().getAnnee());
            milesime.setText(messageDateMillesime);
        }else{
            String messageDateMillesime = context.getString(R.string.text_view_row_no_millesime, "-");
            milesime.setText(messageDateMillesime);
        }
        if (bouteille.getPhoto() != null) {
            avatar.setImageBitmap(Utils.getImage(bouteille.getPhoto()));
        }else{
            avatar.setImageResource(R.drawable.glasses1);
        }

        //bind listeners to the view
        convertView.findViewById(R.id.drunk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "click delete", Toast.LENGTH_SHORT).show();
                listener.degusterBouteille(bouteille);
            }
        });

    }

    @Override
    public int getCount() {
        return bouteilles.size();
    }

    @Override
    public Object getItem(int position) {
        if (bouteilles != null && position<bouteilles.size()) {
            return bouteilles.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
