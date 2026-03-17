package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TestUtil.getProductByIdentifier;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalProducts.getTypicalInventory;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import seedu.address.model.Aliases;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.VendorVault;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.testutil.ProductBuilder;

public class VendorProductLinkUtilTest {

    private final Model model = new ModelManager(new VendorVault(
            getTypicalAddressBook(), getTypicalInventory()), new UserPrefs(), new Aliases());

    @Test
    public void collectLinkedProducts_noLinkedProducts_returnsEmptyList() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        assertTrue(VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail()).isEmpty());
    }

    @Test
    public void collectLinkedProducts_hasLinkedProducts_returnsMatchingProducts() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person anotherPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased() + 1);

        Product linkedActive = new ProductBuilder()
                .withIdentifier("SKU-801")
                .withName("Linked Active")
                .withVendorEmail(person.getEmail().value)
                .build();
        Product linkedArchived = new ProductBuilder()
                .withIdentifier("SKU-802")
                .withName("Linked Archived")
                .withVendorEmail(person.getEmail().value)
                .build()
                .archive();
        Product unlinked = new ProductBuilder()
                .withIdentifier("SKU-803")
                .withName("Unlinked")
                .withVendorEmail(anotherPerson.getEmail().value)
                .build();

        model.addProduct(linkedActive);
        model.addProduct(linkedArchived);
        model.addProduct(unlinked);

        List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail());
        assertEquals(2, linkedProducts.size());
        assertTrue(linkedProducts.contains(linkedActive));
        assertTrue(linkedProducts.contains(linkedArchived));
    }

    @Test
    public void clearVendorEmail_clearsOnlyProvidedProducts() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Product linkedActive = new ProductBuilder()
                .withIdentifier("SKU-804")
                .withName("To Clear Active")
                .withVendorEmail(person.getEmail().value)
                .build();
        Product linkedArchived = new ProductBuilder()
                .withIdentifier("SKU-805")
                .withName("To Clear Archived")
                .withVendorEmail(person.getEmail().value)
                .build()
                .archive();
        Product unlinked = new ProductBuilder()
                .withIdentifier("SKU-806")
                .withName("Unlinked")
                .withVendorEmail("other@example.com")
                .build();

        model.addProduct(linkedActive);
        model.addProduct(linkedArchived);
        model.addProduct(unlinked);

        List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail());
        VendorProductLinkUtil.clearVendorEmail(model, linkedProducts);

        assertTrue(getProductByIdentifier(model, "SKU-804").getVendorEmail().isEmpty());
        assertTrue(getProductByIdentifier(model, "SKU-805").getVendorEmail().isEmpty());
        assertEquals("other@example.com", getProductByIdentifier(model, "SKU-806")
                .getVendorEmail().orElseThrow().value);
    }

    @Test
    public void updateVendorEmail_updatesProvidedProductsAndPreservesArchived() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Email newEmail = new Email("updated.link@example.com");

        Product linkedActive = new ProductBuilder()
                .withIdentifier("SKU-807")
                .withName("To Update Active")
                .withVendorEmail(person.getEmail().value)
                .build();
        Product linkedArchived = new ProductBuilder()
                .withIdentifier("SKU-808")
                .withName("To Update Archived")
                .withVendorEmail(person.getEmail().value)
                .build()
                .archive();

        model.addProduct(linkedActive);
        model.addProduct(linkedArchived);

        List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail());
        VendorProductLinkUtil.updateVendorEmail(model, linkedProducts, newEmail);

        assertEquals(newEmail, getProductByIdentifier(model, "SKU-807").getVendorEmail().orElseThrow());
        assertEquals(newEmail, getProductByIdentifier(model, "SKU-808").getVendorEmail().orElseThrow());
        assertTrue(getProductByIdentifier(model, "SKU-808").isArchived());
    }

    @Test
    public void collectLinkedProducts_nullArguments_throwsNullPointerException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Email personEmail = person.getEmail();

        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.collectLinkedProducts(null, personEmail));
        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.collectLinkedProducts(model, null));
    }

    @Test
    public void clearVendorEmail_nullArguments_throwsNullPointerException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail());

        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.clearVendorEmail(null, linkedProducts));
        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.clearVendorEmail(model, null));
    }

    @Test
    public void updateVendorEmail_nullArguments_throwsNullPointerException() {
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        List<Product> linkedProducts = VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail());
        Email replacementEmail = new Email("x@example.com");

        Executable nullModel = () -> VendorProductLinkUtil.updateVendorEmail(
                null, linkedProducts, replacementEmail);
        Executable nullProducts = () -> VendorProductLinkUtil.updateVendorEmail(
                model, null, replacementEmail);
        Executable nullEmail = () -> VendorProductLinkUtil.updateVendorEmail(
                model, linkedProducts, null);

        assertThrows(NullPointerException.class, nullModel);
        assertThrows(NullPointerException.class, nullProducts);
        assertThrows(NullPointerException.class, nullEmail);
    }
}
