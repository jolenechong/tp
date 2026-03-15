package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class UserPrefsTest {

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        UserPrefs userPref = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPref.setGuiSettings(null));
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAddressBookFilePath(null));
    }

    @Test
    public void setProductsFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setProductsFilePath(null));
    }

    @Test
    public void setAliasFilePath_nullPath_throwsNullPointerException() {
        UserPrefs userPrefs = new UserPrefs();
        assertThrows(NullPointerException.class, () -> userPrefs.setAliasFilePath(null));
    }

    @Test
    public void checkEquals() {
        Path addressBookPath = Path.of("addressBook");
        Path productsFilePath = Path.of("products");
        Path aliasFilePath = Path.of("aliases");

        UserPrefs userPrefs = new UserPrefs();
        UserPrefs userPrefs1 = new UserPrefs();

        userPrefs.setAddressBookFilePath(addressBookPath);
        userPrefs1.setAddressBookFilePath(addressBookPath);

        userPrefs.setProductsFilePath(productsFilePath);
        userPrefs1.setProductsFilePath(productsFilePath);

        userPrefs.setAliasFilePath(aliasFilePath);
        userPrefs1.setAliasFilePath(aliasFilePath);

        assertEquals(userPrefs, userPrefs1);
    }
}
