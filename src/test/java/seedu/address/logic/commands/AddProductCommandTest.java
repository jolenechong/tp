package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_WARN;
import static seedu.address.logic.commands.CommandTestUtil.assertExactlyOneProductWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertSimilarProductNameWarning;
import static seedu.address.logic.commands.CommandTestUtil.assertWarnFeedback;
import static seedu.address.logic.commands.CommandUtil.SEPARATOR_NEW_LINE;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.ReadOnlyVendorVault;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Name;
import seedu.address.model.product.Product;
import seedu.address.model.product.warnings.DuplicateProductWarning;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class AddProductCommandTest {

    // -------------------------------------------------------------------------
    // Product constants for warning and duplicate tests
    // -------------------------------------------------------------------------
    private static final String SKU_DUPLICATE = "SKU-2000";
    private static final String SKU_DIFFERENT = "SKU-2001";
    private static final String SKU_DIFFERENT_2 = "SKU-2002";
    private static final String SKU_MONITOR = "SKU-2005";
    private static final String NAME_OFFICE_CHAIR = "Office Chair";
    private static final String NAME_GAMING_CHAIR = "Gaming Chair";
    private static final String NAME_HOME_CHAIR = "Home Chair";
    private static final String NAME_RASPBERRY_PI = "Raspberry Berry Pi 5";
    private static final String NAME_ARDUINO_UNO = "Arduino Uno";
    private static final String NAME_MONITOR = "Monitor";
    private static final String PRESET_WARNING = "⚠ Warning: Identifier contains unusual symbols, is this intentional?";
    private static final String EMAIL_VENDOR_MISSING = "missing@example.com";
    private static final String EMAIL_VENDOR_EXISTING = "vendor@example.com";
    private static final String VENDOR_NAME = "John Doe";
    private static final String VENDOR_PHONE = "11111111";
    private static final String VENDOR_ADDRESS = "111 John Street";


    @Test
    public void constructor_nullProduct_throwsNullPointerException() {
        // EP: null input belongs to invalid constructor partition.
        assertThrows(NullPointerException.class, () -> new AddProductCommand(null));
    }

    @Test
    public void execute_productAcceptedByModel_addSuccessful() throws Exception {
        // EP: valid product with no duplicates and no warnings should succeed.
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        Product validProduct = new ProductBuilder().build();

        CommandResult commandResult = new AddProductCommand(validProduct).execute(modelStub);

        assertSuccessWithoutWarnings(commandResult, validProduct);
        assertEquals(1, modelStub.productsAdded.size());
        assertEquals(validProduct, modelStub.productsAdded.get(0));
    }

    @Test
    public void execute_withFieldFormatWarnings_warningFeedbackReturned() throws Exception {
        // EP: valid product with parser-provided warnings should return warning feedback.
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();

        Product validProductWithWarnings = new ProductBuilder()
                .withIdentifier(INVALID_IDENTIFIER_WARN)
                .withName(INVALID_PRODUCT_NAME_WARN)
                .build();

        String warnings = Identifier.MESSAGE_WARN + SEPARATOR_NEW_LINE + Name.MESSAGE_WARN;
        AddProductCommand addProductCommand = new AddProductCommand(validProductWithWarnings, warnings);

        CommandResult result = addProductCommand.execute(modelStub);

        String expectedMessage = String.format(AddProductCommand.MESSAGE_SUCCESS + SEPARATOR_NEW_LINE + warnings,
                Messages.formatProduct(validProductWithWarnings));

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_duplicateProductIdentifier_throwsCommandException() {
        // EP: exact same SKU -> reject
        Product existingProduct = buildProduct(SKU_DUPLICATE, NAME_OFFICE_CHAIR);
        Product duplicateProduct = buildProduct(SKU_DUPLICATE, NAME_GAMING_CHAIR);

        ModelStubAcceptingProductAdded modelStub = seedModelStubWithProducts(existingProduct);

        AddProductCommand addProductCommand = new AddProductCommand(duplicateProduct);

        assertThrows(CommandException.class, String.format(Messages.MESSAGE_DUPLICATE_PRODUCT,
                existingProduct.getIdentifier(), existingProduct.getName()), ()
                -> addProductCommand.execute(modelStub));
    }

    @Test
    public void execute_similarProductName_warnAndAddSuccessful() throws Exception {
        // EP: one existing product with a shared name token -> soft warn, still added
        Product existingProduct = buildProduct(SKU_DUPLICATE, NAME_OFFICE_CHAIR);
        Product productToAdd = buildProduct(SKU_DIFFERENT, NAME_GAMING_CHAIR);
        ModelStubAcceptingProductAdded modelStub = seedModelStubWithProducts(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        assertSimilarProductNameWarning(result, existingProduct);
        assertEquals(2, modelStub.productsAdded.size());
        assertEquals(productToAdd, modelStub.productsAdded.get(1));
    }

    @Test
    public void execute_multipleNonSimilarProducts_noWarning() throws Exception {
        // EP: all existing products are non-similar -> success without warning.
        Product existingOne = buildProduct(SKU_DUPLICATE, NAME_OFFICE_CHAIR);
        Product existingTwo = buildProduct(SKU_DIFFERENT, NAME_RASPBERRY_PI);
        Product productToAdd = buildProduct(SKU_DIFFERENT_2, NAME_ARDUINO_UNO);
        ModelStubAcceptingProductAdded modelStub = seedModelStubWithProducts(existingOne, existingTwo);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        assertSuccessWithoutWarnings(result, productToAdd);
    }

    @Test
    public void execute_presetWarningAndSimilarName_warningsJoinedWithNewline() throws Exception {
        // BV: non-empty preset warning should be joined with similar-name warning via newline.
        Product existingProduct = buildProduct(SKU_DUPLICATE, NAME_OFFICE_CHAIR);
        Product productToAdd = buildProduct(SKU_DIFFERENT, NAME_GAMING_CHAIR);
        ModelStubAcceptingProductAdded modelStub = seedModelStubWithProducts(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd, PRESET_WARNING).execute(modelStub);

        assertSimilarProductNameWarning(result, existingProduct);
        String expectedSimilarWarning = String.format(
                DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                existingProduct.getIdentifier(), existingProduct.getName());
        assertWarnFeedback(result, PRESET_WARNING);
        // The two warnings must be separated by a newline
        assertTrue(result.getFeedbackToUser().contains(PRESET_WARNING + "\n" + expectedSimilarWarning));
    }

    @Test
    public void execute_multipleSimilarProducts_onlyFirstWarningAppended() throws Exception {
        // EP: two existing products both share a token with the new product ->
        //     exactly one warning emitted (deduplication / early-exit)
        Product similar1 = buildProduct(SKU_DUPLICATE, NAME_OFFICE_CHAIR);
        Product similar2 = buildProduct(SKU_DIFFERENT, NAME_GAMING_CHAIR);
        Product productToAdd = buildProduct(SKU_DIFFERENT_2, NAME_HOME_CHAIR);
        ModelStubAcceptingProductAdded modelStub = seedModelStubWithProducts(similar1, similar2);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        String warning1 = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                similar1.getIdentifier(), similar1.getName());
        String warning2 = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                similar2.getIdentifier(), similar2.getName());
        assertExactlyOneProductWarning(result.getFeedbackToUser(), warning1, warning2);
    }

    @Test
    public void execute_vendorEmailDoesNotExist_throwsCommandException() {
        // EP: product references non-existent vendor email -> reject.
        Product productWithMissingVendor = buildProduct(SKU_MONITOR, NAME_MONITOR, EMAIL_VENDOR_MISSING);
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();

        AddProductCommand addProductCommand = new AddProductCommand(productWithMissingVendor);

        assertThrows(CommandException.class,
                String.format(AddProductCommand.MESSAGE_VENDOR_DOES_NOT_EXIST, EMAIL_VENDOR_MISSING), () ->
                        addProductCommand.execute(modelStub));
    }

    @Test
    public void execute_vendorEmailExists_addSuccessful() throws Exception {
        // EP: product references existing vendor email -> add succeeds.
        Email existingVendorEmail = new Email(EMAIL_VENDOR_EXISTING);
        Person existingVendor = new PersonBuilder()
                .withName(VENDOR_NAME)
                .withPhone(VENDOR_PHONE)
                .withEmail(existingVendorEmail.value)
                .withAddress(VENDOR_ADDRESS)
                .build();
        Product productWithExistingVendor = buildProduct(SKU_MONITOR, NAME_MONITOR, existingVendorEmail.value);

        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded() {
            @Override
            public Optional<Person> findByEmail(Email email) {
                requireNonNull(email);
                if (existingVendorEmail.equals(email)) {
                    return Optional.of(existingVendor);
                }
                return Optional.empty();
            }
        };

        CommandResult commandResult = new AddProductCommand(productWithExistingVendor).execute(modelStub);

        assertSuccessWithoutWarnings(commandResult, productWithExistingVendor);
        assertEquals(1, modelStub.productsAdded.size());
        assertEquals(productWithExistingVendor, modelStub.productsAdded.get(0));
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        // BV: pending confirmation should always be inactive for addproduct.
        Product validProduct = new ProductBuilder().build();
        AddProductCommand addProductCommand = new AddProductCommand(validProduct);

        PendingConfirmation pendingConfirmation = addProductCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void equals() {
        // EP: equals contract across same instance/value/type/null/different value partitions.
        Product productA = new ProductBuilder().withIdentifier("SKU-A").build();
        Product productB = new ProductBuilder().withIdentifier("SKU-B").build();
        AddProductCommand addProductA = new AddProductCommand(productA);
        AddProductCommand addProductB = new AddProductCommand(productB);

        // same object -> returns true
        assertTrue(addProductA.equals(addProductA));

        // same values -> returns true
        assertTrue(addProductA.equals(new AddProductCommand(productA)));

        // different types -> returns false
        assertFalse(addProductA.equals(1));

        // null -> returns false
        assertFalse(addProductA.equals(null));

        // different product -> returns false
        assertFalse(addProductA.equals(addProductB));
    }

    @Test
    public void toStringMethod() {
        // BV: toString should contain the exact product payload for traceability.
        Product validProduct = new ProductBuilder().build();
        AddProductCommand addProductCommand = new AddProductCommand(validProduct);
        String expected = AddProductCommand.class.getCanonicalName() + "{toAdd=" + validProduct + "}";
        assertEquals(expected, addProductCommand.toString());
    }

    private Product buildProduct(String identifier, String name) {
        return new ProductBuilder().withIdentifier(identifier).withName(name).build();
    }

    private Product buildProduct(String identifier, String name, String vendorEmail) {
        return new ProductBuilder().withIdentifier(identifier).withName(name).withVendorEmail(vendorEmail).build();
    }

    private ModelStubAcceptingProductAdded seedModelStubWithProducts(Product... products) {
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        for (Product product : products) {
            modelStub.seedExistingProduct(product);
        }
        return modelStub;
    }

    private void assertSuccessWithoutWarnings(CommandResult result, Product expectedProduct) {
        assertEquals(String.format(AddProductCommand.MESSAGE_SUCCESS, Messages.formatProduct(expectedProduct)),
                result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setInventory(ReadOnlyInventory inventory) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyInventory getInventory() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setVendorVault(ReadOnlyVendorVault vendorVault) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyVendorVault getVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAliases(ReadOnlyAliases aliases) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAliases getAliases() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findByEmail(Email email) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public void archivePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restorePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Product> findById(Identifier id) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteProduct(Product target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setProduct(Product target, Product editedProduct) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void archiveProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restoreProduct(Product product) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addAlias(Alias alias) throws DuplicateAliasException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Alias findAlias(String aliasStr) throws NoAliasFoundInAliasListException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeAlias(String aliasStr) throws NoAliasFoundInAliasListException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<Alias> getAliasList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Product> getFilteredProductList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredProductList(Predicate<Product> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        /**
         * Commits the current state of VendorVault.
         *
         * This is a stub implementation used in tests where state committing
         * behaviour is not required. The method is invoked as part of normal
         * command execution but performs no action in the stub model.
         */
        @Override
        public void commitVendorVault(String actionSummary) {
            // intentionally left blank
        }

        @Override
        public String undoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public String redoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }
    }

    private class ModelStubAcceptingProductAdded extends ModelStub {
        final ArrayList<Product> productsAdded = new ArrayList<>();

        @Override
        public Optional<Person> findByEmail(Email email) {
            requireNonNull(email);
            return Optional.empty();
        }

        @Override
        public boolean hasProduct(Product product) {
            requireNonNull(product);
            return productsAdded.stream().anyMatch(product::isSameProduct);
        }

        @Override
        public ObservableList<Product> getFilteredProductList() {
            return FXCollections.observableArrayList(productsAdded);
        }

        @Override
        public void addProduct(Product product) {
            requireNonNull(product);
            productsAdded.add(product);
        }

        @Override
        public Optional<Product> findById(Identifier id) {
            requireNonNull(id);
            return productsAdded.stream()
                    .filter(product -> product.getIdentifier().equals(id))
                    .findFirst();
        }

        @Override
        public ReadOnlyInventory getInventory() {
            return new ReadOnlyInventory() {
                @Override
                public ObservableList<Product> getProductList() {
                    return FXCollections.observableArrayList(productsAdded);
                }

                @Override
                public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
                    requireNonNull(candidate);
                    return productsAdded.stream()
                            .filter(p -> exclude == null || !p.equals(exclude))
                            .filter(candidate::isSimilarNameTo)
                            .findFirst();
                }
            };
        }

        @Override
        public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
            return getInventory().findSimilarNameMatch(candidate, exclude);
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
            return Optional.empty();
        }

        void seedExistingProduct(Product product) {
            requireNonNull(product);
            productsAdded.add(product);
        }
    }
}
