package com.DAConcepts;

import java.awt.Polygon;
import java.util.*;

public class RenderBuilding extends RenderClass{
    public float[] zBuffer;
    public float renderDist = 1000000f;
    public RenderClass floor;
    public RenderClass roof;
    public RenderClass wall;
    public float floorWorldHeight = 0;
    public float ceilingWorldHeight = 6;
    public Polygon poly;
    public Game game;
    private boolean playerInside = true;
    float cosine;
    float sine;
    Vector3 pos;
    float yRot;
    float yRot180;
    float BU = 1;

    public RenderBuilding(int width, int height, Game g) {
        super(width,height);
        zBuffer = new float[width*height];
        floor = Texture.E1M1Floor;
        roof = Texture.E1M1Roof;
        wall = Texture.E1M1Wall;
        game = g;
        poly = new Polygon();
        poly.addPoint(-50, -50);
        poly.addPoint(50, -50);
        poly.addPoint(50, 50);
        poly.addPoint(-50, 50);
    }

    public RenderBuilding(int width, int height, RenderClass f, RenderClass r, RenderClass w, Game g) {
        super(width,height);
        zBuffer = new float[width*height];
        floor = f;
        roof = r;
        wall = w;
        game = g;
        poly = new Polygon();
        poly.addPoint(-50, -50);
        poly.addPoint(50, -50);
        poly.addPoint(50, 50);
        poly.addPoint(-50, 50);
    }

    public RenderBuilding(int width, int height, RenderClass f, RenderClass r, RenderClass w, float fWH, float cWH, Game g) {
        super(width,height);
        zBuffer = new float[width*height];
        floor = f;
        roof = r;
        wall = w;
        floorWorldHeight = fWH;
        ceilingWorldHeight = cWH;
        game = g;
        poly = new Polygon();
        poly.addPoint(-50, -50);
        poly.addPoint(50, -50);
        poly.addPoint(50, 50);
        poly.addPoint(-50, 50);
    }

    public RenderBuilding(int width, int height, RenderClass f, RenderClass r, RenderClass w, float fWH, float cWH, int[][] verts, Game g) {
        super(width,height);
        zBuffer = new float[width*height];
        floor = f;
        roof = r;
        wall = w;
        floorWorldHeight = fWH;
        ceilingWorldHeight = cWH;
        game = g;
        poly = new Polygon();
        poly.addPoint(verts[0][0], verts[0][1]);
        poly.addPoint(verts[1][0], verts[1][1]);
        poly.addPoint(verts[2][0], verts[2][1]);
        poly.addPoint(verts[3][0], verts[3][1]);
    }

    public RenderBuilding(int width, int height, RenderClass f, RenderClass r, RenderClass w, float fWH, float cWH, int[][] verts, Game g, boolean plIn) {
        super(width,height);
        zBuffer = new float[width*height];
        floor = f;
        roof = r;
        wall = w;
        floorWorldHeight = fWH;
        ceilingWorldHeight = cWH;
        game = g;
        poly = new Polygon();
        poly.addPoint(verts[0][0], verts[0][1]);
        poly.addPoint(verts[1][0], verts[1][1]);
        poly.addPoint(verts[2][0], verts[2][1]);
        poly.addPoint(verts[3][0], verts[3][1]);
        playerInside = plIn;
    }

    public void floor(Vector3 p, float yr) {
        pos = p;
        Vector3 pos2 = p;//new Vector3(game.player.xPosFloor,game.player.yPosFloor,game.getPlayerHeightPos());
        yRot = game.player.zRot;
        if (poly.contains(pos.x,pos.y)) {
            playerInside = true;
        }
        else {
            playerInside = false;
        }
        float floorPos = ((game.getPlayerHeightPos()+game.getPlayerHeight())-floorWorldHeight)*64;
        float ceilingPos = (ceilingWorldHeight - (game.getPlayerHeightPos()+game.getPlayerHeight()))*64;
        float rotOfY = yRot;
        cosine = (float) Math.cos(-rotOfY*Math.PI/180f);
        sine = (float) Math.sin(-rotOfY*Math.PI/180f);
        //float cosine2 = (float) Math.cos(rotOfY*Math.PI/180f);
        //float sine2 = (float) Math.sin(rotOfY*Math.PI/180f);
        for (int y = 0; y < HEIGHT; y++) {
            boolean isRoof = false;
            float ceiling = (y - HEIGHT/2f)/HEIGHT;
            float z = floorPos/ceiling;
            if (ceiling < 0) {
                isRoof = true;
                z = ceilingPos/-ceiling;
            }
            for (int x = 0; x < WIDTH; x++) {
                float depth = (x-WIDTH/2f)/WIDTH;
                depth *= z;
                float xxx = depth*cosine + z*sine;
                int xx = (int) (pos2.x*floor.WIDTH/*(cosine2*0.6+sine2)*/+xxx);
                float yyy = z*cosine - depth*sine;
                int yy = (int) (yyy+pos2.y*floor.HEIGHT);//*(sine2*0.6+cosine2));
                zBuffer[x+y*WIDTH] = z;
                int color;
                if (!isRoof) {
                    color = floor.PIXELS[(xx & floor.WIDTH-1) + (yy & floor.HEIGHT-1)*floor.WIDTH];
                }
                else {
                    color = roof.PIXELS[(xx & roof.WIDTH-1) + (yy & roof.HEIGHT-1)*roof.WIDTH];
                }
                int transpIntensity = 0x00;
                //cut off platform
                //if not on platform reverse how it cuts off
                /*
                boolean rightOfPlayer = true;
                boolean aheadOfPlayer = true;
                float wallPosY1 = 50;
                float wallPosY2 = -50;
                float wallPosX1 = 10;
                float wallPosX2 = -10;
                float yDist1 = (wallPosY1 - pos.y);
                float xDist1 = (wallPosX1 - pos.x);
                float yDist2 = (wallPosY2 - pos.y);
                float xDist2 = (wallPosX2 - pos.x); // youre forgetting something
                if (renderDist/(xxx) < 0) {
                	rightOfPlayer = false;
                }
                if (renderDist/(yyy) < 0) {
                	aheadOfPlayer = false;
                }
                boolean left = rightOfPlayer&&renderDist/(xxx) < 3200/(xDist1*(Math.abs(cosine)+Math.abs(sine)));
                boolean right = !rightOfPlayer&&renderDist/(xxx) > 3200/(xDist2*(Math.abs(cosine)+Math.abs(sine)));
                boolean ahead = aheadOfPlayer&&renderDist/(yyy) < 3200/(yDist1*(Math.abs(cosine)+Math.abs(sine)));
                boolean behind = !aheadOfPlayer&&renderDist/(yyy) > 3200/(yDist2*(Math.abs(cosine)+Math.abs(sine)));
                if (left||right||ahead||behind) {
                	transpIntensity = 0xFF;
                }
                */
                int transparency = ((0xFF-transpIntensity) << 24) | 0xFFFFFF;
                // wall and window combo
                float xPosForWall = -5f;
                float xSizeForWall = 10f;
                float yPosForWall = 50f;
                float zPosForWall = 0f;
                float zSizeForWall = 6f;
                float sigCheckX = ((((x)-WIDTH/2f)/(HEIGHT/2f)*(yPosForWall/2 - pos.y/2)+pos.x*1.75f-+xPosForWall)/xSizeForWall);
                float sigCheckY = ((((y)-(HEIGHT/2f))/(HEIGHT/2f)*(yPosForWall/2 - pos.y/2)+(zPosForWall+zSizeForWall-1)-pos.z-0.5f)/zSizeForWall);


                //Wall
                float xLeft = -10f;
                float xRight = 10f;
                float yDistanceFromOrigin = 50f;
                float yHeight = 6f;
                boolean pixelIsWall = false;
                //Wall
                
                /*
                if (playerInside) {
                	if (pixelIsWall) {
                		PIXELS[x + y * WIDTH] = 0x00000000;
                	}
                	else {
                    	PIXELS[x + y * WIDTH] = color&transparency;
                	}
                }
                else {
                	if (pixelIsWall) {
                		PIXELS[x + y * WIDTH] = color&transparency;
                	}
                	else {
                		PIXELS[x + y * WIDTH] = 0xFF000000;
                	}
                }
                */
                PIXELS[x + y * WIDTH] = color & transparency;
            }
        }
    }

    public void floorOnlyTransparent(Vector3 p, float yr) {
        pos = p;
        yRot = yr;
        if (poly.contains(pos.x,pos.y)) {
            playerInside = true;
        }
        else {
            playerInside = false;
        }
        float floorPos = ((game.getPlayerHeightPos()+game.getPlayerHeight())-floorWorldHeight)*64;
        float ceilingPos = (ceilingWorldHeight - (game.getPlayerHeightPos()+game.getPlayerHeight()))*64;
        float rotOfY = yRot;
        cosine = (float) Math.cos(-rotOfY*Math.PI/180f);
        sine = (float) Math.sin(-rotOfY*Math.PI/180f);
        for (int y = 0; y < HEIGHT; y++) {
            boolean isRoof = false;
            float ceiling = (y - HEIGHT/2f)/HEIGHT;
            float z = floorPos/ceiling;
            if (ceiling < 0) {
                isRoof = true;
                z = ceilingPos/-ceiling;
            }
            for (int x = 0; x < WIDTH; x++) {
                float depth = (x-WIDTH/2f)/WIDTH;
                depth *= z;
                float xxx = depth*cosine + z*sine;
                int xx = (int) (pos.x*floor.WIDTH+xxx);
                float yyy = z*cosine - depth*sine;
                int yy = (int) (yyy+pos.y*floor.HEIGHT);
                zBuffer[x+y*WIDTH] = z;
                int color;
                if (!isRoof) {
                    color = floor.PIXELS[(xx & floor.WIDTH-1) + (yy & floor.HEIGHT-1)*floor.WIDTH];
                }
                else {
                    color = roof.PIXELS[(xx & roof.WIDTH-1) + (yy & roof.HEIGHT-1)*roof.WIDTH];
                }
                int transpIntensity = 0x00;
                //cut off platform
                //if not on platform reverse how it cuts off
                /*
                boolean rightOfPlayer = true;
                boolean aheadOfPlayer = true;
                float yDist1 = (50 - pos.y);
                float xDist1 = (10 - pos.x);
                float yDist2 = (-50 - pos.y);
                float xDist2 = (-10 - pos.x); // youre forgetting something
                if (renderDist/(xxx) < 0) {
                	rightOfPlayer = false;
                }
                if (renderDist/(yyy) < 0) {
                	aheadOfPlayer = false;
                }
                boolean left = rightOfPlayer&&renderDist/(xxx) < 3200/(xDist1*(Math.abs(cosine)+Math.abs(sine)));
                boolean right = !rightOfPlayer&&renderDist/(xxx) > 3200/(xDist2*(Math.abs(cosine)+Math.abs(sine)));
                boolean ahead = aheadOfPlayer&&renderDist/(yyy) < 3200/(yDist1*(Math.abs(cosine)+Math.abs(sine)));
                boolean behind = !aheadOfPlayer&&renderDist/(yyy) > 3200/(yDist2*(Math.abs(cosine)+Math.abs(sine)));
                if (left||right||ahead||behind) {
                	transpIntensity = 0xFF;
                }
                */
                int transparency = ((0xFF-transpIntensity) << 24) | 0xFFFFFF;
                // wall and window combo
                float xPosForWall = -5f;
                float xSizeForWall = 10f;
                float yPosForWall = 50f;
                float zPosForWall = 0f;
                float zSizeForWall = 6f;
                float sigCheckX = ((((x)-WIDTH/2f)/(HEIGHT/2f)*(yPosForWall/2 - pos.y/2)+pos.x*1.75f-+xPosForWall)/xSizeForWall);
                float sigCheckY = ((((y)-(HEIGHT/2f))/(HEIGHT/2f)*(yPosForWall/2 - pos.y/2)+(zPosForWall+zSizeForWall-1)-pos.z-0.5f)/zSizeForWall);


                //Wall
                float xLeft = -10f;
                float xRight = 10f;
                float yDistanceFromOrigin = 50f;
                float yHeight = 6f;
                boolean pixelIsWall = false;
                //Wall

                /*
                if (playerInside) {
                	if (pixelIsWall) {
                		PIXELS[x + y * WIDTH] = 0x00000000;
                	}
                	else {
                    	PIXELS[x + y * WIDTH] = color&transparency;
                	}
                }
                else {
                	if (pixelIsWall) {
                		PIXELS[x + y * WIDTH] = color&transparency;
                	}
                	else {
                		PIXELS[x + y * WIDTH] = 0xFF000000;
                	}
                }
                */
                if (PIXELS[x + y * WIDTH] == 0x0000FFFF) {
                    PIXELS[x + y * WIDTH] = color & transparency;
                }
            }
        }
    }

    public void renderBothSidesOfWall(float xLeft, float xRight, float yDistance, float zHeight, float tallness) {
        renderWall(xLeft, xRight, yDistance, zHeight, tallness, 0, (xLeft+xRight)/2f, 0xFF00FFFF);
        renderWall(xRight, xLeft, yDistance, zHeight, tallness, 0, (xLeft+xRight)/2f, 0xFF00FFFF);
    }

    public void renderBothSidesOfWall(float xLeft, float xRight, float yDistance, float zHeight, float tallness, float rotOrxPos, boolean isRot) {
        renderWall(xLeft, xRight, yDistance, zHeight, tallness, (isRot ? rotOrxPos : 0), (!isRot ? rotOrxPos : (xLeft+xRight)/2f), 0xFF00FFFF);
        renderWall(xRight, xLeft, yDistance, zHeight, tallness, (isRot ? rotOrxPos : 0), (!isRot ? rotOrxPos : (xLeft+xRight)/2f), 0xFF00FFFF);
    }

    public void renderBothSidesOfWall(float xLeft, float xRight, float yDistance, float zHeight, float tallness, float rot, float xPos) {
        renderWall(xLeft, xRight, yDistance, zHeight, tallness, rot, xPos, 0xFF00FFFF);
        renderWall(xRight, xLeft, yDistance, zHeight, tallness, rot, xPos, 0xFF00FFFF);
    }

    public void renderBothSidesOfWall(float xLeft, float xRight, float yDistance, float zHeight, float tallness, float rot, float xPos, int color) {
        renderWall(xLeft, xRight, yDistance, zHeight, tallness, rot, xPos, color);
        renderWall(xRight, xLeft, yDistance, zHeight, tallness, rot, xPos, color);
    }

    public void renderWall(float xLeft, float xRight, float yDistance, float zHeight, float tallness, float rot, float xPosParam, int color) {
        if ((color&(0xFF<<24)) == 0) {
            color = 0x0000FFFF;
        }
        boolean usingText = color == 0xFF00FFFF;
        if (tallness < 0.5f) {
            zHeight += tallness-0.5f;
        }
        float cosine2 = (float) Math.cos(-(yRot+rot)*Math.PI/180f);
        float sine2 = (float) Math.sin(-(yRot+rot)*Math.PI/180f);
        float cosineRot = (float) Math.cos((rot)*Math.PI/180f);
        float sineRot = (float) Math.sin((rot)*Math.PI/180f);

        float xLeft2 = xLeft-sineRot*yDistance;
        float xRight2 = xRight-sineRot*yDistance;
        float yDist2 = yDistance*cosineRot+(xPosParam)*sineRot;

        float xMult = (float) (1.75f*Math.pow(cosine2,2)+1f*Math.pow(sine2,2));
        float yMult = (float) (1f*Math.pow(cosine2,2)+1.75f*Math.pow(sine2,2));

        float xPos = pos.x*cosineRot-pos.y*sineRot;
        float yPos = pos.y*cosineRot+pos.x*sineRot;

        float xcLeft = ((xLeft2-(xPos))*xMult*2);
        float zcLeft = ((yDist2)-(yPos))*yMult*2f;

        float rotLeftSideX = xcLeft*cosine2-zcLeft*sine2;
        float yCornerTl = ((-zHeight+pos.z)*2);
        float yCornerBl = ((0.5f-zHeight)+pos.z)*2;
        float rotLeftSideZ = zcLeft*cosine2+xcLeft*sine2;

        float xcRight = ((xRight2-(xPos))*xMult*2);
        float zcRight = ((yDist2-(yPos))*yMult*2f);

        float rotRightSideX = xcRight*cosine2-zcRight*sine2;
        float yCornerTr = ((-zHeight+pos.z)*2);
        float yCornerBr = ((0.5f-zHeight)+pos.z)*2;
        float rotRightSideZ = zcRight*cosine2+xcRight*sine2;

        float xPixelLeft = (rotLeftSideX/rotLeftSideZ*HEIGHT+WIDTH/2);
        float xPixelRight = (rotRightSideX/rotRightSideZ*HEIGHT+WIDTH/2);
        if (xLeft <= xRight) {
            float increment = 0.005f*Math.abs(poly.xpoints[0]-poly.xpoints[1]);
            if (rotLeftSideZ < 0 && rotRightSideZ > 0) {
                renderWall(xLeft + increment, xRight, yDistance, zHeight, tallness, rot, xPosParam, color);
                return;
            }
            if (rotLeftSideZ > 0 && rotRightSideZ < 0) {
                renderWall(xLeft, xRight - increment, yDistance, zHeight, tallness, rot, xPosParam, color);
                return;
            }
        }
        else {
            float increment = 0.005f*Math.abs(poly.xpoints[0]-poly.xpoints[1]);
            if (rotLeftSideZ < 0 && rotRightSideZ > 0) {
                renderWall(xLeft - increment, xRight, yDistance, zHeight, tallness, rot, xPosParam, color);
                return;
            }
            if (rotLeftSideZ > 0 && rotRightSideZ < 0) {
                renderWall(xLeft, xRight + increment, yDistance, zHeight, tallness, rot, xPosParam, color);
                return;
            }
        }
        if (xPixelLeft >= xPixelRight) {
            return;
        }

        int xPixelLeftInt = (int) xPixelLeft;
        int xPixelLeftIntBU = (int) xPixelLeft;
        int xPixelRightInt = (int) xPixelRight;
        int xPixelRightIntBU = (int) xPixelRight;
        if (xPixelLeftInt < 0) {
            xPixelLeftInt = 0;
        }
        if (xPixelRightInt > WIDTH) {
            xPixelRightInt = WIDTH;
        }

        float yPixelLeftTop = (yCornerTl/rotLeftSideZ*HEIGHT+HEIGHT/2);
        float yPixelLeftBottom = (yCornerBl/rotLeftSideZ*HEIGHT+HEIGHT/2);
        float yPixelRightTop = (yCornerTr/rotRightSideZ*HEIGHT+HEIGHT/2);
        float yPixelRightBottom = (yCornerBr/rotRightSideZ*HEIGHT+HEIGHT/2);

        for (int x = xPixelLeftInt; x < xPixelRightInt; x++) {
            float pixelRotation = (x-xPixelLeft)/(xPixelRight-xPixelLeft);

            float yPixelTop = yPixelLeftTop + (yPixelRightTop-yPixelLeftTop)*pixelRotation;
            float yPixelBottom = yPixelLeftBottom + (yPixelRightBottom-yPixelLeftBottom)*pixelRotation;
            int yPixelTopInt = (int) (yPixelTop);
            int yPixelTopIntBU = (int) (yPixelTop);
            int yPixelBottomInt = (int) (yPixelBottom);
            int yPixelBottomIntBU = (int) (yPixelBottom);
            if (yPixelTopInt < 0) {
                yPixelTopInt = 0;
            }
            if (yPixelBottomInt > HEIGHT) {
                yPixelBottomInt = HEIGHT;
            }

            for (int y = yPixelTopInt; y < yPixelBottomInt; y++) {
                if (!usingText) {
                    PIXELS[x + y * WIDTH] = color;
                    zBuffer[x + y * WIDTH] = color;
                }
                else {
                    int colorText;
                    int height = (int) (((float) (y - yPixelTopIntBU))/((float) (yPixelBottomIntBU-yPixelTopIntBU)) * wall.HEIGHT);
                    int width = (int) (((float) (x - xPixelLeftIntBU)/((float) (xPixelRightIntBU-xPixelLeftIntBU)))*wall.WIDTH*(Math.abs(xRight-xLeft)*2f)%wall.WIDTH);
                    colorText = wall.PIXELS[width + height*wall.WIDTH];
                    if (colorText == 0xFF00FFFF) {
                        colorText = 0;
                    }
                    PIXELS[x + y * WIDTH] = colorText;
                    zBuffer[x + y * WIDTH] = colorText;
                }
            }
        }
        if (tallness > 0.5f) {
            renderWall(xLeft, xRight, yDistance, zHeight+0.5f, tallness-0.5f, rot, xPosParam, color);
        }
    }

    public int[] add(int[] first, int[] intsToAdd) {
        for (int i = 0; i < intsToAdd.length; i++) {
            if((intsToAdd[i]&(0xFF<<24)) != 0 && first[i] != intsToAdd[i]) {
                first[i] = intsToAdd[i];
            }
        }
        return first;
    }

    public boolean playerIn() {
        return poly.contains(game.getPlayerPos().x,game.getPlayerPos().y);
    }

    public void renderRoom() {
        int inverseMult = -1;
        //if (!poly.contains(game.getPlayerPos().x,game.getPlayerPos().y)) {
            //inverseMult = 1;
        //}
        float mult = Math.abs(poly.xpoints[0]-poly.xpoints[1]);
        float xPos = (poly.xpoints[0]+poly.xpoints[2])/2f;
        float yPos = (poly.ypoints[0]+poly.ypoints[2])/2f;
        Vector3 playerPos = new Vector3(game.getPlayerPos().x,game.getPlayerPos().y,game.getPlayerHeightPos());
        this.floor(playerPos, game.getPlayerRot());
        this.renderDistanceLimiter();
        this.renderWall(-mult / 2 * inverseMult, mult / 2 * inverseMult, yPos, -1f, 6f, 90, -mult / 2 + xPos, 0x00000000);
        this.renderWall(mult / 2 * inverseMult, -mult / 2 * inverseMult, yPos, -1f, 6f, 90, mult / 2 + xPos, 0x00000000);
        this.renderWall(-mult / 2 * inverseMult + xPos, mult / 2 * inverseMult + xPos, yPos - mult / 2, -1f, 6f, 0, -mult / 2, 0x00000000);
        this.renderWall(mult / 2 * inverseMult + xPos, -mult / 2 * inverseMult + xPos, yPos + mult / 2, -1f, 6f, 0, mult / 2, 0x00000000);
    }

    public void renderRoom(int numStartFromBotRight) {
        int inverseMult = -1;
        boolean isIn = true;
        if (!playerIn()) {
            isIn = false;
        }
        float mult = Math.abs(poly.xpoints[0]-poly.xpoints[1]);
        float xPos = (poly.xpoints[0]+poly.xpoints[2])/2f;
        float yPos = (poly.ypoints[0]+poly.ypoints[2])/2f;
        Vector3 playerPos = new Vector3(game.getPlayerPos().x,game.getPlayerPos().y,game.getPlayerHeightPos());
        this.floor(playerPos, game.getPlayerRot());
        //this.renderDistanceLimiter();
        int color1 = 0x00000000; //left
        int color3 = 0x00000000; //bot
        int color2 = 0x00000000; //right
        int color4 = 0x00000000; //top
        if (numStartFromBotRight <= 8) {
            if (numStartFromBotRight + 1 >= 8 || numStartFromBotRight == 1) {
                color3 = 0xFF00FFFF;
            }
            if (numStartFromBotRight <= 3) {
                color2 = 0xFF00FFFF;
            }
            if (numStartFromBotRight - 2 <= 3 && numStartFromBotRight - 2 > 0) {
                color4 = 0xFF00FFFF;
            }
            if (numStartFromBotRight - 4 <= 3 && numStartFromBotRight - 4 > 0) {
                color1 = 0xFF00FFFF;
            }
        }
        else if (numStartFromBotRight == 10) {
            color1 = 0xFF00FFFF;
            color3 = 0xFF00FFFF;
            color2 = 0xFF00FFFF;
            color4 = 0xFF00FFFF;
        }
        int inverseMult1 = inverseMult; //left
        int inverseMult3 = inverseMult; //bot
        int inverseMult2 = inverseMult; //right
        int inverseMult4 = inverseMult; //top
        int amx = (int) (game.getXMAX()/mult*2);
        int amy = (int) (game.getXMAX()/mult*2);
        if ((color1&(0xFF<<24)) == 0) {
            inverseMult1 *= amx;
        }
        if ((color2&(0xFF<<24)) == 0) {
            inverseMult2 *= amx;
        }
        if ((color3&(0xFF<<24)) == 0) {
            inverseMult3 *= amy;
        }
        if ((color4&(0xFF<<24)) == 0) {
            inverseMult4 *= amy;
        }
        //render transparent first
        if ((color1 & (0xFF << 24)) == 0)
            this.renderWall(-mult / 2 * inverseMult1, mult / 2 * inverseMult1, yPos, -1f, 6f, 90, -mult / 2 + xPos, color1);
        if ((color3 & (0xFF << 24)) == 0)
            this.renderWall(-mult / 2 * inverseMult3 + xPos, mult / 2 * inverseMult3 + xPos, yPos - mult / 2, -1f, 6f, 0, -mult / 2, color3);
        if ((color2 & (0xFF << 24)) == 0)
            this.renderWall(mult / 2 * inverseMult2, -mult / 2 * inverseMult2, yPos, -1f, 6f, 90, mult / 2 + xPos, color2);
        if ((color4 & (0xFF << 24)) == 0)
            this.renderWall(mult / 2 * inverseMult4 + xPos, -mult / 2 * inverseMult4 + xPos, yPos + mult / 2, -1f, 6f, 0, mult / 2, color4);
        if ((color1 & (0xFF << 24)) != 0)
            this.renderWall(-mult / 2 * inverseMult1, mult / 2 * inverseMult1, yPos, -1f, 6f, 90, -mult / 2 + xPos, color1);
        if ((color3 & (0xFF << 24)) != 0)
            this.renderWall(-mult / 2 * inverseMult3 + xPos, mult / 2 * inverseMult3 + xPos, yPos - mult / 2, -1f, 6f, 0, -mult / 2, color3);
        if ((color2 & (0xFF << 24)) != 0)
            this.renderWall(mult / 2 * inverseMult2, -mult / 2 * inverseMult2, yPos, -1f, 6f, 90, mult / 2 + xPos, color2);
        if ((color4 & (0xFF << 24)) != 0)
            this.renderWall(mult / 2 * inverseMult4 + xPos, -mult / 2 * inverseMult4 + xPos, yPos + mult / 2, -1f, 6f, 0, mult / 2, color4);
    }

    public void renderRoom(int numStartFromBotRight, int numStartFromBotRight2) {
        pos = new Vector3(game.getPlayerPos().x,game.getPlayerPos().y,game.getPlayerHeightPos());
        int inverseMult = -1;
        boolean isIn = true;
        if (!playerIn()) {
            inverseMult *= -1;
            isIn = false;
        }
        float mult = Math.abs(poly.xpoints[0]-poly.xpoints[1]);
        float xPos = (poly.xpoints[0]+poly.xpoints[2])/2f;
        float yPos = (poly.ypoints[0]+poly.ypoints[2])/2f;
        Vector3 playerPos = new Vector3(game.getPlayerPos().x,game.getPlayerPos().y,game.getPlayerHeightPos());
        int color1 = 0x00000000; //left
        int color3 = 0x00000000; //bot
        int color2 = 0x00000000; //right
        int color4 = 0x00000000; //top
        if (numStartFromBotRight <= 8) {
            if (numStartFromBotRight + 1 >= 8 || numStartFromBotRight == 1) {
                color3 = 0xFF00FFFF;
            }
            if (numStartFromBotRight <= 3) {
                color2 = 0xFF00FFFF;
            }
            if (numStartFromBotRight - 2 <= 3 && numStartFromBotRight - 2 > 0) {
                color4 = 0xFF00FFFF;
            }
            if (numStartFromBotRight - 4 <= 3 && numStartFromBotRight - 4 > 0) {
                color1 = 0xFF00FFFF;
            }
        }
        else if (numStartFromBotRight == 10) {
            color1 = 0xFF00FFFF;
            color3 = 0xFF00FFFF;
            color2 = 0xFF00FFFF;
            color4 = 0xFF00FFFF;
        }
        if (numStartFromBotRight2 <= 8) {
            if (numStartFromBotRight2 + 1 >= 8 || numStartFromBotRight2 == 1) {
                color3 = 0xFF00FFFF;
            }
            if (numStartFromBotRight2 <= 3) {
                color2 = 0xFF00FFFF;
            }
            if (numStartFromBotRight2 - 2 <= 3 && numStartFromBotRight2 - 2 > 0) {
                color4 = 0xFF00FFFF;
            }
            if (numStartFromBotRight2 - 4 <= 3 && numStartFromBotRight2 - 4 > 0) {
                color1 = 0xFF00FFFF;
            }
        }
        else if (numStartFromBotRight2 == 10) {
            color1 = 0xFF00FFFF;
            color3 = 0xFF00FFFF;
            color2 = 0xFF00FFFF;
            color4 = 0xFF00FFFF;
        }
        int inverseMult1 = inverseMult; //left
        int inverseMult3 = inverseMult; //bot
        int inverseMult2 = inverseMult; //right
        int inverseMult4 = inverseMult; //top
        int amx = (int) (game.getXMAX()/mult*2);
        int amy = (int) (game.getXMAX()/mult*2);
        if ((color1&(0xFF<<24)) == 0) {
            inverseMult1 *= amx;
        }
        if ((color2&(0xFF<<24)) == 0) {
            inverseMult2 *= amx;
        }
        if ((color3&(0xFF<<24)) == 0) {
            inverseMult3 *= amy;
        }
        if ((color4&(0xFF<<24)) == 0) {
            inverseMult4 *= amy;
        }
        //render transparent first
        if (isIn) {
            this.floor(playerPos, game.getPlayerRot());
            this.renderDistanceLimiter();
        }
        if ((color1 & (0xFF << 24)) == 0)
            this.renderWall(-mult / 2 * inverseMult1, mult / 2 * inverseMult1, yPos, -1f, 6f, 90, -mult / 2 + xPos, color1);
        if ((color3 & (0xFF << 24)) == 0)
            this.renderWall(-mult / 2 * inverseMult3 + xPos, mult / 2 * inverseMult3 + xPos, yPos - mult / 2, -1f, 6f, 0, -mult / 2, color3);
        if ((color2 & (0xFF << 24)) == 0)
            this.renderWall(mult / 2 * inverseMult2, -mult / 2 * inverseMult2, yPos, -1f, 6f, 90, mult / 2 + xPos, color2);
        if ((color4 & (0xFF << 24)) == 0)
            this.renderWall(mult / 2 * inverseMult4 + xPos, -mult / 2 * inverseMult4 + xPos, yPos + mult / 2, -1f, 6f, 0, mult / 2, color4);
        if (!isIn) {
            this.floorOnlyTransparent(playerPos, game.getPlayerRot());
            inverseMult1 *= -1;
            inverseMult2 *= -1;
            inverseMult3 *= -1;
            inverseMult4 *= -1;
            this.renderDistanceLimiter();
        }
        if ((color1 & (0xFF << 24)) != 0)
            this.renderWall(-mult / 2 * inverseMult1, mult / 2 * inverseMult1, yPos, -1f, 6f, 90, -mult / 2 + xPos, color1);
        if ((color3 & (0xFF << 24)) != 0)
            this.renderWall(-mult / 2 * inverseMult3 + xPos, mult / 2 * inverseMult3 + xPos, yPos - mult / 2, -1f, 6f, 0, -mult / 2, color3);
        if ((color2 & (0xFF << 24)) != 0)
            this.renderWall(mult / 2 * inverseMult2, -mult / 2 * inverseMult2, yPos, -1f, 6f, 90, mult / 2 + xPos, color2);
        if ((color4 & (0xFF << 24)) != 0)
            this.renderWall(mult / 2 * inverseMult4 + xPos, -mult / 2 * inverseMult4 + xPos, yPos + mult / 2, -1f, 6f, 0, mult / 2, color4);
    }
    
    public void walls(Vector3 pos, float yRot) {
        float xPosForWall = -5f;
        float xSizeForWall = 10f;
        float yPosForWall = 50f;
        float zPosForWall = 0f;
        float zSizeForWall = 6f;
        int ySigmaCount = -1;
        for (int i = 0; i < WIDTH*HEIGHT; i++) {
        	float xSigmaVal = ((float) (i%WIDTH))/WIDTH;
        	if (xSigmaVal == 0) {
        		ySigmaCount++;
        	}
        	float xx = (float) (xSigmaVal)*xSizeForWall+xPosForWall-pos.x*1.75f;
        	float yy = (float) (((float) (ySigmaCount))/HEIGHT)*zSizeForWall+0.5f+pos.z-(zPosForWall+zSizeForWall-1);
        	float zz = yPosForWall/2 - pos.y/2;
        	int pixelX = (int) (xx/zz*HEIGHT/2f+WIDTH/2f);
        	int pixelY = (int) (yy/zz*HEIGHT/2f+HEIGHT/2f);
        	if (pixelX >= 0 && pixelX < WIDTH && pixelY >= 0 && pixelY < HEIGHT) {
            	PIXELS[pixelX + pixelY * WIDTH] = 0x00000000;
        	}
        }
    }
    
    public void walls(Vector3 pos, float yRot, int color) {
        Random rand = new Random();
        float xPosForWall = 0f;
        float xSizeForWall = 1f;
        float yPosForWall = 4f;
        float zPosForWall = 0f;
        float zSizeForWall = 6f;
        for (int i = 0; i < 10000; i++) {
        	float xx = (float) rand.nextDouble()*xSizeForWall+xPosForWall-pos.x*1.75f;
        	float yy = (float) rand.nextDouble()*zSizeForWall+0.5f+pos.z-(zPosForWall+zSizeForWall-1);
        	float zz = yPosForWall/2 - pos.y/2;
        	int pixelX = (int) (xx/zz*HEIGHT/2f+WIDTH/2f);
        	int pixelY = (int) (yy/zz*HEIGHT/2f+HEIGHT/2f);
        	if (pixelX >= 0 && pixelX < WIDTH && pixelY >= 0 && pixelY < HEIGHT) {
            	PIXELS[pixelX + pixelY * WIDTH] = color;
        	}
        }
    }
    
    public void walls(Vector3 pos, float yRot, float xPosForWall, float yPosForWall, float zPosForWall) {
        Random rand = new Random();
        float xSizeForWall = 1f;
        float zSizeForWall = 6f;
        for (int i = 0; i < 10000; i++) {
        	float xx = (float) rand.nextDouble()*xSizeForWall+xPosForWall-pos.x*1.75f;
        	float yy = (float) rand.nextDouble()*zSizeForWall+0.5f+pos.z-(zPosForWall+zSizeForWall-1);
        	float zz = yPosForWall/2 - pos.y/2;
        	int pixelX = (int) (xx/zz*HEIGHT/2f+WIDTH/2f);
        	int pixelY = (int) (yy/zz*HEIGHT/2f+HEIGHT/2f);
        	if (pixelX >= 0 && pixelX < WIDTH && pixelY >= 0 && pixelY < HEIGHT) {
            	PIXELS[pixelX + pixelY * WIDTH] = 0xFF000000;
        	}
        }
    }
    
    public void walls(Vector3 pos, float yRot, float xPosForWall, float xSizeForWall, float yPosForWall, float zPosForWall, float zSizeForWall) {
        Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
        	float xx = (float) rand.nextDouble()*xSizeForWall+xPosForWall-pos.x*1.75f;
        	float yy = (float) rand.nextDouble()*zSizeForWall+0.5f+pos.z-(zPosForWall+zSizeForWall-1);
        	float zz = yPosForWall/2 - pos.y/2;
        	int pixelX = (int) (xx/zz*HEIGHT/2f+WIDTH/2f);
        	int pixelY = (int) (yy/zz*HEIGHT/2f+HEIGHT/2f);
        	if (pixelX >= 0 && pixelX < WIDTH && pixelY >= 0 && pixelY < HEIGHT) {
            	PIXELS[pixelX + pixelY * WIDTH] = 0xFF000000;
        	}
        }
    }
    
    public void walls(Vector3 pos, float yRot, float xPosForWall, float xSizeForWall, float yPosForWall, float zPosForWall, float zSizeForWall, float zRot) {
        Random rand = new Random();
        for (int i = 0; i < 10000; i++) {
        	float xx = (float) rand.nextDouble()*xSizeForWall+xPosForWall-pos.x*1.75f;
        	float yy = (float) rand.nextDouble()*zSizeForWall+0.5f+pos.z-(zPosForWall+zSizeForWall-1);
        	float zz = yPosForWall/2 - pos.y/2;
        	int pixelX = (int) (xx/zz*HEIGHT/2f+WIDTH/2f);
        	int pixelY = (int) (yy/zz*HEIGHT/2f+HEIGHT/2f);
        	if (pixelX >= 0 && pixelX < WIDTH && pixelY >= 0 && pixelY < HEIGHT) {
            	PIXELS[pixelX + pixelY * WIDTH] = 0xFF000000;
        	}
        }
    }

    public void renderDistanceLimiter() {
        for (int i = 0; i < WIDTH*HEIGHT; i++) {
            int color = PIXELS[i];
            int brightness = (int) (renderDist/(zBuffer[i]));
            if (brightness < 0) {
                brightness = 0;
            }
            if (brightness > 255) {
                brightness = 255;
            }
            int a = (color >> 24) & 0xff;
            int r = (int) (((color >> 16) & 0xff) * brightness/255f);
            int g = (int) (((color >> 8) & 0xff) * brightness/255f);
            int b = (int) ((color & 0xff) * brightness/255f);
            PIXELS[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
}