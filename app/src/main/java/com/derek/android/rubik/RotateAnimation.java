package com.derek.android.rubik;

import com.derek.android.rubik.Action.ActionCallback;

public class RotateAnimation implements Animation, ActionCallback {
    public static final int CACHE_SIZE = 50;
    Cube3 target;
    boolean animationEnable = true;
    int sum = 0;
    int current = 0;

    private final Action[] actions = new Action[CACHE_SIZE];

    public RotateAnimation() {
        target = Cube3.getInstance();
    }

    public void addAction(Action action) {
        actions[sum % CACHE_SIZE] = action;
        sum++;
    }

    public void clear() {
        sum = 0;
    }

    @Override
    public void setEnableAnimation(boolean enable) {
        animationEnable = enable;
    }

    @Override
    public boolean getEnableAnimation() {
        return animationEnable;
    }

    @Override
    public void nextFrame() {
        if (current < sum) {
            actions[current % CACHE_SIZE].update(target);
        }
    }

    @Override
    public void onActionStart() {
        //nothing special here
    }

    @Override
    public void onActionFinish() {
        current++;
        if (current >= sum) {
            this.setEnableAnimation(true);
            if (listener != null) {
                listener.onAnimationFinish();
            }
        }
    }

    public void setAnimationListener(AnimationListener l) {
        listener = l;
    }

    private AnimationListener listener;

    public interface AnimationListener {
        public void onAnimationFinish();
    }
}
