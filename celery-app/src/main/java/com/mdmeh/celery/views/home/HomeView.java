package com.mdmeh.celery.views.home;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.mdmeh.celery.views.MainLayout;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;

import java.util.concurrent.atomic.AtomicReference;

@PageTitle("Home")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class HomeView extends HorizontalLayout {

    private enum Action { FINISHED_EARLY, THROWN_AWAY }

    public HomeView() {
        addClassName("home-view");

        //LOGO and Intro:

        StreamResource res = new StreamResource("logo.png", () -> {
            // eg. load image data from classpath (src/main/resources/images/image.png)
            return HomeView.class.getClassLoader().getResourceAsStream("images/logo.png");
        });
        Image logoImage = new Image( res,"Celery");
        logoImage.setWidth("175px");
        Label celeryLabel = new Label("[ Celery 🌿 ]");
        celeryLabel.getElement().getStyle().set("fontWeight","bold");
        celeryLabel.getElement().getStyle().set("fontSize","large");
        VerticalLayout logo = new VerticalLayout(
                new Label("________________________"), celeryLabel,logoImage,
                new Label("________________________"));

        logo.setAlignItems(Alignment.CENTER);
        logo.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout actionButtons = new VerticalLayout();

        // Actions:
        AtomicReference<Action> action = null;

        Button throwButton = new Button("Item Gone Bad 😟");
        Button finishedButton = new Button("Item Finished Early 🙀");
        throwButton.setClassName("throwBtn");
        finishedButton.setClassName("finishBtn");
        throwButton.setWidth("full");
        finishedButton.setWidth("full");

        TextField whatItem = new TextField();
        whatItem.setPlaceholder("What Item?");
        whatItem.setVisible(false);

        Button ok = new Button("OK");
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.setVisible(false);

        actionButtons.setAlignItems(Alignment.CENTER);
        actionButtons.add(throwButton,finishedButton, whatItem, ok);

        throwButton.addClickListener( e-> {
//            action.set(Action.THROWN_AWAY);

            throwButton.setEnabled(false);
            finishedButton.setEnabled(false);

            ok.setVisible(true);
            whatItem.setVisible(true);
        } );

        finishedButton.addClickListener( e-> {
//            action.set(Action.FINISHED_EARLY);

            throwButton.setEnabled(false);
            finishedButton.setEnabled(false);

            ok.setVisible(true);
            whatItem.setVisible(true);
        } );

        ok.addClickListener( e-> {
            if(whatItem.isEmpty())
                return;
            
            ok.setVisible(false);
            whatItem.setVisible(false);
            whatItem.clear();

            throwButton.setEnabled(true);
            finishedButton.setEnabled(true);
        } );

        // Finishing up...

        add(logo, actionButtons);

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

    }
}
