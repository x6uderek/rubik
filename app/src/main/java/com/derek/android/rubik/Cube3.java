package com.derek.android.rubik;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;


public class Cube3 {
    private static Cube3 instance;
    private int indicesCount;

    public static final int leftX = 0;
    public static final int middleX = 1;
    public static final int rightX = 2;
    public static final int bottomY = 3;
    public static final int middleY = 4;
    public static final int topY = 5;
    public static final int backZ = 6;
    public static final int middleZ = 7;
    public static final int frontZ = 8;

    public static final int one = 0x10000;
    public static final float cubeEdgeLength = one*2/3;

    protected Cube[][][] cubeArray = new Cube[3][3][3];
    protected Layer[] layers = new Layer[9];

    private IntBuffer vertexBuffer;
    private IntBuffer colorBuffer;
    private ShortBuffer indexBuffer;

    private GL10 gl;
    private int[] viewPort;
    private MatrixGrabber grabber = new MatrixGrabber();
    private ViewportChangeListener listener;

    public static Cube3 getInstance() {
        if (Cube3.instance == null) {
            Cube3.instance = new Cube3();
        }
        return Cube3.instance;
    }

    private Cube3() {
        createCube3();
    }

    public void setGL(GL10 gl){
        this.gl = gl;
    }

    public void setViewPort(int[] viewPort){
        this.viewPort = viewPort;
        listener.onViewportChange(this.viewPort);
    }

    public void setViewportChangeListener(ViewportChangeListener listener){
        this.listener = listener;
    }

    public Ray getRay(float x,float y){
        grabber.getCurrentState(gl);
        return new Ray(grabber,viewPort,x,y);
    }

    private void createCube3() {
        Color red = new Color(one, 0, 0);
        Color green = new Color(0, one, 0);
        Color blue = new Color(0, 0, one);
        Color yellow = new Color(one, one, 0);
        Color cyan = new Color(0, one, one);
        Color magenta = new Color(one, 0, one);

        int coord0 = -one;
        int coord1 = (int)(-one * 0.38);
        int coord2 = (int)(-one * 0.32);
        int coord3 = -coord2;
        int coord4 = -coord1;
        int coord5 = -coord0;
        //back 9 cubes
        cubeArray[0][0][0] = new Cube(coord0, coord1, coord0, coord1, coord0, coord1);
        cubeArray[0][0][1] = new Cube(coord2, coord3, coord0, coord1, coord0, coord1);
        cubeArray[0][0][2] = new Cube(coord4, coord5, coord0, coord1, coord0, coord1);

        cubeArray[0][1][0] = new Cube(coord0, coord1, coord2, coord3, coord0, coord1);
        cubeArray[0][1][1] = new Cube(coord2, coord3, coord2, coord3, coord0, coord1);
        cubeArray[0][1][2] = new Cube(coord4, coord5, coord2, coord3, coord0, coord1);

        cubeArray[0][2][0] = new Cube(coord0, coord1, coord4, coord5, coord0, coord1);
        cubeArray[0][2][1] = new Cube(coord2, coord3, coord4, coord5, coord0, coord1);
        cubeArray[0][2][2] = new Cube(coord4, coord5, coord4, coord5, coord0, coord1);

        //middle 9 cubes
        cubeArray[1][0][0] = new Cube(coord0, coord1, coord0, coord1, coord2, coord3);
        cubeArray[1][0][1] = new Cube(coord2, coord3, coord0, coord1, coord2, coord3);
        cubeArray[1][0][2] = new Cube(coord4, coord5, coord0, coord1, coord2, coord3);

        cubeArray[1][1][0] = new Cube(coord0, coord1, coord2, coord3, coord2, coord3);
        cubeArray[1][1][1] = new Cube(coord2, coord3, coord2, coord3, coord2, coord3);
        cubeArray[1][1][2] = new Cube(coord4, coord5, coord2, coord3, coord2, coord3);

        cubeArray[1][2][0] = new Cube(coord0, coord1, coord4, coord5, coord2, coord3);
        cubeArray[1][2][1] = new Cube(coord2, coord3, coord4, coord5, coord2, coord3);
        cubeArray[1][2][2] = new Cube(coord4, coord5, coord4, coord5, coord2, coord3);

        //front 9 cubes
        cubeArray[2][0][0] = new Cube(coord0, coord1, coord0, coord1, coord4, coord5);
        cubeArray[2][0][1] = new Cube(coord2, coord3, coord0, coord1, coord4, coord5);
        cubeArray[2][0][2] = new Cube(coord4, coord5, coord0, coord1, coord4, coord5);

        cubeArray[2][1][0] = new Cube(coord0, coord1, coord2, coord3, coord4, coord5);
        cubeArray[2][1][1] = new Cube(coord2, coord3, coord2, coord3, coord4, coord5);
        cubeArray[2][1][2] = new Cube(coord4, coord5, coord2, coord3, coord4, coord5);

        cubeArray[2][2][0] = new Cube(coord0, coord1, coord4, coord5, coord4, coord5);
        cubeArray[2][2][1] = new Cube(coord2, coord3, coord4, coord5, coord4, coord5);
        cubeArray[2][2][2] = new Cube(coord4, coord5, coord4, coord5, coord4, coord5);

        for (int i = 0; i < 9; i++) {
            layers[i] = new Layer();
        }

        updateLayers();

        layers[Cube3.topY].setFaceColor(Cube.top, cyan);
        layers[Cube3.bottomY].setFaceColor(Cube.bottom, red);
        layers[Cube3.leftX].setFaceColor(Cube.left, yellow);
        layers[Cube3.rightX].setFaceColor(Cube.right, magenta);
        layers[Cube3.backZ].setFaceColor(Cube.back, blue);
        layers[Cube3.frontZ].setFaceColor(Cube.front, green);

        preDraw();
    }

    private void updateLayers() {
        int i, j;

        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                layers[Cube3.backZ].cubes[i][j] = cubeArray[0][i][j];
                layers[Cube3.middleZ].cubes[i][j] = cubeArray[1][i][j];
                layers[Cube3.frontZ].cubes[i][j] = cubeArray[2][i][j];

                layers[Cube3.leftX].cubes[i][j] = cubeArray[i][j][0];
                layers[Cube3.middleX].cubes[i][j] = cubeArray[i][j][1];
                layers[Cube3.rightX].cubes[i][j] = cubeArray[i][j][2];

                layers[Cube3.bottomY].cubes[i][j] = cubeArray[i][0][j];
                layers[Cube3.middleY].cubes[i][j] = cubeArray[i][1][j];
                layers[Cube3.topY].cubes[i][j] = cubeArray[i][2][j];
            }
        }
        layers[Cube3.backZ].axis = Layer.axisZ;
        layers[Cube3.middleZ].axis = Layer.axisZ;
        layers[Cube3.frontZ].axis = Layer.axisZ;
        layers[Cube3.leftX].axis = Layer.axisX;
        layers[Cube3.middleX].axis = Layer.axisX;
        layers[Cube3.rightX].axis = Layer.axisX;
        layers[Cube3.bottomY].axis = Layer.axisY;
        layers[Cube3.middleY].axis = Layer.axisY;
        layers[Cube3.topY].axis = Layer.axisY;
    }

    public Layer getLayer(int position) {
        return layers[position];
    }

    public void preDraw() {
        indicesCount = 27 * 6 * 6;
        ByteBuffer bb = ByteBuffer.allocateDirect(Vertex.count * 3 * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asIntBuffer();
        vertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(Vertex.count * 4 * 4);
        bb.order(ByteOrder.nativeOrder());
        colorBuffer = bb.asIntBuffer();
        colorBuffer.position(0);


        bb = ByteBuffer.allocateDirect(indicesCount * 2);
        bb.order(ByteOrder.nativeOrder());
        indexBuffer = bb.asShortBuffer();
        indexBuffer.position(0);

        int i, j, k;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j ++) {
                for (k = 0; k < 3; k++) {
                    cubeArray[i][j][k].put(vertexBuffer, colorBuffer, indexBuffer);
                }
            }
        }
    }

    public Color getCubeColor(int x, int y, int z, int position) {
        return cubeArray[z][y][x].getFaceColor(position);
    }

    public void updateArray(int layerId, boolean direction) {
        int i, j;
        if (!direction) {
            if (layerId < 3) { // x axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[2 - j][i][layerId % 3] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisX, direction);
                    }
                }
            }
            else if (layerId < 6) { // y axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[2 - j][layerId % 3][i] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisY, direction);
                    }
                }
            }
            else { // z axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[layerId % 3][2 - j][i] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisZ, direction);
                    }
                }
            }
        }
        else {
            if (layerId < 3) { // x axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[j][2 - i][layerId % 3] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisX, direction);
                    }
                }
            }
            else if (layerId < 6) { // y axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[j][layerId % 3][2 - i] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisY, direction);
                    }
                }
            }
            else { // z axis
                for (i = 0; i < 3; i++) {
                    for (j = 0; j < 3; j++) {
                        cubeArray[layerId % 3][j][2 - i] = layers[layerId].cubes[i][j];
                        layers[layerId].cubes[i][j].updateFace(Layer.axisZ, direction);
                    }
                }
            }
        }
        updateLayers();
    }

    public void draw(GL10 gl) {
        colorBuffer.position(0);
        vertexBuffer.position(0);
        indexBuffer.position(0);

        gl.glVertexPointer(3, GL10.GL_FIXED, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FIXED, 0, colorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, indicesCount, GL10.GL_UNSIGNED_SHORT, indexBuffer);
    }

    public IntBuffer getColorBuffer(){
        return colorBuffer;
    }

    public class Layer {
        public static final short axisX = 0;
        public static final short axisY = 1;
        public static final short axisZ = 3;
        public static final float twoPI = (float)Math.PI * 2.0f;

        public Cube[][] cubes = new Cube[3][3];
        private final Matrix3 transform = new Matrix3();
        short axis;

        public void setFaceColor(int position, Color color) {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    cubes[i][j].setFaceColor(position, color);
                }
            }
        }

        public void update(Matrix3 M, IntBuffer vertexBuffer) {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    cubes[i][j].update(M, vertexBuffer);
                }
            }
        }

        public void addAngle(float angle) {
            while (angle >= twoPI) {
                angle -= twoPI;
            }
            while (angle < 0.0f) {
                angle += twoPI;
            }
            float sin = (float)Math.sin(angle);
            float cos = (float)Math.cos(angle);
            switch (axis) {
            case Layer.axisX:
                transform.m[1][1] = cos;
                transform.m[1][2] = sin;
                transform.m[2][1] = -sin;
                transform.m[2][2] = cos;
                transform.m[0][0] = 1.0f;
                transform.m[0][1] = transform.m[0][2] = transform.m[1][0] = transform.m[2][0] = 0f;
                break;
            case Layer.axisY:
                transform.m[0][0] = cos;
                transform.m[0][2] = sin;
                transform.m[2][0] = -sin;
                transform.m[2][2] = cos;
                transform.m[1][1] = 1f;
                transform.m[0][1] = transform.m[1][0] = transform.m[1][2] = transform.m[2][1] = 0f;
                break;
            case Layer.axisZ:
                transform.m[0][0] = cos;
                transform.m[0][1] = sin;
                transform.m[1][0] = -sin;
                transform.m[1][1] = cos;
                transform.m[2][2] = 1f;
                transform.m[2][0] = transform.m[2][1] = transform.m[0][2] = transform.m[1][2] = 0f;
                break;
            }
            this.update(transform, Cube3.getInstance().vertexBuffer);
        }

        public void saveRotation() {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    cubes[i][j].saveRotation(transform);
                }
            }
        }

        public void saveRotation(Matrix3 M) {
            int i, j;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    cubes[i][j].saveRotation(M);
                }
            }
        }
    }

    public interface ViewportChangeListener{
        void onViewportChange(int[] viewport);
    }
}
