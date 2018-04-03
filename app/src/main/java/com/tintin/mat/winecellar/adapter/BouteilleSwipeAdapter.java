package com.tintin.mat.winecellar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.LruCache;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    private LruCache<String, Bitmap> mMemoryCache;

    public BouteilleSwipeAdapter(Context context, List<Bouteille> bouteilles, BouteilleInterface listener) {
        this.context = context;
        this.listener=listener;
        this.bouteilles = bouteilles;
        this.filteredList = bouteilles;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

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
        if (bouteille.getVignettePath() != null && bouteille.getVignettePath().length()>0) {
            //get bitmap from the Uri
            //avatar.setImageBitmap(Utils.getImage(bouteille.getVignetteBitmap()));
            //avatar.setImageBitmap(BitmapFactory.decodeFile(Uri.parse(bouteille.getVignettePath()).getPath()));

            final Bitmap bitmap = getBitmapFromMemCache(bouteille.getVignettePath());
            if (bitmap != null) {
                avatar.setImageBitmap(bitmap);
            } else {
                if (cancelPotentialDownload(bouteille.getVignettePath(), avatar)) {
                    BitmapDownloaderTask task = new BitmapDownloaderTask(avatar, bouteille);
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    avatar.setImageDrawable(downloadedDrawable);
                    task.execute();
                }
            }


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

    /****************************************/
    /****************************************/

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String vignettePath = bitmapDownloaderTask.bouteille.getVignettePath() ;
            if ((vignettePath == null) || (!vignettePath.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Bitmap o = mMemoryCache.put(key, bitmap);
            //System.out.println("bonjour ");
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private LinkedHashMap l;
        private final /*WeakReference<ImageView>*/ ImageView imageViewReference;
        private Bouteille bouteille;

        public BitmapDownloaderTask(ImageView imageView, Bouteille bouteille) {
            //imageViewReference = new WeakReference<ImageView>(imageView);
            imageViewReference = imageView;
            this.bouteille = bouteille;
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            // params comes from the execute() call.
            final Bitmap bitmap = BitmapFactory.decodeFile(Uri.parse(bouteille.getVignettePath()).getPath());
            addBitmapToMemoryCache(bouteille.getVignettePath(), bitmap);
            return bitmap;
            //return Utils.getImage(bouteille.getVignetteBitmap());
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                //ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageViewReference);
                // Change bitmap only if this process is still associated with it
                if (this == bitmapDownloaderTask) {
                    imageViewReference.setImageBitmap(bitmap);
                }
            }

        }
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.BLACK);
            //avatar.setImageResource(R.drawable.glasses1);
            bitmapDownloaderTaskReference =
                    new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }


    /****************************************/
    /****************************************/


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
