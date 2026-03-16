package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_PRODUCT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_IDENTIFIER_WARN;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRODUCT_NAME_WARN;
import static seedu.address.logic.parser.ParserUtil.NEWLINE;
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
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.model.product.warnings.DuplicateProductWarning;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.ProductBuilder;

public class AddProductCommandTest {
    @Test
    public void constructor_nullProduct_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddProductCommand(null));
    }

    @Test
    public void execute_productAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        Product validProduct = new ProductBuilder().build();

        CommandResult commandResult = new AddProductCommand(validProduct).execute(modelStub);

        assertEquals(String.format(AddProductCommand.MESSAGE_SUCCESS, Messages.formatProduct(validProduct)),
                commandResult.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, commandResult.getFeedbackType());
        assertEquals(1, modelStub.productsAdded.size());
        assertEquals(validProduct, modelStub.productsAdded.get(0));
    }

    @Test
    public void execute_withWarnings_success() throws Exception {
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();

        Product validProductWithWarnings = new ProductBuilder()
                .withIdentifier(INVALID_IDENTIFIER_WARN)
                .withName(INVALID_PRODUCT_NAME_WARN)
                .build();

        String warnings = Identifier.MESSAGE_WARN + NEWLINE + Name.MESSAGE_WARN;
        AddProductCommand addProductCommand = new AddProductCommand(validProductWithWarnings, warnings);

        CommandResult result = addProductCommand.execute(modelStub);

        String expectedMessage = String.format(AddProductCommand.MESSAGE_SUCCESS + NEWLINE + warnings,
                Messages.formatProduct(validProductWithWarnings));

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_duplicateProductIdentifier_throwsCommandException() {
        Product existingProduct = new ProductBuilder()
                .withIdentifier("SKU-2000")
                .withName("Office Chair")
                .build();
        Product duplicateProduct = new ProductBuilder()
                .withIdentifier("SKU-2000")
                .withName("Gaming Chair")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingProduct);

        AddProductCommand addProductCommand = new AddProductCommand(duplicateProduct);

        assertThrows(CommandException.class, MESSAGE_DUPLICATE_PRODUCT, () ->
                addProductCommand.execute(modelStub));
    }
    @Test
    public void execute_similarProductName_warnAndAddSuccessful() throws Exception {
        Product existingProduct = new ProductBuilder()
                .withIdentifier("SKU-3000")
                .withName("Wireless Mouse")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-3001")
                .withName("Wireless Keyboard")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        String expectedWarning = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                existingProduct.getIdentifier(), existingProduct.getName());
        assertTrue(result.getFeedbackToUser().contains(expectedWarning));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
        assertEquals(2, modelStub.productsAdded.size());
        assertEquals(productToAdd, modelStub.productsAdded.get(1));
    }

    @Test
    public void execute_noSimilarProducts_noSimilarNameWarning() throws Exception {
        // All existing products have completely different names
        Product existingProduct = new ProductBuilder()
                .withIdentifier("SKU-4000")
                .withName("Stapler")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-4001")
                .withName("Rubber Band")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        assertFalse(result.getFeedbackToUser().contains(
                DuplicateProductWarning.MESSAGE_SIMILAR_NAME.substring(0, 10)));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_multipleNonSimilarProducts_loopCompletesWithoutWarning() throws Exception {
        Product existingOne = new ProductBuilder()
                .withIdentifier("SKU-4100")
                .withName("Stapler")
                .build();
        Product existingTwo = new ProductBuilder()
                .withIdentifier("SKU-4101")
                .withName("Paper Clips")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-4102")
                .withName("Rubber Band")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingOne);
        modelStub.seedExistingProduct(existingTwo);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        assertFalse(result.getFeedbackToUser().contains("similar name"));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
    }

    @Test
    public void execute_warningValueTrueButDifferentWarningType_noSimilarNameWarning() throws Exception {
        // When there's multiple warnings
        Product toAddWithDifferentWarningType = new ProductWithForcedWarning(
                new Identifier("SKU-4200"),
                new Name("Desk Lamp"),
                new Quantity("5"),
                new RestockThreshold("5"),
                new DuplicateProductWarning(true, "OTHER_WARNING_TYPE"));

        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(new ProductBuilder()
                .withIdentifier("SKU-4201")
                .withName("Desk Mat")
                .build());
        modelStub.seedExistingProduct(new ProductBuilder()
                .withIdentifier("SKU-4202")
                .withName("Desk Organizer")
                .build());

        CommandResult result = new AddProductCommand(toAddWithDifferentWarningType).execute(modelStub);

        assertFalse(result.getFeedbackToUser().contains("similar name"));
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, result.getFeedbackType());
        assertEquals(3, modelStub.productsAdded.size());
    }

    @Test
    public void execute_similarProductName_warningMatchesMessageSimilarName() throws Exception {
        Product existingProduct = new ProductBuilder()
                .withIdentifier("SKU-5000")
                .withName("Mechanical Keyboard")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-5001")
                .withName("Mechanical Pencil")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        String expectedWarning = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                existingProduct.getIdentifier(), existingProduct.getName());
        assertTrue(result.getFeedbackToUser().contains(expectedWarning));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_presetWarningAndSimilarName_warningsJoinedWithNewline() throws Exception {
        Product existingProduct = new ProductBuilder()
                .withIdentifier("SKU-6000")
                .withName("Printer Ink")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-6001")
                .withName("Printer Paper")
                .build();
        String presetWarning = "⚠ Warning: Identifier contains unusual symbols, is this intentional?";
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(existingProduct);

        CommandResult result = new AddProductCommand(productToAdd, presetWarning).execute(modelStub);

        String expectedSimilarWarning = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                existingProduct.getIdentifier(), existingProduct.getName());
        String feedback = result.getFeedbackToUser();
        assertTrue(feedback.contains(presetWarning));
        assertTrue(feedback.contains(expectedSimilarWarning));
        // The two warnings must be separated by a newline
        assertTrue(feedback.contains(presetWarning + "\n" + expectedSimilarWarning));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_multipleSimilarProducts_onlyFirstWarningAppended() throws Exception {
        // When multiple existing products share a name token, only one warning should appear.
        Product similar1 = new ProductBuilder()
                .withIdentifier("SKU-7000")
                .withName("Glass Cup")
                .build();
        Product similar2 = new ProductBuilder()
                .withIdentifier("SKU-7001")
                .withName("Glass Bottle")
                .build();
        Product productToAdd = new ProductBuilder()
                .withIdentifier("SKU-7002")
                .withName("Glass Jar")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();
        modelStub.seedExistingProduct(similar1);
        modelStub.seedExistingProduct(similar2);

        CommandResult result = new AddProductCommand(productToAdd).execute(modelStub);

        String warning1 = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                similar1.getIdentifier(), similar1.getName());
        String warning2 = String.format(DuplicateProductWarning.MESSAGE_SIMILAR_NAME,
                similar2.getIdentifier(), similar2.getName());
        // Exactly one of the two possible warnings should appear, not both
        assertTrue(result.getFeedbackToUser().contains(warning1)
                || result.getFeedbackToUser().contains(warning2));
        assertFalse(result.getFeedbackToUser().contains(warning1)
                && result.getFeedbackToUser().contains(warning2));
        assertEquals(CommandResult.FEEDBACK_TYPE_WARN, result.getFeedbackType());
    }

    @Test
    public void execute_vendorEmailDoesNotExist_throwsCommandException() {
        Product productWithMissingVendor = new ProductBuilder()
                .withIdentifier("SKU-2005")
                .withName("Monitor")
                .withVendorEmail("missing@example.com")
                .build();
        ModelStubAcceptingProductAdded modelStub = new ModelStubAcceptingProductAdded();

        AddProductCommand addProductCommand = new AddProductCommand(productWithMissingVendor);

        assertThrows(CommandException.class,
                String.format(AddProductCommand.MESSAGE_VENDOR_DOES_NOT_EXIST, "missing@example.com"), () ->
                        addProductCommand.execute(modelStub));
    }

    @Test
    public void execute_vendorEmailExists_addSuccessful() throws Exception {
        Email existingVendorEmail = new Email("vendor@example.com");
        Person existingVendor = new PersonBuilder()
                .withName("John Doe")
                .withPhone("11111111")
                .withEmail(existingVendorEmail.value)
                .withAddress("111 John Street")
                .build();
        Product productWithExistingVendor = new ProductBuilder()
                .withIdentifier("SKU-2005")
                .withName("Monitor")
                .withVendorEmail(existingVendorEmail.value)
                .build();

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

        assertEquals(String.format(AddProductCommand.MESSAGE_SUCCESS,
                        Messages.formatProduct(productWithExistingVendor)), commandResult.getFeedbackToUser());
        assertEquals(CommandResult.FEEDBACK_TYPE_SUCCESS, commandResult.getFeedbackType());
        assertEquals(1, modelStub.productsAdded.size());
        assertEquals(productWithExistingVendor, modelStub.productsAdded.get(0));
    }

    @Test
    public void getPendingConfirmation_returnsInactivePendingConfirmation() {
        Product validProduct = new ProductBuilder().build();
        AddProductCommand addProductCommand = new AddProductCommand(validProduct);

        PendingConfirmation pendingConfirmation = addProductCommand.getPendingConfirmation();
        assertFalse(pendingConfirmation.getNeedConfirmation());
    }

    @Test
    public void equals() {
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
        Product validProduct = new ProductBuilder().build();
        AddProductCommand addProductCommand = new AddProductCommand(validProduct);
        String expected = AddProductCommand.class.getCanonicalName() + "{toAdd=" + validProduct + "}";
        assertEquals(expected, addProductCommand.toString());
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

        @Override
        public void commitVendorVault() {
        }

        @Override
        public void undoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoVendorVault() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redoVendorVault() {
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
        public Optional<Person> findByEmail(Email email) {
            requireNonNull(email);
            return Optional.empty();
        }

        void seedExistingProduct(Product product) {
            requireNonNull(product);
            productsAdded.add(product);
        }
    }

    /** Product test double that forces a specific warning result. */
    private static class ProductWithForcedWarning extends Product {
        private final DuplicateProductWarning forcedWarning;

        ProductWithForcedWarning(Identifier identifier, Name name, Quantity quantity, RestockThreshold threshold,
                                 DuplicateProductWarning forcedWarning) {
            super(identifier, name, quantity, threshold);
            this.forcedWarning = forcedWarning;
        }

        @Override
        public DuplicateProductWarning isSameProductWarn(Product otherProduct) {
            return forcedWarning;
        }
    }
}
