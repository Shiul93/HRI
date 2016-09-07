/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Gervasio Varela <gervasio.varela@mytechia.com>
 *
 *   This file is part of Robobo Emotion Module.
 *
 *   Robobo Emotion Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Emotion Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Emotion Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.hri.emotion.webgl;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.emotion.DefaultEmotionModule;
import com.mytechia.robobo.framework.hri.emotion.Emotion;
import com.mytechia.robobo.framework.hri.emotion.IEmotionListener;
import com.mytechia.robobo.framework.hri.emotion.R;
import com.mytechia.robobo.framework.service.RoboboServiceHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class WebGLEmotionDisplayActivity extends Activity implements IEmotionListener {

    private RoboboServiceHelper roboboHelper;


    private WebView myWebView;

    private DefaultEmotionModule emotionModule;

    private int c = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_web_glemotion_display);


        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        myWebView.loadUrl("file:///android_asset/emotions/index.html");
        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                emotionModule.notifyTouchEvent(motionEvent);
                return false;
            }
        });




        //myWebView.loadUrl("javascript:SendMessage('Cylinder','changeColor','');");
        //myWebView.loadUrl("javascript:testEcho('Hello World!')");


        this.roboboHelper = new RoboboServiceHelper(this, new RoboboServiceHelper.Listener() {
            @Override
            public void onRoboboManagerStarted(RoboboManager roboboManager) {

                try {
                    emotionModule =
                            roboboManager.getModuleInstance(DefaultEmotionModule.class);
                    emotionModule.subscribe(WebGLEmotionDisplayActivity.this);
                } catch(ModuleNotFoundException ex) {
                    Log.e("EMOTION-WGL", ex.getMessage());
                }

            }

            @Override
            public void onError(String errorMsg) {

            }
        });
        this.roboboHelper.bindRoboboService(new Bundle());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.roboboHelper.unbindRoboboService();
    }

    @Override
    public void newEmotion(Emotion emotion) {

        final Emotion emotionLocal = emotion;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                switch(emotionLocal) {
                    case NORMAL: myWebView.loadUrl("javascript:emotionNormal()"); break;
                    case HAPPY: myWebView.loadUrl("javascript:emotionHappy()"); break;
                    case ANGRY: myWebView.loadUrl("javascript:emotionAngry()"); break;
                    case LAUGHING: myWebView.loadUrl("javascript:emotionLaughing()"); break;
                    case EMBARRASED: myWebView.loadUrl("javascript:emotionEmbarrased()"); break;
                    case SAD: myWebView.loadUrl("javascript:emotionSad()"); break;
                    case SURPRISED: myWebView.loadUrl("javascript:emotionSurprised()"); break;
                    case SMYLING: myWebView.loadUrl("javascript:emotionSmyling()"); break;
                    case IN_LOVE: myWebView.loadUrl("javascript:emotionInLove()"); break;
                }

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("WebGlActivity","EVENT");
        emotionModule.notifyTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
