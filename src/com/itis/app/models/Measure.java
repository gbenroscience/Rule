/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.models;

/**
 *
 * @author Gbemiro Jiboye
 * 
 * 
 * A Measure object gives the distance magnitude
 * in specified units between 0 and a certain point along the
 * ruler.
 * The object can mutate between various units of measurement of length.
 * For now, this is limited to pixels, inches and millimetres.
 */
public class Measure {

 
  public static double tenthOfInchToScreenPixels(double tenthInch) {
         return inchToScreenPixels(tenthInch/10.0);
    }
     public static double inchToScreenPixels(double inch) {
         return inch * Ruler.DPI;
    }
 
     public static double cmToScreenPixels(double cm) {
        return cm * Ruler.DPI/2.54;
    }
    
       public static double mmToScreenPixels(double mm) { 
           return mm * Ruler.DPI/25.4;
      }
      
      
    
    
    
    
      
}
