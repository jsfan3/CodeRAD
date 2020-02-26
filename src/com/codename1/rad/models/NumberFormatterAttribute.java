/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import com.codename1.rad.text.NumberFormatter;

/**
 * An attribute to specify a {@link NumberFormatter}.  This can be applied to any {@link com.codename1.rad.nodes.Node} as views that need it can use {@link com.codename1.rad.nodes.Node#findInheritedAttribute(java.lang.Class) } to retrieve the formatter.
 * 
 * @author shannah
 * @see com.codename1.rad.ui.UI#decimalFormat(int) 
 * @see com.codename1.rad.ui.UI#currencyFormat() 
 * @see com.codename1.rad.ui.UI#intFormat() 
 * @see com.codename1.rad.nodes.Node#getNumberFormatter() 
 */
public class NumberFormatterAttribute extends Attribute<NumberFormatter> {
    
    public NumberFormatterAttribute(NumberFormatter value) {
        super(value);
    }
    
}
