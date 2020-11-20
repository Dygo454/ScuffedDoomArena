package com.DAConcepts;

public class ScuffedMath {

    public static float clamp(float f, float min, float max) {
        return (f>max) ? max : ((f<min) ? min : f);
    }

    public static float angle(float x, float y) {
        if (x == 0) {
            return 90f * ((y < 0) ? -1 : 1);
        }
        float ans = ((float) (Math.atan((double) (y/x)))*180f/(float) Math.PI);
        if (x < 0) {
            ans += 180;
        }
        while (ans < 0) {
            ans += 360;
        }
        while (ans >= 360) {
            ans -= 360;
        }
        return ans;
    }
}
