package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Clayette;
import com.tintin.mat.winecellar.interfce.ClayetteInterface;

import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class ClayetteSwipeAdapter extends BaseSwipeAdapter {

    private Context context;
    private List<Clayette> clayettes;
    private ClayetteInterface clayetteInterface;

    public ClayetteSwipeAdapter(Context context, List<Clayette> clayettes, ClayetteInterface clayetteInterface) {
        this.context = context;
        this.clayettes = clayettes;
        this.clayetteInterface = clayetteInterface;

    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeClayette;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_clayette, null);
        SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
               // YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.tire_bouchon));
                //Log.e("ListView", "onOpen : position "+position);
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                //Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void fillValues(final int position, View convertView) {

        TextView nom = (TextView) convertView.findViewById(R.id.nomClayetteRowTextView);
        TextView nombreBouteilles = (TextView) convertView.findViewById(R.id.nombreBouteillesRowTextView);

        final Clayette clayette = (Clayette) getItem(position);

        if (clayette.getNom() != null && clayette.getNom().length()>0) {
            nom.setText(clayette.getNom());
        }else{
            nom.setText(clayette.toString());
        }
        String messageNbBouteillesMillesime = "";
        if (clayette.listeBouteilles() != null && clayette.listeBouteilles().size()>0) {
            messageNbBouteillesMillesime = context.getString(R.string.text_view_row_nb_bouteilles, clayette.listeBouteilles().size());
        }else {
            messageNbBouteillesMillesime = context.getString(R.string.text_view_row_nb_bouteilles, 0);
        }
        nombreBouteilles.setText(messageNbBouteillesMillesime);

        //bind listeners to the view
        //final Clayette clayette = (Clayette)getItem(position);
        convertView.findViewById(R.id.edit_clayette).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clayetteInterface.editClayette(position);
            }
        });
        convertView.findViewById(R.id.trash_clayette).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clayetteInterface.supprimerClayette(position);
            }
        });

    }

    @Override
    public int getCount() {
        return clayettes.size();
    }

    @Override
    public Object getItem(int position) {

        if (clayettes != null && position<clayettes.size()) {
            return clayettes.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
