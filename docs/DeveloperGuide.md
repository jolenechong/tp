---
  layout: no_sidebar.md
  title: "Developer Guide"
  pageNav: 3
---

# VendorVault Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete support@adafruit.com`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class, as illustrated below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete support@adafruit.com")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete support@adafruit.com` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component

* stores vendor contact data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores inventory data i.e., all `Product` objects (which are contained in a `UniqueProductList` object).
* stores the current set of `Person`/`Product` objects as a separate _filtered_ list which is exposed as an unmodifiable `ObservableList<Person>`/`ObservableList<Product>`.
* stores alias data i.e., all `Alias` objects (which are contained in a `AliasList` object).
* stores a `UserPref` object that represents the user’s preferences. This is exposed as a `ReadOnlyUserPref` object.
* does not depend on any of the other three components.

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-W08-2/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component
* saves contact, inventory, alias and user preference data in JSON format, and reads them back into corresponding objects.
* inherits from `AddressBookStorage`, `InventoryStorage`, `AliasStorage` and `UserPrefStorage`
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Undo and Redo feature

#### Implementation

The undo/redo mechanism is facilitated by `VersionedVendorVault`. It extends `VendorVault` with an undo/redo history, stored internally as an `vendorVaultStateList` and `currentStatePointer`. Additionally, it implements the following operations:

`VendorVault` is the main data structure that holds the current state of the address book and inventory data. If only the address book data is modified, the inventory data will still be saved in the `VendorVault` state. This allows us to have a single undo/redo mechanism for both address book and inventory data.

* `VersionedVendorVault#commit()` — Saves the current VendorVault state in its history.
* `VersionedVendorVault#undo()` — Restores the previous VendorVault state from its history.
* `VersionedVendorVault#redo()` — Restores a previously undone VendorVault state from its history.
* `VersionedVendorVault#canUndo()` and `VersionedVendorVault#canRedo()` — Checks if undo/redo operations are possible based on the current state of the history.

These operations are exposed in the `Model` interface as `Model#commitVendorVault()`, `Model#undoVendorVault()`, `Model#redoVendorVault()`, `Model#canUndoVendorVault` and `Model#canRedoVendorVault` respectively.

#### Usage Scenario
Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedVendorVault` will be initialized with the initial VendorVault (which includes the address book and inventory internally) state, and the `currentStatePointer` pointing to that single VendorVault state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete support@adafruit.com` command to delete the corresponding vendor contact in VendorVault. The `delete` command calls `Model#commitVendorVault()`, causing the modified state of the VendorVault after the `delete support@adafruit.com` command executes to be saved in the `vendorVaultStateList`, and the `currentStatePointer` is shifted to the newly inserted VendorVault state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/Adafruit …​` to add a new vendor contact. The `add` command also calls `Model#commitVendorVault()`, causing another modified VendorVault state to be saved into the `vendorVaultStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitVendorVault()`, so the VendorVault state will not be saved into the `vendorVaultStateList`.

</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoVendorVault()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous VendorVault state, and restores the VendorVault to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canundoVendorVault()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoVendorVault()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the VendorVault to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `vendorVaultStateList.size() - 1`, pointing to the latest VendorVault state, then there are no undone VendorVault states to restore. The `redo` command uses `Model#canRedoVendorVault()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the VendorVault, such as `list`, will usually not call `Model#commitVendorVault()`, `Model#undoVendorVault()` or `Model#redoVendorVault()`. Thus, the `vendorVaultStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitVendorVault()`. Since the `currentStatePointer` is not pointing at the end of the `vendorVaultStateList`, all VendorVault states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/Adafruit …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire VendorVault.
  * Pros: Easy to implement and support for future commands.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command and future commands are correct.

Given that VendorVault’s data size is expected to remain relatively small (e.g. around 1,000 contacts and 5,000 products) and undoable actions are typically performed in small batches during regular use by small business owners, so the memory overhead of storing snapshots is acceptable.

**Aspect: Granularity of undo/redo scope:**

* **Alternative 1 (current choice):** Single unified VendorVault snapshot per commit.
    * Pros: One consistent state per commit makes it simple to reason about as one undo always undoes exactly one user action regardless of whether it touched contacts, products, or both. 
    * Cons: Even if only contacts are modified, the full inventory snapshot is stored too, wasting memory.

* **Alternative 2:** Separate versioned histories for AddressBook and Inventory.
    * Pros: More fine-grained memory usage.  
    * Cons: Significantly increases complexity. A single user action that touches both (e.g. clear) would need to commit to both histories atomically, and undo would need to roll back both in sync, increasing the risk of histories becoming desynchronised.

Since VendorVault’s data size is expected to be small and the number of undoable actions per session is unlikely to be large, the additional memory usage from storing full snapshots of both internally is acceptable, to get more consistent undo behaviour.

**Aspect: Where the commit is triggered:**

* **Alternative 1 (current choice):** Each command calls Model#commitVendorVault() itself.
    * Pros: Gives each command full control, so commands that should not create a snapshot (e.g. list, find) simply don't call commit.
    * Cons: Future command implementors must remember to call commit.

* **Alternative 2:** LogicManager automatically commits after every successful command execution.
    * Pros: Centralises the responsibility in one place. 
    * Cons: Read-only commands (e.g. list, find) would create unnecessary snapshots unless they are explicitly excluded, requiring a marker interface or flag on the Command class.

Given that only certain commands should create undoable states, we chose Alternative 1 as it gives each command explicit control over when a snapshot is created. This avoids unnecessary snapshots for read-only commands and keeps the undo history meaningful.

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

### Command History Feature

#### Implementation
The command history feature is implemented using a `CommandHistory` class that maintains a list of previously executed commands. Each time a command is executed, it is added to the `CommandHistory`. Additionally, it implements the following operations:
* `CommandHistory#add(String commandText)` — Adds a command as a string to the history.
* `CommandHistory#getPrevious(String currentInput)` — Returns the previous command in the history.
* `CommandHistory#getNext(String currentInput)` — Returns the next command in the history.
* `CommandHistory#resetNavigation()` — Resets the navigation pointer to the end of the history.

These operations are exposed in the `Logic` interface through the `Logic#getCommandHistory()` method, which returns the `CommandHistory` object, allowing the UI to access the command history and implement features such as navigating through previous commands using the up/down arrow keys.

#### Usage Scenario
Given below is an example usage scenario and how the command history behaves at each step.

Step 1. The user executes the command `add n/Adafruit Industries...`. The command is executed and added to the `CommandHistory`.

<box type="info" seamless>

**Note:** Commands are added to the `CommandHistory` only if they execute successfully. This includes commands whose execution produces warnings.

</box>

Step 2. The user executes the command `delete support@adafruit.com`. The command is executed and added to the `CommandHistory`.

Step 3. The user presses the UP arrow key to navigate to the previous command. The `CommandHistory#getPrevious()` method is called with the current input (empty in this case). The command box is then updated with the previous command `delete support@adafruit.com`.

The following sequence diagram shows how the `getPrevious` operation works as described:
<puml src="diagrams/command-history/SequenceDiagram.puml" alt="Command History Sequence Diagram" />

<div style="height: 20px;"></div>

The following activity diagram below summarizes how key presses are handled to navigate through the command history:
<puml src="diagrams/command-history/ActivityDiagram.puml" alt="Command History Activity Diagram" />

#### Design Considerations
**Aspect: How command history stores input:**

* **Alternative 1 (current choice):** Maintain a list of command strings and navigate via an index.
    * Pros: Simple, lightweight and easy to integrate with UI navigation (UP/DOWN keys).
    * Cons: Only stores raw strings, so it cannot preserve command execution context.

* **Alternative 2:** Store command objects with full state.
    * Pros: Could enable richer functionality
    * Cons: More complex, higher memory usage, unnecessary for typical command history navigation.

Since the goal is shell-like navigation, storing only strings is sufficient and keeps the implementation simple.

**Aspect: How navigation handles partially typed input:**

* **Alternative 1 (current choice):** Preserve the current input as a “draft” when the user navigates through history.
    * Pros: More UX-friendly, as user can return to unfinished input after scrolling through history.
    * Cons: Slight increase in code complexity to manage a separate draftCommandText.

* **Alternative 2:** Ignore partially typed input and always replace with history.
    * Pros: Simpler, no need for a draft variable.
    * Cons: User loses in-progress typing when navigating, which is frustrating in practice.

Preserving draft input improves user experience and is easy to implement with minimal overhead.

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

### Data Archiving Feature
#### Implementation
{currently for vendor only}

The archive feature allows vendor contacts to be hidden from the main list without permanently deleting them. Archived vendors remain stored in the system and can be restored later.

The feature introduces two commands:
```
archive EMAIL
restore EMAIL
```

Internally, vendors are represented using the `Person` class. A boolean field `archived` is introduced in the `Person` model to track whether a vendor is archived.


Archived vendors are stored in the same `AddressBook` data structure as active vendors. Instead of deleting vendors, the `archived` field is set to `true`.

The archive and restore operations are exposed through the `Model` interface:
```
Model#archivePerson(Person person)
Model#restorePerson(Person person)
```

These operations update the `Person` object.

#### Command Flow

The following sequence occurs when executing `archive support@adafruit.com`:

1. `AddressBookParser` identifies the command word `archive`.
2. `ArchiveCommandParser` parses the email argument.
3. An `ArchiveCommand` object is created.
4. `LogicManager` executes the command.
5. The command retrieves the vendor from the filtered list.
6. `Model.archivePerson()` is called.
7. The `Person` object is replaced with a new instance with `archived = true`.
8. The UI updates automatically because archived vendors are excluded from the filtered list.

The `restore EMAIL` command follows a similar flow but sets `archived = false`.

#### Filtering Behaviour

Archived vendors are hidden from the default UI display.

The model maintains a filtered list where:
```
person -> !person.isArchived()
```

This ensures archived vendors remain stored but are not displayed in the main vendor list.

When a vendor is restored, the archived flag is set to `false`, causing the vendor to reappear in the UI.

#### Error Handling

Error handling is implemented for email lookups.
{more implementations to be added}


If the email does not match a valid target, a `CommandException` is thrown.

**Model Layer**

Protects internal state consistency. For example, attempting to archive an already archived vendor results in an `IllegalArgumentException`.

#### Design Considerations

**Aspect: Representation of archived vendors**

Option 1 (Current implementation): Use a boolean `archived` flag in the `Person` model.

Pros:
- Simple implementation
- Minimal changes to existing architecture
- Archived vendors remain stored in the same data structure

Cons:
- Requires filtering logic when displaying vendors

Option 2: Maintain a separate archive list.

Pros:
- Clear separation between active and archived vendors

Cons:
- Increases model complexity
- Requires synchronization between lists

Option 1 was chosen as it integrates better with the existing data structure and keeps the implementation lightweight.

#### Future Improvements

Future extensions may allow vendors to be archived using other identifiers such as:
```
archive email john@email.com
archive name Alice
restore phone 91234567
```

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

### Command Alias Feature
To be added.

#### Implementation
#### Usage Scenario
#### Design Considerations

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

### Better Search Feature
To be added.

#### Implementation
#### Usage Scenario
#### Design Considerations

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

## **Appendix: Requirements**

### Product scope

**Target user profile**:

Small business owners who:
* Have many vendors' contacts and inventory to manage
* Are tech-savvy and prefers CLI over GUI

**Value proposition**:

VendorVault helps small business owners seamlessly manage vendor contacts and track inventory in one simple system.
By flagging and sorting low-quantity products, owners instantly know what needs restocking and who to contact, enabling timely action without relying on complex or costly inventory tools.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …      | I want to …               | So that I can …                                                   |
|----------|-------------|---------------------------|-------------------------------------------------------------------|
| `* *`    | new user    | see usage guide           | recap and learn commands                                          |
| `* * *`  | user        | add a new contact         | add new vendors I work with                                       |
| `* * *`  | user        | delete a contact          | remove vendors I no longer work with                              |
| `* * *`  | user        | view contacts             |                                                                   |
| `* *`    | user        | find a contact by name    | locate their details without having to go through the entire list |
| `*`      | user        | sort contacts by name     | browse them easily                                                |
| `* * *`  | user        | add a product             | add new products I sell                                           |
| `* * *`  | user        | delete a product          | remove products I no longer sell                                  |
| `* * *`  | user        | view products             |                                                                   |
| `* *`    | user        | find a product by name    | locate their details without having to go through the entire list |
| `*`      | user        | sort products by name     | browse them easily                                                |
| `* *`    | user        | view inventory statistics | understand my product quantity levels                             |
| `* *`    | expert user | add alias for commands    | create alias for long commands according to my preferences        |
| `* *`    | expert user | delete alias for commands | remove alias for I no longer want to use                          |
| `* *`    | expert user | view aliases for commands | view all aliases that I have set                                  |

### Use cases

(For all use cases below, the **System** is the `VendorVault` application, referred to as `VV` and the **Actor** is the `User`, unless specified otherwise)

**Use case: UC1 - Add a Vendor Contact**

**Preconditions: Application is running, user is on the main screen.**

**MSS**

1. User chooses to add a vendor contact.
2. VV requests for details for the vendor contact.
3. User provides the requested details.
4. VV requests for confirmation.
5. User confirms.
6. VV adds contact and displays a list of vendor contacts.

Use case ends.

**Extensions**

* 3a. VV detects error in provided data (eg. missing compulsory fields, invalid data format).
  * 3a1. VV requests for the correct data.
  * 3a2. User provides new data.

  Steps 3a1-3a2 are repeated until all fields are correct.

  Use case resumes from step 4.

* 3b. VV detects duplicate vendor contact.
  * 3b1. VV requests for correct data that is not a duplicate.
  * 3b2. User provides new data.

  Steps 3b1-3b2 are repeated until all fields are correct.

  Use case resumes from step 4.

* *a. At any time, User chooses to cancel adding the contact.
  * *a1. VV requests to confirm cancellation.
  * *a2. User confirms cancellation.

  Use case ends.

**Use Case: UC2 - View Vendor Contacts**

**Preconditions: Application is running, user is on the main screen.**

**MSS**

1. User chooses to view vendor contact.
2. VV shows the contact information for all vendors

Use case ends.

**Extensions**

* 2a. VV detects there is no contact found.
  * 2a1. VV will create a new contact file with preloaded information.

  Use case resumes from step 2.

**Use case: UC3 - Delete Vendor Contact**

**Preconditions: Application is running, user is on the main screen and has added a contact.**

**MSS**

1. User chooses to delete a contact.
2. VV requests for confirmation for deleting the contact.
3. User confirms deletion.
4. VV deletes contact and displays a list of current contacts.

Use case ends.

**Extensions**

* 2a. User decides not to delete the contact, rejecting the deletion.
  * 2a1. VV displays a list of current vendor contacts.
  
  Use case ends.

**Use Case: UC4 - Add Product**

**Preconditions: Application is running, user is on the main screen.**

**MSS**

1. User chooses to add a Product.
2. VV validates that all compulsory fields are present.
3. VV validates the format of the provided data.
4. VV checks that the product does not already exist.
5. VV creates the product.
6. VV adds the product to the inventory.
7. VV saves the updated inventory to storage.
8. VV displays a success message.

Use case ends.

**Extensions**

* 3a. VV detects error in provided data (e.g. missing compulsory fields, invalid data format).
  * 3a1. VV displays an appropriate error message indicating the invalid or missing field.
  * 3a2. User re-provides the corrected data.
  
  Steps 3a1–3a2 are repeated until all fields are valid.
  
  Use case resumes from step 4.

* 4a. VV detects duplicate product.
  * 4a1. VV displays an error.
  * 4a2. User re-provides the corrected data.
  
  Steps 4a1–4a2 are repeated until a unique ID is provided.
  
  Use case resumes from step 5.

* 7a. Storage file cannot be written or accessed.
  * 7a1. VV displays a failure message indicating inventory could not be saved.
  
  Use case ends.

**Use Case: UC 5 - View Products**

**Preconditions: Application is running, user is on the main screen.**

**MSS**
1. User chooses to view products and their quantity.
2. VV shows the quantity for all products.

Use case ends.

**Extensions**

* 2a. VV detects there are no products found.
  * 2a1. VV will create a new file with preloaded product information.

  Use case resumes from step 2.

**Use case: UC6 - Delete Product**

**Preconditions: Application is running, user is on the main screen and has added a product.**

**MSS**

1. User chooses to delete a product.
2. VV requests for confirmation for deleting the product.
3. User confirms deletion.
4. VV deletes product and displays list of current product.

Use case ends.

**Extensions**

* 2a. User decides not to delete the product, rejecting deletion.
  * 2a1. VV displays a list of current product.
  
  Use case ends.


### Non-Functional Requirements

Usability:
1. The system shall use consistent command formats and prefixes across all features.

Reliability:
1. The system shall not lose existing data when an invalid command is entered.
2. The system should respond to invalid commands and inputs with clear errors and/or appropriate warnings.

Portability:
1. The system shall run on Windows, Mac and Linux as long as it has Java `17` or above installed.

Performance:
1. The system shall respond to any valid command within 2 seconds when the total number of entries does not exceed expected usage limits (1,000 vendors contacts and 5,000 products).

Persistence:
1. The system must save app data locally as JSON and load it when launched.

Documentation:
1. The system should provide a developer guide for future contributors, code accompanied by Javadoc comments and a user guide for users.

Security:
1. The system should store data locally on the user’s device and should not transmit data over any network.

Scalability and Capacity:
1. The system should handle at least 1,000 vendor contacts and 5,000 products (performance beyond these limits is not guaranteed).

Maintainability:
1. The system shall separate Logic, Model, Storage and UI components to support future expansion.
2. The contact management and inventory management components should be separated such that changes in one do not require changes in the other, except through well-defined interfaces.

Testability:
1. At least 75% of the source code should be covered by tests.

Accessibility:
1. The system should allow users to complete all core tasks using commands without requiring mouse interaction.
2. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Contact/Vendor Contact**: A stored record containing a vendor’s name, phone number, email, address, and optional tags.
* **Inventory Item/Product**: A product entry tracked by the system, identified by a unique product ID, name, and quantity.
* **Product ID**: A non-negative integer that uniquely identifies a product in the inventory.
* **Command**: A user input instruction entered into the CLI to perform an action (e.g. add, delete, list).
* **Prefix**: A keyword used to identify parameters in a command (e.g. n/, p/, e/, q/).

<div style="height: 10px;"></div>

---

<div style="height: 10px;"></div>

## **Appendix: Instructions for manual testing**

### Launch and shutdown

1. Initial launch as per [Quick Start](./UserGuide.md#quick-start)

   - Expected: Full-screen GUI with sample contacts and products. 

2. Saving window preferences

   - Resize the window as desired. Move the window to a different location. Close the window.

   - Re-launch app.<br> Expected: The most recent window size and location is retained.

3. _{ more test cases …​ }_

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   - Prerequisites: List all contacts using the `list` command. Multiple contacts in the list.

2. Test case: `delete support@adafruit.com`

   - Expected: Matching contact is deleted from the list. Details of the deleted contact shown in the status message.

3. Test case: `delete notfound@example.com`

   - Expected: No contact is deleted. Error shown in the status message.

4. Other incorrect delete commands to try: `delete`, `delete invalid-email`

   - Expected: Similar to previous.

5. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   - _{explain how to simulate a missing/corrupted file, and the expected behavior}_

2. _{ more test cases …​ }_
