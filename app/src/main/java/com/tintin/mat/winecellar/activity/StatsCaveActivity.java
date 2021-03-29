package com.tintin.mat.winecellar.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tintin.mat.winecellar.R;
import com.tintin.mat.winecellar.bo.Cave;
import com.tintin.mat.winecellar.bo.Couleur;
import com.tintin.mat.winecellar.dao.BouteilleDao;
import com.tintin.mat.winecellar.dao.CouleurDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatsCaveActivity extends AppCompatActivity {

    private Cave cave;

    private PieChart pieChartInCellar;
    private PieChart pieChartDeg;
    private TextView textViewNb ;
    private TextView textViewPct ;

    private int unite;

    private final static int UNITE_NB  = 0;
    private final static int UNITE_PCT = 1;


    private CouleurDao couleurDao = null;
    private BouteilleDao bouteilleDao = null;



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_cave);
        setTitle(R.string.toolbar_infos_cave);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null && getIntent().getExtras().get("Key") != null) {
            cave = (Cave) getIntent().getExtras().get("Key");
        }

        unite = UNITE_NB;

        afficherPie();
        afficherMenu(textViewNb, textViewPct);

        afficherValeurCave();

    }

    @Override
    public void onResume(){
        afficherPie();
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.back_home:
                onBackHome();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackHome() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void afficherPie(){

        textViewNb  = findViewById(R.id.textBouteilleNb  ) ;
        textViewPct = findViewById(R.id.textBouteillePct ) ;

        pieChartInCellar = findViewById(R.id.pieChartInCellar_view);
        pieChartDeg      = findViewById(R.id.pieChartDeg_view);
        initPieChart();
        showPieChart();

        // setting a listener to the pie chart
        //pieChart.setOnChartValueSelectedListener(new pieChartOnChartValueSelectedListener());
    }

    private void initPieChart(){

        List<PieChart> piesList = Arrays.asList(pieChartInCellar, pieChartDeg);
        pieChartInCellar.setCenterText(getString(R.string.text_view_stats_in_cellar_colors));
        pieChartDeg.setCenterText(getString(R.string.text_view_stats_degusted_colors));

        for (PieChart pie:piesList) {

            //remove the description label on the lower left corner, default true if not set
            pie.getDescription().setEnabled(false);

            //enabling the user to rotate the chart, default true
            pie.setRotationEnabled(true);
            //adding friction when rotating the pie chart
            pie.setDragDecelerationFrictionCoef(0.9f);
            //setting the first entry start from right hand side, default starting from top
            pie.setRotationAngle(0);

            //highlight the entry when it is tapped, default true if not set
            pie.setHighlightPerTapEnabled(true);
            //adding animation so the entries pop up from 0 degree
            pie.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            //setting the color of the hole in the middle, default white
            pie.setHoleColor(Color.GRAY);

            pie.setCenterTextColor(Color.WHITE);
            pie.setDrawCenterText(true);

            pie.setEntryLabelColor(Color.BLACK);
            //on ajoute des offsets pour pouvoir afficher les labels qui sont en dehors du chart
            pie.setExtraOffsets(30, 10, 30, 10);

            //To use a solid pie chart instead of a hollow pie chart
            // sets the radius of the hole in the center of the piechart in percent of the maximum radius (max = the radius of the whole chart),
            // default 50%
            pie.setHoleRadius(75f);
            // sets the radius of the transparent circle that is drawn next to the hole in the piechart in percent of the maximum radius
            // (max = the radius of the whole chart), default 55% -> means 5% larger than the center-hole by default
            pie.setTransparentCircleRadius(5f);
        }
    }

    private void showPieChart(){

        // recuperer la liste des couleurs
        if (couleurDao == null){
            couleurDao = new CouleurDao();
        }
        ArrayList<Couleur> listeCouleurs = couleurDao.getAll();

        if (bouteilleDao == null){
            bouteilleDao = new BouteilleDao(this, null);
        }

        //initializing data
        //initializing colors for the entries
        Map<String, Integer> typeAmountInCellarMap = new HashMap<>();
        Map<String, Integer> typeAmountDegMap = new HashMap<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (Couleur couleur:listeCouleurs) {
            int nbBout = (int) bouteilleDao.nbBouteillesByColorAssociatedWithCave(cave, couleur, false);
            if (nbBout > 0) {
                typeAmountInCellarMap.put(couleur.getNom(), nbBout);
            }
            nbBout = (int) bouteilleDao.nbBouteillesByColorAssociatedWithCave(cave, couleur, true);
            if (nbBout > 0) {
                typeAmountDegMap.put(couleur.getNom(), nbBout);
            }
        }
        int[] colorArray = Arrays.copyOfRange(getResources().getIntArray(R.array.pieChartColorArrayForColor),0,java.lang.Math.max(typeAmountInCellarMap.size(),typeAmountDegMap.size()));
        for(int color:colorArray){
            colors.add(color);
        }


        ArrayList<PieEntry> pieEntriesInCellar = new ArrayList<>();
        ArrayList<PieEntry> pieEntriesDeg      = new ArrayList<>();
        
        
        //input data and fit data into pie chart entry
        for(String type: typeAmountInCellarMap.keySet()){
            pieEntriesInCellar.add(new PieEntry(Objects.requireNonNull(typeAmountInCellarMap.get(type)).floatValue(), type));
        }
        for(String type: typeAmountDegMap.keySet()){
            pieEntriesDeg.add(new PieEntry(Objects.requireNonNull(typeAmountDegMap.get(type)).floatValue(), type));
        }

        PieDataSet pieDataSetInCellar = new PieDataSet(pieEntriesInCellar,null);
        PieDataSet pieDataSetDeg      = new PieDataSet(pieEntriesDeg,null);
        Map<PieDataSet, PieChart> mapPie = new HashMap<>();
        mapPie.put(pieDataSetInCellar, pieChartInCellar);
        mapPie.put(pieDataSetDeg, pieChartDeg);

        for (PieDataSet pieDataSet:mapPie.keySet()) {


            //collecting the entries with label name
            //setting text size of the value
            pieDataSet.setValueTextSize(12f);
            pieDataSet.setValueTextColor(Color.BLACK);


            //providing color list for coloring different entries
            pieDataSet.setColors(colors);

            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            //grouping the data set from entry to chart
            PieData pieData = new PieData(pieDataSet);
            //showing the value of the entries, default true if not set
            pieData.setDrawValues(true);

            if (unite == UNITE_PCT) {
                //si on veut du pourcntage, decommenter le code ci-dessous
                //using percentage as values instead of amount
                pieData.setValueFormatter(new PercentFormatter());
                ((PieChart)mapPie.get(pieDataSet)).setUsePercentValues(true);
            }else{
                pieData.setValueFormatter(new DefaultValueFormatter(1));
                ((PieChart)mapPie.get(pieDataSet)).setUsePercentValues(false);
            }

            ((PieChart)mapPie.get(pieDataSet)).setData(pieData);
            ((PieChart)mapPie.get(pieDataSet)).invalidate();
        }

    }

    public void onClickTextBouteilleNb(View view) {
        afficherMenu(textViewNb, textViewPct);

        // afficher les graphes par nombre
        unite = UNITE_NB;
        onResume();
    }

    public void onClickTextBouteillePct(View view) {
        afficherMenu(textViewPct, textViewNb);

        // afficher les graphes par pourcentage
        unite = UNITE_PCT;
        onResume();
    }

    private void afficherMenu(TextView textViewToSpan, TextView textViewToUnspan){

        SpannableString content = new SpannableString( textViewToSpan.getText() ) ;
        content.setSpan( new UnderlineSpan() , 0 , content.length() , 0 ) ;
        content.setSpan(new StyleSpan(Typeface.BOLD), 0 , content.length() , 0);
        textViewToSpan.setText(content) ;

        String t = textViewToUnspan.getText().toString();
        textViewToUnspan.setText(t) ;
    }

    private void afficherValeurCave(){
        //
        TextView textViewValueCave  = findViewById(R.id.textCellarValue) ;

        if (bouteilleDao == null){
            bouteilleDao = new BouteilleDao(this, null);
        }

        float value = bouteilleDao.getValueOfCave(cave);
        textViewValueCave.setText(getString(R.string.stats_cellar_value, value));

    }


    private static class pieChartOnChartValueSelectedListener implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {
            //trigger activities when entry is clicked
        }

        @Override
        public void onNothingSelected() {

        }
    }
}
