package com.derek.android.rubik;

public class Matrix3 {
    float[][] m = new float[3][3];

    public Matrix3() {
    }

    public void reset() {
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                m[i][j] = (i == j ? 1.0f : 0.0f);
            }
        }
    }

    @Override
    public Matrix3 clone() {
        Matrix3 result = new Matrix3();
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                result.m[i][j] = this.m[i][j];
            }
        }
        return result;
    }

    public void convert(Vertex src, Vertex des) {
        des.x = (int) (src.x * this.m[0][0] + src.y * this.m[1][0] + src.z * this.m[2][0]);
        des.y = (int) (src.x * this.m[0][1] + src.y * this.m[1][1] + src.z * this.m[2][1]);
        des.z = (int) (src.x * this.m[0][2] + src.y * this.m[1][2] + src.z * this.m[2][2]);
    }

    public void convert(float[] src, float[] des) {
        des[0] = src[0] * this.m[0][0] + src[1] * this.m[1][0] + src[2] * this.m[2][0];
        des[1] = src[0] * this.m[0][1] + src[1] * this.m[1][1] + src[2] * this.m[2][1];
        des[2] = src[0] * this.m[0][2] + src[1] * this.m[1][2] + src[2] * this.m[2][2];
    }

    public Matrix3 multiply(Matrix3 another) {
        Matrix3 result = new Matrix3();
        int i, j;
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                result.m[i][j] = this.m[i][0] * another.m[0][j]
                               + this.m[i][1] * another.m[1][j]
                               + this.m[i][2] * another.m[2][j];
            }
        }
        return result;
    }

    public static Matrix3 rotate(double angle, int axis){
        Matrix3 result = new Matrix3();
        float sin = (float)Math.sin(angle);
        float cos = (float)Math.cos(angle);
        switch (axis){
            case 0:
                result.m[1][1] = cos;
                result.m[1][2] = sin;
                result.m[2][1] = -sin;
                result.m[2][2] = cos;
                result.m[0][0] = 1.0f;
                result.m[0][1] = result.m[0][2] = result.m[1][0] = result.m[2][0] = 0f;
                break;
            case 1:
                result.m[0][0] = cos;
                result.m[0][2] = sin;
                result.m[2][0] = -sin;
                result.m[2][2] = cos;
                result.m[1][1] = 1f;
                result.m[0][1] = result.m[1][0] = result.m[1][2] = result.m[2][1] = 0f;
                break;
            case 2:
                result.m[0][0] = cos;
                result.m[0][1] = sin;
                result.m[1][0] = -sin;
                result.m[1][1] = cos;
                result.m[2][2] = 1f;
                result.m[2][0] = result.m[2][1] = result.m[0][2] = result.m[1][2] = 0f;
                break;
        }
        return result;
    }
}
