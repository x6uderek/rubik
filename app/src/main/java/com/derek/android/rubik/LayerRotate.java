package com.derek.android.rubik;


public class LayerRotate extends Action {
    public static final int framesPerQuarter = 15;
    private final int layerId;
    private final boolean direction;
    private float currentAngle = 0.0f;
    private float endAngle;
    private float increment;

    public LayerRotate(int layerId, boolean direction, ActionCallback sender) {
        super(sender);
        this.direction = direction;
        this.layerId = layerId;
        sliceDuration();
    }

    public void sliceDuration() {
        if (direction) {
            increment = (float)Math.PI / (getFramesPerQuarter() * 2);
            endAngle = currentAngle + ((float)Math.PI) / 2.0f;
        }
        else {
            increment = -(float)Math.PI / (getFramesPerQuarter() * 2);
            endAngle = currentAngle - ((float)Math.PI) / 2.0f;
        }
    }

    private int getFramesPerQuarter() {
        return sender.getEnableAnimation() ? framesPerQuarter : 1;
    }

    @Override
    public void update(Cube3 c) {
        if (currentAngle == 0.0f) {
            sender.onActionStart();
        }
        if ((direction && currentAngle >= endAngle) || (!direction && currentAngle <= endAngle)) {
            c.getLayer(layerId).saveRotation();
            c.updateArray(layerId, direction);
            sender.onActionFinish();
            return;
        }
        else {
            currentAngle += increment;
            c.getLayer(layerId).addAngle(currentAngle);
        }
    }
}
