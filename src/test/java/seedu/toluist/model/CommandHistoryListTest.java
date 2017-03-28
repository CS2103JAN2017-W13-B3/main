//@@author A0162011A
package seedu.toluist.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for Tag model
 */
public class CommandHistoryListTest {
    String command1 = "history";
    String command2 = "list";
    String unfinishedCommand = "hi";

    @Test
    public void testAddNoneWithoutAnyCommand() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 0);
        assertEquals(commandHistoryList.movePointerDown(), unfinishedCommand);
        assertEquals(commandHistoryList.movePointerUp(unfinishedCommand), unfinishedCommand);
        assertEquals(commandHistoryList.movePointerDown(), unfinishedCommand);

    }

    @Test
    public void testAddNoneWithUnfinishedCommand() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 0);
        assertEquals(commandHistoryList.movePointerDown(), "");
        assertEquals(commandHistoryList.movePointerUp(unfinishedCommand), unfinishedCommand);
        assertEquals(commandHistoryList.movePointerDown(), unfinishedCommand);
        assertEquals(commandHistory.size(), 0);
    }

    @Test
    public void testAddOneWithoutUnfinishedCommand() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory;
        commandHistoryList.recordCommand(command1);
        commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 1);
        assertEquals(commandHistoryList.movePointerUp(""), command1);
        assertEquals(commandHistoryList.movePointerDown(), "");
        assertEquals(commandHistory.size(), 1);
    }
    
    @Test
    public void testAddOneWithUnfinishedCommand() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory;
        commandHistoryList.recordCommand(command1);
        commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 1);
        assertEquals(commandHistoryList.movePointerUp(unfinishedCommand), command1);
        assertEquals(commandHistoryList.movePointerDown(), unfinishedCommand);
        assertEquals(commandHistory.size(), 1);
    }

    @Test
    public void testAddOneThenMovePointerUpALot() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory;
        commandHistoryList.recordCommand(command1);
        commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 1);
        for (int i = 0; i < 10; i++) {
            assertEquals(commandHistoryList.movePointerUp(""), command1);
        }
        assertEquals(commandHistory.size(), 1);
    }

    @Test
    public void testAddOneThenMovePointerDownALot() {
        CommandHistoryList commandHistoryList = new CommandHistoryList();
        ArrayList<String> commandHistory;
        commandHistoryList.recordCommand(command1);
        commandHistory = commandHistoryList.getCommandHistory();
        assertEquals(commandHistory.size(), 1);
        for (int i = 0; i < 10; i++) {
            assertEquals(commandHistoryList.movePointerDown(), "");
        }
        assertEquals(commandHistory.size(), 1);
    }
}
