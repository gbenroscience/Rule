/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itis.app.interfaces;

import com.itis.app.models.Ruler;
import java.awt.Graphics2D;
import java.io.Serializable;

/**
 *
 * @author Gbemiro Jiboye
 */
public interface Sprite extends Serializable{
    
    
    public void draw(Ruler ruler, Graphics2D g);
    
    
    
    
    
}
