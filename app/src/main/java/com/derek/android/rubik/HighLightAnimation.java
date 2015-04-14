package com.derek.android.rubik;

import java.util.HashMap;
import java.util.Map;

/**
 * 选择高亮动画
 */
public class HighLightAnimation implements Animation, ColorGradient.GradientDisabledCallback {
    private Cube3 target;
    private Map<Square,ColorGradient> map = new HashMap<>();

    public HighLightAnimation(){
        target = Cube3.getInstance();
    }

    public void addHighLight(Square face){
        if(!map.containsKey(face)) {
            map.put(face,new ColorGradient(face,this));
        }
    }

    public void cancelHighLight(Square face){
        ColorGradient gradient = map.get(face);
        if(gradient!=null){
            gradient.setEnabled(false);
        }
    }

    @Override
    public void nextFrame() {
        for (ColorGradient gradient : map.values()) {
            gradient.putGradientColor(target.getColorBuffer());
        }
    }

    @Override
    public void onDisabledFinish(Square face) {
        map.remove(face);
    }
}
