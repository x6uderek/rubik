package com.derek.android.rubik;

public abstract class Action {
    protected ActionCallback sender;

    public Action(ActionCallback sender) {
        this.sender = sender;
    }
    public abstract void update(Cube3 c);

    public interface ActionCallback {
        public void setEnableAnimation(boolean enable);
        public boolean getEnableAnimation();
        public void onActionStart();
        public void onActionFinish();
    }
}
