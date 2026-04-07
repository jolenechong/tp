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

    private static final String NAME_LINKED_ACTIVE = "Linked Active";
    private static final String NAME_LINKED_ARCHIVED = "Linked Archived";
    private static final String NAME_UNLINKED = "Unlinked";
    private static final String NAME_CLEAR_ACTIVE = "To Clear Active";
    private static final String NAME_CLEAR_ARCHIVED = "To Clear Archived";
    private static final String NAME_UPDATE_ACTIVE = "To Update Active";

    private static final String SKU_LINKED_ACTIVE = "SKU-801";
    private static final String SKU_LINKED_ARCHIVED = "SKU-802";
    private static final String SKU_UNLINKED = "SKU-803";
    private static final String SKU_CLEAR_ACTIVE = "SKU-804";
    private static final String SKU_CLEAR_ARCHIVED = "SKU-805";
    private static final String SKU_CLEAR_UNLINKED = "SKU-806";
    private static final String SKU_UPDATE_ACTIVE = "SKU-807";
    private static final String SKU_UPDATE_ARCHIVED = "SKU-808";

    private static final String OTHER_EMAIL = "other@example.com";
    private static final Email UPDATED_EMAIL = new Email("updated.link@example.com");
    private static final Email REPLACEMENT_EMAIL = new Email("x@example.com");

    @Test
    public void collectLinkedProducts_noLinkedProducts_returnsEmptyList() {
        Model model = createModel();
        Person person = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        assertTrue(VendorProductLinkUtil.collectLinkedProducts(model, person.getEmail()).isEmpty());
    }

    @Test
    public void collectLinkedProducts_hasLinkedProducts_returnsMatchingProducts() {
        // EP: linked lookup includes both active and archived products with matching vendor email.
        Model model = createModel();
        Person person = getFirstPerson(model);
        Person anotherPerson = getSecondPerson(model);

        Product linkedActive = buildLinkedProduct(SKU_LINKED_ACTIVE, NAME_LINKED_ACTIVE, person.getEmail().value);
        Product linkedArchived = archivedLinkedProduct(
                SKU_LINKED_ARCHIVED, NAME_LINKED_ARCHIVED, person.getEmail().value);
        Product unlinked = buildLinkedProduct(SKU_UNLINKED, NAME_UNLINKED, anotherPerson.getEmail().value);

        addProducts(model, linkedActive, linkedArchived, unlinked);

        List<Product> linkedProducts = collectLinkedProducts(model, person.getEmail());
        assertEquals(2, linkedProducts.size());
        assertTrue(linkedProducts.contains(linkedActive));
        assertTrue(linkedProducts.contains(linkedArchived));
    }

    @Test
    public void clearVendorEmail_clearsOnlyProvidedProducts() {
        Model model = createModel();
        Person person = getFirstPerson(model);

        Product linkedActive = buildLinkedProduct(SKU_CLEAR_ACTIVE, NAME_CLEAR_ACTIVE, person.getEmail().value);
        Product linkedArchived = archivedLinkedProduct(
                SKU_CLEAR_ARCHIVED, NAME_CLEAR_ARCHIVED, person.getEmail().value);
        Product unlinked = buildLinkedProduct(SKU_CLEAR_UNLINKED, NAME_UNLINKED, OTHER_EMAIL);

        addProducts(model, linkedActive, linkedArchived, unlinked);

        List<Product> linkedProducts = collectLinkedProducts(model, person.getEmail());
        VendorProductLinkUtil.clearVendorEmail(model, linkedProducts);

        assertVendorEmailCleared(model, SKU_CLEAR_ACTIVE);
        assertVendorEmailCleared(model, SKU_CLEAR_ARCHIVED);
        assertVendorEmail(model, SKU_CLEAR_UNLINKED, OTHER_EMAIL);
    }

    @Test
    public void clearVendorEmail_emptyProducts_noChanges() {
        Model model = createModel();
        Person person = getFirstPerson(model);
        Product linked = buildLinkedProduct(SKU_CLEAR_ACTIVE, NAME_CLEAR_ACTIVE, person.getEmail().value);
        Product unlinked = buildLinkedProduct(SKU_CLEAR_UNLINKED, NAME_UNLINKED, OTHER_EMAIL);
        addProducts(model, linked, unlinked);

        VendorProductLinkUtil.clearVendorEmail(model, List.of());

        assertVendorEmail(model, SKU_CLEAR_ACTIVE, person.getEmail().value);
        assertVendorEmail(model, SKU_CLEAR_UNLINKED, OTHER_EMAIL);
    }

    @Test
    public void updateVendorEmail_updatesProvidedProductsAndPreservesArchived() {
        Model model = createModel();
        Person person = getFirstPerson(model);

        Product linkedActive = buildLinkedProduct(SKU_UPDATE_ACTIVE, NAME_UPDATE_ACTIVE, person.getEmail().value);
        Product linkedArchived = archivedLinkedProduct(
                SKU_UPDATE_ARCHIVED, NAME_UPDATE_ACTIVE, person.getEmail().value);

        addProducts(model, linkedActive, linkedArchived);

        List<Product> linkedProducts = collectLinkedProducts(model, person.getEmail());
        VendorProductLinkUtil.updateVendorEmail(model, linkedProducts, UPDATED_EMAIL);

        assertEquals(UPDATED_EMAIL, getProductByIdentifier(model, SKU_UPDATE_ACTIVE).getVendorEmail().orElseThrow());
        assertEquals(UPDATED_EMAIL,
                getProductByIdentifier(model, SKU_UPDATE_ARCHIVED).getVendorEmail().orElseThrow());
        assertTrue(getProductByIdentifier(model, SKU_UPDATE_ARCHIVED).isArchived());
    }

    @Test
    public void updateVendorEmail_emptyProducts_noChanges() {
        Model model = createModel();
        Person person = getFirstPerson(model);
        Product linked = buildLinkedProduct(SKU_UPDATE_ACTIVE, NAME_UPDATE_ACTIVE, person.getEmail().value);
        Product unlinked = buildLinkedProduct(SKU_CLEAR_UNLINKED, NAME_UNLINKED, OTHER_EMAIL);
        addProducts(model, linked, unlinked);

        VendorProductLinkUtil.updateVendorEmail(model, List.of(), UPDATED_EMAIL);

        assertVendorEmail(model, SKU_UPDATE_ACTIVE, person.getEmail().value);
        assertVendorEmail(model, SKU_CLEAR_UNLINKED, OTHER_EMAIL);
    }

    @Test
    public void collectLinkedProducts_nullArguments_throwsNullPointerException() {
        Model model = createModel();
        Person person = getFirstPerson(model);
        Email personEmail = person.getEmail();

        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.collectLinkedProducts(null, personEmail));
        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.collectLinkedProducts(model, null));
    }

    @Test
    public void clearVendorEmail_nullArguments_throwsNullPointerException() {
        Model model = createModel();
        Person person = getFirstPerson(model);
        List<Product> linkedProducts = collectLinkedProducts(model, person.getEmail());

        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.clearVendorEmail(null, linkedProducts));
        assertThrows(NullPointerException.class, () ->
                VendorProductLinkUtil.clearVendorEmail(model, null));
    }

    @Test
    public void updateVendorEmail_nullArguments_throwsNullPointerException() {
        Model model = createModel();
        Person person = getFirstPerson(model);
        List<Product> linkedProducts = collectLinkedProducts(model, person.getEmail());

        assertThrows(NullPointerException.class, () ->
            VendorProductLinkUtil.updateVendorEmail(null, linkedProducts, REPLACEMENT_EMAIL));
        assertThrows(NullPointerException.class, () ->
            VendorProductLinkUtil.updateVendorEmail(model, null, REPLACEMENT_EMAIL));
        assertThrows(NullPointerException.class, () ->
            VendorProductLinkUtil.updateVendorEmail(model, linkedProducts, null));
    }

    private Model createModel() {
        return new ModelManager(new VendorVault(getTypicalAddressBook(), getTypicalInventory()),
                new UserPrefs(), new Aliases());
    }

    private Person getFirstPerson(Model model) {
        return model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
    }

    private Person getSecondPerson(Model model) {
        return model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased() + 1);
    }

    private Product buildLinkedProduct(String identifier, String name, String vendorEmail) {
        return new ProductBuilder()
                .withIdentifier(identifier)
                .withName(name)
                .withVendorEmail(vendorEmail)
                .build();
    }

    private Product archivedLinkedProduct(String identifier, String name, String vendorEmail) {
        return buildLinkedProduct(identifier, name, vendorEmail).archive();
    }

    private List<Product> collectLinkedProducts(Model model, Email email) {
        return VendorProductLinkUtil.collectLinkedProducts(model, email);
    }

    private void addProducts(Model model, Product... products) {
        for (Product product : products) {
            model.addProduct(product);
        }
    }

    private void assertVendorEmail(Model model, String identifier, String expectedEmail) {
        assertEquals(expectedEmail,
                getProductByIdentifier(model, identifier).getVendorEmail().orElseThrow().value);
    }

    private void assertVendorEmailCleared(Model model, String identifier) {
        assertTrue(getProductByIdentifier(model, identifier).getVendorEmail().isEmpty());
    }
}
