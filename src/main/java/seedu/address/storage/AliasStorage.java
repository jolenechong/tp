package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ReadOnlyAliases;

/**
 * Represents a storage for {@link seedu.address.model.Aliases}
 */
public interface AliasStorage {

    Path getAliasFilePath();

    Optional<ReadOnlyAliases> readAliases() throws DataLoadingException;

    Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException;

    void saveAliases(ReadOnlyAliases aliasList) throws IOException;

    void saveAliases(ReadOnlyAliases aliasList, Path filePath) throws IOException;
}
