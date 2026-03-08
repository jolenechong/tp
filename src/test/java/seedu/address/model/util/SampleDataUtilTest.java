package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_returnsNonEmptyArray() {
        assertTrue(SampleDataUtil.getSamplePersons().length > 0);
    }

    @Test
    public void getSampleAddressBook_returnsAddressBookWithAllSamplePersons() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        Person[] samplePersons = SampleDataUtil.getSamplePersons();

        assertEquals(samplePersons.length, sampleAddressBook.getPersonList().size());
    }

    @Test
    public void getSampleProducts_returnsNonEmptyArray() {
        assertTrue(SampleDataUtil.getSampleProducts().length > 0);
    }

    @Test
    public void getSampleInventory_returnsInventoryWithAllSampleProducts() {
        ReadOnlyInventory sampleInventory = SampleDataUtil.getSampleInventory();
        Product[] sampleProducts = SampleDataUtil.getSampleProducts();

        assertEquals(sampleProducts.length, sampleInventory.getProductList().size());
    }

    @Test
    public void getSampleProducts_identifiersAreUnique() {
        Product[] sampleProducts = SampleDataUtil.getSampleProducts();

        Set<String> uniqueIdentifiers = Arrays.stream(sampleProducts)
                .map(product -> product.getIdentifier().value)
                .collect(Collectors.toSet());

        assertEquals(sampleProducts.length, uniqueIdentifiers.size());
    }
}
