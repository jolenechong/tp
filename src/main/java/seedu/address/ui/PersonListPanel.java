package seedu.address.ui;

import java.util.logging.Logger;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private static final int SCROLL_ANIMATION_DURATION_MS = 300;
    private static final String SCROLL_BAR_CSS_CLASS = ".scroll-bar";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /**
     * Smoothly scrolls the person list view to the last item using an animation.
     */
    public void scrollToBottom() {
        int lastIndex = personListView.getItems().size() - 1;
        if (lastIndex < 0) {
            return;
        }

        Platform.runLater(() -> {
            // Animate the scroll to smoothly move to the bottom instead of jumping instantly
            ScrollBar scrollBar = getVerticalScrollBar();
            if (scrollBar == null) {
                personListView.scrollTo(lastIndex);
                return;
            }
            double startValue = scrollBar.getValue();
            double endValue = scrollBar.getMax();
            if (startValue >= endValue) {
                // Skip animation if scroll is already at or beyond the bottom
                return;
            }
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(SCROLL_ANIMATION_DURATION_MS),
                            new KeyValue(scrollBar.valueProperty(), endValue, Interpolator.EASE_BOTH))
            );
            timeline.play();
        });
    }

    /**
     * Returns the vertical {@code ScrollBar} of the person list view, or {@code null} if not found.
     */
    private ScrollBar getVerticalScrollBar() {
        for (Node node : personListView.lookupAll(SCROLL_BAR_CSS_CLASS)) {
            if (node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL) {
                return (ScrollBar) node;
            }
        }
        return null;
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
