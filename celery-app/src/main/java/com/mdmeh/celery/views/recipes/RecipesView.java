package com.mdmeh.celery.views.recipes;

import com.mdmeh.celery.views.home.HomeView;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.mdmeh.celery.views.MainLayout;
import com.vaadin.flow.server.StreamResource;

@PageTitle("Recipes")
@Route(value = "recipes", layout = MainLayout.class)
public class RecipesView extends VerticalLayout {

    private static class RecipeContainer extends VerticalLayout {

        public RecipeContainer(int recipe_id) {

            StreamResource res = new StreamResource("banner.jpg", () -> {
                // eg. load image data from classpath (src/main/resources/images/image.png)
                return HomeView.class.getClassLoader().getResourceAsStream("images/banner.jpg");
            });
            Image bannerImage = new Image( res,"Food");
            bannerImage.setWidthFull();

            Label title = new Label();
            title.setText(String.valueOf(recipe_id));

            this.add(bannerImage,title);
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

        RecipeContainer recipeContainer = new RecipeContainer(0);
        RecipeContainer recipeContainer1 = new RecipeContainer(1);

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

}
