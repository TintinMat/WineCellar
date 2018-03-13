package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Bouteille;
import com.tintin.mat.winecellar.interfce.BouteilleInterface;
import com.tintin.mat.winecellar.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mat & Audrey on 22/10/2017.
 */

public class BouteilleSwipeAdapter extends BaseSwipeAdapter implements Filterable {

    private Context context;
    private BouteilleInterface listener;
    private List<Bouteille> bouteilles;
    private List<Bouteille> filteredList;
    private BouteilleFilter bouteilleFilter;

    public BouteilleSwipeAdapter(Context context, List<Bouteille> bouteilles, BouteilleInterface listener) {
        this.context = context;
        this.listener=listener;
        this.bouteilles = bouteilles;
        this.filteredList = bouteilles;

        getFilter();
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
        if (bouteille.getPhotoPath() != null) {
            //get bitmap from the Uri
            avatar.setImageBitmap(Utils.getImage(bouteille.getPhotoPath(), context));
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
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        if (filteredList != null && position<filteredList.size()) {
            return filteredList.get(position);
        }else{
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get custom filter
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (bouteilleFilter == null) {
            bouteilleFilter = new BouteilleFilter();
        }

        return bouteilleFilter;
    }

    /**
     * Custom filter for bouteille list
     * Filter content in bouteille list according to the search text
     */
    private class BouteilleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<Bouteille> tempList = new ArrayList<Bouteille>();

                // search content in friend list
                for (Bouteille btlle : bouteilles) {
                    if (btlle.getDomaine().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            btlle.getAppellation().getNom().toLowerCase().contains(constraint.toString().toLowerCase()) ) {
                        tempList.add(btlle);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = bouteilles.size();
                filterResults.values = bouteilles;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Bouteille>) results.values;
            notifyDataSetChanged();
        }
    }
}
