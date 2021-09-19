package com.mdmeh.celery.views.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdmeh.celery.views.home.HomeView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.mdmeh.celery.views.MainLayout;
import com.vaadin.flow.server.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


@PageTitle("Recipes")
@Route(value = "recipes", layout = MainLayout.class)
public class RecipesView extends VerticalLayout {

    RecipeContainer recipeContainer;
    RecipeContainer recipeContainer1;

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

    public RecipesView() {

        // Fridge:
        Accordion fridgeAccordion = new Accordion();
        VerticalLayout fridgeItems = new VerticalLayout();
        fridgeItems.add( new Label("Test") );
        fridgeAccordion.add("Your Fridge", fridgeItems);

        fridgeAccordion.close();

        // Recipes:

        Label seperator = new Label("~~=============================~~");
        Label recipesLabel = new Label("Recipes 🧾");
        recipesLabel.getElement().getStyle().set("fontWeight","bold");

        ComboBox<String> ingredientFilter = new ComboBox<>();
        ingredientFilter.setLabel("Filter recipes by ingredient");
        ingredientFilter.setReadOnly(true);

        recipeContainer = new RecipeContainer(0);
        recipeContainer1 = new RecipeContainer(1);

        Button refresh = new Button( VaadinIcon.REFRESH.create() );

        refresh.setWidth("320px");

        // Fin

        VerticalLayout content = new VerticalLayout(recipeContainer, recipeContainer1, refresh);
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
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        var ui = getUI().get();
        populate_container(recipeContainer, ui);
        populate_container(recipeContainer1, ui);

    }
}
