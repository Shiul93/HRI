package com.mytechia.robobo.framework.hri.vision.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by luis on 21/7/16.
 */
public class Frame {
    private int width;
    private int height;
    private String frameId;

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    private int seqNum;
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static Bitmap decodeBytes(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
