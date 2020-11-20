package com.DAConcepts;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class En1 {
    private int health = 60;
    private float xPos = 0;
    private float yPos = 0;
    private float heightPos = 3;
    private float zRot;
    private float radius = 0.5f;
    private float height = 2;
    private float stepHeight = 0.75f;
    private Vector3 velocity = new Vector3(0,0,0);
    private Game game;
    private BufferedImage en;
    private SpriteSheet ss;
    private boolean lastState = false;
    private float currCol = 1;
    private boolean attacking = false;
    private float attackingTimer = 0;
    private static float attackingDurr = 0.4f;
    private boolean awake = false;
    private float coolDownTimer = 0;
    private static float coolDownDurr = 3;
    private boolean onCoolDown = true;
    private static float unitsPerSec = 5;
    private int dead = -1;
    private float deadCount = 0;
    float fov = 45;
    private Vector2 start;
    public boolean done = false;
    private float doneCount = 0;

    public static void setSpeed(float speed) {
        unitsPerSec = speed;
    }

    public static void setAttackingDurr(float durr) {
        attackingDurr = durr;
    }

    public static void setCoolDownDur(float durr) {
        coolDownDurr = durr;
    }

    public En1(Game g) {
        game = g;
        ss = g.getEnemySpriteSheet1();
        start = new Vector2(0,0);
    }

    public En1(float x, float y, Game g) {
        xPos = x;
        yPos = y;
        game = g;
        ss = g.getEnemySpriteSheet1();
        start = new Vector2(x,y);
    }

    public En1(float x, float y, float zR, Game g) {
        xPos = x;
        yPos = y;
        zRot = zR;
        game = g;
        ss = g.getEnemySpriteSheet1();
        start = new Vector2(x,y);
    }

    private void attack() {
        if (isDead()) {
            return;
        }
        game.addFB(new FireBall(xPos+game.player.transformForward(zRot).x,yPos+game.player.transformForward(zRot).y,zRot,0.5f,this,game));
        //attac (fireBall)
    }

    public boolean damage(float amt) {
        health -= amt;
        if (health <= 0 && dead <= 0) {
            dead = 1;
            return true;
        }
        return false;
    }

    public void updateCols() {
        if (dead <= 0) {
            Physics.addCollider(new CircleCollider(xPos, yPos, radius, this));
        }
    }

    public void tick() {
        //tick
        xPos = Math.min(Math.max(xPos,0.5f),99.5f);
        yPos = Math.min(Math.max(yPos,0.5f),99.5f);
        checkWake();
        if (dead > 0) {
            doneCount += game.deltaTime;
            if (doneCount >= 5) {
                done = true;
            }
            attacking = false;
        }
        if (awake) {
            if (onCoolDown) {
                coolDownTimer += game.deltaTime;
                if (coolDownTimer >= coolDownDurr) {
                    onCoolDown = false;
                    coolDownTimer = 0;
                }
                moveTowardPlayer();
            }
            else if (inSight() || attacking) {
                if (!attacking) {
                    attacking = true;
                }
                attackingTimer += game.deltaTime;
                if (attackingTimer >= attackingDurr) {
                    attack();
                    onCoolDown = true;
                    attackingTimer = 0;
                    attacking = false;
                }
            }
            else {
                moveTowardPlayer();
            }
        }
        int col = 1;
        int row = 1;
        float angle = ScuffedMath.angle(game.getPlayerPos().x-xPos,game.getPlayerPos().y-yPos)-zRot+12.5f;
        while (angle > 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        row = (int) (angle/45f)+1;
        if (lastState != attacking) {
            currCol = 1;
            lastState = attacking;
        }
        else {
            currCol += 5f * game.deltaTime;
        }
        currCol--;
        currCol %= 4;
        currCol++;
        col = (int) currCol + (attacking ? 4 : 0);
        if (!awake) {
            row = 1;
            col = 1;
        }
        if (dead > 0) {
            row = 9;
            col = Math.min(Math.max(dead,0),5);
            deadCount += 5f * game.deltaTime;
            dead += (int) deadCount;
        }
        en = ss.grabImage(col, row, 64, 64);
    }
    /*
    public Vector2 posTest() {
        float m = (dir.y/dir.x);
        float perpM = -1f/m;
        Vector2 playerCenter = game.getPlayerPos()
        float x = (playerCenter.y-yPos-perpM*playerCenter.x+m*xPos)/(m-perpM);
        float y = m*(x-xPos)+yPos;
        float dist = (float) Math.sqrt(Math.pow(x-playerCenter.x,2)+Math.pow(y-playerCenter.y,2));
        return dist == radius;
    } */

    private void checkWake() {
        awake = dead>=0;
    }

    public void render(Graphics g) {
        if (dead == -1) dead = 0;
        Vector2 screenPos = getScreenPos(xPos,yPos);
        float s = getScreenScale();
        g.drawImage(en,(int) screenPos.x-((int) (Game.SCALE*s)/2),(int) screenPos.y, (int) (Game.SCALE*s), (int) (Game.SCALE*s), game);
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
        int xScreen = (int) ((ang/-fov/2)*Game.WIDTH)+(Game.WIDTH/2);
        float yFOV = 90*2*(((float) Game.HEIGHT)/((float) Game.WIDTH));
        float distance = (float) Math.sqrt(Math.pow(game.getPlayerPos().x-xPos,2) + Math.pow(game.getPlayerPos().y-yPos,2));
        float heightDiff = (heightPos+height)-(game.getPlayerHeightPos()+game.getPlayerHeight());
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
        int xScreen1 = (int) ((ang1/-fov/2)*Game.WIDTH)+(Game.WIDTH/2);
        int xScreen2 = (int) ((ang2/-fov/2)*Game.WIDTH)+(Game.WIDTH/2);
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

    private boolean inSight() {
        float ang = ScuffedMath.angle(game.getPlayerPos().x-xPos,game.getPlayerPos().y-yPos);
        boolean raycast = true; // raycast maybe use y = mx+b lines for physics
        // check logic after you can really see and look at object
        // maybe no
        return (ang-zRot < 45 || ang-zRot > 315) && raycast;
    }

    private void moveTowardPlayer() {
        if (dead != 0) {
            return;
        }
        Vector2 BU = new Vector2(xPos,yPos);
        float ang = ScuffedMath.angle(game.getPlayerPos().x-xPos,game.getPlayerPos().y-yPos);
        float cos = (float) Math.cos((double) ang * Math.PI/180);
        float sin = (float) Math.sin((double) ang * Math.PI/180);
        Vector2 moveVec = new Vector2(cos*game.deltaTime*unitsPerSec,sin*game.deltaTime*unitsPerSec);
        zRot = ang;
        if (dead == 0) {
            xPos += moveVec.x;
            yPos += moveVec.y;
            float distFromPlay = (float) Math.sqrt(Math.pow(xPos-game.player.xPos,2)+Math.pow(yPos-game.player.yPos,2));
            if (game.player.radius+radius > distFromPlay) {
                float moveVecDist = (float) Math.sqrt(Math.pow(moveVec.x,2)+Math.pow(moveVec.y,2));
                float goBack = Math.abs((game.player.radius+radius)-distFromPlay)/moveVecDist;
                xPos -= moveVec.x*(goBack);
                yPos -= moveVec.y*(goBack);
            }
        }
        if ((Math.sqrt(Math.pow(BU.x-xPos,2)+Math.pow(BU.y-yPos,2))) >= 1) {
            xPos = BU.x;
            yPos = BU.y;
        }
    }

    public float getRadius() {
        return radius;
    }

    public float getxPos() {
        return xPos;
    }

    public float getyPos() {
        return yPos;
    }

    public boolean isDead() {
        return dead > 0;
    }
}
