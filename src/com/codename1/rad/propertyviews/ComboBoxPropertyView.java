/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.propertyviews;

import com.codename1.rad.ui.PropertyView;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertyChangeEvent;
import com.codename1.ui.ComboBox;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import java.util.Objects;

/**
 * View for binding to {@link ComboBox} components.
 * @author shannah
 */
public class ComboBoxPropertyView extends PropertyView<ComboBox> {
    
    

    private ActionListener<PropertyChangeEvent> pcl = pce->{
        update();
    };
    
    private ActionListener dcl = evt ->{
        commit();
    };
    
    
    public ComboBoxPropertyView(ComboBox component, Entity entity, FieldNode field) {
        super(component, entity, field);
    }

    @Override
    public void bind() {
        getEntity().addPropertyChangeListener(getProperty(), pcl);
        getComponent().addActionListener(dcl);
    }

    @Override
    public void unbind() {
        getComponent().removeActionListener(dcl);
        getEntity().removePropertyChangeListener(getProperty(), pcl);
    }
    
    @Override
    public void update() {
        Object oldVal = getComponent().getSelectedItem();
        Object newVal = getProperty().getValue(getEntity());
        if (!Objects.equals(oldVal, newVal)) {
            getComponent().setSelectedItem(newVal);
        }
    }
    
    @Override
    public void commit() {
        
        getProperty().setValue(
                getEntity(), 
                getComponent().getSelectedItem()
        );
    }
    
}
