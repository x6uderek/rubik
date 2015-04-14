package com.derek.android.rubik;

/**
 * 动画组包
 */
public class AnimationWrapper implements Animation {
    Animation[] animations;
    public AnimationWrapper(Animation[] animations){
        this.animations = animations;
    }

    @Override
    public void nextFrame() {
        for(Animation animation : animations){
            animation.nextFrame();
        }
    }
}
