package br.com.megalaser.dwgpdf;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rodrigo on 31/01/18.
 */

public class Util {


    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static void setProperty(String key, String value, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        properties.setProperty(key, value);
        properties.store(assetManager.openFd("config.properties").createOutputStream(),  null);
    }

    /**
     * Return path converted for folder filename
     * ex. 53253 to 053000
     * @param fileName
     * @return
     */
    public String convertPathNumber(String fileName) {

        StringBuilder oldValue = new StringBuilder();

        if (fileName.contains("-")) {
            for (int i= 0; i < fileName.length(); i++) {
                if (fileName.charAt(i) != '-') {
                    oldValue.append(fileName.charAt(i));
                } else {
                    break;
                }
            }
        } else {
            for (int i= 0; i < fileName.length(); i++) {
                if (fileName.charAt(i) != '.') {
                    oldValue.append(fileName.charAt(i));
                } else {
                    break;
                }
            }
        }

        int size = oldValue.length();
        Log.d("Log_SMB", oldValue.toString());
        StringBuilder newValue = oldValue;
        switch (size) {
            case 5:
                newValue.append("0").append(fileName.substring(0, 2)).append("000");
                break;
            case 6:
                newValue.append(fileName.substring(0, 3)).append("000");
                break;
            default:
        }
        return newValue.toString();
    }

}
