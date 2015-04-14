package com.derek.android.rubik.robot;

import com.derek.android.rubik.Action;

public interface IRobot {
    public void enableAI();
    public Action next();
    public void disableAI();
}
