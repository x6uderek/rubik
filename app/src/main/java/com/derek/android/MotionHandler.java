package com.derek.android;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;

import com.derek.android.rubik.Action;
import com.derek.android.rubik.Cube3;
import com.derek.android.rubik.CubeRender;
import com.derek.android.rubik.HighLightAnimation;
import com.derek.android.rubik.RotateAnimation;

public abstract class MotionHandler implements Cube3.ViewportChangeListener {
    protected int currentRotateId = -1;
    protected int currentTouchId = -1;
    protected RotateAnimation rotateAnimation;
    protected HighLightAnimation highLightAnimation;
    protected CubeRender render;

    private float scale = 180.0f / 320;

    private float preX;
    private float preY;

    public MotionHandler(RotateAnimation rotateAnimation, HighLightAnimation highLightAnimation, CubeRender render){
        this.rotateAnimation = rotateAnimation;
        this.highLightAnimation = highLightAnimation;
        this.render = render;
        Cube3.getInstance().setViewportChangeListener(this);
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getPointerCount()>2){
            return true;
        }
        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);
        int id = event.getPointerId(index);
        float x = event.getX(index);
        float y = event.getY(index);
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if(onMotionStart(x,y)){
                    currentTouchId = id;
                }
                else {
                    currentRotateId = id;
                    preX = x;
                    preY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(currentRotateId!=-1){
                    int pointIndex = event.findPointerIndex(currentRotateId);
                    float dx = event.getX(pointIndex) - preX;
                    float dy = event.getY(pointIndex) - preY;
                    preX = event.getX(pointIndex);
                    preY = event.getY(pointIndex);
                    render.rorate(-dy * scale,dx * scale);
                }
                else{
                    int pointIndex = event.findPointerIndex(currentTouchId);
                    if(pointIndex!=-1) {
                        onMotionMove(event.getX(pointIndex), event.getY(pointIndex));
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if(currentTouchId==id){
                    Action rotateAction = onMotionEnd(x, y);
                    if(rotateAction!=null) {
                        rotateAnimation.addAction(rotateAction);
                    }
                    currentTouchId = -1;
                }
                else if(currentRotateId==id){
                    currentRotateId = -1;
                }
                break;
        }
        return true;
    }

    @Override
    public void onViewportChange(int[] viewport){
        scale = 180.f / (viewport[2]<viewport[3]?viewport[2]:viewport[3]);
    }

    /**
     *
     * @param x x
     * @param y y
     * @return false for a rotation, true for an action
     */
    public abstract boolean onMotionStart(float x, float y);
    public abstract void onMotionMove(float x, float y);
    public abstract Action onMotionEnd(float x, float y);
}
