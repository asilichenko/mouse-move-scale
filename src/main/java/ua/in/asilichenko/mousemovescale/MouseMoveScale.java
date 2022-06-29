package ua.in.asilichenko.mousemovescale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.RED;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 */
public class MouseMoveScale extends JPanel {

    static final String WINDOW_TITLE = "Moving and Scaling";

    private static final long PERIOD_60_FPS = 17;
    static final int GRID_CELL_SIZE = 50;
    private static final int GRID_RED_LINE = 5 * GRID_CELL_SIZE;

    private Color localZeroPointColor = Color.BLACK;
    private Color absoluteZeroPointColor = Color.GRAY;

    private Color rect1BorderColor = Color.lightGray;
    private Color rect1Color = new Color(255, 237, 22);
    private Rectangle2D.Float rect1 = new Rectangle2D.Float(GRID_CELL_SIZE, GRID_CELL_SIZE, GRID_CELL_SIZE, GRID_CELL_SIZE);

    private Color rect2Color = Color.BLUE.brighter().brighter();
    private Rectangle2D.Float rect2 = new Rectangle2D.Float(2 * GRID_CELL_SIZE, 2 * GRID_CELL_SIZE, GRID_CELL_SIZE, GRID_CELL_SIZE);

    private double zoom = 1;
    private int dragX = 0, dragY = 0;
    private boolean isDragging = false;

    private final JFrame frame;
    private AffineTransform prevTransform = new AffineTransform();

    MouseMoveScale(JFrame frame) {
        this.frame = frame;
        setDoubleBuffered(true);

        setupListeners();

        new Timer("repaint").schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, PERIOD_60_FPS);
    }

    void setupListeners() {
        final MovingAdapter movingAdapter = new MovingAdapter();
        addMouseMotionListener(movingAdapter);
        addMouseListener(movingAdapter);
        addMouseWheelListener(new ScaleHandler());
    }

    private void drawGrid(Graphics2D g2d) {
        // vertical
        IntStream.range(0, getWidth()).filter(x -> 0 == x % GRID_CELL_SIZE)
                .forEach(x -> drawLine(g2d, x, 0, x, getHeight()));
        // horizontal
        IntStream.range(0, getHeight()).filter(y -> 0 == y % GRID_CELL_SIZE)
                .forEach(y -> drawLine(g2d, 0, y, getWidth(), y));
    }

    private void drawLine(Graphics2D g2d, int xStart, int yStart, int xEnd, int yEnd) {
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor((0 == xStart && 0 == yStart % GRID_RED_LINE
                || 0 == yStart && 0 == xStart % GRID_RED_LINE) ? RED : LIGHT_GRAY
        );
        g2d.drawLine(xStart, yStart, xEnd, yEnd);
    }

    void applyTransformation(Graphics2D g2d) {
        final Point mousePosition = getMousePosition();
        if (null != mousePosition) {
            final int x = mousePosition.x, y = mousePosition.y;
            g2d.scale(zoom, zoom);
            g2d.translate(
                    // translateX(Y) are scaled X(Y)
                    (prevTransform.getTranslateX() - x) / prevTransform.getScaleX() + x / zoom,
                    (prevTransform.getTranslateY() - y) / prevTransform.getScaleY() + y / zoom);
            if (isDragging) g2d.translate(dragX / zoom, dragY / zoom);
            dragX = 0;
            dragY = 0;
        } else {
            g2d.setTransform(prevTransform);
        }
        prevTransform = g2d.getTransform();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawGrid(g2d);

        // Zero point
        g2d.setColor(absoluteZeroPointColor);
        g2d.fill(new Ellipse2D.Float(-10, -10, 20, 20));

        final AffineTransform saveT = g2d.getTransform();
        applyTransformation(g2d);

        g2d.setColor(rect1Color);
        g2d.fill(rect1);
        g2d.setColor(rect1BorderColor);
        g2d.draw(rect1);

        g2d.setColor(rect2Color);
        g2d.fill(rect2);

        g2d.setColor(localZeroPointColor);
        g2d.fill(new Ellipse2D.Float(-5, -5, 10, 10));

        g2d.setTransform(saveT);

        g.dispose();
    }

    class MovingAdapter extends MouseAdapter {

        private int pressedX;
        private int pressedY;
        private int buttonPressed;

        @Override
        public void mousePressed(MouseEvent e) {
            pressedX = e.getX();
            pressedY = e.getY();
            buttonPressed = e.getButton();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            dragX = e.getX() - pressedX;
            dragY = e.getY() - pressedY;
            pressedX = e.getX();
            pressedY = e.getY();

            isDragging = MouseEvent.BUTTON3 == buttonPressed; // Mouse2
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            isDragging = false;
        }
    }

    class ScaleHandler implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            final double wheelRotation = e.getPreciseWheelRotation();
            final double zoomScale = 1 + Math.abs(wheelRotation * 0.1);

            if (wheelRotation < 0) zoom /= zoomScale;
            else zoom *= zoomScale;
            if (zoom < 0.01) zoom = 0.01;

            repaint();
        }
    }

    JFrame getFrame() {
        return this.frame;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.add(new MouseMoveScale(frame));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1268, 790);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
