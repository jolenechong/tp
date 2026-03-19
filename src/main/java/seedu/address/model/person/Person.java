package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.person.Phone.VALIDATION_EXCLUDE_DIGITS_REGEX;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String SEPARATOR_SPACE = "\\s+";
    private static final int ZERO_INDEX = 0;

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
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null && otherPerson.getEmail().equals(getEmail());
    }

    /**
     * Returns true if this person has a similar name to {@code otherPerson}.
     * Intended for use by {@code AddressBook} similarity checks.
     */
    public boolean isSimilarNameTo(Person otherPerson) {
        return hasSimilarName(otherPerson);
    }

    /**
     * Returns true if this person has a similar address to {@code otherPerson}.
     * Intended for use by {@code AddressBook} similarity checks.
     */
    public boolean isSimilarAddressTo(Person otherPerson) {
        return hasSimilarAddress(otherPerson);
    }

    /**
     * Returns true if this person has a similar phone number to {@code otherPerson}.
     * Similarity is determined by having Phone.MIN_LENGTH contiguous digits in common.
     * Intended for use by {@code AddressBook} similarity checks.
     */
    public boolean isSimilarPhoneTo(Person otherPerson) {
        return hasSimilarPhone(otherPerson);
    }

    private boolean hasSimilarName(Person otherPerson) {
        String thisName = normalizeName(this.name.fullName);
        String otherName = normalizeName(otherPerson.getName().fullName);

        if (thisName.equals(otherName)) {
            return true;
        }

        String[] thisParts = thisName.split(SEPARATOR_SPACE);
        String[] otherParts = otherName.split(SEPARATOR_SPACE);

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

    private boolean hasSimilarPhone(Person otherPerson) {
        if (otherPerson == null) {
            return false;
        }

        String thisPhoneDigits = this.phone.value.replaceAll(VALIDATION_EXCLUDE_DIGITS_REGEX, "");
        String otherPhoneDigits = otherPerson.getPhone().value.replaceAll(VALIDATION_EXCLUDE_DIGITS_REGEX, "");

        if (thisPhoneDigits.isEmpty() || otherPhoneDigits.isEmpty()
                || thisPhoneDigits.length() < Phone.MIN_LENGTH || otherPhoneDigits.length() < Phone.MIN_LENGTH) {
            return false;
        }

        return hasContiguousMatch(thisPhoneDigits, otherPhoneDigits);
    }

    private boolean hasContiguousMatch(String str1, String str2) {
        if (str1.length() < Phone.MIN_LENGTH || str2.length() < Phone.MIN_LENGTH) {
            return false;
        }

        Set<String> contiguousSubstrings = new HashSet<>();
        for (int i = ZERO_INDEX; i <= str1.length() - Phone.MIN_LENGTH; i++) {
            contiguousSubstrings.add(str1.substring(i, i + Phone.MIN_LENGTH));
        }

        for (int i = ZERO_INDEX; i <= str2.length() - Phone.MIN_LENGTH; i++) {
            if (contiguousSubstrings.contains(str2.substring(i, i + Phone.MIN_LENGTH))) {
                return true;
            }
        }

        return false;
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
