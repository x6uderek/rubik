package com.derek.android.rubik;

import java.nio.IntBuffer;

public class Vertex {
    protected int x;
    protected int y;
    protected int z;
    protected final short index;

    protected Color color = null;

    static short count = 0;

    public static Vertex addVertex(int x, int y, int z) {
        return new Vertex(x, y, z, Vertex.count);
    }

    private Vertex(int x, int y, int z, short index) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
        Vertex.count++;
    }

    //only for update using
    private Vertex() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.index = -1;
    }

    public void put(IntBuffer vertexBuffer, IntBuffer colorBuffer) {
        vertexBuffer.position(index * 3);
        vertexBuffer.put(x);
        vertexBuffer.put(y);
        vertexBuffer.put(z);

        putColor(colorBuffer);
    }

    public void putColor(IntBuffer colorBuffer){
        colorBuffer.position(index * 4);
        if (color!=null) {
            colorBuffer.put(color.red);
            colorBuffer.put(color.green);
            colorBuffer.put(color.blue);
            colorBuffer.put(Color.alpha);
        }
        else {
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(0);
            colorBuffer.put(Color.alpha);
        }
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public void update(Matrix3 M, IntBuffer vertexBuffer) {
        Vertex temp = new Vertex();
        M.convert(this, temp);
        vertexBuffer.position(index * 3);
        vertexBuffer.put(temp.x);
        vertexBuffer.put(temp.y);
        vertexBuffer.put(temp.z);
    }
}
