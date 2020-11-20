package com.DAConcepts;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.*;

public class MouseAxisInput extends JPanel implements MouseMotionListener, MouseListener{
	private static final long serialVersionUID = 1L;
	private Game game;

    public MouseAxisInput(Game g) {
        game = g;
    }

    public void mouseDragged(MouseEvent e) {
        //drag
    }

    public void mouseMoved(MouseEvent e) {
        game.mouseMoved(e);
    }


    public void mouseClicked(MouseEvent e) {
        game.mouseMoved(e);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        //
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        //
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        //
    }

    public void mouseExited(MouseEvent mouseEvent) {
        //
    }
}