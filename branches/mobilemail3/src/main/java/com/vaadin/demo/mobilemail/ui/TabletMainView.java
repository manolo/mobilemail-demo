package com.vaadin.demo.mobilemail.ui;

import com.vaadin.addon.touchkit.ui.HorizontalComponentGroup;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.demo.mobilemail.MobileMailApplication;
import com.vaadin.demo.mobilemail.data.Message;
import com.vaadin.terminal.Page;
import com.vaadin.terminal.Page.BrowserWindowResizeEvent;
import com.vaadin.terminal.Page.BrowserWindowResizeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class TabletMainView extends HorizontalLayout implements MainView,
        BrowserWindowResizeListener, ClickListener {

    private static final long serialVersionUID = 1L;

    MailboxHierarchyManager mailboxHierarchyView = new MailboxHierarchyManager();

    MessageView messageView = new MessageView(false);

    Button showMailboxHierarchyButton;

    private boolean lastOrientationHorizontal;

    private boolean added;

    public TabletMainView() {
        setSizeFull();
        addStyleName("tablet");
    }

    @Override
    public void attach() {
        super.attach();
        setOrientation();
        if (Page.getCurrent() != null) {
            Page.getCurrent().addListener(this);
        }
    }

    private void setOrientation() {
        removeAllComponents();

        if (isHorizontal()) {
            /*
             * Removed possible window currently showing the hierarchy view
             */
            if (mailboxHierarchyView.getParent() != null) {
                Component parent2 = mailboxHierarchyView.getParent();
                if (parent2 instanceof Window) {
                    Window window = (Window) parent2;
                    window.setContent(null);
                    if (window.getUI() != null) {
                        window.getUI().removeWindow(window);
                    }
                }
            }

            addComponent(mailboxHierarchyView);
            addComponent(messageView);
            setExpandRatio(messageView, 1);
            messageView.setLeftComponent(messageView.getNavigationLayout());
        } else {
            showMailboxHierarchyButton = new Button();
            showMailboxHierarchyButton.addListener(this);

            addComponent(messageView);
            showMailboxHierarchyButton.setCaption(mailboxHierarchyView
                    .getCurrentComponent().getCaption());

            HorizontalComponentGroup hc = new HorizontalComponentGroup();
            hc.addComponent(showMailboxHierarchyButton);
            while (messageView.getNavigationLayout().iterator().hasNext()) {
                Component c = messageView.getNavigationLayout().iterator()
                        .next();
                hc.addComponent(c);
            }
            messageView.setLeftComponent(hc);
        }

        lastOrientationHorizontal = isHorizontal();
    }

    @Override
    public void setMessage(Message message, MessageHierarchyView msgView) {
        messageView.setMessage(message, msgView);
    }

    private boolean isHorizontal() {
        int width = 0, height = 0;
        if (Page.getCurrent() != null) {
            width = Page.getCurrent().getBrowserWindowWidth();
            height = Page.getCurrent().getBrowserWindowHeight();
        } else {

            width = ((MobileMailApplication) MobileMailApplication.getCurrent())
                    .getBrowser().getScreenWidth();

            height = ((MobileMailApplication) MobileMailApplication
                    .getCurrent()).getBrowser().getScreenHeight();

            return false;
        }
        return (width > height);

    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == showMailboxHierarchyButton) {
            Popover popover = new Popover();
            Component parent2 = mailboxHierarchyView.getParent();
            if (parent2 != null && parent2 instanceof Popover) {
                ((Popover) parent2).setContent(null);
            }
            popover.setContent(mailboxHierarchyView);
            popover.setClosable(true);
            popover.showRelativeTo(showMailboxHierarchyButton);
            popover.setHeight(((MobileMailApplication) MobileMailApplication
                    .getCurrent()).getBrowser().getScreenHeight() - 100,
                    Unit.PIXELS);
            popover.addListener(new CloseListener() {
                @Override
                public void windowClose(CloseEvent e) {
                    setEnabled(true);
                }
            });
        }

    }

    @Override
    public void setEnabled(boolean enabled) {
        messageView.setEnabled(enabled);
    }

    @Override
    public void browserWindowResized(BrowserWindowResizeEvent event) {
        if (!added && Page.getCurrent() != null) {
            added = true;
            Page.getCurrent().addListener(this);
        }
        if (getApplication() != null) {
            if (isHorizontal() != lastOrientationHorizontal) {
                setOrientation();
            }
        }
    }
}
