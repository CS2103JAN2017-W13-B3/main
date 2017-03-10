package seedu.toluist.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a Task
 */
public class Task implements Comparable<Task> {

    // List of tags is unique
    private HashSet<Tag> allTags = new HashSet<>();
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime completionDateTime;

    /**
     * To be used with json deserialisation
     */
    public Task() {}

    public Task(String description) {
        this(description, null, null);
    }

    public Task(String description, LocalDateTime endDateTime) {
        this(description, null, endDateTime);
    }

    public Task(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.setDescription(description.trim());
        this.setStartDateTime(startDateTime);
        this.setEndDateTime(endDateTime);
        validate();
    }

    public void validate() {
        if (!validateDescriptionMustNotBeEmpty()) {
            throw new IllegalArgumentException("Description must not be empty.");
        }
        if (!validateStartDateMustBeBeforeEndDate()) {
            throw new IllegalArgumentException("Start date must be before end date.");
        }
        if (!validateTaskIsFloatingIsEventOrHasDeadline()) {
            throw new IllegalArgumentException("Task must be floating, must be an event, or has deadline,");
        }
    }

    public boolean validateDescriptionMustNotBeEmpty() {
        return description != null && !description.isEmpty();
    }

    public boolean validateStartDateMustBeBeforeEndDate() {
        if (startDateTime != null && endDateTime != null) {
            return startDateTime.isBefore(endDateTime);
        }
        return true;
    }

    public boolean validateTaskIsFloatingIsEventOrHasDeadline() {
        return startDateTime == null || endDateTime != null;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Task // instanceof handles nulls
                && this.description.equals(((Task) other).description)) // state check
                && this.allTags.equals(((Task) other).allTags)
                && Objects.equals(this.startDateTime, ((Task) other).startDateTime) // handles null
                && Objects.equals(this.endDateTime, ((Task) other).endDateTime) // handles null
                && Objects.equals(this.completionDateTime, ((Task) other).completionDateTime); // handles null
    }

    public void setCompleted(boolean isCompleted) {
        if (isCompleted) {
            completionDateTime = LocalDateTime.now();
        } else {
            completionDateTime = null;
        }
    }

    public void setDeadLine(LocalDateTime deadLine) {
        setEndDateTime(null);
        setStartDateTime(deadLine);
    }

    public void setFromTo(LocalDateTime from, LocalDateTime to) {
        assert from.isBefore(to);
        setStartDateTime(from);
        setEndDateTime(to);
    }

    public boolean addTag(Tag tag) {
        boolean isDifferent = true;
        if (allTags.contains(tag)) {
            isDifferent = false;
        }

        if (isDifferent) {
            this.allTags.add(tag);
        }

        return isDifferent;
    }

    public void removeTag(Tag tag) {
        this.allTags.remove(tag);
    }

    public void replaceTags(Collection<Tag> tags) {
        this.allTags = new HashSet<>(tags);
    }

    public HashSet<Tag> getAllTags() {
        return allTags;
    }

    public boolean isOverdue() {
        return !isCompleted() && (endDateTime != null && endDateTime.isBefore(LocalDateTime.now()));
    }

    public boolean isTask() {
        return startDateTime == null && endDateTime != null;
    }

    public boolean isEvent() {
        return startDateTime != null && endDateTime != null;
    }

    public boolean isCompleted() {
        return completionDateTime != null && completionDateTime.isBefore(LocalDateTime.now());
    }

    public boolean isAnyKeywordsContainedInDescriptionIgnoreCase(String[] keywords) {
        for (String keyword: keywords) {
            if (description.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAnyKeywordsContainedInAnyTagIgnoreCase(String[] keywords) {
        for (String keyword: keywords) {
            for (Tag tag : allTags) {
                if (tag.tagName.toLowerCase().contains(keyword.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int compareTo(Task comparison) {
        if (endDateTime.compareTo(comparison.endDateTime) != 0) {
            return endDateTime.compareTo(comparison.endDateTime);
        } else if (false) {
            //TODO add priority comparison with variable
            return -1;
        } else {
            return this.description.compareToIgnoreCase(comparison.description);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }
}
