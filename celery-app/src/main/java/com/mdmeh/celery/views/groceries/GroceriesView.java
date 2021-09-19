package com.mdmeh.celery.views.groceries;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.mdmeh.celery.views.MainLayout;

import java.util.HashSet;

@PageTitle("Groceries")
@Route(value = "groceries", layout = MainLayout.class)
@PreserveOnRefresh
public class GroceriesView extends VerticalLayout {

    private class TodoItem extends HorizontalLayout {

        VerticalLayout parent;
        String label;

        public TodoItem(String label, VerticalLayout parent, HashSet<String> items) {

            this.label = label;
            this.parent = parent;

            Checkbox checkbox = new Checkbox();
            checkbox.setLabel(label);
            checkbox.setValue(false);

            Button removeButton = new Button(new Icon(VaadinIcon.CLOSE));
            removeButton.addClickListener( e-> {
                items.remove(this.label);
                parent.remove(this);
            });

            add(checkbox,removeButton);
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }

    }

    public GroceriesView() {

//        Image img = new Image("images/empty-plant.png", "placeholder plant");
//        img.setWidth("200px");
//        add(img);
//
//        add(new H2("This place intentionally left empty"));
//        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        Label groceryRunLabel = new Label("Begin grocery run:");
        Icon play = new Icon(VaadinIcon.PLAY_CIRCLE);
        play.setSize("50px");
        Button groceryRun = new Button(play);

        HorizontalLayout todoInput = new HorizontalLayout();
        TextField todoTextField = new TextField();
        Button addTodo = new Button("Add");
        todoTextField.setPlaceholder("Enter new item...");
        todoInput.add(todoTextField, addTodo);

        VerticalLayout checkboxGroup = new VerticalLayout();
        HashSet<String> items = new HashSet<>();

        addTodo.addClickListener( e -> {
            if (todoTextField.isEmpty() || items.contains(todoTextField.getValue()) ) {
                return;
            }
            checkboxGroup.add( new TodoItem( todoTextField.getValue(), checkboxGroup, items ) );
            items.add(todoTextField.getValue());
        });

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "to");

        add(groceryRunLabel, groceryRun, todoInput, checkboxGroup);
    }

}
