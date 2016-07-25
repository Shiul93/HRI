package com.mytechia.robobo.framework.hri.vision.faceDetection.android;

import android.graphics.PointF;
import android.media.FaceDetector;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ACameraModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraListener;
import com.mytechia.robobo.framework.hri.vision.basicCamera.ICameraModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.android.AndroidCameraModule;
import com.mytechia.robobo.framework.hri.vision.basicCamera.android.Frame;
import com.mytechia.robobo.framework.hri.vision.faceDetection.AFaceDetectionModule;
import com.mytechia.robobo.framework.hri.vision.faceDetection.IFaceDetectionModule;

/**
 * Created by luis on 24/7/16.
 */
public class AndroidFaceDetectorModule extends AFaceDetectionModule implements ICameraListener{


    //region VAR
    private FaceDetector faceDetector;
    private FaceDetector.Face[] faces;
    float myEyesDistance;
    int numberOfFaceDetected;
    private ICameraModule cameraModule;
    //endregion

    //region IModule methods

    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        this.cameraModule = manager.getModuleInstance(ICameraModule.class);
        cameraModule.suscribe(this);
        faces =  new FaceDetector.Face[5];

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



    //endregion

    //region ICameraListener Methods
    @Override
    public void onNewFrame(Frame frame) {
        faceDetector = new FaceDetector(frame.getBitmap().getWidth(),frame.getBitmap().getHeight(),1);
        int facenumber = faceDetector.findFaces(frame.getBitmap(),faces);
        if (facenumber>0){
            PointF facecoord = new PointF();
            faces[0].getMidPoint(facecoord);

            notifyFace(facecoord);
        }
    }
    //endregion


}
