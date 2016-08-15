package com.mytechia.robobo.framework.hri.vision.colorDetection.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;

import com.mytechia.commons.framework.exception.ConfigurationException;
import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.vision.basicCamera.Frame;
import com.mytechia.robobo.framework.hri.vision.colorDetection.AColorDetectionModule;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by luis on 9/8/16.
 */
public class OpenCvColorDetectionModule extends AColorDetectionModule {
    private String TAG ="OCVCOolormodule";
    private Context context;
    private int cuentaframes=0;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(context) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        context = manager.getApplicationContext();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }

    public void processFrame(Bitmap bmp){
        Scalar mBlobColorHsv;
        //Log.d(TAG,"Cojo un frame y lo tiro por el retrete, y ya son "+cuentaframes+" frames los que el retrete se ha tragado!");
        //cuentaframes++;
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Mat imageMat = new Mat ( bmp.getHeight(), bmp.getWidth(), CvType.CV_8U);
        Mat hsvMat = new Mat ( bmp.getHeight(), bmp.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bmp32, imageMat);
        Imgproc.cvtColor(imageMat, hsvMat, Imgproc.COLOR_RGB2HSV, 3);
        Mat centerImage = hsvMat.submat((hsvMat.rows()/2) -20,  (hsvMat.rows()/2)+20, (hsvMat.cols()/2) -20, 20 + (hsvMat.cols()/2));
        mBlobColorHsv = Core.mean(centerImage);
        Log.d(TAG,"HSV= "+mBlobColorHsv.toString()+" Mat: "+centerImage.toString());
        float[] floatHsv=new float[3];
        floatHsv[0]=(float)mBlobColorHsv.val[0]*2;
        floatHsv[1]=(float)mBlobColorHsv.val[1];
        floatHsv[2]=(float)mBlobColorHsv.val[2];


        notifyColor(Color.HSVToColor(floatHsv));
        Log.d(TAG,"Color: "+Color.HSVToColor(floatHsv));
        /*if((mBlobColorHsv.val[0]>165)&&(mBlobColorHsv.val[0]<179)){
            Log.d(TAG,"RED");
            notifyColor(Color.RED);
        }
        if((mBlobColorHsv.val[0]>90)&&(mBlobColorHsv.val[0]<120)){
            Log.d(TAG,"BLUE");
            notifyColor(Color);
        }

        if((mBlobColorHsv.val[0]>30)&&(mBlobColorHsv.val[0]<70)){
            Log.d(TAG,"GREEN");
        }*/

    }
}
