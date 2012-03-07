/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Nikolay Gorodnov
 * Created: 20.07.2010 15:40:53
 *
 * $Id$
 */
package com.haulmont.cuba.web.gui.components;

import com.haulmont.chile.core.model.Instance;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.MessageProvider;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.config.WindowConfig;
import com.haulmont.cuba.gui.config.WindowInfo;
import com.haulmont.cuba.gui.data.*;
import com.haulmont.cuba.web.App;
import com.haulmont.cuba.web.toolkit.ui.CustomField;
import com.haulmont.cuba.web.toolkit.ui.ScrollablePanel;
import com.haulmont.cuba.web.toolkit.ui.TokenListLabel;
import com.vaadin.terminal.KeyMapper;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.*;

import java.util.*;

public class WebTokenList extends WebAbstractField<WebTokenList.TokenListImpl> implements TokenList {

    private static final long serialVersionUID = -6490244006772570832L;

    private CollectionDatasource datasource;

    private String captionProperty;

    private CaptionMode captionMode;

    private Position position = Position.TOP;

    private ItemChangeHandler itemChangeHandler;

    private boolean inline;

    private WebButton button;

    private WebLookupPickerField lookupPickerField;

    private String lookupScreen;
    private WindowManager.OpenType lookupOpenMode = WindowManager.OpenType.THIS_TAB;
    private Map<String, Object> lookupScreenParams = null;

    private TokenStyleGenerator tokenStyleGenerator;

    private boolean lookup;

    private boolean editable;

    private boolean simple;

    private boolean multiselect;
    private PickerField.LookupAction lookupAction;

    public WebTokenList() {
        button = new WebButton();
        button.setCaption(MessageProvider.getMessage(TokenList.class, "actions.Add"));

        lookupPickerField = new WebLookupPickerField();
        component = new TokenListImpl();

        setMultiSelect(false);
    }

    @Override
    public CollectionDatasource getDatasource() {
        return datasource;
    }

    @Override
    public MetaProperty getMetaProperty() {
        return null;
    }

    @Override
    public void setDatasource(Datasource datasource, String property) {
    }

    @Override
    public void setDatasource(CollectionDatasource datasource) {
        this.datasource = datasource;
        datasource.addListener(new CollectionDatasourceListener() {
            @Override
            public void collectionChanged(CollectionDatasource ds, Operation operation) {
                if (lookupPickerField != null) {
                    if (isLookup()) {
                        if (getLookupScreen() != null)
                            lookupAction.setLookupScreen(getLookupScreen());
                        else
                            lookupAction.setLookupScreen(null);

                        lookupAction.setLookupScreenOpenType(lookupOpenMode);
                        lookupAction.setLookupScreenParams(lookupScreenParams);
                    }
                }
                component.refreshComponent();
            }

            @Override
            public void itemChanged(Datasource ds, Entity prevItem, Entity item) {
            }

            @Override
            public void stateChanged(Datasource ds, Datasource.State prevState, Datasource.State state) {
            }

            @Override
            public void valueChanged(Object source, String property, Object prevValue, Object value) {
            }
        });
    }

    @Override
    public void setFrame(IFrame frame) {
        super.setFrame(frame);
        lookupPickerField.setFrame(frame);
    }

    @Override
    public String getCaptionProperty() {
        return captionProperty;
    }

    @Override
    public void setCaptionProperty(String captionProperty) {
        this.captionProperty = captionProperty;
    }

    @Override
    public WindowManager.OpenType getLookupOpenMode() {
        return lookupOpenMode;
    }

    @Override
    public void setLookupOpenMode(WindowManager.OpenType lookupOpenMode) {
        this.lookupOpenMode = lookupOpenMode;
    }

    @Override
    public CaptionMode getCaptionMode() {
        return captionMode;
    }

    @Override
    public void setCaptionMode(CaptionMode captionMode) {
        this.captionMode = captionMode;
    }

    @Override
    public LookupField.FilterMode getFilterMode() {
        return lookupPickerField.getFilterMode();
    }

    @Override
    public void setFilterMode(LookupField.FilterMode mode) {
        lookupPickerField.setFilterMode(mode);
    }

    @Override
    public String getOptionsCaptionProperty() {
        return lookupPickerField.getCaptionProperty();
    }

    @Override
    public void setOptionsCaptionProperty(String captionProperty) {
        lookupPickerField.setCaptionProperty(captionProperty);
    }

    @Override
    public CaptionMode getOptionsCaptionMode() {
        return lookupPickerField.getCaptionMode();
    }

    @Override
    public void setOptionsCaptionMode(CaptionMode captionMode) {
        lookupPickerField.setCaptionMode(captionMode);
    }

    @Override
    public CollectionDatasource getOptionsDatasource() {
        return lookupPickerField.getOptionsDatasource();
    }

    @Override
    public void setOptionsDatasource(CollectionDatasource datasource) {
        lookupPickerField.setOptionsDatasource(datasource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        if (datasource != null) {
            List<Object> items = new ArrayList<Object>();
            for (final Object itemId : datasource.getItemIds()) {
                items.add(datasource.getItem(itemId));
            }
            return (T) items;
        } else
            return null;
    }

    @Override
    public List getOptionsList() {
        return lookupPickerField.getOptionsList();
    }

    @Override
    public void setOptionsList(List optionsList) {
        lookupPickerField.setOptionsList(optionsList);
    }

    @Override
    public Map<String, Object> getOptionsMap() {
        return lookupPickerField.getOptionsMap();
    }

    @Override
    public void setOptionsMap(Map<String, Object> map) {
        lookupPickerField.setOptionsMap(map);
    }

    @Override
    public boolean isLookup() {
        return lookup;
    }

    @Override
    public void setLookup(boolean lookup) {
        if (this.lookup != lookup) {
            if (lookup)
                lookupAction = lookupPickerField.addLookupAction();
            else
               lookupPickerField.removeAction(lookupAction);
        }
        this.lookup = lookup;
        component.refreshComponent();
    }

    @Override
    public String getLookupScreen() {
        return lookupScreen;
    }

    @Override
    public void setLookupScreen(String lookupScreen) {
        this.lookupScreen = lookupScreen;
    }

    @Override
    public void setLookupScreenParams(Map<String, Object> params) {
        this.lookupScreenParams = params;
    }

    @Override
    public Map<String, Object> getLookupScreenParams() {
        return lookupScreenParams;
    }

    @Override
    public boolean isMultiSelect() {
        return multiselect;
    }

    @Override
    public void setMultiSelect(boolean multiselect) {
        this.multiselect = multiselect;
        lookupPickerField.setMultiSelect(multiselect);
    }

    @Override
    public String getAddButtonCaption() {
        return button.getCaption();
    }

    @Override
    public void setAddButtonCaption(String caption) {
        button.setCaption(caption);
    }

    @Override
    public String getAddButtonIcon() {
        return button.getIcon();
    }

    @Override
    public void setAddButtonIcon(String icon) {
        button.setIcon(icon);
    }

    @Override
    public ItemChangeHandler getItemChangeHandler() {
        return itemChangeHandler;
    }

    @Override
    public void setItemChangeHandler(ItemChangeHandler handler) {
        this.itemChangeHandler = handler;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean isInline() {
        return inline;
    }

    @Override
    public void setInline(boolean inline) {
        this.inline = inline;
    }

    @Override
    public String getCaption() {
        return component.getCaption();
    }

    @Override
    public void setCaption(String caption) {
        component.setCaption(caption);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean isSimple() {
        return simple;
    }

    @Override
    public void setSimple(boolean simple) {
        this.simple = simple;
        this.component.editor = null;
        this.component.refreshComponent();
    }

    @Override
    public void setTokenStyleGenerator(TokenStyleGenerator tokenStyleGenerator) {
        this.tokenStyleGenerator = tokenStyleGenerator;
    }

    @Override
    public TokenStyleGenerator getTokenStyleGenerator() {
        return tokenStyleGenerator;
    }

    protected String instanceCaption(Instance instance) {
        if (instance == null) { return ""; }
        if (captionProperty != null) {
            if (instance.getMetaClass().getPropertyPath(captionProperty) != null) {
                Object o = instance.getValueEx(captionProperty);
                return o != null ? o.toString() : " ";
            }
            throw new IllegalArgumentException(String.format("Couldn't find property with name '%s'",
                    captionProperty));
        } else
            return instance.getInstanceName();
    }

    public class TokenListImpl extends CustomField {

        private VerticalLayout root;

        private Panel container;

        private Component editor;

        private Map<Instance, TokenListLabel> itemComponents = new HashMap<Instance, TokenListLabel>();
        private Map<TokenListLabel, Instance> componentItems = new HashMap<TokenListLabel, Instance>();
        private KeyMapper componentsMapper = new KeyMapper();

        public TokenListImpl() {
            root = new VerticalLayout();
            root.setSpacing(true);
            root.setSizeFull();

            container = new ScrollablePanel();
            CssLayout layout = new CssLayout();
            container.setContent(layout);
            container.setSizeFull();

            root.addComponent(container);
            root.setExpandRatio(container, 1);

            setCompositionRoot(root);

            setStyleName("token-list");
        }

        protected void initField() {
            final HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setWidth("100%");

            if (!isSimple()) {
                lookupPickerField.setWidth("100%");
                Component lookupComponent = WebComponentsHelper.getComposition(lookupPickerField);
                lookupComponent.setWidth("100%");

                layout.addComponent(lookupComponent);
                layout.setExpandRatio(lookupComponent, 1);
            } else {
                lookupPickerField.setVisible(false);
            }

            button.setStyleName("add-btn");

            Button wrappedButton = (Button) WebComponentsHelper.unwrap(button);
            if (!isSimple()) {
                wrappedButton.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if (isEditable()) {
                            final Entity newItem = lookupPickerField.getValue();
                            if (newItem == null) return;
                            if (itemChangeHandler != null) {
                                itemChangeHandler.addItem(newItem);
                            } else {
                                if (datasource != null)
                                    datasource.addItem(newItem);
                            }
                            lookupPickerField.setValue(null);
                        }
                    }
                });
            } else {
                wrappedButton.addListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {

                        String windowAlias;
                        if (getLookupScreen() != null) {
                            windowAlias = getLookupScreen();
                        } else if (getOptionsDatasource() != null) {
                            windowAlias = getOptionsDatasource().getMetaClass().getName() + ".browse";
                        } else {
                            windowAlias = getDatasource().getMetaClass().getName() + ".browse";
                        }

                        WindowConfig windowConfig = AppContext.getBean(WindowConfig.class);
                        WindowInfo windowInfo = windowConfig.getWindowInfo(windowAlias);

                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("windowOpener", WebTokenList.this.<IFrame>getFrame().getId());
                        if (isMultiSelect()) {
                            params.put("multiSelect", "true");
                        }

                        WindowManager wm = App.getInstance().getWindowManager();
                        wm.openLookup(windowInfo, new Window.Lookup.Handler() {
                            @Override
                            public void handleLookup(Collection items) {
                                if (isEditable()) {
                                    if (items == null || items.isEmpty()) return;
                                    for (final Object item : items) {
                                        if (itemChangeHandler != null) {
                                            itemChangeHandler.addItem(item);
                                        } else {
                                            datasource.addItem((Entity) item);
                                        }
                                    }
                                }
                            }
                        }, lookupOpenMode, params);
                    }
                });
            }
            layout.addComponent(wrappedButton);

            editor = layout;
        }

        public void refreshComponent() {
            if (inline) {
                addStyleName("inline");
            }

            if (editor == null) {
                initField();
            }

            if (editor != null) {
                root.removeComponent(editor);
            }

            if (isEditable()) {
                if (position == Position.TOP) {
                    root.addComponentAsFirst(editor);
                } else {
                    root.addComponent(editor);
                    editor.setWidth("100%");
                }
            }

            container.removeAllComponents();

            if (datasource != null) {
                // New tokens
                for (final Object itemId : datasource.getItemIds()) {
                    final Instance item = datasource.getItem(itemId);
                    TokenListLabel f = itemComponents.get(item);
                    if (f == null) {
                        f = createToken();
                        itemComponents.put(item, f);
                        componentItems.put(f, item);
                    }
                    f.setEditable(isEditable());
                    f.setValue(instanceCaption(item));
                    f.setWidth("100%");
                    setTokenStyle(f, itemId);
                    container.addComponent(f);
                }
            }

            root.requestRepaint();
        }

        protected TokenListLabel createToken() {
            final TokenListLabel label = new TokenListLabel();
            String key = componentsMapper.key(label);
            label.setKey(key);
            label.setWidth("100%");
            label.addListener(new TokenListLabel.RemoveTokenListener() {
                @Override
                public void removeToken(final TokenListLabel source) {
                    if (isEditable()) {
                        doRemove(source);
                    }
                }
            });
            return label;
        }

        private void doRemove(TokenListLabel source) {
            Instance item = componentItems.get(source);
            if (item != null) {
                itemComponents.remove(item);
                componentItems.remove(source);

                if (itemChangeHandler != null) { //todo test
                    itemChangeHandler.removeItem(item);
                } else {
                    datasource.removeItem((Entity) item);
                }
            }
        }

        @Override
        public Class<?> getType() {
            return List.class;
        }

        protected void setTokenStyle(TokenListLabel label, Object itemId) {
            if (tokenStyleGenerator != null) {
                String styleName = tokenStyleGenerator.getStyle(itemId);
                if (styleName != null && !styleName.equals("")) {
                    label.setStyleName(styleName);
                }
            }
        }
    }
}