//@@author A0127545A
package guitests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import seedu.toluist.commons.util.DateTimeUtil;
import seedu.toluist.model.Tag;
import seedu.toluist.model.Task;
import seedu.toluist.model.Task.TaskPriority;

/**
 * Gui tests for add task command
 */
public class AddTaskCommandTest extends ToLuistGuiTest {

    private static IllegalArgumentException illegalArgumentException1 =
            new IllegalArgumentException("Description must not be empty.");
    private static IllegalArgumentException illegalArgumentException2 =
            new IllegalArgumentException("Start date must be before end date.");
    private static IllegalArgumentException illegalArgumentException3 =
            new IllegalArgumentException("Task priority must be either 'low' or 'high'.");
    private static IllegalArgumentException illegalArgumentException4 =
            new IllegalArgumentException("Invalid recurring frequency string");
    private Tag tag1 = new Tag("tag1");
    private Tag tag2 = new Tag("tag2");
    private Tag tag3 = new Tag("tag3");

    @Test
    public void addFloatingTask() {
        // add one task
        String taskDescription = "do homework for Melvin";
        String command = "add " + taskDescription;
        commandBox.runCommand(command);
        Task task = new Task(taskDescription);
        assertTrue(isTaskShown(task));

        // add another task, with tags
        String taskDescription2 = "drink Koi after school";
        String command2 = "add " + taskDescription2 + " tags/tag1 tag2 tag3 priority/high";
        commandBox.runCommand(command2);
        Task task2 = new Task(taskDescription2, null, null);
        task2.setTaskPriority(TaskPriority.HIGH);
        task2.replaceTags(new ArrayList<>(Arrays.asList(tag1, tag2, tag3)));
        assertTrue(areTasksShown(task, task2));
    }

    @Test
    public void addTaskWithDeadline() {
        // add task with deadline
        String taskDescription1 = "get v0.2 ready";
        LocalDateTime endDate1 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        String command1 = "add " + taskDescription1 + " by/" + endDate1;
        commandBox.runCommand(command1);
        Task task1 = new Task(taskDescription1, endDate1);
        assertTrue(isTaskShown(task1));

        // add task without description with deadline
        String taskDescription2 = "";
        LocalDateTime endDate2 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        String command2 = "add " + taskDescription2 + " by/" + endDate2;
        Task task2 = null;
        try {
            commandBox.runCommand(command2);
            task2 = new Task(taskDescription2, endDate2);
            fail("Should throw an IllegalArgumentException here since description is empty.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException1.getMessage()));
        }
        assertTrue(isTaskShown(task1));
        assertFalse(isTaskShown(task2));

        // add another task with deadline and tags
        String taskDescription3 = "get v0.3 ready";
        LocalDateTime endDate3 = DateTimeUtil.parseDateString("22 Mar 2017, 12pm");
        String command3 = "add " + taskDescription3 + " by/" + endDate3 + " priority/low tags/ tag1 tag2";
        commandBox.runCommand(command3);
        Task task3 = new Task(taskDescription3, null, endDate3);
        task3.replaceTags(new ArrayList<>(Arrays.asList(tag1, tag2)));
        assertTrue(areTasksShown(task1, task3));
        assertFalse(isTaskShown(task2));
    }

    @Test
    public void addEvent() {
        // add event
        String taskDescription1 = "attend CS2103T tutorial";
        LocalDateTime startDate1 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        LocalDateTime endDate1 = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        String command1 = "add " + taskDescription1 + " from/" + startDate1 + " to/" + endDate1;
        commandBox.runCommand(command1);
        Task task1 = new Task(taskDescription1, startDate1, endDate1);
        assertTrue(isTaskShown(task1));

        // add event with start date after end date
        String taskDescription2 = "attend CS2103T tutorial";
        LocalDateTime startDate2 = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        LocalDateTime endDate2 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        String command2 = "add " + taskDescription2 + " from/" + startDate2 + " to/" + endDate2;
        Task task2 = null;
        try {
            commandBox.runCommand(command2);
            task2 = new Task(taskDescription2, startDate2, endDate2);
            fail("Should throw an IllegalArgumentException here since start date is after end date.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException2.getMessage()));
        }
        assertTrue(isTaskShown(task1));
        assertFalse(isTaskShown(task2));

        // add event without description
        String taskDescription3 = "";
        LocalDateTime startDate3 = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        LocalDateTime endDate3 = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        String command3 = "add " + taskDescription3 + " from/" + startDate3 + " to/" + endDate3;
        Task task3 = null;
        try {
            commandBox.runCommand(command3);
            task3 = new Task(taskDescription3, startDate3, endDate3);
            fail("Should throw an IllegalArgumentException here since description is empty.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException1.getMessage()));
        }
        assertTrue(isTaskShown(task1));
        assertFalse(isTaskShown(task2));
        assertFalse(isTaskShown(task3));

        // add another event with tags
        String taskDescription4 = "attend CS2101 tutorial";
        LocalDateTime startDate4 = DateTimeUtil.parseDateString("16 Mar 2017, 10am");
        LocalDateTime endDate4 = DateTimeUtil.parseDateString("16 Mar 2017, 12pm");
        String command4 = "add " + taskDescription4 + " tags/tag3" +
                 " from/" + startDate4 + " to/" + endDate4 + " priority/high";
        commandBox.runCommand(command4);
        Task task4 = new Task(taskDescription4, startDate4, endDate4);
        task4.setTaskPriority(TaskPriority.HIGH);
        task4.replaceTags(new ArrayList<>(Arrays.asList(tag3)));
        assertTrue(areTasksShown(task1, task4));
        assertFalse(isTaskShown(task2));
        assertFalse(isTaskShown(task3));
    }

    @Test
    public void addMultipleTypeTask_shouldNotBeCreated() {
        String taskDescription = "attend CS2103T tutorial";
        LocalDateTime startDate = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        LocalDateTime endDate1 = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        LocalDateTime endDate2 = DateTimeUtil.parseDateString("15 Mar 2017, 5pm");
        String command1 = "add " + taskDescription + " from/" + startDate + " to/" + endDate1 + " by/" + endDate2;
        commandBox.runCommand(command1);
        Task task1 = new Task(taskDescription, startDate, endDate1);
        Task task2 = new Task(taskDescription, startDate, endDate2);
        Task task3 = new Task(taskDescription, endDate2);
        Task task4 = new Task(taskDescription);
        assertFalse(isTaskShown(task1));
        assertFalse(isTaskShown(task2));
        assertFalse(isTaskShown(task3));
        assertFalse(isTaskShown(task4));
    }

    @Test
    public void addTaskWithInvalidPriorityLevel_shouldNotBeCreated() {
        String taskDescription = "attend CS2103T tutorial";
        String priorityString = "high low";
        String command = "add " + taskDescription + " priority/" + priorityString;
        Task task1 = null;
        Task task2 = null;
        try {
            commandBox.runCommand(command);
            task1 = new Task(taskDescription);
            task2 = new Task(taskDescription);
            task2.setTaskPriority(priorityString);
            fail("Should not reach here since priority is both high and low at the same time.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException3.getMessage()));
        }
        assertFalse(isTaskShown(task1));
        assertFalse(isTaskShown(task2));

        priorityString = "";
        command = "add " + taskDescription + " priority/" + priorityString;
        try {
            commandBox.runCommand(command);
            task1 = new Task(taskDescription);
            task2 = new Task(taskDescription);
            task2.setTaskPriority(priorityString);
            fail("Should not reach here since priority is blank.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException3.getMessage()));
        }
        assertFalse(isTaskShown(task1));
        assertFalse(isTaskShown(task2));
    }

    @Test
    public void addRecurringFloatingTask() {
        String taskDescription = "shower";
        String recurFrequencyString = "daily";
        String command = "add " + taskDescription + " repeat/" + recurFrequencyString;
        commandBox.runCommand(command);
        Task task1 = new Task(taskDescription);
        task1.setRecurring(recurFrequencyString);
        assertTrue(isTaskShown(task1));

        LocalDateTime recurUntilEndDate = DateTimeUtil.parseDateString("11 April 2017, 2pm");
        command = "add " + taskDescription + " repeat/" + recurFrequencyString + " repeatuntil/" + recurUntilEndDate;
        commandBox.runCommand(command);
        Task task2 = new Task(taskDescription);
        task2.setRecurring(recurUntilEndDate, recurFrequencyString);
        assertTrue(areTasksShown(task1, task2));
    }

    @Test
    public void addRecurringTaskWithDeadline() {
        String taskDescription = "shower";
        String recurFrequencyString = "daily";
        LocalDateTime endDate = DateTimeUtil.parseDateString("9pm");
        String command = "add " + taskDescription + " by/" + endDate + " repeat/" + recurFrequencyString;
        commandBox.runCommand(command);
        Task task1 = new Task(taskDescription, endDate);
        task1.setRecurring(recurFrequencyString);
        assertTrue(isTaskShown(task1));

        taskDescription = "do CS2103T project";
        recurFrequencyString = "weekly";
        endDate = DateTimeUtil.parseDateString("28 April 2017, 11pm");
        LocalDateTime recurUntilEndDate = DateTimeUtil.parseDateString("11 April 2017, 2pm");
        command = "add " + taskDescription + " by/" + endDate + " repeat/" + recurFrequencyString
                + " repeatuntil/" + recurUntilEndDate;
        commandBox.runCommand(command);
        Task task2 = new Task(taskDescription, endDate);
        task2.setRecurring(recurUntilEndDate, recurFrequencyString);
        assertTrue(areTasksShown(task1, task2));
    }

    @Test
    public void addRecurringEvent() {
        String taskDescription = "shower";
        String recurFrequencyString = "daily";
        LocalDateTime from = DateTimeUtil.parseDateString("9pm");
        LocalDateTime to = DateTimeUtil.parseDateString("10pm");
        String command = "add " + taskDescription + " from/" + from + " to/" + to + " repeat/" + recurFrequencyString;
        commandBox.runCommand(command);
        Task task1 = new Task(taskDescription, from, to);
        task1.setRecurring(recurFrequencyString);
        assertTrue(isTaskShown(task1));

        taskDescription = "do CS2103T project";
        recurFrequencyString = "weekly";
        from = DateTimeUtil.parseDateString("28 April 2017, 9pm");
        to = DateTimeUtil.parseDateString("28 April 2017, 11pm");
        LocalDateTime recurUntilEndDate = DateTimeUtil.parseDateString("11 April 2017, 2pm");
        command = "add " + taskDescription + " from/" + from + " to/" + to + " repeat/" + recurFrequencyString
                + " repeatuntil/" + recurUntilEndDate;
        commandBox.runCommand(command);
        Task task2 = new Task(taskDescription, from, to);
        task2.setRecurring(recurUntilEndDate, recurFrequencyString);
        assertTrue(areTasksShown(task1, task2));
    }

    @Test
    public void addRecurringTaskWithWrongParams_shouldThrowException() {
        // Recurring event with empty repeat
        String taskDescription = "shower";
        LocalDateTime from = DateTimeUtil.parseDateString("15 Mar 2017, 12pm");
        LocalDateTime to = DateTimeUtil.parseDateString("15 Mar 2017, 1pm");
        String recurFrequencyString = "";
        String command = "add " + taskDescription + " repeat/" + recurFrequencyString
                + " from/" + from + " to/" + to;
        Task task = null;
        try {
            commandBox.runCommand(command);
            task = new Task(taskDescription, from, to);
            task.setRecurring(recurFrequencyString);
            fail("Should not reach here since recurring task must have a repeat frequency.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException4.getMessage()));
        }
        assertTrue(isTaskShown(task));

        // Recurring event with two repeats
        recurFrequencyString = "weekly yearly";
        command = "add " + taskDescription + " repeat/" + recurFrequencyString
                + " from/" + from + " to/" + to;
        Task task2 = null;
        try {
            commandBox.runCommand(command);
            task2 = new Task(taskDescription, from, to);
            task2.setRecurring(recurFrequencyString);
            fail("Should not reach here since recurring task must have only one repeat frequency.");
        } catch (IllegalArgumentException illegalArgumentException) {
            assertTrue(illegalArgumentException.getMessage().equals(illegalArgumentException4.getMessage()));
        }
        assertTrue(isTaskShown(task2));
    }
}
