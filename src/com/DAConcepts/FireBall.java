package com.DAConcepts;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FireBall {
    private float xPos = 0;
    private float yPos = 0;
    private float zRot = 0;
    private float radius = 0.5f;
    private Game game;
    private boolean boom = false;
    public boolean done = false;
    private int col = 1;
    private int row = 1;
    private float counter = 0;
    private En1 parent;
    private BufferedImage currSprite;
    private SpriteSheet ss;
    float speed = 0.8f;

    public FireBall(float x, float y, float z, float r, En1 self, Game g) {
        xPos = x;
        yPos = y;
        zRot = z-90f;
        radius = r;
        game = g;
        parent = self;
        ss = game.getFireBallSpriteSheet();
    }

    public void tick() {
        float angle = ScuffedMath.angle(game.getPlayerPos().x-xPos,game.getPlayerPos().y-yPos)-zRot+12.5f;
        angle -= 90;
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        counter += game.deltaTime;
        counter %= 3;
        row = (int) (angle/45f)+1;
        if (boom && row < 9) {
            row = 9;
            col = 1;
        }
        if (row < 9) {
            float num = counter/3*2;
            col = (int) num+1;
        }
        else {
            float num = counter/3*3;
            col = (int) num+1;
            if (col == 3) {
                done = true;
            }
        }
        currSprite = ss.grabImage(col, row, 64, 64);
        if (boom) {
            return;
        }
        xPos += game.player.transformForward(zRot).x*speed;
        yPos += game.player.transformForward(zRot).y*speed;
        if (radius+game.player.radius > dist(new Vector2(xPos,yPos), game.getPlayerPos())) {
            boom = true;
            game.player.damage((float) Math.random()*20+20);
            return;
        }
        for (En1 en : game.getEnemies1()) {
            if (radius + en.getRadius() > dist(new Vector2(xPos, yPos), new Vector2 (en.getxPos(),en.getyPos()))) {
                if (en == parent) {
                    continue;
                }
                boom = true;
                en.damage((float) Math.random()*20+20);
                return;
            }
        }
        for (En2 en : game.getEnemies2()) {
            if (radius + en.getRadius() > dist(new Vector2(xPos, yPos), new Vector2 (en.getxPos(),en.getyPos()))) {
                boom = true;
                en.damage((float) Math.random()*20+20);
                return;
            }
        }
        if (xPos <= 0.5 || xPos >= 99.5f || yPos <= 0.5 || yPos >= 99.5f) {
            boom = true;
            return;
        }
    }

    private float dist(Vector2 location, Vector2 other) {
        return (float) Math.sqrt(Math.pow(location.x-other.x,2)+Math.pow(location.y-other.y,2));
    }

    public void render(Graphics g) {
        if (done) {
            return;
        }
        Vector2 screenPos = getScreenPos(xPos,yPos);
        float s = getScreenScale();
        g.drawImage(currSprite,(int) screenPos.x-((int) (Game.SCALE*s)/2),(int) screenPos.y, (int) (Game.SCALE*s), (int) (Game.SCALE*s), game);
    }

    private Vector2 getScreenPos(float x,float y) {
        //Make sooooo much better
        float playerRot = game.getPlayerRot();
        float ang = ScuffedMath.angle(game.getPlayerPos().x-x,game.getPlayerPos().y-y)+180 - playerRot;
        while (ang < 0) {
            ang += 360;
        }
        while (ang >= 360) {
            ang -= 360;
        }
        ang -= 90;
        int xScreen = (int) ((ang/-45/2)*Game.WIDTH)+(Game.WIDTH/2);
        float yFOV = 90*2*(((float) Game.HEIGHT)/((float) Game.WIDTH));
        float distance = (float) Math.sqrt(Math.pow(game.getPlayerPos().x-xPos,2) + Math.pow(game.getPlayerPos().y-yPos,2));
        float heightDiff = (2.5f+1)-(game.getPlayerHeightPos()+game.getPlayerHeight());
        float yAng = ScuffedMath.angle(distance,heightDiff);
        if (yAng > 180) {
            yAng -= 360;
        }
        int yScreen = (int) (((yAng-(yFOV/2))/yFOV)*Game.HEIGHT*-1);
        return new Vector2(xScreen*Game.SCALE,yScreen*Game.SCALE);
    }

    private Vector2[] getScreenPoses(Vector2 viewTR) {
        float ang1 = ScuffedMath.angle(game.getPlayerPos().x-(xPos-viewTR.x*radius),game.getPlayerPos().y-(yPos-viewTR.y*radius))+180-game.getPlayerRot();
        float ang2 = ScuffedMath.angle(game.getPlayerPos().x-(xPos+viewTR.x*radius),game.getPlayerPos().y-(yPos+viewTR.y*radius))+180-game.getPlayerRot();
        while (ang1 < 0) {
            ang1 += 360;
        }
        while (ang1 >= 360) {
            ang1 -= 360;
        }
        while (ang2 < 0) {
            ang2 += 360;
        }
        while (ang2 >= 360) {
            ang2 -= 360;
        }
        ang1 -= 90;
        ang2 -= 90;
        int xScreen1 = (int) ((ang1/-45/2)*Game.WIDTH)+(Game.WIDTH/2);
        int xScreen2 = (int) ((ang2/-45/2)*Game.WIDTH)+(Game.WIDTH/2);
        if (xScreen1 < 0 && xScreen2 >= 0) {
            if (ang2 > ang1) {
                ang1 += 360;
            }
            else {
                ang2 += 360;
            }
            xScreen1 = (int) ((ang1/-90)*Game.WIDTH)+(Game.WIDTH/2);
            xScreen2 = (int) ((ang2/-90)*Game.WIDTH)+(Game.WIDTH/2);
        }
        int yScreen1 = 0;
        int yScreen2 = 0;
        return new Vector2[] {new Vector2(xScreen1*Game.SCALE,yScreen1*Game.SCALE),new Vector2(xScreen2*Game.SCALE,yScreen2*Game.SCALE)};
    }

    private Vector2 getViewTR() {
        float ang = ScuffedMath.angle(game.getPlayerPos().x-xPos,game.getPlayerPos().y-yPos)-90;
        return new Vector2 ((float) Math.cos(ang*Math.PI/180),(float) Math.sin(ang*Math.PI/180));
    }

    private float getScreenScale() {
        Vector2 left = getScreenPoses(getViewTR())[0];
        Vector2 right = getScreenPoses(getViewTR())[1];
        float size = Math.abs(left.x-right.x);
        return size;
    }
}