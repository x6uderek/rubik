package com.derek.android.rubik;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;

/**
 * picking ray
 */
public class Ray {
    public Ray(MatrixGrabber matrixGrabber,int[] viewPort,float xTouch, float yTouch){
        float[] temp = new float[4];

        float winy =(float)viewPort[3] - yTouch;
        int result = GLU.gluUnProject(xTouch, winy, 1f, matrixGrabber.mModelView, 0, matrixGrabber.mProjection, 0, viewPort, 0, temp, 0);
        if(result == GL10.GL_TRUE){
            farCoords[0] = temp[0] / temp[3] * Cube3.one;
            farCoords[1] = temp[1] / temp[3] * Cube3.one;
            farCoords[2] = temp[2] / temp[3] * Cube3.one;
        }
        result = GLU.gluUnProject(xTouch, winy, 0, matrixGrabber.mModelView, 0, matrixGrabber.mProjection, 0, viewPort, 0, temp, 0);
        if(result == GL10.GL_TRUE){
            nearCoords[0] = temp[0] / temp[3] * Cube3.one;
            nearCoords[1] = temp[1] / temp[3] * Cube3.one;
            nearCoords[2] = temp[2] / temp[3] * Cube3.one;
        }
    }

    public float[] farCoords = new float[3];
    public float[] nearCoords = new float[3];
}
