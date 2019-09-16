/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.util;

/**
 *
 * @author Imaxinacion
 */
public class Utils {
     /**
     * @param value The double value to be rounded
     * @param precision The precision.. e.g passing 1 means to 1 decimal place,
     * passing 2 means to 2 decimal places, etc.
     * @return the value rounded to the specified precision
     */
    public static final double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
    
}
