package com.autoclicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Provides a full-screen transparent overlay to allow the user
 * to select a rectangular area on their screen.
 */
public class AreaSelector extends JFrame {

    private Point startPoint;
    private Point endPoint;
    private Rectangle selectedRectangle;
    private boolean selectionComplete = false;

    public AreaSelector(MainFrame parent) {
        // Set up undecorated, full screen, transparent window
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // This makes the frame semi-transparent.
        // On some systems this might require the frame to be visible first or have a specific background.
        setBackground(new Color(0, 0, 0, 64)); // 25% opaque black
        
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        // Panel to draw the selection rectangle
        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (startPoint != null && endPoint != null) {
                    int x = Math.min(startPoint.x, endPoint.x);
                    int y = Math.min(startPoint.y, endPoint.y);
                    int width = Math.abs(startPoint.x - endPoint.x);
                    int height = Math.abs(startPoint.y - endPoint.y);

                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRect(x, y, width, height);
                    
                    // Fill the selected area with a slightly different color to highlight it
                    g2d.setColor(new Color(255, 0, 0, 32));
                    g2d.fillRect(x, y, width, height);
                }
            }
        };
        drawPanel.setOpaque(false);
        add(drawPanel);

        // Mouse listeners for drawing the rectangle
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                endPoint = startPoint;
                drawPanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                int x = Math.min(startPoint.x, endPoint.x);
                int y = Math.min(startPoint.y, endPoint.y);
                int width = Math.abs(startPoint.x - endPoint.x);
                int height = Math.abs(startPoint.y - endPoint.y);
                
                selectedRectangle = new Rectangle(x, y, width, height);
                selectionComplete = true;
                
                // Close the selector and notify parent
                dispose();
                parent.onAreaSelected(selectedRectangle);
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                drawPanel.repaint();
            }
        });
        
        // Add an escape key listener to cancel selection
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "cancelSelection");
        getRootPane().getActionMap().put("cancelSelection", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
                parent.onAreaSelected(null); // Cancelled
            }
        });
    }

    /**
     * Shows the selector overlay.
     */
    public void startSelection() {
        startPoint = null;
        endPoint = null;
        selectedRectangle = null;
        selectionComplete = false;
        setVisible(true);
    }
}
