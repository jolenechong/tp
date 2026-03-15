package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.Aliases;
import seedu.address.model.ReadOnlyAliases;
import seedu.address.model.alias.Alias;

/**
 * An Immutable Alias List that is serializable to JSON format.
 */
@JsonRootName(value = "aliasList")
public class JsonSerializableAlias {

    private final List<JsonAdaptedAlias> aliasList = new ArrayList<>();

    @JsonCreator
    public JsonSerializableAlias(@JsonProperty("aliasList") List<JsonAdaptedAlias> aliases) {
        this.aliasList.addAll(aliases);
    }

    public JsonSerializableAlias(ReadOnlyAliases source) {
        aliasList.addAll(source.getAliasList().stream().map(JsonAdaptedAlias::new).collect(Collectors.toList()));
    }

    /**
     * Converts this aliases into the model's {@code Aliases} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Aliases toModelType() throws IllegalValueException {
        Aliases aliases = new Aliases();
        for (JsonAdaptedAlias jsonAdaptedAlias : aliasList) {
            Alias alias = jsonAdaptedAlias.toModelType();

            aliases.addAlias(alias);
        }
        return aliases;
    }
}
