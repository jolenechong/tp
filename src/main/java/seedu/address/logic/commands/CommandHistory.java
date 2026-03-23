package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores command text history and supports shell-like navigation through it.
 */
public class CommandHistory {

    private static final int LATEST_POSITION = -1;
    private final List<String> commandTexts = new ArrayList<>();
    private int currentIndex = LATEST_POSITION;
    private String draftCommandText = "";

    /**
     * Adds a command text to history and resets navigation to the latest position.
     *
     * @param commandText the command text to add to history.
     */
    public void add(String commandText) {
        requireNonNull(commandText);

        if (commandText.isBlank()) {
            return;
        }

        commandTexts.add(commandText);
        resetNavigation();
    }

    /**
     * Returns the previous command text from history, or the current input if history is empty.
     *
     * @param currentInput the current input to preserve if history is empty or
     *                     when navigating back to the latest position.
     */
    public String getPrevious(String currentInput) {
        requireNonNull(currentInput);

        if (commandTexts.isEmpty()) {
            return currentInput;
        }

        if (atLatestPosition()) {
            draftCommandText = currentInput;
            currentIndex = commandTexts.size() - 1;
            return getCurrentCommandOrDraft();
        }

        moveIndexBack();

        return getCurrentCommandOrDraft();
    }

    /**
     * Returns the next command text from history, or the preserved draft when at the latest position.
     *
     * @param currentInput the current input to preserve if history is empty.
     * @return the next command text from history, preserved draft when at the latest position,
     *         or current input if history is empty.
     */
    public String getNext(String currentInput) {
        requireNonNull(currentInput);

        if (commandTexts.isEmpty()) {
            return currentInput;
        }

        moveIndexForward();

        return getCurrentCommandOrDraft();
    }

    private boolean atLatestPosition() {
        return currentIndex == LATEST_POSITION;
    }

    private void moveIndexBack() {
        if (currentIndex > 0) {
            currentIndex--;
        }
    }

    private void moveIndexForward() {
        if (atLatestPosition()) {
            return; // already at draft
        }
        if (currentIndex < commandTexts.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = LATEST_POSITION;
        }
    }

    private String getCurrentCommandOrDraft() {
        assert currentIndex >= -1 && currentIndex < commandTexts.size()
                : "currentIndex out of bounds: " + currentIndex;

        if (atLatestPosition()) {
            return draftCommandText;
        }
        return commandTexts.get(currentIndex);
    }

    /**
     * Resets cursor to the latest position so the next Up starts from the newest command.
     */
    public void resetNavigation() {
        currentIndex = LATEST_POSITION;
        draftCommandText = "";
    }

    /**
     * Returns an immutable snapshot of the command history.
     */
    public List<String> getHistorySnapshot() {
        return List.copyOf(commandTexts);
    }
}
