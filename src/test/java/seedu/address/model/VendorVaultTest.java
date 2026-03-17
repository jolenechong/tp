package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class VendorVaultTest {

    private final VendorVault vendorVault = new VendorVault();

    @Test
    public void constructor() {
        assertEquals(0, vendorVault.getPersonList().size());
        assertEquals(0, vendorVault.getProductList().size());
    }

    @Test
    public void constructor_withReadOnlyVendorVault_copiesData() {
        VendorVault source = createTypicalVendorVault();
        VendorVault copied = new VendorVault(source);

        assertEquals(source, copied);
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> vendorVault.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyVendorVault_replacesData() {
        VendorVault newData = createTypicalVendorVault();
        vendorVault.resetData(newData);

        assertEquals(newData, vendorVault);
    }

    @Test
    public void setAddressBook_replacesOnlyAddressBookData() {
        VendorVault vendorVault = new VendorVault();
        vendorVault.setInventory(getTypicalInventory());

        vendorVault.setAddressBook(getTypicalAddressBook());

        assertEquals(getTypicalAddressBook().getPersonList(), vendorVault.getPersonList());
        assertEquals(getTypicalInventory().getProductList(), vendorVault.getProductList());
    }

    @Test
    public void setInventory_replacesOnlyInventoryData() {
        VendorVault vendorVault = new VendorVault();
        vendorVault.setAddressBook(getTypicalAddressBook());

        vendorVault.setInventory(getTypicalInventory());

        assertEquals(getTypicalAddressBook().getPersonList(), vendorVault.getPersonList());
        assertEquals(getTypicalInventory().getProductList(), vendorVault.getProductList());
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        vendorVault.resetData(createTypicalVendorVault());
        assertThrows(UnsupportedOperationException.class, () -> vendorVault.getPersonList().remove(0));
    }

    @Test
    public void getProductList_modifyList_throwsUnsupportedOperationException() {
        vendorVault.resetData(createTypicalVendorVault());
        assertThrows(UnsupportedOperationException.class, () -> vendorVault.getProductList().remove(0));
    }

    @Test
    public void getAddressBook_returnsAddressBookData() {
        vendorVault.setAddressBook(getTypicalAddressBook());

        assertEquals(getTypicalAddressBook(), vendorVault.getAddressBook());
    }

    @Test
    public void getInventory_returnsInventoryData() {
        vendorVault.setInventory(getTypicalInventory());

        assertEquals(getTypicalInventory(), vendorVault.getInventory());
    }

    // ---------------- Helper methods ----------------

    private Person buildPerson(String name, String phone, String email, String address) {
        return new PersonBuilder()
                .withName(name)
                .withPhone(phone)
                .withEmail(email)
                .withAddress(address)
                .build();
    }

    private Product buildProduct(String identifier, String name) {
        return new ProductBuilder()
                .withIdentifier(identifier)
                .withName(name)
                .build();
    }

    private VendorVault createVaultWithPerson(Person person) {
        return createVendorVaultWithPersons(person);
    }

    private VendorVault createVaultWithProduct(Product product) {
        return createVendorVaultWithProducts(product);
    }

    // ---------------- Person Tests ----------------

    @Test
    public void findSimilarNameMatch_matchExists_returnsMatchingPerson() {
        Person existing = buildPerson("Acme Supplies", "90000001", "acme@example.com", "10 Business Park");
        VendorVault vault = createVaultWithPerson(existing);
        Person candidate = buildPerson("Acme Partners", "90000002", "candidate@example.com", "99 Other Street");

        assertEquals(Optional.of(existing), vault.findSimilarNameMatch(candidate, null));
    }

    @Test
    public void findSimilarNameMatch_matchingPersonExcluded_returnsEmpty() {
        Person existing = buildPerson("Acme Supplies", "90000003", "acme2@example.com", "11 Business Park");
        VendorVault vault = createVaultWithPerson(existing);
        Person candidate = buildPerson("Acme Partners", "90000004", "candidate2@example.com", "100 Other Street");

        assertEquals(Optional.empty(), vault.findSimilarNameMatch(candidate, existing));
    }

    @Test
    public void findSimilarAddressMatch_matchExists_returnsMatchingPerson() {
        Person existing = buildPerson("North Vendor", "90000005", "north@example.com", "123 Woodlands Avenue 5");
        VendorVault vault = createVaultWithPerson(existing);
        Person candidate = buildPerson("South Vendor", "90000006", "south@example.com", "Woodlands");

        assertEquals(Optional.of(existing), vault.findSimilarAddressMatch(candidate, null));
    }

    @Test
    public void findSimilarAddressMatch_matchingPersonExcluded_returnsEmpty() {
        Person existing = buildPerson("North Vendor", "90000007", "north2@example.com", "456 Clementi Road");
        VendorVault vault = createVaultWithPerson(existing);
        Person candidate = buildPerson("West Vendor", "90000008", "west@example.com", "Clementi");

        assertEquals(Optional.empty(), vault.findSimilarAddressMatch(candidate, existing));
    }

    // ---------------- Product Tests ----------------

    @Test
    public void findSimilarNameMatch_productMatchExists_returnsMatchingProduct() {
        Product existing = buildProduct("SKU-8000", "Wireless Mouse");
        VendorVault vault = createVaultWithProduct(existing);
        Product candidate = buildProduct("SKU-8001", "Wireless Keyboard");

        assertEquals(Optional.of(existing), vault.findSimilarNameMatch(candidate, null));
    }

    @Test
    public void findSimilarNameMatch_matchingProductExcluded_returnsEmpty() {
        Product existing = buildProduct("SKU-8100", "Printer Ink");
        VendorVault vault = createVaultWithProduct(existing);
        Product candidate = buildProduct("SKU-8101", "Printer Paper");

        assertEquals(Optional.empty(), vault.findSimilarNameMatch(candidate, existing));
    }

    @Test
    public void toStringMethod() {
        String expected = VendorVault.class.getCanonicalName() + "{addressBook=" + vendorVault.getAddressBook()
                + ", inventory=" + vendorVault.getInventory() + "}";
        assertEquals(expected, vendorVault.toString());
    }

    @Test
    public void equals() {
        VendorVault expected = createTypicalVendorVault();
        VendorVault copy = createTypicalVendorVault();

        assertTrue(expected.equals(copy));
        assertTrue(expected.equals(expected));
        assertFalse(expected.equals(null));
        assertFalse(expected.equals(1));

        VendorVault differentAddressBook = createTypicalVendorVault();
        differentAddressBook.setAddressBook(new AddressBook());
        assertFalse(expected.equals(differentAddressBook));

        VendorVault differentInventory = createTypicalVendorVault();
        differentInventory.setInventory(new Inventory());
        assertFalse(expected.equals(differentInventory));
    }

    private VendorVault createTypicalVendorVault() {
        VendorVault typicalVendorVault = new VendorVault();
        typicalVendorVault.setAddressBook(getTypicalAddressBook());
        typicalVendorVault.setInventory(getTypicalInventory());
        return typicalVendorVault;
    }

    private VendorVault createVendorVaultWithPersons(Person... persons) {
        VendorVault customVendorVault = new VendorVault();
        for (Person person : persons) {
            customVendorVault.getAddressBook().addPerson(person);
        }
        return customVendorVault;
    }

    private VendorVault createVendorVaultWithProducts(Product... products) {
        VendorVault customVendorVault = new VendorVault();
        for (Product product : products) {
            customVendorVault.getInventory().addProduct(product);
        }
        return customVendorVault;
    }
}
