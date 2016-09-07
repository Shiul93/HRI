package com.mytechia.robobo.framework.hri.touch.android;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
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

            onLongPress(motionEvent);

        }else {
            notifyTap(Math.round(coords.x), Math.round(coords.y));
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

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

        long time =(motionEvent1.getEventTime()-motionEvent.getEventTime());
        Log.d("AT_module","onFling "+time);
        int x1,x2,y1,y2;

        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);

        x1 = Math.round(coords.x);
        y1 = Math.round(coords.y);


        MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
        motionEvent1.getPointerCoords(0,coords1);

        x2 = Math.round(coords1.x);
        y2 = Math.round(coords1.y);
        Log.d("AT_module","x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2);
        double distance = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
        Log.d("AT_module","Distance: "+distance );
        int motionx = x1-x2;
        int motiony = y1-y2;
        //y1 - y2 for top left reference system
        double angle = Math.atan2((y1-y2),(x2-x1));
        Log.d("AT_module","Angle: "+angle);
        if (angle<0){angle = Math.PI +(Math.PI+angle);}
        if (Math.abs(motionx)>Math.abs(motiony)){
            if (motionx>=0){
                notifyFling(TouchGestureDirection.LEFT,angle,time,distance);
            }else {
                notifyFling(TouchGestureDirection.RIGHT,angle,time,distance);
            }
        }else{
            if (motiony>=0){
                notifyFling(TouchGestureDirection.UP,angle,time,distance);
            }else {
                notifyFling(TouchGestureDirection.DOWN,angle,time,distance);
            }
        }
        return true;
    }
}
