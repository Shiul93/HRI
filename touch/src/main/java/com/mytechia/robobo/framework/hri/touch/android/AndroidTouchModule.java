package com.mytechia.robobo.framework.hri.touch.android;

import android.content.Context;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;



import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.touch.ATouchModule;
import com.mytechia.robobo.framework.hri.touch.TouchGestureDirection;

import java.sql.Timestamp;

/**
 * Created by luis on 5/4/16.
 */
public class AndroidTouchModule extends ATouchModule implements GestureDetector.OnGestureListener {

    private GestureDetectorCompat mDetector;

    private String TAG = "TouchModule";
    public  AndroidTouchModule(){
        super();
    }
    long startupTime ;

    public void startup(RoboboManager manager){
        Looper.prepare();
        startupTime = System.currentTimeMillis();
        mDetector = new GestureDetectorCompat(manager.getApplicationContext(),this);

    }

    public void shutdown(){

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }


    public boolean onTouchEvent(MotionEvent event){

        return this.mDetector.onTouchEvent(event);

    }

    public boolean feedTouchEvent(MotionEvent event){

        return this.mDetector.onTouchEvent(event);

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        Log.d(TAG,"Current "+motionEvent.getEventTime()+"ms");
        Log.d(TAG,"Event "+motionEvent.getDownTime()+"ms");
        Log.d(TAG,"Difference "+(motionEvent.getEventTime()-(int)motionEvent.getDownTime())+"ms");
        if((motionEvent.getEventTime()-(int)motionEvent.getDownTime())>=500){
            //TODO Mirar por que no salta bien el otro listener
            onLongPress(motionEvent);

        }else {
            notifyTap(Math.round(coords.x), Math.round(coords.y));
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //TODO Dar soporte a varios dedos,¿Pointer count y media de posiciones?
        Log.d("AT_module","onScroll");
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
        motionEvent1.getPointerCoords(0,coords1);
        int motionx = Math.round(coords.x)-Math.round(coords1.x);
        int motiony = Math.round(coords.y)-Math.round(coords1.y);
        if (Math.abs(motionx)>Math.abs(motiony)){
            if (motionx>=0){
                notifyCaress(TouchGestureDirection.LEFT);
            }else {
                notifyCaress(TouchGestureDirection.RIGHT);
            }
        }else{
            if (motiony>=0){
                notifyCaress(TouchGestureDirection.UP);
            }else {
                notifyCaress(TouchGestureDirection.DOWN);
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d("AT_module","onLongPress");
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        notifyTouch(Math.round(coords.x), Math.round(coords.y));

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //TODO Dar soporte a varios dedos,¿Pointer count y media de posiciones?
        Log.d("AT_module","onFling "+(motionEvent1.getEventTime()-motionEvent.getEventTime()));
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
        motionEvent1.getPointerCoords(0,coords1);
        int motionx = Math.round(coords.x)-Math.round(coords1.x);
        int motiony = Math.round(coords.y)-Math.round(coords1.y);
        if (Math.abs(motionx)>Math.abs(motiony)){
            if (motionx>=0){
                notifyFling(TouchGestureDirection.LEFT);
            }else {
                notifyFling(TouchGestureDirection.RIGHT);
            }
        }else{
            if (motiony>=0){
                notifyFling(TouchGestureDirection.UP);
            }else {
                notifyFling(TouchGestureDirection.DOWN);
            }
        }
        return true;
    }
}
