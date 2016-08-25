package com.mytechia.robobo.framework.hri.vision.colorDetection.opencv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

/**
 * Created by luis on 9/8/16.
 * http://www.workwithcolor.com/orange-brown-color-hue-range-01.htm
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

    public void processFrame(Bitmap bmp) {
        Scalar mBlobColorHsv = new Scalar(0, 0, 0);
        //Log.d(TAG,"Cojo un frame y lo tiro por el retrete, y ya son "+cuentaframes+" frames los que el retrete se ha tragado!");
        //cuentaframes++;


        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Mat imageMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);
        Mat hsvMat = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC3);

        Mat bwimage = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp32, imageMat);

        Imgproc.cvtColor(imageMat, hsvMat, Imgproc.COLOR_RGB2HSV, 3);


        Imgproc.cvtColor(imageMat, bwimage, Imgproc.COLOR_RGB2GRAY, 1);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.Canny(bwimage, bwimage, 75, 100);

        Imgproc.findContours(bwimage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        bwimage.release();



        double maxArea = -1;
        int maxAreaIdx = -1;

        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);

            if ((contourarea > maxArea)) {
                maxArea = contourarea;
                maxAreaIdx = idx;
            }
        }

        Log.d(TAG, "MAXAREA: " + maxArea);


        if (maxArea > 1000) {
            MatOfPoint contour = contours.get(maxAreaIdx);
            Mat contourMat = Mat.zeros(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);


            Imgproc.drawContours(contourMat, contours, maxAreaIdx, new Scalar(1), -1);


            Rect boundingRect = Imgproc.boundingRect(contour);






            mBlobColorHsv = Core.mean(hsvMat, contourMat); //(float) sum/pointList.size();
            hsvMat.release();



            float[] floatHsv = new float[3];
            floatHsv[0] = (float) mBlobColorHsv.val[0] * 2;
            floatHsv[1] = (float) mBlobColorHsv.val[1];
            floatHsv[2] = (float) mBlobColorHsv.val[2];

            Imgproc.drawContours(imageMat, contours, maxAreaIdx, new
                    Scalar(255));

            Utils.matToBitmap(imageMat, bmp);


            int colorrgb = Color.HSVToColor(floatHsv);
            if ((mBlobColorHsv.val[0] > 173) && (mBlobColorHsv.val[0] <= 179)) {
                Log.d(TAG, "RED" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.RED, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 0) && (mBlobColorHsv.val[0] <= 8)) {
                Log.d(TAG, "RED" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.RED, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 9) && (mBlobColorHsv.val[0] <= 37)) {
                Log.d(TAG, "YELLOW" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.YELLOW, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 38) && (mBlobColorHsv.val[0] <= 74)) {
                Log.d(TAG, "GREEN" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.GREEN, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 75) && (mBlobColorHsv.val[0] <= 104)) {
                Log.d(TAG, "CYAN" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.CYAN, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 105) && (mBlobColorHsv.val[0] <= 149)) {
                Log.d(TAG, "BLUE" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.BLUE, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }
            if ((mBlobColorHsv.val[0] > 150) && (mBlobColorHsv.val[0] <= 172)) {
                Log.d(TAG, "MAGENTA" + mBlobColorHsv.val[0]);
                notifyColor(colorrgb, Color.MAGENTA, boundingRect.x, boundingRect.y, boundingRect.height, boundingRect.width, bmp);
            }

        }
    }
}
