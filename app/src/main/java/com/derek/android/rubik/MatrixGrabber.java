package com.derek.android.rubik;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by xuran on 2015/3/24.
 */
public class MatrixGrabber {
    public MatrixGrabber() {
        mModelView = new float[16];
        mProjection = new float[16];
    }

    /**
     * Record the current modelView and projection matrix state.
     * Has the side effect of setting the current matrix state to GL_MODELVIEW
     * @param gl
     */
    public void getCurrentState(GL10 gl) {
        getCurrentProjection(gl);
        getCurrentModelView(gl);
    }

    /**
     * Record the current modelView matrix state. Has the side effect of
     * setting the current matrix state to GL_MODELVIEW
     * @param gl
     */
    public void getCurrentModelView(GL10 gl) {
        getMatrix(gl, GL10.GL_MODELVIEW, mModelView);
    }

    /**
     * Record the current projection matrix state. Has the side effect of
     * setting the current matrix state to GL_PROJECTION
     * @param gl
     */
    public void getCurrentProjection(GL10 gl) {
        getMatrix(gl, GL10.GL_PROJECTION, mProjection);
    }

    private void getMatrix(GL10 gl, int mode, float[] mat) {
        MatrixTrackingGL gl2 = (MatrixTrackingGL) gl;
        gl2.getMatrix(mode, mat, 0);
    }

    public float[] mModelView;
    public float[] mProjection;
}
