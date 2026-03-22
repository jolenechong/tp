package seedu.address.model.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class VendorEmailMatchesContactsPredicateTest {

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withEmail("alice@example.com").build();
        Person bob = new PersonBuilder().withEmail("bob@example.com").build();

        List<Person> firstContactList = Collections.singletonList(alice);
        List<Person> secondContactList = Arrays.asList(alice, bob);

        VendorEmailMatchesContactsPredicate firstPredicate =
                new VendorEmailMatchesContactsPredicate(firstContactList);
        VendorEmailMatchesContactsPredicate secondPredicate =
                new VendorEmailMatchesContactsPredicate(secondContactList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        VendorEmailMatchesContactsPredicate firstPredicateCopy =
                new VendorEmailMatchesContactsPredicate(firstContactList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_vendorEmailMatchesContacts_returnsTrue() {
        Person alice = new PersonBuilder().withEmail("alice@example.com").build();
        VendorEmailMatchesContactsPredicate predicate =
                new VendorEmailMatchesContactsPredicate(Collections.singletonList(alice));

        Product matchingProduct = new ProductBuilder()
                .withIdentifier("SKU-3001")
                .withName("Vendor Linked Item")
                .withQuantity("10")
                .withThreshold("5")
                .withVendorEmail("alice@example.com")
                .build();

        assertTrue(predicate.test(matchingProduct));
    }

    @Test
    public void test_vendorEmailDoesNotMatchOrIsMissingOrArchived_returnsFalse() {
        Person alice = new PersonBuilder().withEmail("alice@example.com").build();
        VendorEmailMatchesContactsPredicate predicate =
                new VendorEmailMatchesContactsPredicate(Collections.singletonList(alice));

        Product differentVendorProduct = new ProductBuilder()
                .withIdentifier("SKU-3002")
                .withName("Different Vendor Item")
                .withQuantity("10")
                .withThreshold("5")
                .withVendorEmail("bob@example.com")
                .build();

        Product noVendorProduct = new ProductBuilder()
                .withIdentifier("SKU-3003")
                .withName("No Vendor Item")
                .withQuantity("10")
                .withThreshold("5")
                .withoutVendorEmail()
                .build();

        Product archivedMatchingProduct = new ProductBuilder()
                .withIdentifier("SKU-3004")
                .withName("Archived Vendor Item")
                .withQuantity("10")
                .withThreshold("5")
                .withVendorEmail("alice@example.com")
                .build()
                .archive();

        assertFalse(predicate.test(differentVendorProduct));
        assertFalse(predicate.test(noVendorProduct));
        assertFalse(predicate.test(archivedMatchingProduct));
    }

    @Test
    public void toStringMethod() {
        Person alice = new PersonBuilder().withEmail("alice@example.com").build();
        VendorEmailMatchesContactsPredicate predicate =
                new VendorEmailMatchesContactsPredicate(Collections.singletonList(alice));

        String output = predicate.toString();
        assertTrue(output.contains("contacts=["));
        assertTrue(output.contains("alice@example.com"));
    }
}

