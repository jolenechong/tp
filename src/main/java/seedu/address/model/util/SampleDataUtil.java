package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.Inventory;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyInventory;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.product.Identifier;
import seedu.address.model.product.Product;
import seedu.address.model.product.Quantity;
import seedu.address.model.product.RestockThreshold;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code VendorVault} with sample data.
 */
public class SampleDataUtil {
    private static seedu.address.model.person.Name personName(String value) {
        return new seedu.address.model.person.Name(value);
    }

    private static seedu.address.model.product.Name productName(String value) {
        return new seedu.address.model.product.Name(value);
    }

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(personName("Adafruit Industries"), new Phone("64601234"), new Email("support@adafruit.com"),
                    new Address("151 Varick St, New York, NY 10013, USA"),
                    getTagSet("IOT")),
            new Person(personName("Cytron Technologies Pte. Ltd"), new Phone("65480668 (Office), 91234567 (Sales)"), new Email("sg.sales@cytron.io"),
                    new Address("09 Collyer Quay"),
                    getTagSet("electronics")),
            new Person(personName("TechSource Electronics"), new Phone("61234567"), new Email("sales@techsource.com"),
                    new Address("15 Kallang Way, Singapore"),
                    getTagSet("electronics", "IOT")),
            new Person(personName("Synapse Supply"), new Phone("+65 62981234, +60 169876543"), new Email("hello@synapse.sg"),
                    new Address("3 Kaki Bukit Road, Singapore 415978"),
                    getTagSet("wholesale")),
            new Person(personName("Rochor & Co."), new Phone("63330881, 69041288"), new Email("support.rochor@yahoo.com"),
                    new Address("Sim Lim Square #05-55"),
                    getTagSet()),
            new Person(personName("ByteLabs"), new Phone("65607845 (Mike)"), new Email("bytelabs@gmail.com"),
                    new Address("Ubi Techpark Lot 9"),
                    getTagSet("partner", "refurb")),
            new Person(personName("Soonheng Retail & Logistics"), new Phone("+60 22 3650 7080"), new Email("orders" +
                    "@soonheng.com.my"), new Address("Jalan Ciku No. 03, Kluang"), getTagSet()),
            new Person(personName("Audio House"), new Phone("68412122"), new Email("sales@ah.com"), new Address(
                    "Audio House Building #01-01"), getTagSet("electronics", "home"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

    public static Product[] getSampleProducts() {
        return new Product[] {
            new Product(new Identifier("SKU-1003"), productName("Arduino Uno R4"),
                    new Quantity("50"), new RestockThreshold("10"), new Email("sales@techsource.com")),
            new Product(new Identifier("SKU-288"), productName("HP LaserJet (M428fdw)"),
                    new Quantity("17"), new RestockThreshold("5")),
            new Product(new Identifier("DE/5"), productName("PlayStation"),
                    new Quantity("0"), new RestockThreshold("0"), new Email("sg.sales@cytron.io")),
            new Product(new Identifier("POS-THERMAL-80"), productName("Thermal Receipt Printer"),
                    new Quantity("8"), new RestockThreshold("3")),
            new Product(new Identifier("DE/870"), productName("ASUS X870 Motherboard"),
                    new Quantity("11"), new RestockThreshold("3"), new Email("hello@synapse.sg")),
            new Product(new Identifier("SKU-256/SG"), productName("microSD Card 256GB"),
                    new Quantity("30"), new RestockThreshold("15"), new Email("support.rochor@yahoo.com")),
            new Product(new Identifier("SKU-610/INTL"), productName("Light & Color Sensor"),
                    new Quantity("50"), new RestockThreshold("25"), new Email("support@adafruit.com")),
            new Product(new Identifier("DE/480-REF"), productName("ThinkPad T480 Refurbished"),
                    new Quantity("3"), new RestockThreshold("0"), new Email("bytelabs@gmail.com")),
            new Product(new Identifier("CAM-817"), productName("Nikon 24-70MM Camera"),
                    new Quantity("1"), new RestockThreshold("3"), new Email("sales@ah.com")),
            new Product(new Identifier("DE/339"), productName("NVMe SSD 2TB"),
                    new Quantity("10"), new RestockThreshold("10"), new Email("support.rochor@yahoo.com")),
        };
    }

    public static ReadOnlyInventory getSampleInventory() {
        Inventory sampleInv = new Inventory();
        for (Product sampleProduct : getSampleProducts()) {
            sampleInv.addProduct(sampleProduct);
        }
        return sampleInv;
    }
}
