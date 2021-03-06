/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.controllers;

import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionNode.ActionNodeEvent;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base class for all Controller classes.
 * 
 * Each application should implement a single {@link ApplicationController} class which will be used as the app's main lifecycle class.  Forms should all have associated {@link FormController} classes.  
 * In some cases, for more complex views, you may also want to have a dedicated {@link ViewController} for the view also.  
 * 
 * == Controller Hierarchy
 * 
 * Similar to {@link com.codename1.ui.Component}, controllers have a hierarchy that is used for managing user navigation and event dispatch. 
 * A Controller may have a "parent" controller.  All events received by a Controller will propagate up to its parent controller if it isn't 
 * consumed.  Additionally, {@link FormController} views its "parent" controller as the previous form for navigation purposes.  E.g. If a {@link FormController}
 * has a parent controller that is also a {@link FormController}, then it will automatically add a "back" event to its form so that the user will return
 * to the "parent" controller's form when the user selects "back".
 * 
 * == Actions and Events
 * 
 * The primary mechanism for receiving notification about user actions is via Actions.  The controller defines the action, and the passes it to the view, associating it with
 * {@link ActionCategory}.  If the {@link EntitView} supports that {@link ActionCategory} it will fire an {@link ActionNode.ActionNodeEvent} event which the controller can process.
 * 
 * For example, the {@link com.codename1.rad.ui.entityviews.ProfileAvatarView} view supports the {@link com.codename1.rad.ui.entityviews.ProfileAvatarView#PROFILE_AVATAR_CLICKED} category
 * so a controller can register an action with that view as follows:
 * 
 * [source,java]
 * ----
 * public class MyViewController extends ViewController {
 *     public static final ActionNode showDetails = UI.action(icon(FontImage.MATERIAL_INFO)); <1>
 *     public MyViewController(Controller parent, Entity profile) {
 *         super(parent);
 *         setLayout(new BorderLayout());
 *         ProfileAvatarView view = new ProfileAvatarView(profile, new ViewNode(
 *             actions(ProfileAvatarView.PROFILE_AVATAR_CLICKED, showDetails) <2>
 *         ), 10);
 *         addActionListener(showDetails, evt->{ <3>
 *             evt.consume();
 * 
 *             new ProfileDetailsController(this, profile).getView().show(); <4>
 *         });
 *     }
 *        
 * }
 * ----
 * <1> We define the action.  It doesn't need be `static` or `public`.  It just needs to be addressable within the Controller.  I make it public and static so that
 * it is also easily accessible by other controllers that may want to listen for that action.
 * <2> We add the `showDetails` action to the ViewNode of the `ProfileAvatarView`'s `PROFILE_AVATAR_CLICKED category so that it knows to fire that action when users click on the avatar.
 * <3> We add a listener for the `showDetails` action so that we can handle the event where a user clicks on the avatar.
 * <4> This is a made up class `ProfileDetailsController`, but we assume it is a subclass of {@link FormController}, and it shows the details for a profile.
 * 
 * 
 * @author shannah
 */
public class Controller implements ActionListener<ControllerEvent> {
    private ViewNode node;
    private Controller parent;
    private Map<Class,Object> lookups;
    
    private static class ActionHandler {
        private ActionNode action;
        private ActionListener<ActionNodeEvent> handler;
        private ActionListener<ControllerEvent> wrapperListener;
        
    }
    
    private List<ActionHandler> actionHandlers = new ArrayList<>();
    
    
    
    private EventDispatcher listeners = new EventDispatcher();
    {
        listeners.addListener(this);
    }
    
    /**
     * Creates a controller with a given parent controller.
     * @param parent The parent controller of this controller.  
     */
    public Controller(Controller parent) {
        this.parent = parent;
    }
    
    /**
     * Adds a controller listener.  Controller listeners are notified of Controller
     * events that are dispatched using {@link #dispatchEvent(com.codename1.ui.controllers.ControllerEvent) }.
     * 
     * This is the means by which information propagates up the controller hierarchy from views and 
     * sub-controllers.
     * @param l 
     */
    public void addEventListener(ActionListener<ControllerEvent> l) {
        listeners.addListener(l);
    }
    
    public void removeEventListener(ActionListener<ControllerEvent> l) {
        listeners.removeListener(l);
    }
    
    private ActionHandler findActionHandler(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = null;
        action = (ActionNode)action.getCanonicalNode();
        for (ActionHandler candidate : actionHandlers) {
            if (candidate.action == action && candidate.handler == l) {
                return candidate;
            }
        }
        return null;
    }
    
    public void addActionListener(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = new ActionHandler();
        h.action = (ActionNode)action.getCanonicalNode();
        h.handler = l;
        h.wrapperListener = evt -> {
            if (ActionNode.getActionNodeEvent(evt, action) != null) {
                l.actionPerformed((ActionNodeEvent)evt);
            }
        };
        actionHandlers.add(h);
        addEventListener(h.wrapperListener);
        
    }
    
    public void removeActionListener(ActionNode action, ActionListener<ActionNodeEvent> l) {
        ActionHandler h = findActionHandler(action, l);
        if (h != null) {
            listeners.removeListener(h.wrapperListener);
            actionHandlers.remove(h);
        }
    }
    
    /**
     * Dispatches an event first to listeners of this controller, and then, if not consumed yet,
     * to listeners of the parent controller.  The event will propagate up the controller hierarchy
     * until it is either consumed, or until it reaches the top of the hierarchy (i.e. parent == null).
     * @param evt The event to be dispatched.
     */
    protected void dispatchEvent(ControllerEvent evt) {
        listeners.fireActionEvent(evt);
        if (!evt.isConsumed() && parent != null) {
            parent.dispatchEvent(evt);
        }
    }
    
   
    /**
     * Gets the parent controller for this controller.  All controllers except for the ApplicationController
     * should have a parent.  This controller hierarchy is used to keep a navigation history also.  For example,
     * the parent controller of a FormController is the "previous" form.  Hence the back command of a FormController
     * will go "back" to the parent controller's form.
     * @return 
     */
    public Controller getParent() {
        return parent;
    }
    
    /**
     * Should be overridden by subclasses to handle ControllerEvent s.  This is the
     * cornerstone of how information is passed "up" the controller hierarchy, from the view.  The view
     * or sub-controller, dispatches an event.  The event propagates up the controller hierarchy, until
     * a controller consumes the event.
     * @param evt 
     */
    @Override
    public void actionPerformed(ControllerEvent evt) {
        
    }
    
    /**
     * Gets the FormController for the current controller context. This will walk up the 
     * controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link FormController}.
     * If none is found, it returns null.
     * @return The FormController, or null if none found.
     */
    public FormController getFormController() {
        if (this instanceof FormController) {
            return (FormController)this;
        }
        if (parent != null) {
            return parent.getFormController();
        }
        return null;
    }
    
    /**
     * Gets the section controller for the current controller context.  This will walk up the 
     * controller hierarchy until it finds an instance of {@link AppSectionController}.
     * 
     * @return The AppSectionController, or null if none found.
     */
    public AppSectionController getSectionController() {
        if (this instanceof AppSectionController) {
            return (AppSectionController)this;
        }
        if (parent != null) {
            return parent.getSectionController();
        }
        return null;
        
    }
    
    /**
     * Gets the ApplicationController for the current controller context.  This will walk up
     * the controller hierarchy (i.e. {@link #getParent()} until it finds an instance of {@link ApplicationController}.
     * @return The ApplicationController or null if none found.
     */
    public ApplicationController getApplicationController() {
        if (this instanceof ApplicationController) {
            return (ApplicationController)this;
        }
        if (parent != null) {
            return parent.getApplicationController();
        }
        return null;
    }
    
    public void setParent(Controller parent) {
        this.parent = parent;
    }
    
    protected ViewNode createViewNode() {
        return new ViewNode();
    }
    
    public ViewNode getViewNode() {
        if (node == null) {
            node = createViewNode();
            ViewNode parentNode = null;
            if (parent != null) {
                parentNode = parent.getViewNode();
            }
            node.setParent(parentNode);
        }
        return node;
    }
    
    public <T> T lookup(Class<T> type) {
        if (lookups != null) {
            T out = (T)lookups.get(type);
            if (out != null) {
                return out;
            }
        }
        if (parent != null) {
            return parent.lookup(type);
        }
        return null;
    }
    
    public void addLookup(Object obj) {
        if (lookups == null) {
            lookups = new HashMap<>();
        }
        lookups.put(obj.getClass(), obj);
    }
    
    
}
