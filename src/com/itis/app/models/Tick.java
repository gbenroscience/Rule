/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.models;

import com.itis.app.interfaces.Sprite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Imaxinacion
 */
public class Tick implements Sprite {

    public static final int TICK_SHORT = 1;
    public static final int TICK_AVG = 2;
    public static final int TICK_LONG = 3;
    
     
    /**
     * May be one of:
     * <ol>
     * <li>{@link Tick#TICK_SHORT}</li>
     * <li>{@link Tick#TICK_AVG}</li>
     * <li>{@link Tick#TICK_LONG}</li>
     * </ol>
     *
     */
    private int tickType;
    /**
     * Store the unit length here
     */
    private int pixel;
    /**
     * Store the pixel length here.
     */
    private String label;

   /**
    * 
    * @param tickType
    * @param pixel Store the pixel length here
    * @param label Store the unit length here
    */
    public Tick(int tickType, int pixel, String label) {
        this.pixel = pixel;
        this.tickType = tickType;
        this.label = label;
    }

    public int getTickType() {
        return tickType;
    }
    
    
    
 
     

    @Override
    public void draw(Ruler r, Graphics2D g) {

        if (isTicksFromLeft()) {
            drawTickFromLeft(r, g);
        } else if (isTicksFromRight()) {
            drawTickFromRight(r, g);
        } else if (isTicksFromTop()) {
            drawTickFromTop(r, g);
        } else if (isTicksFromBottom()) {
            drawTickFromBottom(r, g);
        }

    }

    private void drawTickFromLeft(Ruler r, Graphics2D g) {

        BasicStroke s = new BasicStroke(thickness());

        g.setStroke(s);
        g.setColor(getColor());
        g.drawLine(0, pixel, (int) length(),  pixel);

        if (this.tickType == Tick.TICK_LONG) {
            g.drawString(label, (int) length() + Ruler.Config.TEXT_TICK_PADDING, pixel);  
        }

    }

    private void drawTickFromRight(Ruler r, Graphics2D g) {
        BasicStroke s = new BasicStroke(thickness());

        g.setStroke(s);
        g.setColor(getColor());
        g.drawLine(r.getWidth() - (int) length(),  pixel, r.getWidth(),  pixel);

        if (this.tickType == Tick.TICK_LONG) {
           int strWid = g.getFontMetrics().stringWidth(label);
            g.drawString(label, r.getWidth() - (int) length() -  Ruler.Config.TEXT_TICK_PADDING -  strWid, pixel);
        }
    }

    private void drawTickFromTop(Ruler r, Graphics2D g) {

        BasicStroke s = new BasicStroke(thickness());

        g.setStroke(s);
        g.setColor(getColor());
        g.drawLine( pixel, 0, pixel, (int) length());

        if (this.tickType == Tick.TICK_LONG) {
            g.drawString(label, pixel, (int) (length()*1.5) + Ruler.Config.TEXT_TICK_PADDING);
        }
    }

    private void drawTickFromBottom(Ruler r, Graphics2D g) {
        BasicStroke s = new BasicStroke(thickness());

        g.setStroke(s);
        g.setColor(getColor());
        g.drawLine( pixel, r.getHeight() - (int) length(), pixel, r.getHeight());

        if (this.tickType == Tick.TICK_LONG) {
           int strHei = g.getFontMetrics().getHeight();
            g.drawString(label, pixel, r.getHeight() - (int) length() - Ruler.Config.TEXT_TICK_PADDING );
        }
        

    }

    /**
     *
     * @return the length in pixels of the tick
     */
    public double length() {
        switch (this.tickType) {

            case Tick.TICK_SHORT:

                return Ruler.Config.TICK_LENGTH_SHORT;
            case Tick.TICK_AVG:

                return Ruler.Config.TICK_LENGTH_AVG;
            case Tick.TICK_LONG:

                return Ruler.Config.TICK_LENGTH_LONG;
            default:
                return Ruler.Config.TICK_LENGTH_SHORT;
        }

    }

    public float thickness() {
        return Ruler.Config.TICK_HEIGHT;
    }

    /**
     *
     * @return the side of the ruler that the tick should be drawn on... 1 means
     * draw starting from the left on a vertical ruler or from the top on a
     * horizontal ruler 2 means draw starting from the right on a vertical ruler
     * or from the bottom on a horizontal ruler
     *
     */
    public double direction() {
        return Ruler.Config.TICK_DIRECTION;
    }

    public Color getColor() {
        return Ruler.Config.COLOR_MODE == Ruler.AppearanceConstants.NIGHT ? Color.WHITE : Color.black;
    }

    /**
     *
     * @return the ruler's orientation
     */
    public int getRulerOrientation() {
        return Ruler.Config.ORIENTATION;
    }

    public boolean isVerticalRuler() {
        return Ruler.Config.ORIENTATION == Ruler.OrientationConstants.VERTICAL;
    }

    public boolean isHorizontalRuler() {
        return Ruler.Config.ORIENTATION == Ruler.OrientationConstants.HORIZONTAL;
    }

    public boolean isTicksFromLeft() {
        return isVerticalRuler() && Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.LEFT_OR_TOP;
    }

    public boolean isTicksFromRight() {
        return isVerticalRuler() && Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM;
    }

    public boolean isTicksFromTop() {
        return isHorizontalRuler() && Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.LEFT_OR_TOP;
    }

    public boolean isTicksFromBottom() {
        return isHorizontalRuler() && Ruler.Config.TICK_DIRECTION == Ruler.TickDirectionConstants.RIGHT_OR_BOTTOM;
    }

}
