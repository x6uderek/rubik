package com.derek.android.rubik;

import com.derek.android.rubik.Cube3.Layer;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Cube {
    public static final int left = 0;
    public static final int right = 1;
    public static final int bottom = 2;
    public static final int top = 3;
    public static final int back = 4;
    public static final int front = 5;

    Matrix3 transform;

    protected Square[] faces = new Square[6];

    public Cube(int left, int right, int bottom, int top, int back, int front) {
        Vertex leftBottomBack      = Vertex.addVertex(left, bottom, back);
        Vertex rightBottomBack     = Vertex.addVertex(right, bottom, back);

        Vertex leftTopBack         = Vertex.addVertex(left, top, back);
        Vertex rightTopBack        = Vertex.addVertex(right, top, back);

        Vertex leftBottomFront     = Vertex.addVertex(left, bottom, front);
        Vertex rightBottomFront    = Vertex.addVertex(right, bottom, front);

        Vertex leftTopFront        = Vertex.addVertex(left, top, front);
        Vertex rightTopFront       = Vertex.addVertex(right, top, front);

        faces[Cube.left] = new Square(leftBottomBack,leftTopBack,leftTopFront,leftBottomFront);
        faces[Cube.right] = new Square(rightBottomBack, rightTopBack, rightTopFront, rightBottomFront);
        faces[Cube.bottom] = new Square(rightBottomBack, rightBottomFront, leftBottomFront, leftBottomBack);
        faces[Cube.top] = new Square(rightTopFront, leftTopFront, leftTopBack, rightTopBack);
        faces[Cube.back] = new Square(leftBottomBack, leftTopBack, rightTopBack, rightBottomBack);
        faces[Cube.front] = new Square(rightBottomFront, leftBottomFront, leftTopFront, rightTopFront);
    }

    public void setFaceColor(int position, Color color) {
        faces[position].setColor(color);
    }

    public Color getFaceColor(int position) {
        return faces[position].getColor();
    }

    public Square getFace(int position){
        return faces[position];
    }

    public void put(IntBuffer vertexBuffer, IntBuffer colorBuffer, ShortBuffer indexBuffer) {
        for (Square square : faces) {
            square.put(vertexBuffer, colorBuffer, indexBuffer);
        }
    }

    public void updateFace(short axis, boolean direction) {
        Square temp;
        switch (axis) {
        case Layer.axisX:
            if (direction) {
                temp = faces[Cube.top];
                faces[Cube.top] = faces[Cube.back];
                faces[Cube.back] = faces[Cube.bottom];
                faces[Cube.bottom] = faces[Cube.front];
                faces[Cube.front] = temp;
            }
            else {
                temp = faces[Cube.top];
                faces[Cube.top] = faces[Cube.front];
                faces[Cube.front] = faces[Cube.bottom];
                faces[Cube.bottom] = faces[Cube.back];
                faces[Cube.back] = temp;
            }
            break;
        case Layer.axisY:
            if (direction) {
                temp = faces[Cube.front];
                faces[Cube.front] = faces[Cube.right];
                faces[Cube.right] = faces[Cube.back];
                faces[Cube.back] = faces[Cube.left];
                faces[Cube.left] = temp;
            }
            else {
                temp = faces[Cube.front];
                faces[Cube.front] = faces[Cube.left];
                faces[Cube.left] = faces[Cube.back];
                faces[Cube.back] = faces[Cube.right];
                faces[Cube.right] = temp;
            }
            break;
        case Layer.axisZ:
            if (direction) {
                temp = faces[Cube.top];
                faces[Cube.top] = faces[Cube.right];
                faces[Cube.right] = faces[Cube.bottom];
                faces[Cube.bottom] = faces[Cube.left];
                faces[Cube.left] = temp;
            }
            else {
                temp = faces[Cube.top];
                faces[Cube.top] = faces[Cube.left];
                faces[Cube.left] = faces[Cube.bottom];
                faces[Cube.bottom] = faces[Cube.right];
                faces[Cube.right] = temp;
            }
            break;
        }
    }

    public void saveRotation(Matrix3 M) {
        if (transform != null) {
            this.transform = this.transform.multiply(M);
        }
        else {
            transform = M.clone();
        }
    }

    public void update(Matrix3 M, IntBuffer vertexBuffer) {
        if (transform != null) {
            M = transform.multiply(M);
        }
        for (int i = 0; i < 6; i++) {
            faces[i].update(M, vertexBuffer);
        }
    }
}
