package com.tintin.mat.winecellar.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.tintin.mat.winecellar.BuildConfig;
import com.tintin.mat.winecellar.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 13/03/2018.
 */

public class ManageExternalFileSystemDao extends DAOBase {

    protected Context myContext;

    public ManageExternalFileSystemDao(Context pContext, DatabaseHandler databaseHandler) {
        super(pContext, databaseHandler);
    }

    /* save photo in 90% quality, full size and vignette in 90% quality, 480 px width
    * return List<photoPath, vignettePath>*/
    protected List<String> saveImageToExternalStorage(String photoPath) {
        File myDirPhoto = Utils.getPublicAlbumStorageDir(Utils.getApplicationFullName(myContext));
        File myDirVignette = Utils.getPublicVignetteStorageDir(Utils.getApplicationFullName(myContext));
        Bitmap finalBitmap = Utils.getImage(photoPath, myContext);
        myDirPhoto.mkdirs();
        myDirVignette.mkdirs();
        File filePhoto = null;
        File fileVignette = null;
        do{
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            filePhoto = new File(myDirPhoto, fname);
            fileVignette = new File(myDirVignette, fname);
        }
        while (filePhoto == null || filePhoto.exists());
        try {
            if (fileVignette.exists()){
                fileVignette.delete();
            }
            //
            filePhoto.createNewFile(); // if file already exists will do nothing
            fileVignette.createNewFile(); // if file already exists will do nothing
            FileOutputStream outPhoto = new FileOutputStream(filePhoto, false);
            FileOutputStream outVignette = new FileOutputStream(fileVignette, false);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outPhoto);
            //finalBitmap.compress(Bitmap.CompressFormat.JPEG, 0, outVignette);

            // diminuer la taille pour la vignette
            float aspectRatio = finalBitmap.getWidth() /
                    (float) finalBitmap.getHeight();
            int width = 480;
            int height = Math.round(width / aspectRatio);

            Bitmap finalVignetteBitmap = Bitmap.createScaledBitmap(finalBitmap, width, height, false);

            finalVignetteBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outVignette);
            outPhoto.flush();
            outVignette.flush();
            outPhoto.close();
            outVignette.close();
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "ManageExternalFileSystemDao.saveImageToExternalStorage ",e );
            }
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(myContext, new String[] { filePhoto.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
        MediaScannerConnection.scanFile(myContext, new String[] { fileVignette.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        return new ArrayList<String>(Arrays.asList(Uri.fromFile(filePhoto).toString(), Uri.fromFile(fileVignette).toString()));

    }

    protected boolean deleteImageFromExternalStorage(String photoPath) {
        boolean ret = true;
        if (photoPath != null) {
            ret = false;
            Uri uri = Uri.parse(photoPath);
            File file = new File(uri.getPath());
            if (file.exists()) {
                try {
                    ret = file.delete();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "ManageExternalFileSystemDao.deleteImageFromExternalStorage ", e);
                    }
                }
            }
        }
        return ret;

    }
}
