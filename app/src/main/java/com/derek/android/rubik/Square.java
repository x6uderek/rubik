package com.derek.android.rubik;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Square {
    final Vertex[] vertices;

    /**
     *
     * @param v1 v1
     * @param v2 v2
     * @param v3 v3
     * @param v4 v4的颜色决定面的颜色
     */
    public Square(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        vertices = new Vertex[4];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        vertices[3] = v4;
    }

    public Square(Vertex v1, Vertex v2, Vertex v3, Vertex v4, Color color) {
        vertices = new Vertex[4];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        vertices[3] = v4;
        vertices[3].setColor(color);
    }

    public void setColor(Color color) {
        if (color != null) {
            vertices[3].setColor(color);
        }
    }

    public Color getColor() {
        return vertices[3].getColor();
    }

    public void put(IntBuffer vertexBuffer, IntBuffer colorBuffer, ShortBuffer indicesBuffer) {
        for (int i = 0; i < 4; i++) {
            vertices[i].put(vertexBuffer,colorBuffer);
        }
        indicesBuffer.put(vertices[1].index);
        indicesBuffer.put(vertices[0].index);
        indicesBuffer.put(vertices[3].index);

        indicesBuffer.put(vertices[1].index);
        indicesBuffer.put(vertices[2].index);
        indicesBuffer.put(vertices[3].index);
    }

    public void update(Matrix3 M, IntBuffer vertexBuffer) {
        for (int i = 0; i < 4; i++) {
            vertices[i].update(M, vertexBuffer);
        }
    }
}
