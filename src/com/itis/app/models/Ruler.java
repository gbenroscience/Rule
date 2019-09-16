/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.models;

import com.itis.app.util.ComponentDragger;
import com.itis.app.util.FileOps;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask; 
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow; 
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 *
 * @author Gbemiro Jiboye
 */
public class Ruler implements Runnable, Serializable {

    abstract class Select implements Serializable {

        Font f = new Font("Sans Serif", Font.BOLD, 10);
        String[] options;

       
        Point location = new Point();

        int selectedIndex = 0;

        private final int stroke = 2;

        public Select(String options[]) {
            this.options = options;
        }

        public Color getBorderColor() {
            return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.DAY ? Color.BLACK : Color.WHITE;
        }

        public Color getSelectedCellColor() {
            return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.DAY ? Color.BLACK : Color.WHITE;
        }

        public Color getDeselectedCellColor() {
            return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.DAY ? Color.WHITE : Color.BLACK;
        }

        public Color getSelectedTextColor() {
            return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.DAY ? Color.WHITE : Color.BLACK;
        }

        public Color getDeselectedTextColor() {
            return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.DAY ? Color.BLACK : Color.WHITE;
        }

        /**
         *
         * @param rectHei rectHei is the height of the select's rectangle. It is
         * twice the text height.
         * @return the y location of the main rectangle
         */
        public int getYLocation(int rectHei) {
            return (rulerHeight - rectHei) / 2; //
            //    return Ruler.Config.START_PADDING+Ruler.Config.START_MARGIN;
        }

        public void draw(Graphics2D g) {

            g.setFont(f);

            FontMetrics fm = g.getFontMetrics();

            int rectWidth = 0;
            int[] txtWidths = new int[options.length];

            int i = 0;
            for (String txt : options) {
                var w = fm.stringWidth(txt);
                rectWidth += w;
                txtWidths[i++] = w;
            }

            rectWidth *= 2;

            if (rectWidth >= rulerWidth - 2 * Ruler.Config.TICK_LENGTH_LONG) {
                rectWidth = rulerWidth - (int) (2 * Ruler.Config.TICK_LENGTH_LONG);
            }

            int avgCellWid = (int) ((1.0 * rectWidth) / (1.0 * options.length));

            int textHei = fm.getHeight();

            int rectHei = textHei * 2;

//            int rectX = (rulerSize.width - rectWidth) / 2;
//            int rectY = getYLocation(rectHei);
//            
            int rectX = location.x;
            int rectY = location.y;

            g.setColor(getBorderColor());

            g.setStroke(new BasicStroke(stroke));
            g.drawRect(rectX, rectY, rectWidth, rectHei);

            int x = 0;
            for (i = 0; i < txtWidths.length; i++) {
                int w = txtWidths[i];
                int dx = Math.abs((int) ((avgCellWid - w) * 0.5));

                x = rectX + i * avgCellWid;

                g.setColor(selectedIndex == i ? getSelectedCellColor() : getDeselectedCellColor());
                var rectInnerWidth = avgCellWid - 2 * stroke;
                var rectInnerHeight = rectHei - 2 * stroke;
                g.fillRect(x + stroke, rectY + stroke, rectInnerWidth, rectInnerHeight);
                g.setColor(selectedIndex == i ? getSelectedTextColor() : getDeselectedTextColor());
                if (i != txtWidths.length - 1) {
                    g.drawLine(x + stroke + rectInnerWidth, rectY + stroke, x + stroke + rectInnerWidth, rectY + rectInnerHeight);
                }
                g.drawString(options[i], x + dx, rectY + (5 * (rectHei - textHei) / 4));

            }

        }

        public Rectangle getBounds() {
            BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) b.getGraphics();

            FontMetrics fm = g.getFontMetrics();

            int rectWidth = 0;
            int[] txtWidths = new int[options.length];

            int i = 0;
            for (String txt : options) {
                var w = fm.stringWidth(txt);
                rectWidth += w;
                txtWidths[i++] = w;
            }

            rectWidth *= 2;

            int textHei = fm.getHeight();

            int rectHei = textHei * 2;

            int rectX = location.x;
            int rectY = location.y;

            return new Rectangle(rectX, rectY, rectWidth, rectHei);
        }

        public Rectangle getBoundsFor(int index) {
            Rectangle mainRect = getBounds();
            BufferedImage b = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) b.getGraphics();

            int avgCellWid = (int) (((double) mainRect.width) / ((double) options.length));

            avgCellWid = avgCellWid - 2 * stroke;
            int cellHeight = mainRect.height - 2 * stroke;

            int x = mainRect.x + (index + 1) * stroke + index * avgCellWid;
            int y = mainRect.y + stroke;

            return new Rectangle(x, y, avgCellWid, cellHeight);

        }

        public boolean processSelection(Point p) {
            for (int i = 0; i < options.length; i++) {
                if (getBoundsFor(i).contains(p)) {
                    this.selectedIndex = i;
                    onSelect(i);
                    return true;
                }
            }
            return false;
        }

        public boolean clickedIndexAt(Point p, int index) {
            return getBoundsFor(index).contains(p);
        }

        public abstract void onSelect(int index);

    }

    public static final class UnitConstants {

        public static final int UNIT_PIXEL = 1;
        public static final int UNIT_INCH = 2;
        public static final int UNIT_CM = 3;

    }

    public static final class OrientationConstants {

        public static final int HORIZONTAL = 1;
        public static final int VERTICAL = 2;

    }

    /**
     * States what side of the ruler the tick will be drawn on... LEFT, RIGHT
     * (for vertical rulers), TOP or BOTTOM(for horizontal rulers)
     */
    public static final class TickDirectionConstants {

        public static final int LEFT_OR_TOP = 1;
        public static final int RIGHT_OR_BOTTOM = 2;

    }

    public static final class AppearanceConstants {
        public static final int DAY = 1;
        public static final int NIGHT = 2;
    }

    public static final class Config {

        public static int ORIENTATION = OrientationConstants.VERTICAL;
        public static int TICK_DIRECTION = TickDirectionConstants.RIGHT_OR_BOTTOM;// thickness of ticks in pixels

        public static int COLOR_MODE = AppearanceConstants.NIGHT;

        public static int TICK_LENGTH_SHORT = 10;// in pixels
        public static int TICK_LENGTH_AVG = 15;// in pixels 
        public static int TICK_LENGTH_LONG = 20;// in pixels 

        public static float TICK_HEIGHT = 1f;// thickness of ticks in pixels

        /**
         * The ruler's width when vertical and the ruler's height when
         * horizontal
         */
        public static int RULER_SIZE = 180;

        /**
         * The distance between text and the tick it describes
         */
        public static int TEXT_TICK_PADDING = 4;

        /**
         * The update rate for the ruler's graphics
         */
        public static int RULER_REFRESH_RATE_MS = 500;

        public static int UNITS = UnitConstants.UNIT_CM;

        public static int START_PADDING = 8;

        public static int START_MARGIN = 8;

        public static final Color BTN_BORDER_DARK_MODE = Color.BLACK;
        public static final Color BTN_BORDER_DAY_MODE = Color.WHITE;

        public static final Color SELECTED_BG_DARK_MODE = Color.BLACK;
        public static final Color SELECTED_BG_DAY_MODE = new Color(0, 0, 0, 100);

        public static final Color DESELECTED_BG_DARK_MODE = Color.BLACK;
        public static final Color DESELECTED_BG_DAY_MODE = new Color(0, 0, 0, 100);

        public static final Color SELECTED_COLOR_DARK_MODE = Color.BLACK;
        public static final Color SELECTED_COLOR_DAY_MODE = Color.BLACK;

        public static final Color DESELECTED_COLOR_DARK_MODE = Color.WHITE;
        public static final Color DESELECTED_COLOR_DAY_MODE = Color.WHITE;

    }

    private transient JPanel panel;
    private transient Thread timer;
    private transient BufferedImage image;
    int screenWidth, screenHeight;

    private transient int rulerWidth ,  rulerHeight; 

    private transient List<Tick> ticks = new ArrayList();

    private transient Select unitSelect;
    private transient Select modeSelect;
    private transient Select orientationSelect;

    /**
     *
     * Stores how many dots represent an inch on this screen. // (MM * DPI)/25.4
     *
     */
    public static final int DPI = Toolkit.getDefaultToolkit().getScreenResolution();

    public Ruler() {

        init();
        show();
    }

    private void init() {

        ticks = new ArrayList<>();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth = screenSize.width;
        this.screenHeight = screenSize.height;
        image = null;
        getWidth();
        getHeight();
        ticksLogic();
    }

    private void initSelects() {
        unitSelect = new Select(new String[]{"CM", "IN", "PX"}) {
            @Override
            public void onSelect(int index) {
                switch (index) {

                    case 0:
                        Ruler.Config.UNITS = Ruler.UnitConstants.UNIT_CM;
                        break;
                    case 1:
                        Ruler.Config.UNITS = Ruler.UnitConstants.UNIT_INCH;
                        break;
                    case 2:
                        Ruler.Config.UNITS = Ruler.UnitConstants.UNIT_PIXEL;
                        break;
                    default:

                        break;
                }
                init();
            }
        };

        modeSelect = new Select(new String[]{"DAY", "NGT"}) {
            @Override
            public void onSelect(int index) {
                switch (index) {

                    case 0:
                        Ruler.Config.COLOR_MODE = Ruler.AppearanceConstants.DAY;
                        break;
                    case 1:
                        Ruler.Config.COLOR_MODE = Ruler.AppearanceConstants.NIGHT;
                        break;
                    default:

                        break;
                }
                init();
            }
        };
        orientationSelect = new Select(new String[]{"HOR", "VER"}) {
            @Override
            public void onSelect(int index) {
                ticks.clear();
                switch (index) {

                    case 0:
                        flipHorizontal();
                        break;
                    case 1:
                        flipVertical();
                        break;
                    default:

                        break;
                }
                init();
            }
        };

        
        
        
        TimerTask t = new TimerTask() {
            @Override
            public void run() {

                switch (Ruler.Config.UNITS) {

                    case UnitConstants.UNIT_CM:
                        unitSelect.selectedIndex = 0;

                        unitSelect.onSelect(0);
                        break;
                    case UnitConstants.UNIT_INCH:
                        unitSelect.selectedIndex = 1;
                        unitSelect.onSelect(1);
                        break;
                    case UnitConstants.UNIT_PIXEL:
                        unitSelect.selectedIndex = 2;
                        unitSelect.onSelect(2);
                        break;

                    default:
                        break;
                }

                switch (Ruler.Config.ORIENTATION) {

                    case OrientationConstants.HORIZONTAL:
                        orientationSelect.selectedIndex = 0;
                        orientationSelect.onSelect(0);
                        break;
                    case OrientationConstants.VERTICAL:
                        orientationSelect.selectedIndex = 1;
                        orientationSelect.onSelect(1);
                        break;

                    default:
                        break;
                }

                switch (Ruler.Config.COLOR_MODE) {

                    case AppearanceConstants.DAY:
                        modeSelect.selectedIndex = 0;
                        modeSelect.onSelect(0);
                        break;
                    case AppearanceConstants.NIGHT:
                        modeSelect.selectedIndex = 1;
                        modeSelect.onSelect(1);
                        break;

                    default:
                        break;
                }
            }
        };
        
        java.util.Timer tim= new java.util.Timer();
        tim.schedule(t, 500); 
        
        
        
        

    }

    private void layoutSelects() {
        if (unitSelect == null || modeSelect == null || orientationSelect == null) {
            initSelects();
        }
        if (isVerticalRuler()) {

            if (Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.LEFT_OR_TOP) {
                Rectangle bounds = unitSelect.getBounds();
                unitSelect.location.x = Ruler.Config.RULER_SIZE - bounds.width - Ruler.Config.START_PADDING;
                unitSelect.location.y = Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN;

                modeSelect.location.x = unitSelect.location.x;
                modeSelect.location.y = 2 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.height;

                Rectangle bounds1 = modeSelect.getBounds();
                orientationSelect.location.x = unitSelect.location.x;
                orientationSelect.location.y = 3 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.height + bounds1.height;

            } else if (Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM) {
                Rectangle bounds = unitSelect.getBounds();
                unitSelect.location.x = Ruler.Config.START_PADDING;
                unitSelect.location.y = Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN;

                modeSelect.location.x = unitSelect.location.x;
                modeSelect.location.y = 2 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.height;

                Rectangle bounds1 = modeSelect.getBounds();
                orientationSelect.location.x = unitSelect.location.x;
                orientationSelect.location.y = 3 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.height + bounds1.height;

            }

        } else {

            if (Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.LEFT_OR_TOP) {
                Rectangle bounds = unitSelect.getBounds();
                unitSelect.location.x = Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN;
                unitSelect.location.y = Ruler.Config.RULER_SIZE - bounds.height - Ruler.Config.START_PADDING;

                modeSelect.location.x = 2 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.width;
                modeSelect.location.y = unitSelect.location.y;

                Rectangle bounds1 = modeSelect.getBounds();
                orientationSelect.location.x = 3 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.width + bounds1.width;
                orientationSelect.location.y = unitSelect.location.y;
            } else if (Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM) {
                Rectangle bounds = unitSelect.getBounds();
                unitSelect.location.x = Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN;
                unitSelect.location.y = Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN;

                modeSelect.location.x = 2 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.width;
                modeSelect.location.y = unitSelect.location.y;

                Rectangle bounds1 = modeSelect.getBounds();
                orientationSelect.location.x = 3 * (Ruler.Config.START_PADDING + Ruler.Config.START_MARGIN) + bounds.width + bounds1.width;
                orientationSelect.location.y = unitSelect.location.y;

            }

        }
    }

    private double autoGetLength(int size) {

        if (Ruler.Config.UNITS == UnitConstants.UNIT_INCH) {
            return Measure.tenthOfInchToScreenPixels(size);
        }
        if (Ruler.Config.UNITS == UnitConstants.UNIT_CM) {
            return Measure.mmToScreenPixels(size);
        }
        if (Ruler.Config.UNITS == UnitConstants.UNIT_PIXEL) {
            return size;
        }

        return -1;
    }
    
    public void onRulerInitialized(){
        layoutSelects();
        repaint(panel);
    }

    private void ticksLogic() {

        ticks.clear();
        int ctr = 0;
boolean isVert = Ruler.Config.ORIENTATION == OrientationConstants.VERTICAL;


int rulerScreenLen = isVert ? screenHeight - Config.START_PADDING : screenWidth - Config.START_PADDING;

        double relevantDimension =  autoGetLength(rulerScreenLen);

        int totalDots = (int) (relevantDimension / autoGetLength(1));

        var firstTick = new Tick(Tick.TICK_LONG, Config.START_PADDING + Config.START_MARGIN, "0");
        ticks.add(firstTick);

        while (totalDots-- > 0) {

            int pixelSize = (int) autoGetLength(++ctr) + Config.START_PADDING + Config.START_MARGIN;

            String label = String.valueOf(ctr);
            if (ctr == 0) {
                var tick = new Tick(Tick.TICK_AVG, pixelSize, label);
                ticks.add(tick);
            } else if (ctr % 10 == 0) {
                var tick = new Tick(Tick.TICK_LONG, pixelSize, label);
                ticks.add(tick);
            } else if (ctr % 5 == 0) {
                var tick = new Tick(Tick.TICK_AVG, pixelSize, label);
                ticks.add(tick);
            } else {
                var tick = new Tick(Tick.TICK_SHORT, pixelSize, label);
                ticks.add(tick);
            }
           
          if(pixelSize > rulerScreenLen){
                break;
            }  
          

        }

       // System.out.println("totalDots: " + relevantDimension + " , ticks: " + ticks.size());

    }

    public int getWidth() {
        if (isHorizontalRuler()) {
            rulerWidth = screenWidth;
            rulerHeight = Ruler.Config.RULER_SIZE;
        }
        if (isVerticalRuler()) {
            rulerWidth = Ruler.Config.RULER_SIZE;
            rulerHeight = screenHeight;
        }
        return rulerWidth;
    }
    
       public int getHeight() {
        if (isHorizontalRuler()) {
            rulerWidth = screenWidth;
            rulerHeight = Ruler.Config.RULER_SIZE;
        }
        if (isVerticalRuler()) {
            rulerWidth = Ruler.Config.RULER_SIZE;
            rulerHeight = screenHeight;
        }
        return rulerHeight;
    }

    public boolean isVerticalRuler() {
        return Ruler.Config.ORIENTATION == Ruler.OrientationConstants.VERTICAL;
    }

    public boolean isHorizontalRuler() {
        return Ruler.Config.ORIENTATION == Ruler.OrientationConstants.HORIZONTAL;
    }

    private final void setup() {

        init();
        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Ruler.this.draw();
                g.drawImage(Ruler.this.image, 0, 0, Ruler.this.rulerWidth, Ruler.this.rulerHeight, new Color(255, 255, 255, 0), this);

            }
        };

        panel.setSize(Ruler.this.getWidth(), Ruler.this.getHeight());
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);

        applyLookAndFeel();

        panel.addMouseListener(new MouseAdapter() {
            private long clickStartDate;

            private long lastClickedDateBeforeThisSetOfClicks;

            private ArrayList<Integer> clicks = new ArrayList<>();

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point p = e.getPoint();

                if (unitSelect.processSelection(p) || modeSelect.processSelection(p) || orientationSelect.processSelection(p)) {
                    repaint(panel);
                    return;
                }

                clickStartDate = new Date().getTime();

                new ComponentDragger(panel, e){
                    @Override
                    public void onDrag(int direction) {
                        boolean changed = false;
                       switch(direction){
                           
                           case ComponentDragger.LEFT:
                               if(Ruler.Config.ORIENTATION == Ruler.OrientationConstants.VERTICAL){
                                   if(Ruler.Config.TICK_DIRECTION != Ruler.TickDirectionConstants.LEFT_OR_TOP){
                                       Ruler.Config.TICK_DIRECTION = Ruler.TickDirectionConstants.LEFT_OR_TOP;
                                       changed = true;
                                   }
                               }
                               break;
                           case ComponentDragger.RIGHT:
                             if(Ruler.Config.ORIENTATION == Ruler.OrientationConstants.VERTICAL){
                                   if(Ruler.Config.TICK_DIRECTION != Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM){
                                       Ruler.Config.TICK_DIRECTION = Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM;
                                       changed = true;
                                   }
                               }   
                               break;
                           case ComponentDragger.UP:
                             if(Ruler.Config.ORIENTATION == Ruler.OrientationConstants.HORIZONTAL){
                                   if(Ruler.Config.TICK_DIRECTION != Ruler.TickDirectionConstants.LEFT_OR_TOP){
                                       Ruler.Config.TICK_DIRECTION = Ruler.TickDirectionConstants.LEFT_OR_TOP;
                                       changed = true;
                                   }
                               }       
                               break;
                           case ComponentDragger.DOWN:
                              if(Ruler.Config.ORIENTATION == Ruler.OrientationConstants.HORIZONTAL){
                                   if(Ruler.Config.TICK_DIRECTION != Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM){
                                       Ruler.Config.TICK_DIRECTION = Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM;
                                       changed = true;
                                   }
                               }      
                               break;
                           default:
                               
                               break;
                           
                           
                           
                       }
                       if(changed){
                       initSelects();
                       layoutSelects();
                        repaint(panel);
                       }
                    }
                
                };
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

//To change body of generated methods, choose Tools | Templates.
                /**
                 * Check that the last click does not belong to another set of
                 * clicks.
                 */
                if (new Date().getTime() - lastClickedDateBeforeThisSetOfClicks > 1300 && !clicks.isEmpty()) {
                    clicks.remove(0);
                    this.lastClickedDateBeforeThisSetOfClicks = clickStartDate;
                }

                if (new Date().getTime() - clickStartDate <= 500) {
                    clicks.add(1);
                    this.lastClickedDateBeforeThisSetOfClicks = clickStartDate;
                } else {
                    clickStartDate = 0;
                }

                if (clicks.size() == 2) {

                    JWindow window = (JWindow) panel.getTopLevelAncestor();
                    JFrame f = new JFrame();
                    f.setAlwaysOnTop(true);
                    int choice = JOptionPane.showConfirmDialog(f, "1. Choose `Yes` to open Settings\n2. Choose `No` to exit this APP\n"
                            + "3. Choose `Cancel` to continue using this app.", "OPTIONS", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    switch (choice) {

                        case JOptionPane.YES_OPTION:
                            openSettings();
                            break;
                        case JOptionPane.NO_OPTION:
                            window.dispose();
                            Runtime.getRuntime().exit(0);
                            break;
                        case JOptionPane.CANCEL_OPTION:

                            break;
                    }

                    clicks.clear();

                }

            }

        });
       
        timer = new Thread(this);
        timer.start();
    }

    public void draw() {

        Dimension sz = new Dimension(Ruler.this.getWidth(),Ruler.this.getHeight());
        if (image == null) {
            image = new BufferedImage(sz.width, sz.height, BufferedImage.TYPE_INT_RGB);
        }

        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(getColor());
        g.fill3DRect(0, 0, sz.width, sz.height, true);

        if (isVerticalRuler()) {
            g.setColor(getHeaderColor());
            g.fill3DRect(0, 0, sz.width, Config.START_PADDING, true);
        }

        if (isHorizontalRuler()) {
            g.setColor(getHeaderColor());
            g.fill3DRect(0, 0, Config.START_PADDING, sz.width, true);
        }
if(unitSelect != null && modeSelect != null && orientationSelect != null){
        unitSelect.draw(g);
        modeSelect.draw(g);
        orientationSelect.draw(g);
}

        for (var t : ticks) {
            if (t.getTickType() == Tick.TICK_SHORT && Ruler.Config.UNITS == Ruler.UnitConstants.UNIT_PIXEL) {
                continue;
            }
            t.draw(this, g);
        }

    }

    public void save() {
        new FileOps().write(Ruler.this);
    }

    private void openSettings() {

    }

    public Color getColor() {
        return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.NIGHT ? Color.BLACK : Color.WHITE;
    }

    public Color getHeaderColor() {
        return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.NIGHT ? Color.LIGHT_GRAY : Color.DARK_GRAY;
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Ruler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    int count = 0;

    @Override
    public void run() {

        while (true) {
            try {

                Thread.sleep(Ruler.Config.RULER_REFRESH_RATE_MS);
       //         panel.repaint();
            } catch (InterruptedException e) {
            }
        }

    }//end method run.

    public void repaint(JPanel panel) {
        draw();
        panel.repaint();
    }

    public void show() {

        setup();
        final JWindow window = new JWindow();

        repaint(panel);
        window.setOpacity(1);

        window.addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                window.setShape(new Ellipse2D.Double(0, 0, window.getWidth(), window.getHeight()));
                
                Dimension sz = new Dimension(Ruler.this.getWidth(),Ruler.this.getHeight());
                window.setShape(new Rectangle(0, 0, sz.width, sz.height));
                window.add(panel);
                window.setLocation((screenWidth - window.getSize().width) / 2, (screenHeight - window.getSize().height) / 2);
                window.setVisible(true);
                 onRulerInitialized();
            }
        });

        window.setSize(panel.getSize());

        window.setAlwaysOnTop(true);

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Ruler.this.save();
            }
        });
        init();

    }

    public void flipHorizontal() {
        Config.ORIENTATION = OrientationConstants.HORIZONTAL;
       
        Dimension sz = new Dimension(Ruler.this.getWidth(),Ruler.this.getHeight());
        panel.setSize(sz);
        panel.getTopLevelAncestor().setSize(sz);
        init();
        repaint(panel);
    }

    public void flipVertical() {
        Config.ORIENTATION = OrientationConstants.VERTICAL;
        
        Dimension sz = new Dimension(Ruler.this.getWidth(),Ruler.this.getHeight());
        panel.setSize(sz);
        panel.getTopLevelAncestor().setSize(sz);

        init();
        repaint(panel);

    }

    public void toggleOrientation() {
        if (Config.ORIENTATION == OrientationConstants.HORIZONTAL) {
            flipVertical();
        } else {
            flipHorizontal();
        }
    }

    public static void main(String[] args) {

        FileOps fileOps = new FileOps();

        Ruler r = fileOps.read();

        if (r == null) {
            System.out.println("Couldn't load ruler");

            r = new Ruler();

            r.save();

        }
    }
}
