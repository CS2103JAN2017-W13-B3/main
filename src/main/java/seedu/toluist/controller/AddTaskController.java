//@@author A0127545A
package seedu.toluist.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import seedu.toluist.commons.core.LogsCenter;
import seedu.toluist.commons.util.DateTimeUtil;
import seedu.toluist.commons.util.StringUtil;
import seedu.toluist.controller.commons.TagParser;
import seedu.toluist.controller.commons.TaskTokenizer;
import seedu.toluist.model.Tag;
import seedu.toluist.model.Task;
import seedu.toluist.model.TodoList;
import seedu.toluist.ui.commons.CommandResult;

/**
 * AddTaskController is responsible for adding a task (and event)
 */
public class AddTaskController extends Controller {
    private static final Logger logger = LogsCenter.getLogger(AddTaskController.class);

    private static final String COMMAND_TEMPLATE = "(?iu)^add"
            + "(\\s+(?<description>.+))?";

    private static final String COMMAND_ADD_TASK = "add";

    private static final String RESULT_MESSAGE_ADD_TASK = "New task added";
    private static final String RESULT_MESSAGE_ERROR_DATE_INPUT =
            "Something is wrong with the given dates input";

    public void execute(String command) {
        logger.info(getClass().getName() + " will handle command");

        TodoList todoList = TodoList.getInstance();
        CommandResult commandResult;

        HashMap<String, String> tokens = tokenize(command);

        String description = tokens.get(TaskTokenizer.PARAMETER_TASK_DESCRIPTION);

        String eventStartDateToken = tokens.get(TaskTokenizer.KEYWORD_EVENT_START_DATE);
        LocalDateTime eventStartDateTime = DateTimeUtil.parseDateString(eventStartDateToken);

        String eventEndDateToken = tokens.get(TaskTokenizer.KEYWORD_EVENT_END_DATE);
        LocalDateTime eventEndDateTime = DateTimeUtil.parseDateString(eventEndDateToken);

        String taskDeadlineToken = tokens.get(TaskTokenizer.KEYWORD_TASK_DEADLINE);
        LocalDateTime taskDeadline = DateTimeUtil.parseDateString(taskDeadlineToken);

        String tagsToken = tokens.get(TaskTokenizer.KEYWORD_TASK_TAGS);
        Set<Tag> tags = TagParser.parseTags(tagsToken);

        String taskPriority = tokens.get(TaskTokenizer.KEYWORD_TASK_PRIORITY);

        String recurringFrequency = tokens.get(TaskTokenizer.TASK_RECURRING_FREQUENCY_KEYWORD);

        String recurringUntilEndDateToken = tokens.get(TaskTokenizer.TASK_RECURRING_UNTIL_END_DATE);
        LocalDateTime recurringUntilEndDate = DateTimeUtil.parseDateString(recurringUntilEndDateToken);

        commandResult = add(todoList, description, eventStartDateTime, eventEndDateTime,
                taskDeadline, taskPriority, tags, recurringFrequency, recurringUntilEndDate);

        if (todoList.save()) {
            uiStore.setTasks(todoList.getTasks());
        }

        uiStore.setCommandResult(commandResult);
    }

    public HashMap<String, String> tokenize(String command) {
        return TaskTokenizer.tokenize(COMMAND_TEMPLATE, command, false, true);
    }

    private CommandResult add(TodoList todoList, String description,
            LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime,
            LocalDateTime taskDeadline, String taskPriority, Set<Tag> tags,
            String recurringFrequency, LocalDateTime recurringUntilEndDate) {
        if (!isValidTaskType(eventStartDateTime, eventEndDateTime, taskDeadline)) {
            return new CommandResult(RESULT_MESSAGE_ERROR_DATE_INPUT);
        }

        Task task;
        if (eventStartDateTime != null && eventEndDateTime != null) {
            task = new Task(description, eventStartDateTime, eventEndDateTime);
        } else if (taskDeadline != null) {
            task = new Task(description, taskDeadline);
        } else {
            task = new Task(description);
        }
        if (taskPriority != null) {
            task.setTaskPriority(taskPriority);
        }
        if (StringUtil.isPresent(recurringFrequency)) {
            if (recurringUntilEndDate == null) {
                task.setRecurring(recurringFrequency);
            } else {
                task.setRecurring(recurringUntilEndDate, recurringFrequency);
            }
        }
        task.replaceTags(tags);
        todoList.add(task);
        return new CommandResult(RESULT_MESSAGE_ADD_TASK);
    }

    /**
     * Checks whether the user input for dates is valid.
     * The input is valid if there is only one possible task type, or zero (task type is floating task).
     * @param eventStartDateTime
     * @param eventEndDateTime
     * @param taskDeadline
     * @return true if there is at most 1 possible task type
     */
    private boolean isValidTaskType(LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime,
            LocalDateTime taskDeadline) {
        int numberOfTaskTypes = 0;
        // Must have both event start date time AND end date time to be valid event
        if (eventStartDateTime != null && eventEndDateTime != null) {
            numberOfTaskTypes++;
        }
        if (taskDeadline != null) {
            numberOfTaskTypes++;
        }
        return numberOfTaskTypes <= 1;
    }

    public boolean matchesCommand(String command) {
        return command.matches(COMMAND_TEMPLATE);
    }

    public static String[] getCommandWords() {
        return new String[] { COMMAND_ADD_TASK };
    }
}
