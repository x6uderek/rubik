package com.derek.android;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import com.derek.android.rubik.Action;
import com.derek.android.rubik.Cube3;
import com.derek.android.rubik.CubeRender;
import com.derek.android.rubik.HighLightAnimation;
import com.derek.android.rubik.RotateAnimation;

public abstract class MotionHandler implements Cube3.ViewportChangeListener {
    protected int currentRotateId = -1;
    protected RotateAnimation rotateAnimation;
    protected HighLightAnimation highLightAnimation;
    protected CubeRender render;

    private float scaleX = 180.0f / 320;
    private float scaleY = 180.0f / 480;

    private float preX;
    private float preY;

    public MotionHandler(RotateAnimation rotateAnimation, HighLightAnimation highLightAnimation, CubeRender render){
        this.rotateAnimation = rotateAnimation;
        this.highLightAnimation = highLightAnimation;
        this.render = render;
        Cube3.getInstance().setViewportChangeListener(this);
    }

    public boolean onTouchEvent(MotionEvent event){
        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);
        int id = event.getPointerId(index);
        float x = event.getX(index);
        float y = event.getY(index);
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if(!onMotionStart(x,y)){
                    currentRotateId = id;
                    preX = x;
                    preY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(id==currentRotateId){
                    float dx = x - preX;
                    float dy = y - preY;
                    preX = x;
                    preY = y;
                    render.rorate(-dy * scaleY,dx * scaleX);
                }
                else{
                    onMotionMove(x,y);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if(id==currentRotateId) {
                    currentRotateId = -1;
                }
                else{
                    Action rotateAction = onMotionEnd(x, y);
                    if(rotateAction!=null) {
                        rotateAnimation.addAction(rotateAction);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onViewportChange(int[] viewport){
        scaleX = 180.f / viewport[2];
        scaleY = 180.f / viewport[3];
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
