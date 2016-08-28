package com.mytechia.robobo.framework.hri.messaging.gmailBackground;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.messaging.AMessagingModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * Created by luis on 4/8/16.
 * https://github.com/yesidlazaro/GmailBackground
 */
public class GmailBackgroundMessagingModule extends AMessagingModule {

    //region VAR
    private Context context;
    private String TAG = "GmailMessagingModule";
    private String mailDirection;
    private String mailPassword;
    //endregion
    //region IMessagingModule Methods
    @Override
    public void sendMessage(String text, String addresee) {
        Log.d(TAG,"SENDMESSAGE");
        BackgroundMail.newBuilder(context)
                .withUsername(mailDirection)
                .withPassword(mailPassword)
                .withMailto(addresee)
                .withSubject("ROBOBO NOTIFICATION, ID:"+ new RandomString(10).nextString())
                .withBody(text)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Send Success");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Log.d(TAG,"Send Fail");
                    }
                })
                .send();
    }

    @Override
    public void sendMessage(String text, String addresee, Bitmap photoAtachment) {
        String imagepath = saveToExternalStorage(photoAtachment);
        Log.d(TAG,"SENDMESSAGE");
        BackgroundMail.newBuilder(context)
                .withUsername(mailDirection)
                .withPassword(mailPassword)
                .withMailto(addresee)
                .withSubject("ROBOBO NOTIFICATION, ID:"+ new RandomString(10).nextString())
                .withBody(text)
                .withAttachments(imagepath)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG,"Send Success");
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Log.e(TAG,"Send Fail");
                    }
                })
                .send();

    }


    private String saveToExternalStorage(Bitmap bm){
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/req_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    //endregion

    //region IModule Methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("messaging.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailDirection = properties.getProperty("emailaccount");
        mailPassword = properties.getProperty("emailpasswd");


        context = manager.getApplicationContext();
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return "GmailBackground Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }
    //endregion
    private class RandomString {

        private  final char[] symbols;

         {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomString(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }


}

