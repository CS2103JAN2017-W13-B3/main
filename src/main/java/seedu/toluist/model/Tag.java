package seedu.toluist.model;

/**
 * Tag model
 */
public class Tag {

    public String tagName;

    public Tag() {}

    /**
     * Validates given tag name.
     */
    public Tag(String name) {
        assert name != null;
        String trimmedName = name.trim();
        this.tagName = trimmedName;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Tag // instanceof handles nulls
                && this.tagName.equals(((Tag) other).tagName)); // state check
    }
}