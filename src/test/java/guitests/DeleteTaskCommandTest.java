package guitests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import seedu.toluist.commons.util.DateTimeUtil;
import seedu.toluist.model.Task;
import seedu.toluist.testutil.TypicalTestTodoLists;

/**
 * Gui tests for delete task command
 */
public class DeleteTaskCommandTest extends ToLuistGuiTest {
    @Test
    public void deleteTask() {
        Task task = new TypicalTestTodoLists().getTypicalTasks()[0];
        String command = "delete 1";
        commandBox.runCommand(command);
        assertFalse(isTaskShown(task));
    }

    @Test
    public void deleteMultipleTasksIndividually() {
        // Start with empty list
        commandBox.runCommand("delete 2");
        commandBox.runCommand("delete 1");

        // add one task
        String taskDescription = "do homework for Melvin";
        String command = "add " + taskDescription;
        commandBox.runCommand(command);
        Task task = new Task(taskDescription);

        // add task with deadline
        String taskDescription2 = "get v0.2 ready";
        LocalDateTime endDate2 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        String command2 = "add " + taskDescription2 + " enddate/" + endDate2;
        commandBox.runCommand(command2);
        Task task2 = new Task(taskDescription2, endDate2);

        // add event
        String taskDescription3 = "attend CS2103T tutorial";
        LocalDateTime startDate3 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        LocalDateTime endDate3 = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        String command3 = "add " + taskDescription3 + " startdate/" + startDate3 + " enddate/" + endDate3;
        commandBox.runCommand(command3);
        Task task3 = new Task(taskDescription3, startDate3, endDate3);

        assertTrue(isTaskShown(task));
        assertTrue(isTaskShown(task2));
        assertTrue(isTaskShown(task3));

        commandBox.runCommand("delete 3");
        assertTrue(isTaskShown(task));
        assertTrue(isTaskShown(task2));
        assertFalse(isTaskShown(task3));

        commandBox.runCommand("delete 1");
        assertFalse(isTaskShown(task));
        assertTrue(isTaskShown(task2));
        assertFalse(isTaskShown(task3));

        commandBox.runCommand("delete 1");
        assertFalse(isTaskShown(task));
        assertFalse(isTaskShown(task2));
        assertFalse(isTaskShown(task3));
    }

    public void deleteMultipleTasksTogether(String deleteCommand, int[] taskNumberLeft) {
        // Start with empty list
        commandBox.runCommand("delete 2");
        commandBox.runCommand("delete 1");

        for (int i = 1; i <= 10; i++) {
            String command = "add task " + i;
            commandBox.runCommand(command);
        }

        commandBox.runCommand(deleteCommand);

        Set<Integer> setOfTaskNumberLeft = new HashSet<Integer>();
        for (int taskNumber: taskNumberLeft) {
            setOfTaskNumberLeft.add(taskNumber);
        }

        for (int i = 1; i <= 10; i++) {
            Task task = new Task("task " + i);
            if (setOfTaskNumberLeft.contains(i)) {
                assertTrue(isTaskShown(task));
            } else {
                assertFalse(isTaskShown(task));
            }
        }
    }

    @Test
    public void deleteMultipleTasksTogether1() {
        String command = "delete 2-4, 6 7  11 ";
        int[] taskNumberLeft = {1, 5, 8, 9, 10};
        deleteMultipleTasksTogether(command, taskNumberLeft);
    }

    @Test
    public void deleteMultipleTasksTogether2() {
        String command = "delete 1-3 5- 7,  9 -10";
        int[] taskNumberLeft = {4, 8};
        deleteMultipleTasksTogether(command, taskNumberLeft);
    }

    @Test
    public void deleteMultipleTasksTogether3() {
        String command = "delete  - 2, 4 - 6, 8- ";
        int[] taskNumberLeft = {3, 7};
        deleteMultipleTasksTogether(command, taskNumberLeft);
    }
}
