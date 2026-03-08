package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.person.warnings.DuplicatePersonWarning;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    private static final String PHONE_SPECIFICATIONS_SEPARATOR = "(";
    private static final String WHITESPACE_REGEX = "\\s+";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both contacts are considered the same
     * 1. They have the same email
     * 2. They have the same phone number or same as at least one of comma-separated phone numbers
     * Note: The (specification) part of the phone number is ignored when comparing phone numbers.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (otherPerson.getEmail().equals(getEmail())
                || hasOverlappingPhoneEntry(otherPerson));
    }

    private boolean hasOverlappingPhoneEntry(Person otherPerson) {
        Set<String> currentPhoneEntries = getNormalizedPhoneEntries(phone.value);
        Set<String> otherPhoneEntries = getNormalizedPhoneEntries(otherPerson.getPhone().value);
        return currentPhoneEntries.stream().anyMatch(otherPhoneEntries::contains);
    }

    private static Set<String> getNormalizedPhoneEntries(String phoneValue) {
        return Arrays.stream(phoneValue.split(ParserUtil.COMMA_SEPARATOR))
                .map(String::trim)
                .map(Person::extractPhoneNumberPart)
                .collect(Collectors.toSet());
    }

    private static String extractPhoneNumberPart(String phoneEntry) {
        int specificationStart = phoneEntry.indexOf(PHONE_SPECIFICATIONS_SEPARATOR);
        if (specificationStart == -1) {
            return phoneEntry;
        }
        return phoneEntry.substring(0, specificationStart).trim();
    }

    /**
     * Returns true if both contacts have similar characteristics that warrant a warning.
     * Checks for:
     * 1. Similar names (case-insensitive or normalized whitespace match or overlapping name parts)
     * 2. Similar addresses (partial substring match)
     */
    public DuplicatePersonWarning isSamePersonWarn(Person otherPerson) {
        if (hasSimilarName(otherPerson)) {
            return new DuplicatePersonWarning(true, DuplicatePersonWarning.MESSAGE_SIMILAR_NAME);
        }

        return new DuplicatePersonWarning(
                hasSimilarAddress(otherPerson),
                DuplicatePersonWarning.MESSAGE_SIMILAR_ADDRESS);
    }

    private boolean hasSimilarName(Person otherPerson) {
        String thisName = normalizeName(this.name.fullName);
        String otherName = normalizeName(otherPerson.getName().fullName);

        if (thisName.equals(otherName)) {
            return true;
        }

        String[] thisParts = thisName.split(ParserUtil.SPACE_SEPARATOR);
        String[] otherParts = otherName.split(ParserUtil.SPACE_SEPARATOR);

        Set<String> nameParts = new HashSet<>(Arrays.asList(thisParts));

        for (String part : otherParts) {
            if (nameParts.contains(part)) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeName(String name) {
        return name.toLowerCase().trim().replaceAll(WHITESPACE_REGEX, " ");
    }

    private boolean hasSimilarAddress(Person otherPerson) {
        String thisAddress = this.address.value.toLowerCase().trim();
        String otherAddress = otherPerson.getAddress().value.toLowerCase().trim();

        return thisAddress.contains(otherAddress) || otherAddress.contains(thisAddress);
    }

    /**
     * Returns true if this person is archived.
     */
    public boolean isArchived() {
        return tags.contains(new Tag("archived"));
    }

    /**
     * Returns a new Person with the archived tag added.
     */
    public Person archive() {
        Set<Tag> newTags = new HashSet<>(tags);
        newTags.add(new Tag("archived"));
        return new Person(name, phone, email, address, newTags);
    }

    /**
     * Returns a new Person with the archived tag removed.
     */
    public Person restore() {
        Set<Tag> newTags = new HashSet<>(tags);
        newTags.remove(new Tag("archived"));
        return new Person(name, phone, email, address, newTags);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
