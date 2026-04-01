package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_ALIAS;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
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
import seedu.address.model.product.Product;


public class AliasCommandTest {

    @Test
    public void execute_validAlias_success() throws Exception {
        Alias validAlias = new Alias("ls", ListCommand.COMMAND_WORD);
        ModelStub modelStub = new ModelStubAcceptingAlias();

        CommandResult result = new AliasCommand(validAlias).execute(modelStub);

        assertEquals(String.format(AliasCommand.MESSAGE_ADD_ALIAS_SUCCESS, "ls"), result.getFeedbackToUser());
    }

    @Test
    public void execute_duplicateAlias_throwsCommandException() throws CommandException {
        Alias duplicateAlias = new Alias("ls", ListCommand.COMMAND_WORD);
        ModelStub modelStub = new ModelStubThrowingDuplicateAliasException();

        assertThrows(CommandException.class, MESSAGE_DUPLICATE_ALIAS, () ->
                new AliasCommand(duplicateAlias).execute(modelStub)
        );
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        Alias validAlias = new Alias("ls", ListCommand.COMMAND_WORD);

        assertThrows(NullPointerException.class, () ->
                new AliasCommand(validAlias).execute(null)
        );
    }

    @Test
    public void getPendingConfirmation_returnsNewPendingConfirmation() {
        Alias validAlias = new Alias("ls", ListCommand.COMMAND_WORD);
        AliasCommand command = new AliasCommand(validAlias);

        assertNotNull(command.getPendingConfirmation());
        assertFalse(command.getPendingConfirmation().getNeedConfirmation());
    }

    @Test
    public void execute_noArgCommandAndEmptyAliasList_returnsEmptyMessage() throws Exception {
        ModelStub modelStub = new ModelStubWithEmptyAliasList();
        CommandResult result = new AliasCommand().execute(modelStub);
        assertEquals(AliasCommand.MESSAGE_EMPTY_ALIAS_LIST, result.getFeedbackToUser());
    }

    @Test
    public void execute_noArgCommandAndNonEmptyAliasList_returnsFormattedList() throws Exception {
        ModelStub modelStub = new ModelStubWithAliasList();
        CommandResult result = new AliasCommand().execute(modelStub);

        String expected = AliasCommand.MESSAGE_DISPLAY_ALIAS_LIST
                + "1) " + ListCommand.COMMAND_WORD + " -> " + "ls" + "\n";
        assertEquals(expected, result.getFeedbackToUser());
    }

    @Test
    public void execute_noArgCommandAndNullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AliasCommand().execute(null)
        );
    }

    @Test
    public void hasAlias_noArgConstructor_returnsFalse() {
        assertFalse(new AliasCommand().hasAlias());
    }

    @Test
    public void hasAlias_aliasConstructor_returnsTrue() {
        Alias validAlias = new Alias("ls", ListCommand.COMMAND_WORD);
        assertTrue(new AliasCommand(validAlias).hasAlias());
    }

    private class ModelStubWithEmptyAliasList extends ModelStub {
        @Override
        public ReadOnlyAliases getAliases() {
            return new ReadOnlyAliases() {
                @Override
                public List<Alias> getAliasList() {
                    return List.of();
                }
            };
        }
    }

    private class ModelStubWithAliasList extends ModelStub {
        @Override
        public ReadOnlyAliases getAliases() {
            return new ReadOnlyAliases() {
                @Override
                public List<Alias> getAliasList() {
                    return List.of(
                            new Alias("ls", ListCommand.COMMAND_WORD)
                    );
                }
            };
        }
    }

    private class ModelStubAcceptingAlias extends ModelStub {
        @Override
        public void addAlias(Alias alias) {
            // accepts without throwing
        }
    }

    private class ModelStubThrowingDuplicateAliasException extends ModelStub {
        @Override
        public void addAlias(Alias alias) throws DuplicateAliasException {
            throw new DuplicateAliasException();
        }
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setProduct(Product target, Product editedProduct) {
            throw new AssertionError("This method should not be called.");
        }

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
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
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
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarNameMatch(Person candidate, Person exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Product> findSimilarNameMatch(Product candidate, Product exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarPhoneMatch(Person candidate, Person exclude) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Optional<Person> findSimilarAddressMatch(Person candidate, Person exclude) {
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
        public void archiveProduct(Product target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restoreProduct(Product target) {
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
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
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
        public void archivePerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void restorePerson(Person person) {
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
}
