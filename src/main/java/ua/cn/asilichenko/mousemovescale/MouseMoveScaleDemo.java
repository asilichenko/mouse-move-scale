package ua.cn.asilichenko.mousemovescale;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Creation date: 08.05.2022
 *
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 */
public class MouseMoveScaleDemo extends MouseMoveScale {

    private static final String WINDOW_TITLE = MouseMoveScale.WINDOW_TITLE + "[Demo] ";

    private final AtomicInteger demoState = new AtomicInteger(0);

    private MouseMoveScaleDemo(JFrame frame) {
        super(frame);
    }

    @Override
    void setupListeners() {
        addMouseWheelListener(e -> {
            demoState.addAndGet(-e.getWheelRotation());
            repaint();
        });
    }

    @Override
    void applyTransformation(Graphics2D g2d) {
        final int state = Math.abs(demoState.get()) % 12;
        final String windowTitle;

        final Color pointColor = Color.RED.darker();

        final int prevX = GRID_CELL_SIZE, prevY = GRID_CELL_SIZE / 2,
                newX = GRID_CELL_SIZE, newY = 2 * GRID_CELL_SIZE;

        final double prevScale = 2, newScale = 3;
        final int pointR0 = 5, pointRPrev = (int) (pointR0 * prevScale), pointRNew = (int) (pointR0 * newScale);

        switch (state) {
            case 0:
                windowTitle = "source state";
                g2d.scale(prevScale, prevScale);
                g2d.translate(prevX, prevY);
                break;
            case 1:
                windowTitle = "source state with subsequent relative scaling point";

                g2d.setColor(pointColor);
                g2d.fillOval(newX - pointRPrev, newY - pointRPrev, 2 * pointRPrev, 2 * pointRPrev);

                g2d.scale(prevScale, prevScale);
                g2d.translate(prevX, prevY);
                break;
            case 2:
                windowTitle = "new scaling is done";
                g2d.setColor(pointColor);
                g2d.fillOval(newX - pointRNew, newY - pointRNew, 2 * pointRNew, 2 * pointRNew);

                g2d.scale(newScale, newScale);
                g2d.translate(
                        prevX - newX / prevScale + newX / newScale,
                        prevY - newY / prevScale + newY / newScale
                );
                break;
            case 3:
                windowTitle = "initial state";
                break;
            case 4:
                windowTitle = "initial state with source state scaling";
                g2d.scale(prevScale, prevScale);
                break;
            case 5:
                windowTitle = "initial state with source state scaling and transition";
                g2d.scale(prevScale, prevScale);
                g2d.translate(prevX, prevY);
                break;
            case 6:
                windowTitle = "place the pointer to source state";

                g2d.setColor(pointColor);
                g2d.fillOval(newX - pointRPrev, newY - pointRPrev, 2 * pointRPrev, 2 * pointRPrev);

                g2d.scale(prevScale, prevScale);
                g2d.translate(prevX, prevY);
                break;
            case 7:
                windowTitle = "unscale source state to see pointer origin offset";
                g2d.setColor(pointColor);
                g2d.fillOval((int) (newX / prevScale) - pointR0, (int) (newY / prevScale) - pointR0,
                        2 * pointR0, 2 * pointR0);
                g2d.translate(prevX, prevY);
                break;
            case 8:
                windowTitle = "let's place pointer to destination state";
                g2d.setColor(pointColor);
                g2d.fillOval(newX - pointRNew, newY - pointRNew, 2 * pointRNew, 2 * pointRNew);

                g2d.scale(newScale, newScale);
                g2d.translate(
                        prevX - newX / prevScale + newX / newScale,
                        prevY - newY / prevScale + newY / newScale
                );
                break;
            case 9:
                windowTitle = "and how it looks like with no scaling - to see pointer origin offset for destination state";
                g2d.setColor(pointColor);
                g2d.fillOval((int) (newX / newScale - pointR0), (int) (newY / newScale - pointR0),
                        2 * pointR0, 2 * pointR0);

                g2d.scale(newScale, newScale);
                g2d.translate(
                        prevX - newX / prevScale + newX / newScale,
                        prevY - newY / prevScale + newY / newScale
                );

                final AffineTransform transform = g2d.getTransform();
                final double translateX = transform.getTranslateX();
                final double translateY = transform.getTranslateY();
                g2d.translate(-translateX / newScale, -translateY / newScale);
                g2d.scale(1 / newScale, 1 / newScale);
                g2d.translate(translateX / newScale, translateY / newScale);
                break;
            case 10:
                windowTitle = "make initial state scaled by destination factor";
                g2d.scale(newScale, newScale);
                break;
            case 11:
                windowTitle = "and move it to proper place";

                g2d.scale(newScale, newScale);
                g2d.translate(
                        prevX - newX / prevScale + newX / newScale,
                        prevY - newY / prevScale + newY / newScale
                );
                break;
            default:
                windowTitle = "";
        }
        getFrame().setTitle(WINDOW_TITLE + "State " + state + ": " + windowTitle);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.add(new MouseMoveScaleDemo(frame));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1268, 790);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
