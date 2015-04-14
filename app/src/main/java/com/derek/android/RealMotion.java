package com.derek.android;

import android.util.Log;

import com.derek.android.rubik.Action;
import com.derek.android.rubik.Cube3;
import com.derek.android.rubik.CubeRender;
import com.derek.android.rubik.HighLightAnimation;
import com.derek.android.rubik.LayerRotate;
import com.derek.android.rubik.Ray;
import com.derek.android.rubik.RotateAnimation;
import com.derek.android.rubik.Square;
import com.derek.android.rubik.Triangle;
import com.derek.android.util.Vector;

/**
 * 拾取操作魔方
 */
public class RealMotion extends MotionHandler {

    private int[] curFace = new int[3];
    private int[] tmpFace = new int[3];

    public RealMotion(RotateAnimation animation,HighLightAnimation highLightAnimation,CubeRender render){
        super(animation,highLightAnimation,render);
    }

    @Override
    public boolean onMotionStart(float x, float y) {
        if(!getTouchSquare(x,y,curFace)){
            return false;
        }
        setHighLight();
        return true;
    }

    private boolean getTouchSquare(float x, float y, int[] face){
        Cube3 cube3 = Cube3.getInstance();
        Ray ray = cube3.getRay(x,y);
        float[] curIntersection = new float[3];
        face[0] = -1;
        float curD = -1;

        float[] intersection = new float[3];
        for (short i=0; i<6; i++){
            for (short j=0; j<2; j++){
                if(Triangle.intersectRayAndTriangle(ray,triangles[i][j],intersection)==1){
                    float d = Vector.length(Vector.minus(intersection,ray.nearCoords));
                    if(curD<0 || curD>d){
                        curD = d;
                        face[0] = i;
                        System.arraycopy(intersection,0,curIntersection,0,3);
                    }
                }
            }
        }
        if(face[0]==-1){
            return false;
        }
        //intersection到三角形边 c到ab的距离
        float[] ac = Vector.minus(curIntersection,triangles[face[0]][0].V0);
        float[] ab = Vector.minus(triangles[face[0]][0].V1,triangles[face[0]][0].V0);
        float d1 = Vector.length(Vector.crossProduct(ac,ab))/Vector.length(ab);

        ab = Vector.minus(triangles[face[0]][0].V2,triangles[face[0]][0].V0);
        float d2 = Vector.length(Vector.crossProduct(ac,ab))/Vector.length(ab);
        face[1] = (int)Math.floor(d1/Cube3.cubeEdgeLength);
        face[2] = (int)Math.floor(d2/Cube3.cubeEdgeLength);
        return !(face[1] > 2 || face[2] > 2);
    }

    @Override
    public void onMotionMove(float x, float y) {
        //todo ?
    }

    @Override
    public Action onMotionEnd(float x, float y) {
        cancelHighLight();
        if(!getTouchSquare(x,y,tmpFace)){
            return null;
        }
        for(int i=0; i<3; i++){
            tmpFace[i] = tmpFace[i]-curFace[i];
        }
        if(tmpFace[0]!=0){//different face
            return null;
        }
        Log.d("action","cur:"+String.valueOf(curFace[0])+","+String.valueOf(curFace[1])+","+String.valueOf(curFace[2]));
        Log.d("action","tmp:"+String.valueOf(tmpFace[0])+","+String.valueOf(tmpFace[1])+","+String.valueOf(tmpFace[2]));
        if(tmpFace[1]==0 && tmpFace[2]!=0){
            return getAction(curFace[0],curFace[1],tmpFace[2]>0,true);
        }
        else if(tmpFace[2]==0 && tmpFace[1]!=0){
            return getAction(curFace[0],curFace[2],tmpFace[1]>0,false);
        }
        return null;
    }

    private Action getAction(int faceindex, int index, boolean direction, boolean x){
        Action action = null;
        switch (faceindex){
            case 0:
                action = new LayerRotate(index + 3 + (x?3:0),!direction,rotateAnimation);
                break;
            case 1:
                action = new LayerRotate(index + 3 + (x?3:0),direction,rotateAnimation);
                break;
            case 2:
                action = new LayerRotate(index + (x?6:0),x==direction,rotateAnimation);
                break;
            case 3:
                action = new LayerRotate(index + (x?6:0),x!=direction,rotateAnimation);
                break;
            case 4:
                action = new LayerRotate(index + (x?3:0),direction,rotateAnimation);
                break;
            case 5:
                action = new LayerRotate(index + (x?3:0),!direction,rotateAnimation);
                break;
        }
        return action;
    }

    private void setHighLight(){
        highLightAnimation.addHighLight(getCurFace());
    }

    private void cancelHighLight(){
        highLightAnimation.cancelHighLight(getCurFace());
    }

    private Square getCurFace(){
        Cube3 cube3 = Cube3.getInstance();
        Square face = null;
        if(curFace[0]>=0 && curFace[0]<=6){
            face = cube3.getLayer((3*curFace[0]+curFace[0]%2)/2).cubes[curFace[1]][curFace[2]].getFace(curFace[0]);
        }
        return face;
    }

    private float[] topLeftFront = new float[]{-Cube3.one,Cube3.one,Cube3.one};
    private float[] topLeftBack = new float[]{-Cube3.one,Cube3.one,-Cube3.one};
    private float[] topRightFront = new float[]{Cube3.one,Cube3.one,Cube3.one};
    private float[] topRightBack = new float[]{Cube3.one,Cube3.one,-Cube3.one};
    private float[] bottomLeftFront = new float[]{-Cube3.one,-Cube3.one,Cube3.one};
    private float[] bottomLeftBack = new float[]{-Cube3.one,-Cube3.one,-Cube3.one};
    private float[] bottomRightFront = new float[]{Cube3.one,-Cube3.one,Cube3.one};
    private float[] bottomRightBack = new float[]{Cube3.one,-Cube3.one,-Cube3.one};

    private Triangle[][] triangles = new Triangle[][]{
            {
                    new Triangle(bottomLeftBack,topLeftBack,bottomLeftFront),
                    new Triangle(topLeftFront,topLeftBack,bottomLeftFront)
            },//0 left
            {
                    new Triangle(bottomRightBack,topRightBack,bottomRightFront),
                    new Triangle(topRightFront,topRightBack,bottomRightFront)
            },//1 right
            {
                    new Triangle(bottomLeftBack,bottomRightBack,bottomLeftFront),
                    new Triangle(bottomRightFront,bottomRightBack,bottomLeftFront)
            },//2 bottom
            {
                    new Triangle(topLeftBack,topRightBack,topLeftFront),
                    new Triangle(topRightFront,topRightBack,topLeftFront)
            },//3 top
            {
                    new Triangle(bottomLeftBack,bottomRightBack,topLeftBack),
                    new Triangle(topRightBack,bottomRightBack,topLeftBack)
            },//4 back
            {
                    new Triangle(bottomLeftFront,bottomRightFront,topLeftFront),
                    new Triangle(topRightFront,bottomRightFront,topLeftFront)
            },//5 front
    };
}
