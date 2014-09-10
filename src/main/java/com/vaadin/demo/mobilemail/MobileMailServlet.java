package com.vaadin.demo.mobilemail;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.touchkit.server.TouchKitServlet;
import com.vaadin.addon.touchkit.settings.TouchKitSettings;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true, 
     initParams = { @WebInitParam(name = "productionMode", value = "false") })
public class MobileMailServlet extends TouchKitServlet {

    private final UIProvider uiProvider = new UIProvider() {
        @Override
        public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
            String userAgent = event.getRequest().getHeader("user-agent")
                    .toLowerCase();
            // webkit: safari, chrome, android, iOS
            // gecko: FF, IE11
            // trident/6|7: IE10/11
            // windows phone: WP
            if (userAgent.matches(".*(webkit|gecko|windows phone|trident/(6|7)).*")) {
                return MobileMailUI.class;
            } else {
                return FallbackUI.class;
            }
        }
    };

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();

        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                event.getSession().addUIProvider(uiProvider);
            }
        });

        TouchKitSettings s = getTouchKitSettings();
        s.getWebAppSettings().setWebAppCapable(true);
        s.getWebAppSettings().setStatusBarStyle("black");
        String contextPath = getServletConfig().getServletContext()
                .getContextPath();

        s.getApplicationIcons().addApplicationIcon(
                contextPath + "/VAADIN/themes/mobilemail/apple-touch-icon.png");
    }
}
