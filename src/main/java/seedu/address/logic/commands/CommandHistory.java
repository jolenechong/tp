package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Stores command text history and supports shell-like navigation through it.
 */
public class CommandHistory {

    private static final int LATEST_POSITION = -1;
    private static final Logger logger = LogsCenter.getLogger(CommandHistory.class);

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
            logger.fine("Ignored blank command.");
            return;
        }

        commandTexts.add(commandText);
        logger.info("Command added to history. Total commands: " + commandTexts.size());
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
            logger.fine("History empty. Returning current input.");
            return currentInput;
        }

        if (atLatestPosition()) {
            draftCommandText = currentInput;
            currentIndex = commandTexts.size() - 1;

            logger.fine("Navigated UP to latest history entry. Index: " + currentIndex);
            return getCurrentCommandOrDraft();
        }

        moveIndexBack();
        logger.fine("Navigated UP. Index: " + currentIndex);

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

        if (atLatestPosition()) {
            if (draftCommandText.isEmpty()) {
                logger.fine("Not navigating history. Returning current input.");
                return currentInput;
            }

            logger.fine("At latest position. Restoring draft.");
            return draftCommandText;
        }

        moveIndexForward();

        logger.fine("Navigated DOWN. Index: " + currentIndex);

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
        logger.fine("Navigation reset to latest position.");
    }

    /**
     * Returns an immutable snapshot of the command history.
     */
    public List<String> getHistorySnapshot() {
        return List.copyOf(commandTexts);
    }
}
