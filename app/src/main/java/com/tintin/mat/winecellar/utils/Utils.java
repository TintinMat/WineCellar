package com.tintin.mat.winecellar.utils;

import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.tintin.mat.winecellar.BuildConfig;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by Mat & Audrey on 30/10/2017.
 */

public class Utils {


    public static Bitmap getImage(String photoPath, Context myContext){
        InputStream imageStream = null;
        Bitmap finalBitmap = null;
        if (photoPath != null && photoPath.length()>0) {
            try {
                imageStream = myContext.getContentResolver().openInputStream(Uri.parse(photoPath));
                finalBitmap = BitmapFactory.decodeStream(imageStream);
                if (imageStream != null) {
                    imageStream.close();
                }
            } catch (FileNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "<getImage> Error : " + e.getLocalizedMessage());
                }
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "<getImage> Error : " + ioe.getLocalizedMessage());
                }
            }
        }
        return finalBitmap;
    }

    public static byte[] getImageBytes(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }else{
            return null;
        }
    }

    public static byte[] getImageBytes(String photoPath, Context myContext){
        if (photoPath != null && photoPath.length()>0) {
            File f = new File(Uri.parse(photoPath).getPath());
            try {
                return FileUtils.readFileToByteArray(f);
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "<getImageBytes> Error : " + e.getLocalizedMessage());
                }
                return null;
            }
        }
        return null;

        //return getImageBytes(getImage(photPath, myContext));
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to write */
    public static boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getPublicAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "Directory not created " );
            }
        }
        return file;
    }

    public static File getPublicVignetteStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(getPublicAlbumStorageDir(albumName), "vignettes");
        if (!file.mkdirs()) {
            if (BuildConfig.DEBUG){
                Log.e(TAG, "Directory not created " );
            }
        }
        return file;
    }


    public static String getApplicationFullName(Context context){
        if (getPackageName(context).length() > 0) {
            return getPackageName(context) + "." + getApplicationName(context);
        }else{
            return getApplicationName(context);
        }
    }

    private static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private static String getPackageName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (applicationInfo != null) {
            return applicationInfo.packageName;
        }
        else{
            return "";
        }
    }
}
