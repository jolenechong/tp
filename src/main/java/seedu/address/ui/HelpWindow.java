package seedu.address.ui;

import static seedu.address.logic.commands.ContactCommand.getContactCommands;
import static seedu.address.logic.commands.GeneralCommand.getGeneralCommands;
import static seedu.address.logic.commands.InventoryCommand.getInventoryCommands;

import java.util.List;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.CommandWord;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://ay2526s2-cs2103t-w08-2.github.io/tp/UserGuide.html";

    public static final String DISPLAY_CONTACT_COMMANDS = "Contact Commands";
    public static final String DISPLAY_INVENTORY_COMMANDS = "Inventory Commands";
    public static final String DISPLAY_GENERAL_COMMANDS = "General Commands";

    private static final String LABEL_HEADER_CLASS = "label-help-header";
    private static final String COMMAND_GROUP_CLASS = "command-group";
    private static final String COMMAND_SEPARATOR_CLASS = "command-separator";
    private static final String COMMAND_NAME_CLASS = "command-name";
    private static final String COMMAND_EXAMPLE_CLASS = "command-example";
    private static final String COMMAND_DESCRIPTION_CLASS = "command-desc";
    private static final String COMMAND_ROW_CLASS = "command-row";

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    private static final int nameLabelSpacing = 90;

    @FXML
    private Button copyButton;

    @FXML
    private VBox commandListContainer;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        populateCommandsToCommandListContainer();
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Populate the {@code commandListContainer} with all of VendorVault commands.
     */
    public void populateCommandsToCommandListContainer() {
        commandListContainer.getChildren().addAll(
                createSectionHeadingLabel(DISPLAY_CONTACT_COMMANDS),
                createCommandGroup(getContactCommands()),
                createSectionHeadingLabel(DISPLAY_INVENTORY_COMMANDS),
                createCommandGroup(getInventoryCommands()),
                createSectionHeadingLabel(DISPLAY_GENERAL_COMMANDS),
                createCommandGroup(getGeneralCommands()));
    }

    private Label createSectionHeadingLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add(LABEL_HEADER_CLASS);
        return label;
    }

    private VBox createCommandGroup(List<CommandWord> commands) {
        VBox group = new VBox(0);
        group.getStyleClass().add(COMMAND_GROUP_CLASS);

        for (int i = 0; i < commands.size(); i++) {
            CommandWord cmd = commands.get(i);
            group.getChildren().add(
                    createCommandBox(cmd.getCommandWord(), cmd.getCommandUsage(), cmd.getCommandDescription()));
            if (i < commands.size() - 1) {
                group.getChildren().add(createSeparator());
            }
        }

        return group;
    }

    private Region createSeparator() {
        Region separator = new Region();
        separator.getStyleClass().add(COMMAND_SEPARATOR_CLASS);
        return separator;
    }

    private HBox createCommandBox(String name, String commandEg, String description) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add(COMMAND_NAME_CLASS);
        nameLabel.setMinWidth(nameLabelSpacing);
        nameLabel.setMaxWidth(nameLabelSpacing);

        Label commandEgLabel = new Label(commandEg);
        commandEgLabel.getStyleClass().add(COMMAND_EXAMPLE_CLASS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add(COMMAND_DESCRIPTION_CLASS);

        HBox row = new HBox(10, nameLabel, commandEgLabel, spacer, descriptionLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add(COMMAND_ROW_CLASS);

        return row;
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
