package com.DAConcepts;

public class Physics {
    static CircleCollider[] colliders;

    public static void start() {
        colliders = new CircleCollider[0];
    }

    public static void resetColliders() {
        start();
    }

    public static void addCollider(CircleCollider cc) {
        CircleCollider[] answer = new CircleCollider[colliders.length+1];
        for (int i = 0; i < colliders.length; i++) {
            answer[i] = colliders[i];
        }
        answer[colliders.length] = cc;
        colliders = answer;
    }

    public static Vector2 raycastGet(Vector2 dir, Vector2 pos) {
        for (CircleCollider cc : colliders) {
            if (cc.intersects(dir,pos)) {
                //if (((cc.getCenter().x-pos.x < 0 && dir.x > 0) || (cc.getCenter().x-pos.x > 0 && dir.x < 0)) && ((cc.getCenter().y-pos.y < 0 && dir.y > 0) || (cc.getCenter().y-pos.y > 0 && dir.y < 0))) {
                //    continue;
                //}
                //else {
                    return cc.intersectPoint(dir,pos);
                //}
            }
        }
        if (dir.x == 0) {
            return new Vector2(pos.x,Math.abs(dir.y)/dir.y*50+50);
        }
        float xMult = Math.abs(dir.x)/dir.x;
        if (dir.y == 0) {
            return new Vector2(Math.abs(dir.x)/dir.x*50+50, pos.y);
        }
        float yMult = Math.abs(dir.y)/dir.y;
        float x = ((dir.y/dir.x)*(pos.x)-pos.y+(50f*yMult+50))*(dir.x/dir.y);
        float y = (dir.y/dir.x)*((50f*xMult+50)-pos.x)+pos.y;
        if ((x <= 100 && xMult > 0) || (x >= 0 && xMult < 0)) {
            return new Vector2(x,(50f*yMult+50));
        }
        return new Vector2((50f*xMult+50),y);
    }

    public static Object raycast(Vector2 dir, Vector2 pos) {
        for (CircleCollider cc : colliders) {
            if (cc.intersects(dir,pos)) {
                //if (((cc.getCenter().x-pos.x < 0 && dir.x > 0) || (cc.getCenter().x-pos.x > 0 && dir.x < 0)) && ((cc.getCenter().y-pos.y < 0 && dir.y > 0) || (cc.getCenter().y-pos.y > 0 && dir.y < 0))) {
                //    continue;
                //}
                //else {
                return cc.getParent();
                //}
            }
        }
        return null;
    }

    public static Object raycast(Vector2 dir, Vector2 pos, Object self) {
        for (CircleCollider cc : colliders) {
            if (cc.intersects(dir,pos)) {
                //if (((cc.getCenter().x-pos.x < 0 && dir.x > 0) || (cc.getCenter().x-pos.x > 0 && dir.x < 0)) && ((cc.getCenter().y-pos.y < 0 && dir.y > 0) || (cc.getCenter().y-pos.y > 0 && dir.y < 0))) {
                //    continue;
                //}
                //else {
                if (cc.getParent() != self) {
                    return cc.getParent();
                }
                //}
            }
        }
        return null;
    }

    public static Object raycast(Vector2 dir, Vector2 pos, Class[] classes) {
        for (CircleCollider cc : colliders) {
            if (cc.intersects(dir,pos)) {
                if (((cc.getCenter().x-pos.x < 0 && dir.x > 0) || (cc.getCenter().x-pos.x > 0 && dir.x < 0)) && ((cc.getCenter().y-pos.y < 0 && dir.y > 0) || (cc.getCenter().y-pos.y > 0 && dir.y < 0))) {
                    continue;
                }
                else {
                    boolean isIn = false;
                    for (Class type : classes) {
                        if (cc.getParent().getClass() == type) {
                            isIn = true;
                            break;
                        }
                    }
                    if (isIn) {
                        continue;
                    }
                    return cc.getParent();
                }
            }
        }
        return null;
    }
}
