package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAliases;

/**
 * A class to access Alias data stored as a json file on the hard disk.
 */
public class JsonAliasStorage implements AliasStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonInventoryStorage.class);

    private Path filePath;

    public JsonAliasStorage(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getAliasFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAliases> readAliases() throws DataLoadingException {
        return readAliases(filePath);
    }

    @Override
    public Optional<ReadOnlyAliases> readAliases(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableAlias> jsonAliases = JsonUtil.readJsonFile(
                filePath, JsonSerializableAlias.class);
        if (!jsonAliases.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAliases.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveAliases(ReadOnlyAliases aliases) throws IOException {
        saveAliases(aliases, filePath);
    }

    @Override
    public void saveAliases(ReadOnlyAliases aliases, Path filePath) throws IOException {
        requireNonNull(aliases);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAlias(aliases), filePath);
    }

}
