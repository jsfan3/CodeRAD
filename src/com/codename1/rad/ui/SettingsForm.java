/*
 * Copyright 2020 Codename One.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.ui;

import com.codename1.components.SpanLabel;
import com.codename1.rad.attributes.PropertySelectorAttribute;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.attributes.UIIDPrefix;
import com.codename1.rad.attributes.WidgetType;
import com.codename1.rad.controllers.FieldEditorFormController;
import com.codename1.rad.controllers.ViewController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property.Description;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.SectionNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 * A settings form similar to the iOS and Android "Settings" applications.  
 * 
 * .Sample settings form
 * image::https://media.giphy.com/media/LnjBVgjdaHHiVMHSXi/giphy.gif[]
 * 
 * == Example
 * 
[source,java]
----
package com.codename1.samples;

import static com.codename1.ui.CN.*;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.models.BooleanProperty;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import static com.codename1.rad.models.EntityType.description;
import static com.codename1.rad.models.EntityType.tags;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Person;
import com.codename1.rad.schemas.PostalAddress;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.SettingsForm;
import static com.codename1.rad.ui.UI.*;
import com.codename1.ui.Button;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListModel;


/**
* * This sample demonstrates the use of the SettingsForm class edit properties of an Entity, using
* * the CodeRAD cn1lib.
* * *{slash}
public class SettingsFormSample {
    
    /**
*     * Define the actions to include in the settings form
*     * *{slash}
    public static final ActionNode 
            
            /**
 *            * A username field.
 *            *{slash}
            username = action(
                label("Username"),
                    
                /**
 *                * By adding a property to the action, the SettingsForm will
 *                * display the property value on the action.
 *                *{slash}
                property(entity->{
                    return new PropertySelector(entity, Thing.identifier);
                }),
                
                /**
 *                * We make the username action editable by adding a text field
 *                *{slash}
                textField(
                        label("Enter Username"),
                        description("Please enter a new username below"),
                        tags(Thing.identifier)
                )
            ),
            
            /**
*             * A sample "switch" widget
*             *{slash}
            qualityFilter = action(
                    label("Quality filter"),
                    
                    /**
*                     * This will be rendered as a toggle switch in the settings form
*                     * because we include the toggleSwitch() node here.
*                     *{slash}
                    toggleSwitch(
                        /**
*                         * The property that the toggle switch should be bound to.
*                         *{slash}
                        property(UserProfile.qualityFilter),
                        description("Filter lower-quality content from your notifications. This won't filter out notifications from people you follow or accounts you've interacted with recently.")
                    )
            ),
            
            /**
*             * A phone action.  In this sample we bind this to the "telephone" property,
*             * but we don't make it editable directly.  The intention is that we'll 
*             * add a custom action listener for this action and handle it in our own way.
*             *{slash}
            phone = action(
                    label("Phone"),
                    
                    /**
*                     * Display the telephone property on the menu item.
*                     *{slash}
                    tags(Person.telephone)
            ),
            email = action(
                    label("Email"),
                    tags(Person.email)
            ),
            password = action(
                    label("Password")
            ),
            
            /**
*             * An action to display and change the "country" of the entity.
*             *{slash}
            country = action(
                    label("Country"),
                    
                    /**
*                     * Use a radio list with BoxLayout.Y layout to change the country
*                     * selection.
*                     *{slash}
                    radioListY(
                        label("Select Country"),
                        description("Please select a country from the list below"),
                        options(new DefaultListModel("Canada", "United States", "Mexico", "Spain", "England", "France")),
                        tags(PostalAddress.addressCountry)
                    )
            ),
            yourTwitterData = action(
                    label("Your twitter data")
            ),
            security = action(
                    label("Security")
            ),
            deactivate = action(
                    label("Deactivate your account")
            );
    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        Form f = new Form(BoxLayout.y());
        Button b = new Button("Open Settings");
        b.addActionListener(e->{
            new SettingsFormController().getView().show();
        });
        f.add(b);
        f.show();
        
    }

    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }
    
    public class SettingsFormController extends FormController {
        
        public SettingsFormController() {
            super(null);
            // Create the view model
            UserProfile profile = new UserProfile();
            
            // Create the view
            SettingsForm view = new SettingsForm(profile, getViewNode());
        
            // Set the title
            setTitle("Settings and privacy");

            // Set the view.  This will be wrapped in a form.
            setView(view);
        }
        
        /**
*         * This method creates the View node that is used for building views with this
*         * controller.  The view node we build here is designed to be used by the 
*         * SettingsForm view to generate a settings form.  The SettingsForm expects 
*         * a list of SectionNodes, each with its only list of FieldNodes.
*         * @return 
*         *{slash}
        @Override
        protected ViewNode createViewNode() {
            return new ViewNode(
                    section(
                            label("Login and Security"),
                            username, phone, email, password, qualityFilter
                    ),
                    section(
                            label("Data and permissions"),
                            country, yourTwitterData, security
                    ),
                    section(
                            deactivate
                    )
            );
        }
        
    }
 
    /**
*     * A dummy entity we use for this sample.  Represents a user profile.
*     *{slash}
    public static class UserProfile extends Entity {
        public static BooleanProperty qualityFilter;
        private static final EntityType TYPE = new EntityType() {{
            
            /**
*             * The "name" field.
*             *{slash}
            string(tags(Thing.name));
            
            /**
*             * The "id" field
*             *{slash}
            string(tags(Thing.identifier));
            
            /**
*             * The thumbnailUrl field
*             *{slash}
            string(tags(Thing.thumbnailUrl));
            
            /**
*             * The "country" field.
*             *{slash}
            string(tags(PostalAddress.addressCountry));
            
            /**
*             * The qualityFilter field.  We don't use tags on this one because 
*             * this property doesn't have any corresponding generic tag that would
*             * apply.
*             *{slash}
            qualityFilter = Boolean();
        }};
        {
            /**
*             * For entities we always need to set the entity type in the initializer.
*             *{slash}
            setEntityType(TYPE);
        }
    }

}
----
* 
* == {@link ViewNode} Format
* 
* The view node used to build the settings form should contain zero or more {@link SectionNode} children, which
* will be rendered as sections of the settings form using {@link SettingsFormSection}.  Each section should include
* one or more {@link ActionNode} child nodes, which will be rendered as {@link SettingsFormActionView}.
* 
* See the docs for {@link SettingsFormActionView} for details on the expected format of each {@link ActionNode}.
* 
* The complete example above uses the following view node:
* 
* [source,java]
* ----
* new ViewNode(
        section(
                label("Login and Security"),
                username, phone, email, password, qualityFilter
        ),
        section(
                label("Data and permissions"),
                country, yourTwitterData, security
        ),
        section(
                deactivate
        )
  );
* ----
* 
* In this example, the settings form is split into 30 sections, "Login and Security", "Data and Permissions", and a last section that has no heading.
* 
* == Styles
* 
* . `SettingsForm` - UIID for the SettingForm component itself.
* 
* See {@link SettingsFormSection} and {@link SettingsFormActionView} documentation for styles used in the subcomponents.
* 
* === UIID Prefixing
* 
* If you add the {@link UIIDPrefix} attribute to the ViewNode, that prefix will be added to all UIIDs in this component and subcomponents.
* 
* 
 * @author shannah
 */
public class SettingsForm extends AbstractEntityView implements WidgetTypes {
    private ViewNode node;
    
    /**
     * Creates a new SettingsForm to edit properties of the given entity.
     * 
     * 
     * 
     * @param entity The entity to be edited by this settings form.
     * @param node The node defining the sections and fields in this form.  
     */
    public SettingsForm(Entity entity, ViewNode node) {
        super(entity);
        this.node = node;
        node.setAttributesIfNotExists(UI.viewFactory(new SettingsFormActionViewFactory()));
        String uiidPrefix = node.getUIIDPrefix("");
        String uiid = node.getUIID("SettingsForm");
        
        setUIID(uiidPrefix + uiid);
        $(this).addTags("SettingsForm");
        setLayout(BoxLayout.y());
        setScrollableY(true);
        NodeList l = node.getChildNodes(SectionNode.class);
        for (Node n : l) {
            SectionNode sn = (SectionNode)n;
            add(new SettingsFormSection(entity, sn));
        }
        
    }

    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }
    
    /**
     * Container used to render a section of {@link SettingsForm}.  Generally you wouldn't use this view directly.  It is
     * just used by {@link SettingsForm}.  This view uses a {@link SectionNode} for its configuration.  The {@link SectionNode}
     * is expected to contain {@link ActionNode} child nodes, which will each be rendered as {@link SettingsFormActionView}.  If there
     * is a {@link com.codename1.rad.models.Property.Label} attribute, it will be rendered as a section heading.
     * 
     * == Styles
     * 
     * . `SettingsFormSection` - The UIID for the SettingsFormSection component itself.
     * . `SettingsFormSectionLabel` - The UIID for the section label.
     * 
     * == UIID Prefixing
     * 
     * If the section node or its ancestors contains a {@link UIIDPrefix} attribute, it will prefix
     * all UIIDs with the given prefix.
     * 
     * 
     */
    public static class SettingsFormSection extends Container {
        public SettingsFormSection(Entity entity, SectionNode section) {
            super(BoxLayout.y());
            String uiidPrefix = section.getUIIDPrefix("");
            String uiid = section.getUIID("SettingsFormSection");
            
            setUIID(uiidPrefix + uiid);
            $(this).addTags("SettingsFormSection", "left-edge");
            com.codename1.rad.models.Property.Label l = section.getLabel();
            if (l != null) {
                Label lbl = new Label(l.getValue(entity), uiidPrefix+uiid+"Label");
                $(lbl).addTags("left-inset");
                add(lbl);
            }
            for (Node n : section.getChildNodes(ActionNode.class)) {
                ActionNode an = (ActionNode)n;
                if (!an.isEnabled(entity)) {
                    continue;
                }
                add(an.createView(entity));
            }
        }
    }
    
    /**
     * A view for rendering an {@link ActionNode} as a menu item in {@link SettingsForm}.  You typically wouldn't use this class
     * directly.  You would use {@link SettingsForm}, which sets {@link SettingsFormActionViewFactory} as the {@link ActionViewFactory}
     * for its actions, which renders actions using this class.  The following documentation is still useful for customizing the look 
     * and behaviour of the {@link SettingsForm} items, since the {@link ActionNode} and its {@link Attribute}s can affect how
     * this view is rendered.
     * 
     * .A sample rendering of an action
     * image::https://i.imgur.com/ipsG5kr.png[]
     * 
     * .A basic example that would render the image shown above.
     * [source,java]
     * ----
     * new SettingsFormActionView(new MyEntity(), action(label("Phone")));
     * ----
     * 
     * NOTE: For all code examples on this page we have included a static import of {@link com.codename1.rad.ui.UI} as it includes
     * many convenience static methods for creating attributes and notes of particular types.  Common methods you'll see here are {@link UI#action(com.codename1.rad.models.Attribute...) } to 
     * create {@link ActionNode}, {@link UI#label(java.lang.String) } to create a {@link Property.Label}, {@link UI#toggleSwitch(com.codename1.rad.models.Attribute...) } to create a toggle switch and more.
     * 
     * == Rendering Property Values
     * 
     * If add a {@link PropertySelectorAttribute} to the action, the resulting value will be rendered
     * in this view, aligned right.  E.g.
     * 
     * [source,java]
     * ----
     * UserProfile profile = new UserProfile();
     * profile.setText(Thing.identifier, "Steve");
     * new SettingsFormActionView(profile, 
     *      action(label("Username"), tags(Thing.identifier))
     * );
     * ----
     * 
     * .Rendering action that has a property set in it.
     * image::https://i.imgur.com/ano8VOg.png[]
     * 
     * == Editable Settings
     * 
     * Add a {@link FieldNode} child node to the action to make the property editable.  Some widget types
     * ({@link WidgetTypes#RADIO}, {@link WidgetTypes#SWITCH}, {@link WidgetTypes#CHECKBOX}) will be rendered
     * directly on the view itself.  Other types will be rendered on a new form.
     * 
     * === Example with Switch
     * 
     * [source,java]
     * ----
     * new SettingsFormActionView(profile, 
     *      action(label("Quality Filter"), toggleSwitch(property(UserProfile.qualityFilter)))
     * );
     * ----
     * 
     * image::https://media.giphy.com/media/XGCQTqCYE21RGwvTCV/giphy.gif[]
     * 
     * === Example with {@link WidgetTypes#RADIO_LIST}
     * 
     * [source,java]
     * ----
     * new SettingsFormActionView(profile, 
     *      action(
     *               label("Country"),
     *               radioListY(
     *                   label("Select Country"),
     *                   description("Please select a country from the list below"),
     *                   options(new DefaultListModel("Canada", "United States", "Mexico", "Spain", "England", "France")),
     *                   tags(PostalAddress.addressCountry)
     *               )
     *       )
     * );
     * ----
     * 
     * image::https://media.giphy.com/media/fA2tCkPPtCKmRnAdZo/giphy.gif[]
     * 
     * == Description text
     * 
     * You can also add description text to an action for the user's assistance, by
     * adding a {@link Property.Description} attribute to the action.  E.g.
     * 
     * [source,java]
     * ----
     * new SettingsFormActionView(profile, action(
                    label("Quality filter"),
                    
                    toggleSwitch(
                        property(UserProfile.qualityFilter),
                        description("Filter lower-quality content from your notifications. This won't filter out notifications from people you follow or accounts you've interacted with recently.")
                    )
            ));
     * ----
     * 
     * image::https://i.imgur.com/gTFckED.png[]
     * 
     * == Styles
     * 
     * One way to customize the look of this view is by overriding the styles that it uses
     * in your application's stylesheet.  You can also change the styles entirely by 
     * providing a {@link UIIDPrefix} attribute to either the {@link ViewNode} of the
     * {@link SettingsView}, or any of the individual {@link ActionNode}s.  This will cause
     * all of the UIIDs used to be prefixed with your desired prefix.
     * 
     * The TwitterUIKit overrides the look of the SettingsForm by setting a UIIDPrefix of "TWT" so that,
     * instead of using uiid="SettingsFormAction", it uses "TWTSettingsFormAction", and instead of 
     * "SettingsFormActionValue" it uses "TWTSettingsFormActionValue".  It then implements these styles
     * in its own stylesheet.
     * 
     * The following styles are used by this view:
     * 
     * . `SettingsFormAction` - The main UIID for the view itself.
     * . `SettingsFormActionValue` - The UIID used to render the property value.
     * . `SettingsFormActionDescriptionText` - The UIID to render the description text.
     * . `SettingsFormActionLabel` - The UIID used for the action label.
     * . `SettingsFormActionArrowButton` - The UIID used to render the arrow button.
     * 
     * This view also sets a UIID for rendering button lists that are rendered in a separate form"
     * 
     * . `SettingsFormButtonList` - The UIID for button lists.
     * . `SettingsFormButtonListCell` - The UIID individual button in a button list.
     */
    public static class SettingsFormActionView extends Container {
        
        public SettingsFormActionView(Entity entity, ActionNode action) {
            super(new BorderLayout());
            Container cnt = this;
            String uiidPrefix = action.getUIIDPrefix("");
            String uiid = action.getUIID("SettingsFormAction");
            cnt.setUIID(uiidPrefix+uiid);
            $(cnt).addTags("SettingsFormAction", "left-edge");
            String labelText = action.getLabelText(entity);
            
            FieldNode fieldNode = (FieldNode)action.findAttribute(FieldNode.class);
            if (fieldNode == null) {
                fieldNode = new FieldNode();
                fieldNode.setParent(action);
                
            } 
            
            WidgetType wtype = fieldNode.getWidgetType(entity.getEntityType());
            
            
            PropertySelector psel = fieldNode.getPropertySelector(entity);
            
            String value = "";
            Component valueView = null;
            boolean expandable = true;
            if (psel != null ) {
                
                if (wtype == RADIO || wtype == CHECKBOX || wtype == SWITCH) {
                    valueView = fieldNode.getViewFactory().createPropertyView(entity, fieldNode);
                    expandable = valueView == null;
                    if (valueView == null) {
                        valueView = new Label("", uiidPrefix+uiid+"Value");
                    }
                } else {

                    value = psel.getText("");
                    Label valueLabel = new Label(value, uiidPrefix+uiid+"Value");
                    valueView = new LabelPropertyView(valueLabel, entity, fieldNode);
                }
            } else {
                PropertySelectorAttribute pselAtt = (PropertySelectorAttribute)fieldNode.findInheritedAttribute(PropertySelectorAttribute.class);
                if (pselAtt != null) {
                    valueView = new Label(pselAtt.getValue(entity).getText(""), uiidPrefix+uiid+"Value");
                }
            }
            
            SpanLabel description = null;
            
            Description fieldDesc = fieldNode.getDescription(entity.getEntityType());
            Description actionDesc = action.getDescription();
            if (!expandable && actionDesc == null && fieldDesc != null) {
                actionDesc = fieldDesc;
            }
            
            if (actionDesc != null) {
                description = new SpanLabel(actionDesc.getValue());
                $(description).addTags("left-inset");
                description.setUIID(uiidPrefix+uiid+"Description");
                description.setTextUIID(uiidPrefix+uiid+"DescriptionText");
                Container wrap = new Container(BoxLayout.y());
                wrap.stripMarginAndPadding();
                
                cnt = new Container(new BorderLayout());
                cnt.stripMarginAndPadding();
                
                wrap.add(cnt);
                wrap.add(description);
                
                add(BorderLayout.CENTER, wrap);
                
            }
            Label lbl = new Label(labelText, uiidPrefix+uiid+"Label");
            $(lbl).addTags("left-inset");
            cnt.add(BorderLayout.CENTER, lbl);
            if (expandable) {
                Button button = new Button("", uiidPrefix+uiid+"ArrowButton");
                FontImage.setIcon(button, FontImage.MATERIAL_ARROW_FORWARD_IOS, -1);

                



                cnt.add(BorderLayout.EAST, BorderLayout.centerEastWest(valueView, button, null).stripMarginAndPadding());
                cnt.setLeadComponent(button);
                
                FieldNode fFieldNode = fieldNode;
                button.addActionListener(evt->{
                    evt.consume();
                    ActionEvent ae = action.fireEvent(entity, button);
                    if (ae.isConsumed()) {
                        return;
                    }
                    FieldNode proxy = (FieldNode)fFieldNode.createProxy(fFieldNode.getParent());
                    if (wtype == RADIO_LIST || wtype == CHECKBOX_LIST || wtype == SWITCH_LIST) {
                        proxy.setAttributesIfNotExists(new UIID("SettingsFormButtonList"));
                    }
                    if (wtype != null) {
                        FieldEditorFormController ctl = new FieldEditorFormController(
                                ViewController.getViewController(button), 
                                entity, 
                                proxy
                        );
                        ctl.getView().show();
                    }
                });
            } else {
                cnt.add(BorderLayout.EAST, valueView);
            }
            
            
        }
    }
    
    
    /**
     * ViewFactory for rendering actions on {@link SettingsForm}.  It will render the action using {@link SettingsFormActionView}.
     */
    public static class SettingsFormActionViewFactory implements ActionViewFactory {

        @Override
        public Component createActionView(Entity entity, ActionNode action) {
            return new SettingsFormActionView(entity, action);
        }
        
    }
    
}
