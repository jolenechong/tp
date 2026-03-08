package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Test that a {@code Person} matches exactly with another {@code Person}
 */
public class NameEqualsKeywordsPredicate implements Predicate<Person> {
    private final Person person;

    public NameEqualsKeywordsPredicate(Person person) {
        this.person = person;
    }

    @Override
    public boolean test(Person other) {
        return this.person.equals(other);
    }
}
