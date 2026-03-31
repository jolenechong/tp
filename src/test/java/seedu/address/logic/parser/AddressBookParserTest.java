package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddProductCommand;
import seedu.address.logic.commands.AliasCommand;
import seedu.address.logic.commands.ArchiveProductCommand;
import seedu.address.logic.commands.CancelCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandType;
import seedu.address.logic.commands.ConfirmCommand;
import seedu.address.logic.commands.DeleteAliasCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteProductCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.EditProductCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindProductCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListAllCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListProductsCommand;
import seedu.address.logic.commands.PendingConfirmation;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.RestoreProductCommand;
import seedu.address.logic.commands.SetThresholdCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.ReadOnlyVendorVault;
import seedu.address.model.alias.Alias;
import seedu.address.model.alias.exceptions.DuplicateAliasException;
import seedu.address.model.alias.exceptions.NoAliasFoundInAliasListException;
import seedu.address.model.person.Email;
import seedu.address.model.person.NameContainsKeywordsScoredPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.product.Product;
import seedu.address.model.product.ProductNameContainsKeywordsScoredPredicate;
import seedu.address.model.product.RestockThreshold;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;
import seedu.address.testutil.ProductBuilder;
import seedu.address.testutil.ProductUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();
    private final PendingConfirmation confirmation = new PendingConfirmation(() -> Optional.empty(), () ->
            Optional.empty());

    @Test
    public void parseCommand_validAlias_resolvesToOriginalCommand() throws Exception {
        final String newAlias = "ls";

        Model model = new ModelStub() {
            @Override
            public Alias findAlias(String aliasStr) {
                return new Alias(newAlias, ListCommand.COMMAND_WORD);
            }
        };

        Command result = parser.parseCommand(newAlias, new PendingConfirmation(), model);
        assertTrue(result instanceof ListCommand);
    }

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person),
                new PendingConfirmation(), new ModelManager());
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_addProduct() throws Exception {
        Product product = new ProductBuilder().build();
        AddProductCommand command = (AddProductCommand) parser.parseCommand(ProductUtil.getAddCommand(product),
                new PendingConfirmation(), new ModelManager());
        assertEquals(new AddProductCommand(product), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(), new ModelManager()) instanceof ClearCommand);
    }

    @Test
    public void parseCommand_confirmationCommandLowerCase() throws Exception {
        assertTrue(parser.parseCommand(ConfirmCommand.COMMAND_WORD, confirmation, new ModelManager())
                instanceof ConfirmCommand);
    }

    @Test
    public void parseCommand_confirmationCommandUpperCase() throws Exception {
        assertTrue(parser.parseCommand(ConfirmCommand.COMMAND_WORD.toUpperCase(), confirmation, new ModelManager())
                instanceof ConfirmCommand);
    }

    @Test
    public void parseCommand_cancelCommand() throws Exception {
        assertTrue(parser.parseCommand("n", confirmation, new ModelManager()) instanceof CancelCommand);
    }

    /**
     * The needsConfirmation boolean does not affect equality checks.
     *
     */
    @Test
    public void parseCommand_delete() throws Exception {
        Person person = new PersonBuilder().build();
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + person.getEmail().toString(),
                new PendingConfirmation(),
                new ModelManager());
        assertEquals(new DeleteCommand(person.getEmail(), true), command);
        assertEquals(new DeleteCommand(person.getEmail(), false), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + person.getEmail().value + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor),
                new PendingConfirmation(),
                new ModelManager());
        assertEquals(new EditCommand(person.getEmail(), descriptor), command);
    }

    @Test
    public void parseCommand_undo() throws Exception {
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof UndoCommand);
        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(),
                new ModelManager()) instanceof UndoCommand);
    }

    @Test
    public void parseCommand_redo() throws Exception {
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof RedoCommand);
        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(),
                new ModelManager()) instanceof RedoCommand);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(),
                new ModelManager()) instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")),
                new PendingConfirmation(),
                new ModelManager());
        assertEquals(new FindCommand(new NameContainsKeywordsScoredPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_findProduct() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindProductCommand command = (FindProductCommand) parser.parseCommand(
                FindProductCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")),
                new PendingConfirmation(),
                new ModelManager());
        assertEquals(new FindProductCommand(new ProductNameContainsKeywordsScoredPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(),
                new ModelManager()) instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3",
                new PendingConfirmation(),
                new ModelManager()) instanceof ListCommand);
    }

    @Test
    public void parseCommand_listAll() throws Exception {
        assertTrue(parser.parseCommand(ListAllCommand.COMMAND_WORD, new PendingConfirmation(), new ModelManager())
                instanceof ListAllCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand("", new PendingConfirmation(), new ModelManager()));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand",
                new PendingConfirmation(), new ModelManager()));
    }

    @Test
    public void parseCommand_listProducts() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        Command command = parser.parseCommand("listproduct", new PendingConfirmation(), new ModelManager());

        assertTrue(command instanceof ListProductsCommand);
    }

    @Test
    public void parseCommand_archiveProduct() throws Exception {
        Command command = parser.parseCommand("archiveproduct id/coffee",
                new PendingConfirmation(),
                new ModelManager());

        assertTrue(command instanceof ArchiveProductCommand);
    }

    @Test
    public void parseCommand_restoreProduct() throws Exception {
        Command command = parser.parseCommand("restoreproduct id/coffee",
                new PendingConfirmation(),
                new ModelManager());

        assertTrue(command instanceof RestoreProductCommand);
    }

    @Test
    public void parseCommand_listproduct_throwsParseException() {
        assertThrows(ParseException.class, ()
            -> parser.parseCommand("listproducts", new PendingConfirmation(), new ModelManager()));
    }

    @Test
    public void parseCommand_deleteProduct() throws Exception {
        assertTrue(parser.parseCommand(
                DeleteProductCommand.COMMAND_WORD + " P001",
                new PendingConfirmation(),
                new ModelManager())
                instanceof DeleteProductCommand);
    }

    @Test
    public void parseCommand_deleteProduct_extraArgs() throws Exception {
        assertTrue(parser.parseCommand(
                "deleteproduct P001 extra",
                new PendingConfirmation(),
                new ModelManager())
                instanceof DeleteProductCommand);
    }

    @Test
    public void parseCommand_editProduct() throws Exception {
        AddressBookParser parser = new AddressBookParser();

        assertTrue(
            parser.parseCommand(
                "editproduct SKU-1001 n/iPad",
                new PendingConfirmation(),
                new ModelManager())
            instanceof EditProductCommand
        );
    }

    @Test
    public void parseCommand_setThreshold() throws Exception {
        SetThresholdCommand command = (SetThresholdCommand) parser.parseCommand(
                SetThresholdCommand.COMMAND_WORD + " 5",
                new PendingConfirmation(),
                new ModelManager());
        assertEquals(new SetThresholdCommand(new RestockThreshold("5")), command);
    }

    @Test
    public void parseCommand_alias() throws Exception {
        Command command = parser.parseCommand(AliasCommand.COMMAND_WORD + " "
                        + CommandType.LIST.getCommandWord() + " ls",
                new PendingConfirmation(),
                new ModelManager());

        assertTrue(command instanceof AliasCommand);
    }

    @Test
    public void parseCommand_aliasInvalidFormat_throwsParseException() {
        assertThrows(ParseException.class, ()
                -> parser.parseCommand(AliasCommand.COMMAND_WORD + "  a",
                    new PendingConfirmation(),
                    new ModelManager()));
    }

    @Test
    public void parseCommand_aliasCommandWordMismatch_throwsParseException() {
        assertThrows(ParseException.class, ()
                -> parser.parseCommand(AliasCommand.COMMAND_WORD + " lst ls",
                    new PendingConfirmation(),
                    new ModelManager()));
    }

    @Test
    public void parseCommand_aliasNewAliasHasSpace_throwsParseException() {
        assertThrows(ParseException.class, ()
                -> parser.parseCommand(AliasCommand.COMMAND_WORD + " list ls ls",
                    new PendingConfirmation(),
                    new ModelManager()));
    }

    @Test
    public void parseCommand_deletealias() throws Exception {
        Command command = parser.parseCommand(DeleteAliasCommand.COMMAND_WORD + " "
                    + CommandType.LIST.getCommandWord() + "ls",
                    new PendingConfirmation(),
                    new ModelManager());

        assertTrue(command instanceof DeleteAliasCommand);
    }

    @Test
    public void parseCommand_deletealiasNoArguments_throwsParseException() {
        assertThrows(ParseException.class, ()
                -> parser.parseCommand(DeleteAliasCommand.COMMAND_WORD,
                    new PendingConfirmation(),
                    new ModelManager()));
    }

    @Test
    public void parseCommand_deletealiasMoreThanOneArguments_throwsParseException() {
        assertThrows(ParseException.class, ()
                -> parser.parseCommand(DeleteAliasCommand.COMMAND_WORD + " a a",
                    new PendingConfirmation(),
                    new ModelManager()));
    }

    /**
     * A default model stub that has all of the methods failing.
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
        public Optional<Product> findById(String id) {
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

        @Test
        public void parseCommand_editProduct() throws Exception {
            AddressBookParser parser = new AddressBookParser();

            Model model = new ModelManager();
            PendingConfirmation pendingConfirmation = new PendingConfirmation();

            Command command = parser.parseCommand(
                    "editproduct SKU-1001 n/iPad",
                    pendingConfirmation,
                    model
            );

            assertTrue(command instanceof EditProductCommand);
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

        @Override
        public void commitVendorVault(String actionSummary) {
            // stub method
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
