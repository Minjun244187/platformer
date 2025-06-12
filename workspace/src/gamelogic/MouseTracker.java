package gamelogic;

import java.awt.*;
import java.awt.event.*;

public class MouseTracker implements MouseMotionListener {
    private int mouseX = 0;
    private int mouseY = 0;

    public MouseTracker(Component component) {
        component.addMouseMotionListener(this);
    }

    public void draw(Graphics g) {
        String coordText = "(" + mouseX + ", " + mouseY + ")";
        int textWidth = g.getFontMetrics().stringWidth(coordText);

        g.setColor(Color.WHITE);
        g.fillRect(mouseX, mouseY - 20, textWidth + 6, 18);
        g.setColor(Color.BLACK);
        g.drawRect(mouseX, mouseY - 20, textWidth + 6, 18);
        g.drawString(coordText, mouseX + 3, mouseY - 6);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
}
