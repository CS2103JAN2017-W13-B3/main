//@@author A0162011A
package guitests;

import org.junit.Test;

import seedu.toluist.commons.core.Messages;
import seedu.toluist.model.Tag;
import seedu.toluist.model.Task;
import seedu.toluist.testutil.TypicalTestTodoLists;

/**
 * Gui Tests for tag command
 */
public class TagCommandTest extends ToLuistGuiTest {
    String tagName = "aTag";

    @Test
    public void addTag_singleTag() {
        Task task = new TypicalTestTodoLists().getTypicalTasks()[0];
        task.addTag(new Tag(tagName));
        String command = "tag 1 " + tagName;
        runCommandThenCheckForTasks(command, new Task[] { task }, new Task[0]);
    }

    @Test
    public void addTag_MultipleTags() {
        String tagName2 = "bTag";
        Task task = new TypicalTestTodoLists().getTypicalTasks()[0];
        task.addTag(new Tag(tagName));
        task.addTag(new Tag(tagName2));
        String command = " taG 1 " + tagName + " " + tagName2;
        runCommandThenCheckForTasks(command, new Task[] { task }, new Task[0]);
    }

    @Test
    public void addTag_MultipleTagsWithDuplicate() {
        Task task = new TypicalTestTodoLists().getTypicalTasks()[0];
        task.addTag(new Tag(tagName));
        String command = " taG 1 " + tagName + " " + tagName;
        runCommandThenCheckForTasks(command, new Task[] { task }, new Task[0]);
    }

    @Test
    public void addTag_InvalidIndex() {
        String[] validCommandWithInvalidIndex = { "tag 0 aTag", "tag 1000 aTag"};
        for (String command : validCommandWithInvalidIndex) {
            runCommandThenCheckForResultMessage(command, Messages.MESSAGE_INVALID_TASK_INDEX);
        }
    }

    @Test
    public void addTag_InvalidFormat() {
        String[] invalidCommands = { "tag", "tag aTag", "tag aTag bTag", "tag aTag 1",  "tag 1"};
        for (String command : invalidCommands) {
            runCommandThenCheckForResultMessage(command,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "tag"));
        }
    }
}
