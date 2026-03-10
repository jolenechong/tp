package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {

    @Test
    public void getPreviousAndNext_navigatesStoredCommands() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("add n/Alice p/123 e/a@x.com a/Blk 1");

        assertEquals("add n/Alice p/123 e/a@x.com a/Blk 1", history.getPrevious(""));
        assertEquals("list", history.getPrevious(""));
        assertEquals("list", history.getPrevious(""));

        assertEquals("add n/Alice p/123 e/a@x.com a/Blk 1", history.getNext(""));
    }

    @Test
    public void getPreviousAndNext_restoresDraftInputAtLatestPosition() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("clear");

        assertEquals("clear", history.getPrevious("add n/Bob"));
        assertEquals("add n/Bob", history.getNext("ignored"));
        assertEquals("add n/Bob", history.getNext("ignored"));
    }

    @Test
    public void add_blankCommand_ignored() {
        CommandHistory history = new CommandHistory();
        history.add("   ");

        assertEquals("draft", history.getNext("draft"));
        assertEquals("draft", history.getPrevious("draft"));
    }

    @Test
    public void add_nullCommand_ignored() {
        CommandHistory history = new CommandHistory();
        history.add(null);

        assertEquals("draft", history.getNext("draft"));
        assertEquals("draft", history.getPrevious("draft"));
    }

}
