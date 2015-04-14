package com.derek.android.rubik;

import com.derek.android.rubik.Action.ActionCallback;
import com.derek.android.rubik.Cube3.Layer;

public class RubikRotate extends Action implements ActionCallback {
    private boolean started = false;
    private byte finishCount = 0;
    LayerRotate[] layerActions = new LayerRotate[3];

    public RubikRotate(short axis,boolean direction, ActionCallback sender) {
        super(sender);
        switch (axis) {
        case Layer.axisX:
            layerActions[0] = new LayerRotate(Cube3.leftX, direction, this);
            layerActions[1] = new LayerRotate(Cube3.middleX, direction, this);
            layerActions[2] = new LayerRotate(Cube3.rightX, direction, this);
            break;
        case Layer.axisY:
            layerActions[0] = new LayerRotate(Cube3.bottomY, direction, this);
            layerActions[1] = new LayerRotate(Cube3.middleY, direction, this);
            layerActions[2] = new LayerRotate(Cube3.topY, direction, this);
            break;
        case Layer.axisZ:
            layerActions[0] = new LayerRotate(Cube3.backZ, direction, this);
            layerActions[1] = new LayerRotate(Cube3.middleZ, direction, this);
            layerActions[2] = new LayerRotate(Cube3.frontZ, direction, this);
            break;
        }
    }

    @Override
    public void update(Cube3 c) {
        layerActions[0].update(c);
        layerActions[1].update(c);
        layerActions[2].update(c);
    }

    @Override
    public void onActionStart() {
        if (!started) {
            started = true;
            sender.onActionStart();
        }
    }

    @Override
    public void onActionFinish() {
        if (finishCount >= 2) {
            sender.onActionFinish();
        }
        else {
            finishCount++;
        }
    }

    @Override
    public void setEnableAnimation(boolean enable) {
        sender.setEnableAnimation(enable);
    }

    @Override
    public boolean getEnableAnimation() {
        return sender.getEnableAnimation();
    }
}
