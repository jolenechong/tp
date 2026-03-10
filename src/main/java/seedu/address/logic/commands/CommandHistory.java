package seedu.address.logic.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores command text history and supports shell-like navigation through it.
 */
public class CommandHistory {
    private final List<String> commandTexts = new ArrayList<>();
    private int currentIndex = 0;
    private String draftCommandText = "";

    /**
     * Adds a command text to history and resets navigation to the latest position.
     *
     * @param commandText the command text to add to history.
     */
    public void add(String commandText) {
        if (commandText == null || commandText.isBlank()) {
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
        if (commandTexts.isEmpty()) {
            return currentInput;
        }

        if (currentIndex == commandTexts.size()) {
            draftCommandText = currentInput;
        }

        if (currentIndex > 0) {
            currentIndex--;
        }

        return commandTexts.get(currentIndex);
    }

    /**
     * Returns the next command text from history, or the preserved draft when at the latest position.
     *
     * @param currentInput the current input to preserve if history is empty.
     * @return the next command text from history, preserved draft when at the latest position,
     *         or current input if history is empty.
     */
    public String getNext(String currentInput) {
        if (commandTexts.isEmpty()) {
            return currentInput;
        }

        if (currentIndex < commandTexts.size() - 1) {
            currentIndex++;
            return commandTexts.get(currentIndex);
        }

        currentIndex = commandTexts.size();
        return draftCommandText;
    }

    /**
     * Resets cursor to the latest position so the next Up starts from the newest command.
     */
    public void resetNavigation() {
        currentIndex = commandTexts.size();
        draftCommandText = "";
    }
}
