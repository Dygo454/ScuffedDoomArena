package com.DAConcepts;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    Game game;

    public KeyInput(Game g) {
        this.game = g;
    }

    public void keyPressed(KeyEvent e) {
        game.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        game.keyReleased(e);
    }
}
