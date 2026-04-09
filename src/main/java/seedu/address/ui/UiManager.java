package seedu.address.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;

/**
 * The manager of the UI component.
 */
public class UiManager implements Ui {

    public static final String ALERT_DIALOG_PANE_FIELD_ID = "alertDialogPane";

    private static final Logger logger = LogsCenter.getLogger(UiManager.class);
    private static final String ICON_APPLICATION = "/images/vendor_vault.png";

    private static final String DARK_THEME_RESOURCE_PATH = "/view/DarkTheme.css";
    private static final String[] UI_FONT_CANDIDATES = {"Segoe UI", "Helvetica", "Helvetica Neue", "Arial",
        "Microsoft Sans Serif"};
    private static final String[] SEMIBOLD_FONT_CANDIDATES = {"Segoe UI Semibold", "Helvetica Neue Medium",
        "Helvetica Neue Bold", "Arial Bold"};
    private static final String[] LIGHT_FONT_CANDIDATES = {"Segoe UI Light", "Helvetica Light", "Helvetica Neue Light"};
    private static final String[] MONO_FONT_CANDIDATES = {"Consolas", "Menlo", "SF Mono", "Monaco", "Monospaced"};

    private String uiFont;
    private String uiFontSemibold;
    private String uiFontLight;
    private String monoFont;
    private String resolvedDarkThemeUrl;

    private Logic logic;
    private MainWindow mainWindow;

    /**
     * Creates a {@code UiManager} with the given {@code Logic}.
     */
    public UiManager(Logic logic) {
        this.logic = logic;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting UI...");

        //Set the application icon.
        primaryStage.getIcons().add(getImage(ICON_APPLICATION));

        try {
            logFontCandidateAvailability();
            initBrandFonts();
            resolvedDarkThemeUrl = buildResolvedDarkThemeStylesheetUrl();

            mainWindow = new MainWindow(primaryStage, logic);
            replaceDarkThemeStylesheet(mainWindow.getPrimaryStage());
            mainWindow.show();
            mainWindow.fillInnerParts();

        } catch (Throwable e) {
            logger.severe(StringUtil.getDetails(e));
            showFatalErrorDialogAndShutdown("Fatal error during initializing", e);
        }
    }

    private Image getImage(String imagePath) {
        return new Image(MainApp.class.getResourceAsStream(imagePath));
    }

    /**
     * Resolves the concrete font family names used by the theme.
     *
     * <p>Each font role is chosen from ordered candidates to keep typography
     * consistent across platforms while still falling back to available fonts.
     */
    private void initBrandFonts() {
        uiFont = resolveFirstAvailableFontName(UI_FONT_CANDIDATES);
        uiFontSemibold = resolveFirstAvailableFontName(appendFallbackCandidate(SEMIBOLD_FONT_CANDIDATES, uiFont));
        uiFontLight = resolveFirstAvailableFontName(appendFallbackCandidate(LIGHT_FONT_CANDIDATES, uiFont));
        monoFont = resolveFirstAvailableFontName(MONO_FONT_CANDIDATES);

        logger.info(String.format("Resolved fonts: ui='%s', semibold='%s', light='%s', mono='%s'",
            uiFont, uiFontSemibold, uiFontLight, monoFont));
    }

    private void logFontCandidateAvailability() {
        Set<String> availableFontNamesLower = new HashSet<>();
        for (String fontName : Font.getFontNames()) {
            availableFontNamesLower.add(fontName.toLowerCase());
        }

        logCandidateGroup("UI candidates", availableFontNamesLower,
            UI_FONT_CANDIDATES);
        logCandidateGroup("Semibold candidates", availableFontNamesLower,
            SEMIBOLD_FONT_CANDIDATES);
        logCandidateGroup("Light candidates", availableFontNamesLower,
            LIGHT_FONT_CANDIDATES);
        logCandidateGroup("Monospace candidates", availableFontNamesLower,
            MONO_FONT_CANDIDATES);
    }

    /**
     * Logs availability of the given candidate font names.
     *
     * <p>Used to diagnose platform-specific font fallback decisions without
     * changing runtime behavior.
     */
    private void logCandidateGroup(String groupName,
                                   Set<String> availableFontNamesLower,
                                   String... candidates) {
        List<String> statuses = new ArrayList<>();
        for (String candidate : candidates) {
            boolean isAvailable = availableFontNamesLower.contains(candidate.toLowerCase());
            statuses.add(candidate + "=" + (isAvailable ? "available" : "missing"));
        }
        logger.fine(groupName + ": " + String.join(", ", statuses));
    }

    private String resolveFirstAvailableFontName(String... candidates) {
        List<String> fontNames = Font.getFontNames();
        for (String candidate : candidates) {
            for (String fontName : fontNames) {
                if (fontName.equalsIgnoreCase(candidate)) {
                    return fontName;
                }
            }
        }
        return Font.getDefault().getName();
    }

    private String[] appendFallbackCandidate(String[] candidates, String fallbackCandidate) {
        String[] combined = new String[candidates.length + 1];
        System.arraycopy(candidates, 0, combined, 0, candidates.length);
        combined[candidates.length] = fallbackCandidate;
        return combined;
    }

    /**
     * Builds a temporary stylesheet URL with brand font placeholders resolved.
     *
     * @return URI string to the resolved temporary stylesheet
     * @throws IOException if the template cannot be read or the temp file cannot be written
     */
    private String buildResolvedDarkThemeStylesheetUrl() throws IOException {
        String cssTemplate = readCssTemplate();
        String resolvedCss = cssTemplate
                .replace("__VV_FONT_UI__", escapeFontForCss(uiFont))
                .replace("__VV_FONT_UI_SEMIBOLD__", escapeFontForCss(uiFontSemibold))
                .replace("__VV_FONT_UI_LIGHT__", escapeFontForCss(uiFontLight))
                .replace("__VV_FONT_MONO__", escapeFontForCss(monoFont));

        Path tempCss = Files.createTempFile("vendervault-darktheme-", ".css");
        Files.writeString(tempCss, resolvedCss, StandardCharsets.UTF_8);
        tempCss.toFile().deleteOnExit();
        return tempCss.toUri().toString();
    }

    private String readCssTemplate() throws IOException {
        try (InputStream input = MainApp.class.getResourceAsStream(DARK_THEME_RESOURCE_PATH)) {
            if (input == null) {
                throw new IOException("Missing stylesheet resource: " + DARK_THEME_RESOURCE_PATH);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String escapeFontForCss(String family) {
        return family.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Replaces the default dark theme stylesheet on the given stage with the
     * pre-resolved temporary stylesheet URL.
     */
    private void replaceDarkThemeStylesheet(Stage stage) {
        if (stage.getScene() == null || stage.getScene().getRoot() == null) {
            return;
        }

        List<String> stylesheets = stage.getScene().getStylesheets();
        for (int i = 0; i < stylesheets.size(); i++) {
            if (stylesheets.get(i).endsWith("DarkTheme.css")) {
                stylesheets.set(i, resolvedDarkThemeUrl);
                return;
            }
        }
        stylesheets.add(resolvedDarkThemeUrl);
    }

    void showAlertDialogAndWait(Alert.AlertType type, String title, String headerText, String contentText) {
        Stage owner = null;
        if (mainWindow != null) {
            owner = mainWindow.getPrimaryStage();
        }
        showAlertDialogAndWait(owner, type, title, headerText, contentText);
    }

    /**
     * Shows an alert dialog on {@code owner} with the given parameters.
     * This method only returns after the user has closed the alert dialog.
     */
    private void showAlertDialogAndWait(Stage owner, AlertType type, String title, String headerText,
                                               String contentText) {
        final Alert alert = new Alert(type);
        alert.getDialogPane().getStylesheets().add(resolvedDarkThemeUrl);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setId(ALERT_DIALOG_PANE_FIELD_ID);
        alert.showAndWait();
    }

    /**
     * Shows an error alert dialog with {@code title} and error message, {@code e},
     * and exits the application after the user has closed the alert dialog.
     */
    private void showFatalErrorDialogAndShutdown(String title, Throwable e) {
        logger.severe(title + " " + e.getMessage() + StringUtil.getDetails(e));
        showAlertDialogAndWait(Alert.AlertType.ERROR, title, e.getMessage(), e.toString());
        Platform.exit();
        System.exit(1);
    }

}
