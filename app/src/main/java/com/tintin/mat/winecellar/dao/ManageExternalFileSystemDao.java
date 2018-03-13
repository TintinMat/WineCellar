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

    protected String saveImageToExternalStorage(String photoPath) {
        File myDir = Utils.getPublicAlbumStorageDir(Utils.getApplicationName(myContext));
        Bitmap finalBitmap = Utils.getImage(photoPath, myContext);
        myDir.mkdirs();
        String fname = "";
        File file = null;
        do{
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            fname = "Image-" + n + ".jpg";
            file = new File(myDir, fname);
        }
        while (file == null || file.exists());
        try {
            file.createNewFile(); // if file already exists will do nothing
            FileOutputStream out = new FileOutputStream(file, false);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "ManageExternalFileSystemDao.saveImageToExternalStorage ",e );
            }
        }


        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(myContext, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

        return Uri.fromFile(file).toString();

    }

    protected boolean deleteImageFromExternalStorage(String photoPath) {
        Uri uri = Uri.parse(photoPath);
        File file = new File(uri.getPath());
        boolean ret = false;
        if (file.exists()) {
            try {
                ret = file.delete();
            } catch (Exception e) {
                if (BuildConfig.DEBUG){
                    Log.e(TAG, "ManageExternalFileSystemDao.deleteImageFromExternalStorage ",e );
                }
            }
        }
        return ret;

    }
}
