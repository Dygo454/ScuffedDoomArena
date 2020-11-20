package com.DAConcepts;

public class BuildingScreenLayer extends RenderClass{
    private RenderBuilding[][] roomss;
    private int roomsGridWidth = 1;
    private int roomsGridHeight = 1;
    private int size = 100;

    public BuildingScreenLayer(int width, int height, Game game) {
        super(width, height);
        game.setXMAX(roomsGridWidth*size);
        game.setYMAX(roomsGridHeight*size);
        roomss = new RenderBuilding[roomsGridHeight][roomsGridWidth];
        for (int i = 0; i < roomsGridHeight; i++) {
            for (int n = 0; n < roomsGridWidth; n++) {
                int[][] vertsForRoom = {{n*size, i*size}, {(n+1)*size, i*size}, {(n+1)*size, (i+1)*size}, {n*size, (i+1)*size}};
                RenderClass floor = new RenderClass[]{Texture.E1M1Floor, Texture.E1M1Roof, Texture.E1M1Wall}[(n+i)%3];
                RenderClass roof = new RenderClass[]{Texture.E1M1Floor, Texture.E1M1Roof, Texture.E1M1Wall}[(n+i+1)%3];
                RenderClass wall = new RenderClass[]{Texture.E1M1Floor, Texture.E1M1Roof, Texture.E1M1Wall}[(n+i+2)%3];
                float floorHeight = 0;
                float roofHeight = 6;
                roomss[i][n] = new RenderBuilding(width, height, floor, roof, wall, floorHeight, roofHeight, vertsForRoom, game);
            }
        }
    }

    public void render(Game game) {
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            PIXELS[i] = 0xFF<<24;
        }
        int[] currRoom = new int[] {(int) game.getPlayerPos().x/size, (int) game.getPlayerPos().y/size};
        int max = Math.max(roomsGridWidth,roomsGridHeight);
        for (int n = max-1; n >= 0; n--) {
            for (int i = max-1; i > 0; i--) {
                int xCoord = currRoom[0];
                int yCoord = currRoom[1];
                if (xCoord+i >= 0 && xCoord+i < roomsGridWidth && yCoord+n >= 0 && yCoord+n < roomsGridHeight) {
                    int x = xCoord+i;
                    int y = yCoord+n;
                    float rot = game.getPlayerRot();
                    if (!((currRoom[1] > y && (rot > 315f || rot < 45f)) || (currRoom[1] < y && (rot > 135f && rot < 225f)) || (currRoom[0] > x && (rot > 225f && rot < 315f)) || (currRoom[0] < x && (rot > 45f || rot < 135f)))) {
                        int num = 9;
                        int num2 = 9;
                        if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                            num2 = 7;
                        } else if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1)) {
                            num = 1;
                            num2 = 6;
                        } else if ((x == 0) && (y == 0) && (y == roomsGridHeight - 1)) {
                            num = 1;
                            num2 = 4;
                        } else if ((x == 0) && (y == 0)) {
                            num = 7;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (x == 0)) {
                            num = 3;
                            num2 = 6;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (y == 0)) {
                            num = 3;
                            num2 = 8;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                        } else if ((x == roomsGridWidth - 1) && (x == 0)) {
                            num = 2;
                            num2 = 6;
                        } else if ((y == roomsGridHeight - 1) && (y == 0)) {
                            num = 4;
                            num2 = 8;
                        } else if (x == 0) num = 6;
                        else if (y == 0) num = 8;
                        else if (x == roomsGridWidth - 1) num = 2;
                        else if (y == roomsGridHeight - 1) num = 4;
                        roomss[y][x].renderRoom(num, num2);
                        draw(roomss[y][x], 0, 0);
                    }
                }
                if (xCoord-i >= 0 && xCoord-i < roomsGridWidth && yCoord-n >= 0 && yCoord-n < roomsGridHeight) {
                    int x = xCoord-i;
                    int y = yCoord-n;
                    int num = 9;
                    int num2 = 9;
                    float rot = game.getPlayerRot();
                    if (!((currRoom[1] > y && (rot > 315f || rot < 45f)) || (currRoom[1] < y && (rot > 135f && rot < 225f)) || (currRoom[0] > x && (rot > 225f && rot < 315f)) || (currRoom[0] < x && (rot > 45f || rot < 135f)))) {
                        if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                            num2 = 7;
                        } else if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1)) {
                            num = 1;
                            num2 = 6;
                        } else if ((x == 0) && (y == 0) && (y == roomsGridHeight - 1)) {
                            num = 1;
                            num2 = 4;
                        } else if ((x == 0) && (y == 0)) {
                            num = 7;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (x == 0)) {
                            num = 3;
                            num2 = 6;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (y == 0)) {
                            num = 3;
                            num2 = 8;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                        } else if ((x == roomsGridWidth - 1) && (x == 0)) {
                            num = 2;
                            num2 = 6;
                        } else if ((y == roomsGridHeight - 1) && (y == 0)) {
                            num = 4;
                            num2 = 8;
                        } else if (x == 0) num = 6;
                        else if (y == 0) num = 8;
                        else if (x == roomsGridWidth - 1) num = 2;
                        else if (y == roomsGridHeight - 1) num = 4;
                        roomss[y][x].renderRoom(num, num2);
                        draw(roomss[y][x], 0, 0);
                    }
                }
                if (xCoord-n >= 0 && xCoord-n < roomsGridWidth && yCoord+i >= 0 && yCoord+i < roomsGridHeight) {
                    int x = xCoord-n;
                    int y = yCoord+i;
                    int num = 9;
                    int num2 = 9;
                    float rot = game.getPlayerRot();
                    if (!((currRoom[1] > y && (rot > 315f || rot < 45f)) || (currRoom[1] < y && (rot > 135f && rot < 225f)) || (currRoom[0] > x && (rot > 225f && rot < 315f)) || (currRoom[0] < x && (rot > 45f || rot < 135f)))) {
                        if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                            num2 = 7;
                        } else if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1)) {
                            num = 1;
                            num2 = 6;
                        } else if ((x == 0) && (y == 0) && (y == roomsGridHeight - 1)) {
                            num = 1;
                            num2 = 4;
                        } else if ((x == 0) && (y == 0)) {
                            num = 7;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (x == 0)) {
                            num = 3;
                            num2 = 6;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (y == 0)) {
                            num = 3;
                            num2 = 8;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                        } else if ((x == roomsGridWidth - 1) && (x == 0)) {
                            num = 2;
                            num2 = 6;
                        } else if ((y == roomsGridHeight - 1) && (y == 0)) {
                            num = 4;
                            num2 = 8;
                        } else if (x == 0) num = 6;
                        else if (y == 0) num = 8;
                        else if (x == roomsGridWidth - 1) num = 2;
                        else if (y == roomsGridHeight - 1) num = 4;
                        roomss[y][x].renderRoom(num, num2);
                        draw(roomss[y][x], 0, 0);
                    }
                }
                if (xCoord+n >= 0 && xCoord+n < roomsGridWidth && yCoord-i >= 0 && yCoord-i < roomsGridHeight) {
                    int x = xCoord+n;
                    int y = yCoord-i;
                    int num = 9;
                    int num2 = 9;
                    float rot = game.getPlayerRot();
                    if (!((currRoom[1] > y && (rot > 315f || rot < 45f)) || (currRoom[1] < y && (rot > 135f && rot < 225f)) || (currRoom[0] > x && (rot > 225f && rot < 315f)) || (currRoom[0] < x && (rot > 45f || rot < 135f)))) {
                        if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                            num2 = 7;
                        } else if ((x == 0) && (y == 0) && (x == roomsGridWidth - 1)) {
                            num = 1;
                            num2 = 6;
                        } else if ((x == 0) && (y == 0) && (y == roomsGridHeight - 1)) {
                            num = 1;
                            num2 = 4;
                        } else if ((x == 0) && (y == 0)) {
                            num = 7;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (x == 0)) {
                            num = 3;
                            num2 = 6;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1) && (y == 0)) {
                            num = 3;
                            num2 = 8;
                        } else if ((x == roomsGridWidth - 1) && (y == roomsGridHeight - 1)) {
                            num = 3;
                        } else if ((x == roomsGridWidth - 1) && (x == 0)) {
                            num = 2;
                            num2 = 6;
                        } else if ((y == roomsGridHeight - 1) && (y == 0)) {
                            num = 4;
                            num2 = 8;
                        } else if (x == 0) num = 6;
                        else if (y == 0) num = 8;
                        else if (x == roomsGridWidth - 1) num = 2;
                        else if (y == roomsGridHeight - 1) num = 4;
                        roomss[y][x].renderRoom(num, num2);
                        draw(roomss[y][x], 0, 0);
                    }
                }
            }
        }
        int x = currRoom[0];
        int y = currRoom[1];
        int num = 9;
        if (roomsGridWidth == 1 && roomsGridHeight == 1) {
            num = 10;
            roomss[y][x].renderRoom(num);
        }
        else {
            int num2 = 9;
            if ((x == 0)&&(y==0)&&(x == roomsGridWidth-1)&&(y==roomsGridHeight-1)) {
                num = 3;
                num2 = 7;
            }
            else if ((x == 0)&&(y==0)&&(x == roomsGridWidth-1)) {
                num = 1;
                num2 = 6;
            }
            else if ((x == 0)&&(y==0)&&(y == roomsGridHeight-1)) {
                num = 1;
                num2 = 4;
            }
            else if ((x == 0)&&(y==0)) {
                num = 7;
            }
            else if ((x == roomsGridWidth-1)&&(y==roomsGridHeight-1)&&(x==0)) {
                num = 3;
                num2 = 6;
            }
            else if ((x == roomsGridWidth-1)&&(y==roomsGridHeight-1)&&(y==0)) {
                num = 3;
                num2 = 8;
            }
            else if ((x == roomsGridWidth-1)&&(y==roomsGridHeight-1)) {
                num = 3;
            }
            else if ((x == roomsGridWidth-1)&&(x==0)) {
                num = 2;
                num2 = 6;
            }
            else if ((y==roomsGridHeight-1)&&(y==0)) {
                num = 4;
                num2 = 8;
            }
            else if (x == 0) num = 6;
            else if (y == 0) num = 8;
            else if (x == roomsGridWidth-1) num = 2;
            else if (y == roomsGridHeight-1) num = 4;
            roomss[y][x].renderRoom(num, num2);
        }
        draw(roomss[y][x],0,0);
    }
}
