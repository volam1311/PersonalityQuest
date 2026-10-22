package com.example.personalityquest.Services.navigation;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.ScreenEnum;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Loads application screens and manages the primary stage */
public class NavigationService {

    public final static String fxmlPrefix = "/com/example/personalityquest/";

    public static Stage stage;
    private static ThemeService themeService;
    private static final ScreenFactory screenFactory = new ScreenFactory();

    /** Sets the primary stage used for navigation
     * @param currentStage the primary JavaFX stage
     */
    public static void Init(Stage currentStage){
        stage = currentStage;
        EnsureThemeService();
    }

    public static ThemeService GetThemeService() {
        EnsureThemeService();
        return themeService;
    }

    private static void EnsureThemeService() {
        if (themeService == null) {
            themeService = new ThemeService();
            themeService.addThemeChangeListener(theme -> ApplyThemeToCurrentScene());
        }
    }

    /** Loads the requested screen into the primary stage
     * @param screen the screen to load
     * @throws IOException if the screen resource cannot be loaded
     */
    public static void LoadScreen(ScreenEnum screen) throws IOException {
        String fxmlString = SetFxmlDoc(screen);

        FXMLLoader fxmlLoader = new FXMLLoader(UpdateAccountPersonalDetailsApplication.class.getResource(fxmlString));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        String stylesheet = StylesheetForCurrentTheme();
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet);
        }
        stage.setTitle(TitleFor(screen));
        stage.setScene(scene);
        stage.show();
    }

    /** Returns the FXML resource path for a screen
     * @param screenEnum the screen whose FXML path is requested
     * @return the FXML resource path
     */
    public static String SetFxmlDoc(ScreenEnum screenEnum){
        return fxmlPrefix + screenFactory.Create(screenEnum).fxmlFileName();
    }

    private static String TitleFor(ScreenEnum screen) {
        return screenFactory.Create(screen).title();
    }

    private static void ApplyThemeToCurrentScene() {
        if (stage == null || stage.getScene() == null) {
            return;
        }
        String stylesheet = StylesheetForCurrentTheme();
        if (stylesheet == null) {
            return;
        }
        var stylesheets = stage.getScene().getStylesheets();
        stylesheets.removeIf(url -> url.contains("/css/theme-"));
        stylesheets.add(stylesheet);
    }

    private static String StylesheetForCurrentTheme() {
        var resource = UpdateAccountPersonalDetailsApplication.class.getResource(
                fxmlPrefix + "css/" + GetThemeService().getCurrentTheme().getStylesheetName());
        if (resource == null) {
            return null;
        }
        return resource.toExternalForm();
    }
}
