package com.DAConcepts;

public class CircleCollider {
    private Vector2 center;
    private float radius;
    private Object parent;

    public CircleCollider(float xPos, float yPos, float r, Object self) {
        center = new Vector2(xPos,yPos);
        radius = r;
        parent = self;
    }

    public Vector2 intersectPoint(Vector2 dir, Vector2 pos) {
        float m = (dir.y/dir.x);
        float perpM = -1f/m;
        float x = (center.y-pos.y-perpM*center.x+m*pos.x)/(m-perpM);
        float y = m*(x-pos.x)+pos.y;
        return new Vector2(x,y);
    }

    public boolean intersects(Vector2 dir, Vector2 pos) {
        if ((dir.y > 0 && pos.y > center.y+radius) || ((dir.y < 0 && pos.y < center.y-radius))) {
            return false;
        }
        float m = (dir.y/dir.x);
        float perpM = -1f/m;
        float x = (center.y-pos.y-perpM*center.x+m*pos.x)/(m-perpM);
        float y = m*(x-pos.x)+pos.y;
        float dist = (float) Math.sqrt(Math.pow(x-center.x,2)+Math.pow(y-center.y,2));
        return dist < radius;
    }

    public boolean intersectsInclusive(Vector2 dir, Vector2 pos) {
        if ((dir.y > 0 && pos.y > center.y+radius) || ((dir.y < 0 && pos.y < center.y-radius))) {
            return false;
        }
        float m = (dir.y/dir.x);
        float perpM = -1f/m;
        float x = (center.y-pos.y-perpM*center.x+m*pos.x)/(m-perpM);
        float y = m*(x-pos.x)+pos.y;
        float dist = (float) Math.sqrt(Math.pow(x-center.x,2)+Math.pow(y-center.y,2));
        return dist <= radius;
    }

    public Object getParent() {
        return parent;
    }

    public Vector2 getCenter() {
        return center;
    }
}
