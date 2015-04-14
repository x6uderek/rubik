package com.derek.android.rubik;

import java.nio.IntBuffer;

/**
 * 颜色渐变
 */
public class ColorGradient{
    private static final Color highLight = new Color(Cube3.one,Cube3.one,Cube3.one);
    private static final int MAX_HIGHLIGHT_FRAME = 24;
    private int highLightFrame = 0;
    private boolean colorGradientToHighLight = true;
    private boolean enabled = true;

    private GradientDisabledCallback callback;
    private Square face;

    public ColorGradient(Square face,GradientDisabledCallback callback){
        this.face = face;
        this.callback = callback;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    public void putGradientColor(IntBuffer colorBuffer){
        Color org = face.getColor();
        colorBuffer.position(face.vertices[3].index*4);
        if(!enabled){
            colorBuffer.put(org.red);
            colorBuffer.put(org.green);
            colorBuffer.put(org.blue);
            colorBuffer.put(Color.alpha);
            callback.onDisabledFinish(face);
            return;
        }
        highLightFrame++;
        if(colorGradientToHighLight) {
            colorBuffer.put(org.red + highLightFrame * (highLight.red - org.red) / MAX_HIGHLIGHT_FRAME);
            colorBuffer.put(org.green + highLightFrame * (highLight.green - org.green) / MAX_HIGHLIGHT_FRAME);
            colorBuffer.put(org.blue + highLightFrame * (highLight.blue - org.blue) / MAX_HIGHLIGHT_FRAME);
        }
        else {
            colorBuffer.put(highLight.red + highLightFrame * (org.red - highLight.red) / MAX_HIGHLIGHT_FRAME);
            colorBuffer.put(highLight.green + highLightFrame * (org.green - highLight.green) / MAX_HIGHLIGHT_FRAME);
            colorBuffer.put(highLight.blue + highLightFrame * (org.blue - highLight.blue) / MAX_HIGHLIGHT_FRAME);
        }
        colorBuffer.put(Color.alpha);
        if(highLightFrame>=MAX_HIGHLIGHT_FRAME){
            highLightFrame = 0;
            colorGradientToHighLight = !colorGradientToHighLight;
        }
    }


    public interface GradientDisabledCallback{
        void onDisabledFinish(Square face);
    }
}
