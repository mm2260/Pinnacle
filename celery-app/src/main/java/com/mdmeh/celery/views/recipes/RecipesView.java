package com.mdmeh.celery.views.recipes;

import com.mdmeh.celery.views.home.HomeView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.mdmeh.celery.views.MainLayout;
import com.vaadin.flow.server.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;


@PageTitle("Recipes")
@Route(value = "recipes", layout = MainLayout.class)
public class RecipesView extends VerticalLayout {

    RecipeContainer recipeContainer;
    Button refresh;

    private static class RecipeContainer extends VerticalLayout {

        Accordion recipe = new Accordion();
        public int recipe_id;

        public RecipeContainer(int recipe_id) {

            this.recipe_id = recipe_id;

            StreamResource res = new StreamResource("banner.jpg", () -> {
                // eg. load image data from classpath (src/main/resources/images/image.png)
                return HomeView.class.getClassLoader().getResourceAsStream("images/banner.jpg");
            });
            Image bannerImage = new Image( res,"Food");
            bannerImage.setWidthFull();

            recipe.close();

            this.setClassName("recipeContainer");
            this.add(bannerImage,recipe);
            this.setJustifyContentMode(JustifyContentMode.CENTER);
            this.setAlignItems(Alignment.START);

        }
    }

    private void populate_container(RecipeContainer recipeContainer, UI ui) {
        new Thread() {
            public void run() {
                try {

                    URL url = new URL("http://127.0.0.1:8000/recipe/fetch/title/"+recipeContainer.recipe_id);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    InputStream responseStream = connection.getInputStream();
                    String title = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    url = new URL("http://127.0.0.1:8000/recipe/fetch/ingredients/"+recipeContainer.recipe_id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    responseStream = connection.getInputStream();
                    String ingredients = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    url = new URL("http://127.0.0.1:8000/recipe/fetch/instructions/"+recipeContainer.recipe_id);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    responseStream = connection.getInputStream();
                    String instructions = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    ui.access( () -> {
                        recipeContainer.recipe.add(title, new VerticalLayout(
                                new H4("Ingredients:"),new Label(ingredients),
                                new H4("Instructions:"),new Label(instructions)) );
                    } );


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static class FridgeItem extends HorizontalLayout {

        VerticalLayout parent;
        String label;

        public FridgeItem(String label, VerticalLayout parent, HashSet<String> items) {

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

            checkbox.addValueChangeListener( e-> {
                if (checkbox.getValue()) {
                    checkbox.getElement().getStyle().set("text-decoration", "line-through");
                } else {
                    checkbox.getElement().getStyle().remove("text-decoration");
                }
            } );

            add(checkbox,removeButton);
            setJustifyContentMode(JustifyContentMode.CENTER);
            setAlignItems(Alignment.CENTER);
        }

        @Override
        public int hashCode() {
            return this.label.hashCode();
        }

    }

    private void repopulate_container(RecipeContainer recipeContainer, UI ui) {
        new Thread() {
            public void run() {
                try {

                    URL url = new URL("http://127.0.0.1:8000/recipe/recommend/1");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    InputStream responseStream = connection.getInputStream();
                    String ridStr = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
                    int rid = Integer.parseInt(ridStr);

                    url = new URL("http://127.0.0.1:8000/recipe/fetch/title/"+rid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    responseStream = connection.getInputStream();
                    String title = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    url = new URL("http://127.0.0.1:8000/recipe/fetch/ingredients/"+rid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    responseStream = connection.getInputStream();
                    String ingredients = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    url = new URL("http://127.0.0.1:8000/recipe/fetch/instructions/"+rid);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("accept", "application/json");
                    responseStream = connection.getInputStream();
                    String instructions = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

                    ui.access( () -> {
                        recipeContainer.recipe.add(title, new VerticalLayout(
                                new H4("Ingredients:"),new Label(ingredients),
                                new H4("Instructions:"),new Label(instructions)) );
                    } );


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public RecipesView() {

        // Fridge:
        Accordion fridgeAccordion = new Accordion();
        VerticalLayout fridgeItems = new VerticalLayout();

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
            checkboxGroup.add( new FridgeItem( todoTextField.getValue(), checkboxGroup, items ) );
            items.add(todoTextField.getValue());
            todoTextField.clear();
        });

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "to");


        fridgeAccordion.add("Your Fridge", new VerticalLayout( todoInput, checkboxGroup ));
        fridgeAccordion.close();

        // Recipes:

        Label seperator = new Label("~~=============================~~");
        Label recipesLabel = new Label("Recipes 🧾");
        recipesLabel.getElement().getStyle().set("fontWeight","bold");

        ComboBox<String> ingredientFilter = new ComboBox<>();
        ingredientFilter.setLabel("Filter recipes by ingredient");
        ingredientFilter.setReadOnly(true);

        recipeContainer = new RecipeContainer(0);

        refresh = new Button( VaadinIcon.REFRESH.create() );
        refresh.setWidth("320px");

        // Fin

        VerticalLayout content = new VerticalLayout(recipeContainer, refresh);
        content.setAlignItems(Alignment.CENTER);
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.setWidthFull();

        add(fridgeAccordion, seperator, recipesLabel, ingredientFilter,content);

        ingredientFilter.setWidthFull();

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.START);
        getStyle().set("text-align", "center");
        setClassName("recipesView");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        var ui = getUI().get();
        populate_container(recipeContainer, ui);

        refresh.addClickListener( e -> {
            repopulate_container( recipeContainer, ui );
        } );

    }
}
