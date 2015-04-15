package com.derek.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.derek.android.rubik.Action;
import com.derek.android.rubik.Animation;
import com.derek.android.rubik.AnimationWrapper;
import com.derek.android.rubik.CubeRender;
import com.derek.android.rubik.HighLightAnimation;
import com.derek.android.rubik.LayerRotate;
import com.derek.android.rubik.MatrixTrackingGL;
import com.derek.android.rubik.RotateAnimation;
import com.derek.android.rubik.RotateAnimation.AnimationListener;
import com.derek.android.rubik.robot.DefaultAI;
import com.derek.android.rubik.robot.IRobot;

import java.util.Random;

import javax.microedition.khronos.opengles.GL;

public class RubikActivity extends Activity {
    private GLSurfaceView view;
    private CubeRender render;
    private RotateAnimation animation;
    private HighLightAnimation highLightAnimation;

    private MotionHandler motionHandler;
    private IRobot robot;
    Random random = new Random(System.currentTimeMillis());

    private boolean AIEnable = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new GLSurfaceView(this.getApplication());
        view.setGLWrapper(new GLSurfaceView.GLWrapper() {
            @Override
            public GL wrap(GL gl) {
                return new MatrixTrackingGL(gl);
            }
        });
        animation = new RotateAnimation();
        highLightAnimation = new HighLightAnimation();
        render = new CubeRender(new AnimationWrapper(new Animation[]{animation,highLightAnimation}));
        render.onRestoreInstanceState(savedInstanceState);
        motionHandler = new RealMotion(animation,highLightAnimation,render);
        view.setRenderer(render);
        setContentView(view);
        robot = new DefaultAI(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationFinish() {
                if (AIEnable) {
                    Action action = robot.next();
                    if (action != null) {
                        animation.addAction(action);
                    }
                    else {
                        AIEnable = false;
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        render.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return motionHandler.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.ai:
            if (!AIEnable) {
                AIEnable = true;
                robot.enableAI();
                Action action = robot.next();
                if (action != null) {
                    animation.addAction(action);
                }
            }
            else {
                AIEnable = false;
                robot = new DefaultAI(animation);
            }
            break;
        case R.id.renew:
            animation.setEnableAnimation(false);
            for (int i = 0; i < 30; i++) {
                int layerId = random.nextInt(9);
                boolean direction = random.nextBoolean();
                animation.addAction(new LayerRotate(layerId, direction, animation));
            }
            break;
        case R.id.help:
            break;
        }
        return true;
    }
}