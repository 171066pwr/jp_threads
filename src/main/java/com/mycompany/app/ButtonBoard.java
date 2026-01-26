package com.mycompany.app;

public class ButtonBoard extends GameBoard {
    private final GraphicButton[][] buttons;

    public ButtonBoard(int width, int height) {
        buttons = new GraphicButton[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                buttons[i][j] = new GraphicButton("", 0, null);
            }
        }
    }

    public void repaint(int x, int y) {
        buttons[x][y].repaint();
    }
}
