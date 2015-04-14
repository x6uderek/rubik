package com.derek.android.rubik;

public class Color {
    public final int red;
    public final int green;
    public final int blue;

    public static final int alpha = 0x10000;

    /**
     * default color is black
     */
    public Color() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Color) {
            Color another = (Color)o;
            return (another.red == this.red && another.green == this.green && another.blue == this.blue);
        }
        return false;
    }
}
