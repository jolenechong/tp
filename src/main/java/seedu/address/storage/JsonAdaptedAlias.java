package seedu.address.storage;

import static seedu.address.logic.Messages.MESSAGE_ALIAS_CANNOT_BE_EMPTY;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_CONTAINS_SPACE;
import static seedu.address.logic.Messages.MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.CommandType;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.alias.Alias;

/**
 * Jackson-friendly version of {@link Alias}.
 */
public class JsonAdaptedAlias {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Alias's %s field is missing";

    private final String alias;
    private final String originalCommand;

    /**
     * Constructs a {@code JsonAdaptedAlias} with the given alias details.
     */
    @JsonCreator
    public JsonAdaptedAlias(@JsonProperty("alias") String alias,
                            @JsonProperty("originalCommand") String originalCommand) {
        this.alias = alias;
        this.originalCommand = originalCommand;
    }

    /**
     * Converts a given {@code Alias} into this class for Jackson use.
     */
    public JsonAdaptedAlias(Alias aliasObj) {
        this.alias = aliasObj.getAlias();
        this.originalCommand = aliasObj.getOriginalCommand();
    }

    /**
     * Converts this Jackson-friendly adapted alias object into the model's {@code Alias} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted alias.
     */
    public Alias toModelType() throws IllegalValueException {
        if (alias == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    "alias"));
        }

        if (originalCommand == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    "original command"));
        }

        if (!CommandType.isValidAliasCommand(originalCommand)) {
            throw new ParseException(MESSAGE_ORIGINAL_COMMAND_DOES_NOT_EXISTS);
        }

        if (alias.isEmpty()) {
            throw new ParseException(MESSAGE_ALIAS_CANNOT_BE_EMPTY);
        }

        if (alias.contains(" ")) {
            throw new ParseException(MESSAGE_ALIAS_CONTAINS_SPACE);
        }

        if (CommandType.isValidAliasCommand(alias)) {
            throw new ParseException(MESSAGE_ALIAS_IS_A_PREDEFINED_COMMAND);
        }

        return new Alias(alias, originalCommand);
    }
}
