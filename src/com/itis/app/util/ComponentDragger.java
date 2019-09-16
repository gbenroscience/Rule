/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.util;

/**
 *
 * @author GBEMIRO
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Objects of this class have the ability to drag objects of class
 * java.awt.Window and javax.swing.JComponent.
 *
 * This will help application programmers write code that can windows that have
 * no title bar around and so on.
 *
 * @version 1.0
 * @since Mathron Version 1.0,2010
 * @author GBEMIRO
 */
public abstract class ComponentDragger {

    public static final int LEFT = 1;
    public static final int UP = 2;
    public static final int RIGHT = 3;
    public static final int DOWN = 4;

//The x-location of the point where the ,mouse is first pressed on the JComponent object to be used for the drag
    private int initMousePressedX;
//The y-location of the point where the ,mouse is first pressed on the JComponent object to be used for the drag
    private int initMousePressedY;
//The x-location of the top left corner of the top level parent of the JComponent.
    private int initXOnComponentTopParent;

//The y-location of the top left corner of the top level parent of the JComponent.
    private int initYOnComponentTopParent;

    private MouseWatch1 watch1;
    private MouseWatch2 watch2;
/**
 * 
 * @param direction The most prominent direction of drag. 
 * The parameter states the direction of drag.
 * It is one of:
 * <ol>
 * <li>{@link ComponentDragger#LEFT}</li>
 * <li>{@link ComponentDragger#UP}</li>
 * <li>{@link ComponentDragger#RIGHT}</li>
 * <li>{@link ComponentDragger#DOWN}</li>
 * </ol>
 * 
 */
    public abstract void onDrag(int direction);
    
    
    public String directionName(int direction){
        switch(direction){
            
            case LEFT:
                return "LEFT";
            case UP:
                return "UP";
            case RIGHT:
                return "RIGHT";
            case DOWN:
                return "DOWN";
            default:
                return "";
            
            
        }
    }

    /**
     * Call this constructor to drag any object of class Window around given
     * that it has a JComponent object attached to it.
     *
     * This constructor shall be called in association with a mousePressed event
     * attached to the object to be dragged.
     *
     *
     * @param comp the JComponent object attached to the window to be dragged.
     * @param evt the MouseEvent object
     *
     *
     */
    public ComponentDragger(final JComponent comp, MouseEvent evt) {

        Point initComponentTopParentLocation = comp.getTopLevelAncestor().getLocation();
        initXOnComponentTopParent = initComponentTopParentLocation.x;
        initYOnComponentTopParent = initComponentTopParentLocation.y;

        Point evtPoint = evt.getLocationOnScreen();

        initMousePressedX = evtPoint.x;
        initMousePressedY = evtPoint.y;

        watch1 = new MouseWatch1(comp);
        watch2 = new MouseWatch2(comp);

        comp.addMouseListener(watch1);
        comp.addMouseMotionListener(watch2);

    }//end constructor ComponentDragger(args)

    private class MouseWatch1 extends MouseAdapter {

        private JComponent comp;

        public MouseWatch1(JComponent comp) {
            this.comp = comp;
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            comp.removeMouseMotionListener(watch2);
            comp.removeMouseListener(this);
        }

    }

    private class MouseWatch2 extends MouseMotionAdapter {

        private JComponent comp;

        public MouseWatch2(JComponent comp) {
            this.comp = comp;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                Point p = e.getLocationOnScreen();
                int x = p.x;
                int y = p.y;

                int Sdx = initXOnComponentTopParent + (x - initMousePressedX);
                int Sdy = initYOnComponentTopParent + (y - initMousePressedY);
                
                int dx = (x - initMousePressedX);
                int dy = (y - initMousePressedY);
                

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) {
                        onDrag(RIGHT);
                    } else {
                        onDrag(LEFT);
                    }
                } else {
                    if (dy > 0) {
                        onDrag(DOWN);
                    } else {
                        onDrag(UP);
                    }
                }

                comp.getTopLevelAncestor().setLocation(Sdx, Sdy);

            } catch (NullPointerException nolian) {

            }

        }

    }

}//end class ComponentDragger
