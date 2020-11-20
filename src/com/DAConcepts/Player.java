package com.DAConcepts;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

public class Player {
    private float sensitivity = 1/8f;
    int health = 100;
    int shield = 100;
    int[] ammo = new int[] {100,0,0,0};
    public float xPos = 0.5f;
    public float yPos = 0.5f;
    public float xPosFloor = 0.5f;
    public float yPosFloor = 0.5f;
    public float heightPos = 0;
    public float zRot = 0;
    public float radius = 1f;
    public float height = 1.5f;
    float stepHeight = 0.6f;
    private int playerNum;
    private Vector3 velocity = new Vector3(0,0,0);
    private Vector3 angularVelocity = new Vector3(0,0,0);
    private static int playerCount;
    private Game game;
    private float inputAxisHorizontal;
    private float inputAxisVertical;
    private float viewInputAxisHorizontal;
    private float sprintingMult = 1;
    private float sprintSpeed = 1.5f;
    private float inputAxisUp;
    private float walkSpeed = 10f;
    private float shootKey = 0;
    private int spriteSheetInd = 0;
    float[][] bulletSprites;
    private SpriteSheet bulletss;
    private SpriteSheet gun;
    private boolean shottyCoolDown = false;
    private float shottyCoolDownCount = 0f;
    private float shottyCoolDownCheck = 1f;
    private CircleCollider col;
    private int spriteCol = 0;
    public boolean dead = false;
    public boolean won = false;
    public int killCount = 0;

    public void setUpSpriteSheets() {
        col = new CircleCollider(xPos,yPos,radius,this);
        BufferedImageLoader loader = new BufferedImageLoader();
        try {
            gun = new SpriteSheet(loader.LoadImage("res/SpriteSheets/GunsDoomIISpriteSheet.png"));
            bulletss = new SpriteSheet(loader.LoadImage("res/SpriteSheets/BulletHitDoomSpriteSheet.png"));
        } catch (IOException e) {
            System.out.println("File Not Found");
            System.exit(1);
        }
    }

    public Player(Game g) {
        col = new CircleCollider(xPos,yPos,radius,this);
        playerNum = playerCount;
        playerCount++;
        game = g;
        bulletSprites = new float[0][];
        setUpSpriteSheets();
    }

    public Player(float x, float y, Game g) {
        col = new CircleCollider(xPos,yPos,radius,this);
        xPos = x;
        yPos = y;
        playerNum = playerCount;
        playerCount++;
        game = g;
        bulletSprites = new float[0][];
        setUpSpriteSheets();
    }

    public Player(float x, float y, float zR, Game g) {
        col = new CircleCollider(xPos,yPos,radius,this);
        xPos = x;
        yPos = y;
        zRot = zR;
        playerNum = playerCount;
        playerCount++;
        game = g;
        bulletSprites = new float[0][];
        setUpSpriteSheets();
    }

    public CircleCollider getCol() {
        return col;
    }

    public int getPlayerNum() {
		return playerNum;
	}

	public void setInputAxisHorizontal(float x) {
        this.inputAxisHorizontal = x;
    }

    public float getInputAxisHorizontal() {
        return inputAxisHorizontal;
    }

    public void setInputAxisVertical(float x) {
        this.inputAxisVertical = x;
    }

    public float getInputAxisVertical() {
        return inputAxisVertical;
    }

    public void setInputAxisUp(float x) {
        this.inputAxisUp = x;
    }

    public float getInputAxisUp() {
        return inputAxisUp;
    }

    public void setSprintingSpeed(float num) {
        sprintingMult = num;
    }

    public float getSprintSpeed() {
        return sprintSpeed;
    }

    public void setShootKey(float num) {
        shootKey = num;
    }

    public float getShootKey() {
        return shootKey;
    }

    public void mouseMoved(int x1,int x2) {
        zRot += (x1-x2)*sensitivity;
        while (zRot >= 360) {
            zRot -= 360;
        }
        while (zRot < 0) {
            zRot += 360;
        }
    }

    public void move() {
        float xBU = xPos;
        float yBU = yPos;
        float xMove = getInputAxisHorizontal();
        float yMove = getInputAxisVertical();
        velocity.x = xMove*walkSpeed*sprintingMult;
        velocity.y = yMove*walkSpeed*sprintingMult;
        velocity.z = getInputAxisUp();
        if (!grounded()) {
            velocity.z += -9.81f*game.deltaTime;
        }
        if (!Game.usingMouse) {
            float xLook = getViewInputAxisHorizontal();
            angularVelocity.z = xLook * 180;
        }
        zRot += angularVelocity.z*game.deltaTime;
        while (zRot >= 360) {
            zRot -= 360;
        }
        while (zRot < 0) {
            zRot += 360;
        }
        float xRelMove = velocity.x*game.deltaTime;
        float yRelMove = velocity.y*game.deltaTime;
        Polygon bounds = new Polygon();
        bounds.addPoint(0,0);
        bounds.addPoint(game.getXMAX(),0);
        bounds.addPoint(game.getXMAX(),game.getYMAX());
        bounds.addPoint(0,game.getYMAX());
        xPos += xRelMove*transformRight().x+yRelMove*transformForward().x;
        float coef = 0.6f;
        xPosFloor += xRelMove*transformRight().x*coef+yRelMove*transformForward().x;
        if (!bounds.contains(xPos+0.5f,0.5) || !bounds.contains(xPos-0.5f,0.5)) {
            xPos = (xPos >= game.getXMAX()-0.5f) ? game.getXMAX()-0.5f : 0.5f;
            xPosFloor = (xPosFloor >= game.getXMAX()-0.5f) ? game.getXMAX()-0.5f : 0.5f;
        }
        yPos += xRelMove*transformRight().y+yRelMove*transformForward().y;
        yPosFloor += xRelMove*transformRight().y*coef+yRelMove*transformForward().y;
        if (!bounds.contains(0.5,yPos+0.5f) || !bounds.contains(0.5,yPos-0.5f)) {
            yPos = (yPos >= game.getYMAX()-0.5) ? game.getYMAX()-0.5f : 0.5f;
            yPosFloor = (yPosFloor >= game.getYMAX()-0.5) ? game.getYMAX()-0.5f : 0.5f;
        }
        for (En1 enemy : game.getEnemies1()) {
            if (enemy.isDead()) {
                continue;
            }
            float distFromPlay = (float) Math.sqrt(Math.pow(xPos-enemy.getxPos(),2)+Math.pow(yPos-enemy.getyPos(),2));
            if (enemy.getRadius() + radius > distFromPlay) {
                float xMoved = xBU-xPos;
                float yMoved = yBU-yPos;
                float distMoved = (float) Math.sqrt(Math.pow(xMoved,2)+Math.pow(yMoved,2));
                float multForBack = Math.abs((enemy.getRadius()+radius)-distFromPlay)/distMoved;
                xPos = xBU;//+xMoved*(1-multForBack);
                yPos = yBU;//+yMoved*(1-multForBack);
                health -= (game.tickCount%30 == 0) ? 5f : 0f;
            }
        }
        for (En2 enemy : game.getEnemies2()) {
            if (enemy.isDead()) {
                continue;
            }
            float distFromPlay = (float) Math.sqrt(Math.pow(xPos-enemy.getxPos(),2)+Math.pow(yPos-enemy.getyPos(),2));
            if (enemy.getRadius() + radius > distFromPlay) {
                float xMoved = xBU-xPos;
                float yMoved = yBU-yPos;
                float distMoved = (float) Math.sqrt(Math.pow(xMoved,2)+Math.pow(yMoved,2));
                float multForBack = Math.abs((enemy.getRadius()+radius)-distFromPlay)/distMoved;
                xPos = xBU;//+xMoved*(1-multForBack);
                yPos = yBU;//+yMoved*(1-multForBack);
                health -= (game.tickCount%30 == 0) ? 5f : 0f;
            }
        }
    }

    public float getDistance(Vector2 v1, Vector2 v2) {
        return (float) Math.sqrt(Math.pow(v1.x-v2.x,2)+Math.pow(v1.y-v2.y,2));
    }

    public float getAng(Vector2 v1, Vector2 v2) {
        if ((v1.x-v2.x) == 0) {
            return ((v1.y-v2.y)) < 0 ? 270 : 90;
        }
        float ang = ((float) (Math.atan((v1.y-v2.y)/(v1.x-v2.x))*180f/Math.PI) + (((v1.x-v2.x) < 0) ? 180: 0));
        while (ang < 0) {
            ang += 360;
        }
        while (ang >= 360) {
            ang -= 360;
        }
        return ang;
    }

    public void damage(float amt) {
        if (shield <= 0) {
            health -= amt;
        }
        else if (shield <= amt) {
            health -= (amt-shield);
            shield = 0;
        }
        else {
            shield -= amt;
        }
        if (health <= 0) {
            dead = true;
        }
    }

    public void shoot() {
        if (getShootKey() == 1 && !shottyCoolDown) {
            shottyCoolDown = true;
            for (int i = 0; i < 9; i++) {
                float mult = (float) Math.random();
                float xAng = ((i-4)/4f + mult)*2;
                Vector2 transformForward = transformForward(zRot+xAng);
                Object hit = Physics.raycast(transformForward,new Vector2(xPos,yPos));
                if (hit == null) {
                    bulletSprites = put(bulletSprites, 0f, 0f, xAng, Physics.raycastGet(transformForward,new Vector2(xPos,yPos)));
                }
                else if (hit.getClass() == En1.class) {
                    En1 e = (En1) hit;
                    if (e.damage((float) Math.random()*20f+5)) {
                        killCount++;
                    }
                    bulletSprites = put(bulletSprites, 1f, 0f, xAng, Physics.raycastGet(transformForward,new Vector2(xPos,yPos)));
                }
                else if (hit.getClass() == En2.class) {
                    En2 e = (En2) hit;
                    if (e.damage((float) Math.random()*20f+5)) {
                        killCount++;
                    }
                    bulletSprites = put(bulletSprites, 1f, 0f, xAng, Physics.raycastGet(transformForward,new Vector2(xPos,yPos)));
                }
                if (killCount >= 100) {
                    won = true;
                }
            }
        }
        else if (shottyCoolDown) {
            shottyCoolDownCount += game.deltaTime;
            if (shottyCoolDownCheck <= shottyCoolDownCount) {
                shottyCoolDownCount = 0;
                shottyCoolDown = false;
            }
            spriteCol = (int) (shottyCoolDownCount*10/shottyCoolDownCheck);
        }
    }

    private float[][] put(float[][] bulletSpritesParam, float b, float v, float xPlace, Vector2 posOfBull) {
        float[][] answer = new float[bulletSpritesParam.length+1][];
        int index = 0;
        for (float[] nums : bulletSpritesParam) {
            answer[index] = nums;
            index++;
        }
        answer[bulletSpritesParam.length] = new float[] {b,v,xPlace,xPlace-zRot,posOfBull.x,posOfBull.y};
        return answer;
    }

    private float[][] addToSecond(float[][] bulletSpritesParam) {
        float[][] answer = bulletSpritesParam;
        int index = 0;
        for (float[] nums : answer) {
            answer[index] = new float[]{nums[0], nums[1] + game.deltaTime, nums[2], nums[3], nums[4], nums[5]};
            index++;
        }
        return answer;
    }

    private float[][] remove(float[][] bulletSpritesParam) {
        float[][] answer = new float[bulletSpritesParam.length][];
        int index = 0;
        for (float[] nums : bulletSpritesParam) {
            if (nums[1] > 0.6f) {
                continue;
            }
            else {
                answer[index] = nums;
            }
            index++;
        }
        int count = 0;
        for (float[] nums : answer) {
            if (nums != null) {
                count++;
            }
        }
        float[][] answerLessNull = new float[count][];
        int ind = 0;
        for (float[] nums : answer) {
            if (nums != null) {
                answerLessNull[ind] = nums;
                ind++;
            }
        }
        return answerLessNull;
    }

    private Vector2[] getSpriteSheetPosses(float[][] bulletSpritesParam) {
        Vector2[] answer = new Vector2[bulletSpritesParam.length];
        int index = 0;
        for (float[] nums : bulletSpritesParam) {
            int col = (int) (nums[0] * 3);
            col += Math.min((int) (nums[1]*5)+1, 3);
            answer[index] = new Vector2(1,col);
            index++;
        }
        return answer;
    }

    public Vector2 transformRight() {
        return new Vector2 ((float) Math.cos(zRot*Math.PI/180),(float) Math.sin(zRot*Math.PI/180));
    }

    public Vector2 transformForward() {
        return new Vector2 ((float) Math.cos((zRot+90)*Math.PI/180),(float) Math.sin((zRot+90)*Math.PI/180));
    }

    public Vector2 transformForward(float rot) {
        return new Vector2 ((float) Math.cos((rot+90)*Math.PI/180),(float) Math.sin((rot+90)*Math.PI/180));
    }

    public void setViewInputAxisHorizontal(float val) {
        viewInputAxisHorizontal = val;
    }

    private float getViewInputAxisHorizontal() {
        return viewInputAxisHorizontal;
    }

    public boolean grounded() {
        return true;
    }

    public void updateCols() {
        shoot();
        col = new CircleCollider(xPos,yPos,radius,this);
        Physics.addCollider(col);
    }

    public void tick() {
        updateBulletSprites();
        move();
    }

    private void updateBulletSprites() {
        bulletSprites = addToSecond(bulletSprites);
        bulletSprites = remove(bulletSprites);
    }

    private float angleLoop(float ang) {
        float angle = ang;
        while (angle >= 360) {
            angle -= 360;
        }
        while (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public void render(Graphics g) {
        Vector2[] sprites = getSpriteSheetPosses(bulletSprites);
        int ind = 0;
        for (Vector2 coord : sprites) {
            BufferedImage bulletSprite = bulletss.grabImage((int) coord.y, (int) coord.x, 32, 32);
            int size = 10;
            float dist = getDistance(new Vector2(xPos,yPos),new Vector2(bulletSprites[ind][4],bulletSprites[ind][5]));
            float screenPos = (angleLoop(getAng(new Vector2(bulletSprites[ind][4],bulletSprites[ind][5]),new Vector2(xPos,yPos))-(zRot))-45f)/(-90f)*Game.WIDTH+Game.WIDTH;
            screenPos *= Game.SCALE;
            g.drawImage(bulletSprite, (int) screenPos-(Game.SCALE*size/2), Game.HEIGHT/2*Game.SCALE-(Game.SCALE*size/2), Game.SCALE*size, Game.SCALE*size, game);
            ind++;
        }
        BufferedImage gunSprite = gun.grabImage(spriteCol+1,1,201,201);
        g.drawImage(gunSprite, Game.WIDTH*Game.SCALE/2-(Game.HEIGHT*Game.SCALE/2),0,Game.HEIGHT*Game.SCALE,Game.HEIGHT*Game.SCALE,game);
    }
}
