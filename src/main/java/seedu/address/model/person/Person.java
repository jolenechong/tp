package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.person.Phone.VALIDATION_EXCLUDE_DIGITS_REGEX;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String COMMA_REGEX = ",";
    private static final int START_INDEX = 0;

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
     * Returns true if this person has a name similar to the specified {@code otherPerson}.
     * <p>
     * Two names are considered similar if they share one or more words.
     * </p>
     *
     * @param otherPerson the person to compare with
     * @return {@code true} if the names are similar, {@code false} otherwise
     */
    public boolean isSimilarNameTo(Person otherPerson) {
        requireNonNull(otherPerson);
        return hasSimilarName(otherPerson);
    }

    /**
     * Returns true if this person has an address similar to the specified {@code otherPerson}.
     * <p>
     * Two addresses are considered similar if one fully contains the other.
     * </p>
     *
     * @param otherPerson the person to compare with
     * @return {@code true} if the addresses are considered similar, {@code false} otherwise
     */
    public boolean isSimilarAddressTo(Person otherPerson) {
        requireNonNull(otherPerson);
        return hasSimilarAddress(otherPerson);
    }

    /**
     * Returns true if this person has a phone number similar to the specified {@code otherPerson}.
     * <p>
     * Two phone numbers are considered similar if they share any
     * {@code Phone.MIN_LENGTH} contiguous digits.
     * </p>
     *
     * @param otherPerson the person to compare with
     * @return {@code true} if the phone numbers are similar, {@code false} otherwise
     */
    public boolean isSimilarPhoneTo(Person otherPerson) {
        requireNonNull(otherPerson);
        return hasSimilarPhone(otherPerson);
    }

    private boolean hasSimilarName(Person otherPerson) {
        String thisName = normalizeName(this.name.fullName);
        String otherName = normalizeName(otherPerson.getName().fullName);

        if (thisName.equals(otherName)) {
            return true;
        }

        String[] thisParts = thisName.split(WHITESPACE_REGEX);
        String[] otherParts = otherName.split(WHITESPACE_REGEX);

        Set<String> nameParts = new HashSet<>(Arrays.asList(thisParts));

        return Arrays.stream(otherParts)
                .anyMatch(nameParts::contains);
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
        String[] thisPhoneNumbers = splitAndCleanPhoneNumbers(this.phone.value);
        String[] otherPhoneNumbers = splitAndCleanPhoneNumbers(otherPerson.getPhone().value);

        for (String thisNumber : thisPhoneNumbers) {
            if (thisNumber.length() < Phone.MIN_LENGTH) {
                continue;
            }

            for (String otherNumber : otherPhoneNumbers) {
                if (otherNumber.length() < Phone.MIN_LENGTH) {
                    continue;
                }

                if (hasContiguousMatch(thisNumber, otherNumber)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static String[] splitAndCleanPhoneNumbers(String phoneString) {
        return Arrays.stream(phoneString.split(COMMA_REGEX))
                .map(String::trim)
                .map(s -> s.replaceAll(VALIDATION_EXCLUDE_DIGITS_REGEX, ""))
                .toArray(String[]::new);
    }

    private static boolean hasContiguousMatch(String str1, String str2) {
        if (str1.length() < Phone.MIN_LENGTH || str2.length() < Phone.MIN_LENGTH) {
            return false;
        }

        Set<String> substringOfFirst = generatePhoneSubstrings(str1);

        return generatePhoneSubstrings(str2)
                .stream().anyMatch(substringOfFirst::contains);
    }

    private static Set<String> generatePhoneSubstrings(String input) {
        Set<String> substrings = new HashSet<>();
        for (int i = START_INDEX; i <= input.length() - Phone.MIN_LENGTH; i++) {
            substrings.add(input.substring(i, i + Phone.MIN_LENGTH));
        }
        return substrings;
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
        StringBuilder sb = new StringBuilder();
        sb.append(name);

        sb.append(", Phone: ").append(phone);
        sb.append(", Email: ").append(email);
        sb.append(", Address: ").append(address);

        if (!tags.isEmpty()) {
            sb.append(", Tags: ").append(tags);
        }

        return sb.toString();
    }

}
