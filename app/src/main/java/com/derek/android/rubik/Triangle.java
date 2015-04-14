package com.derek.android.rubik;

import com.derek.android.util.Vector;

import java.util.Arrays;

/**
 * Triangle for ray-picking
 */
public class Triangle {
    public float[] V0;
    public float[] V1;
    public float[] V2;

    public Triangle(float[] V0, float[] V1, float[] V2){
        this.V0 =V0;
        this.V1 = V1;
        this.V2 = V2;
    }

    private static final float SMALL_NUM =  0.00000001f;

    /**
     * 检测射线与三角形相交
     * @param R 射线
     * @param T 三角形
     * @param I 焦点
     * @return -1:无法构造三角形（三点一线，三点重合）
     *         0:无交点
     *         1:相交于I
     *         2:射线与三角形共面
     */
    public static int intersectRayAndTriangle(Ray R, Triangle T, float[] I){
        float[]    u, v, n;             // triangle vectors
        float[]    dir, w0, w;          // ray vectors
        float     r, a, b;             // params to calc ray-plane intersect
        // get triangle edge vectors and plane normal
        u =  Vector.minus(T.V1, T.V0);
        v =  Vector.minus(T.V2, T.V0);
        n =  Vector.crossProduct(u, v);             // cross product
        if (Arrays.equals(n, new float[]{0.0f, 0.0f, 0.0f})){           // triangle is degenerate
            return -1;                 // do not deal with this case
        }
        dir =  Vector.minus(R.farCoords, R.nearCoords);             // ray direction vector
        w0 = Vector.minus(R.nearCoords, T.V0);
        a = - Vector.dot(n,w0);
        b =  Vector.dot(n,dir);
        if (Math.abs(b) < SMALL_NUM) {     // ray is parallel to triangle plane
            if (a == 0){                // ray lies in triangle plane
                return 2;
            }else{
                return 0;             // ray disjoint from plane
            }
        }
        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0f){                   // ray goes away from triangle
            return 0;                  // => no intersect
        }
        float[] tempI =  Vector.addition(R.nearCoords,  Vector.scalarProduct(r, dir));           // intersect point of ray and plane
        I[0] = tempI[0];
        I[1] = tempI[1];
        I[2] = tempI[2];
        // is I inside T?
        float    uu, uv, vv, wu, wv, D;
        uu =  Vector.dot(u,u);
        uv =  Vector.dot(u,v);
        vv =  Vector.dot(v,v);
        w =  Vector.minus(I, T.V0);
        wu =  Vector.dot(w,u);
        wv = Vector.dot(w,v);
        D = (uv * uv) - (uu * vv);
        // get and test parametric coords
        float s, t;
        s = ((uv * wv) - (vv * wu)) / D;
        if (s < 0.0f || s > 1.0f)        // I is outside T
            return 0;
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0f || (s + t) > 1.0f)  // I is outside T
            return 0;

        return 1;                      // I is in T
    }
}
