---
  layout: no_sidebar.md
  title: "User Guide"
  pageNav: 5
---
<br>

# VendorVault User Guide

Are you a small business owner who prefers **typing commands** to get things done quickly?

VendorVault is a **desktop app for managing your vendors and inventory all in one place**. It combines the speed of typing commands with the simplicity of a visual interface, allowing you to organise your vendor contacts and track your products efficiently.

Spend less time searching through spreadsheets and switching between apps. VendorVault keeps your business information organised so you can focus on what matters most: **growing your business**.

<!-- * Table of Contents -->
<page-nav-print />

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Quick start

Follow these steps to get VendorVault up and running:

1. Ensure Java 17 or above is installed.
   * Full guide for installation [here](https://se-education.org/guides/tutorials/javaInstallation.html). If you are familiar with the process, you can download Java directly [here](https://www.oracle.com/asean/java/technologies/downloads/).<br>

   <box type="important" seamless>

     Mac users: Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

   </box>

2. Download the latest version of VendorVault [here](https://github.com/AY2526S2-CS2103T-W08-2/tp/releases/).
    * Specifically, choose to download the `.jar` file.
    * If necessary, move the file to a folder you want to use as the _home folder_ for VendorVault.
<br><br>
3. Open a terminal for your Operating System (OS) and launch VendorVault:

<tabs>
  <tab header="Windows">

Open **Command Prompt** and run:

```bash
cd PATH_TO_FOLDER_CONTAINING_JAR_FILE
java -jar vendorvault.jar
```

For example, if you placed the `.jar` file in your Downloads folder:

```bash
cd C:\Users\YOUR_USERNAME\Downloads
java -jar vendorvault.jar
```

  </tab>
  <tab header="Mac">

Open **Terminal** and run:

```bash
cd PATH_TO_FOLDER_CONTAINING_JAR_FILE
java -jar vendorvault.jar
```

For example, if you placed the `.jar` file in your Downloads folder:

```bash
cd ~/Downloads
java -jar vendorvault.jar
```

  </tab>
  <tab header="Linux">

Open **Terminal** and run:

```bash
cd PATH_TO_FOLDER_CONTAINING_JAR_FILE
java -jar vendorvault.jar
```

For example, if you placed the `.jar` file your Downloads folder:

```bash
cd ~/vendorvault
java -jar vendorvault.jar
```

<box type="tip" seamless>

If you get a permission error, make the file executable first: `chmod +x vendorvault.jar`

</box>

  </tab>
</tabs>

   VendorVault should start up. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png =1000x)
<br><br>
4. At the top left of the app, you should see a box where you can start typing commands. For more information, you can access the list of available commands with [`help`](#viewing-help-help).

Some example commands you can try:

* `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore` : Adds a vendor contact named `TechSource Electronics`.

* `delete sales@techsource.com` : Deletes `TechSource Electronics`.

* `addproduct id/SKU-1003 n/Arduino Uno R4 q/50 th/10 e/sales@techsource.com`: Adds a product `Arduino Uno R4`.

* `deleteproduct SKU-1003`: Deletes `Arduino Uno R4`.

5\. Refer to the [Features](#features) below for details of each command, or [Command Summary](#command-summary) for a quick summary of all commands.

<box type="tip" seamless>

**Tip:** Press the UP or DOWN arrow keys to navigate through your previous commands easily.

</box>

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Features

### Before you begin

<box type="definition" seamless>
<!-- <box type="important" seamless> -->


VendorVault keeps your data in one of three states:

| <div style="width:200px">State</div>| <div style="width:250px">What it means</div>| <div style="width:250px">Related commands</div>                                           |
|--------------|------------------------------------------------|-----------------------------------------------------------|
| **Active**   | Visible on the home page        | `listall`                    |
| **Archived** | Hidden but recoverable           | `archive` / `archiveproduct`                              |
| **Deleted**  | Permanently gone          | `delete` / `deleteproduct` / `clear` / `clearproduct`     |

When in doubt, **archive, don't delete.**

</box>

<box type="info" seamless>

**Notes about destructive commands:**

* You can use undo to restore the data only **within the same app session**.
* If you need the contact or product again, consider using [`archive`](#archiving-a-contact-archive) / [`archiveproduct`](#archiving-a-product-archiveproduct).

</box>

<box type="info" seamless>

**Notes about confirmation prompt:**

* To skip the confirmation prompt, include the `-y` flag after the command.

</box>

<box type="info" seamless>

**Notes about the command format:**

* Words in `UPPER_CASE` are the parameters to be supplied.
* Items in square brackets such as `[t/TAG]` are optional.
* Parameters can be supplied in any order.


</box>

<div style="height: 20px;"></div>

### Managing Vendor Contacts

<div style="height: 10px;"></div>

#### Adding a contact: `add`

Adds a vendor contact. Emails are automatically converted to lowercase to avoid duplicates.

Format:

```
add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​
```

Examples:

* `add n/Adafruit Industries p/64601234 e/support@adafruit.com a/151 Varick St, New York, NY 10013, USA`
* `add n/Cytron Technologies Pte. Ltd. p/65480668 (Office), 91234567 (Sales) e/sg.sales@cytron.io a/09 Collyer Quay
t/electronics t/wholesale`

<box type="info" seamless>

**Expected output:**<br>
![add](images/v1.5/add.png =500x)

</box>

<box type="tip" seamless>

**Tip:** A contact can have any number of tags or none at all.

</box>

<panel header="How can I include multiple phone numbers?" type="seamless">

To include multiple phone numbers for a contact, you can **separate them with commas** in the `p/` parameter.

For example, the following command adds a contact with two phone numbers: `61234567` and `87654321`:

`add n/DigiKey Singapore p/61234567, 87654321 e/sg.sales@digikey.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951`

</panel>

<panel header="What contacts are considered duplicates?" type="seamless" id="faq-duplicate-contacts">

A contact is considered a duplicate if it has the **same email as an existing contact**. For example:<br>

* `add n/DigiKey Singapore p/61234567 e/contact@company.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951`
* `add n/DigiKey Singapore p/61234567, 12345678 e/contact@company.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951`

For more information about warnings related to duplicate, refer to the [warning guide](#duplicate-warnings) below

</panel>

<br>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-add-contact) below.

<div style="height: 30px;"></div>

#### Listing all contacts : `list`

Shows a list of all **active** contacts in the VendorVault.

Format:

```
list
```

<div style="height: 30px;"></div>

#### Editing a contact : `edit`

Edits a contact with the given email. Only the fields you specify will be updated, all other fields stay the same.

Format:

```
edit EMAIL [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​
```

Examples:

* `edit support@adafruit.com p/98196742 a/New York, USA` Updates the phone number and address.
* `edit sg.sales@cytron.io n/Cytron t/` Updates the name and clears all existing tags.

<box type="info" seamless>

**Expected output:** The contact's updated fields are reflected immediately in the contact list. For example, editing `e/` will show the new email, and editing `n/` will show the updated name.

</box>

<panel header="What happens when I edit a contact's tag?" type="seamless">

The existing tags are **removed and replaced with the new tags you specified**, new tags are not cumulative.

For example, if a contact has tags `t/electronics t/supplier` and you run `edit EMAIL t/wholesale`, the existing tags will be replaced with `t/wholesale`.

</panel>

<panel header="How do I remove all tags from a contact?" type="seamless">

Simply type `edit EMAIL t/` without specifying any tags. You will be prompted to confirm the removal of all tags.

</panel>

<br>

The same rules for multiple phone numbers and duplicates that apply to `add` also apply to `edit`. For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-edit-contact) below.

<div style="height: 30px;"></div>

#### Locating contacts: `find`

Displays contacts with name and/or tag matching the given keyword(s).

Format:

```
find [n/NAME_KEYWORD]... [t/TAG_KEYWORD]...
```
(at least one prefix required)

Examples:
* `find n/TechSource t/electronics` matches name containing `TechSource` and tag `electronics`

<box type="info" seamless>

**Expected output:**<br>
![find](images/v1.5/find.png =500x)

</box>

<div style="height: 30px;"></div>

#### Archiving a contact : `archive`

Hides a contact from the home page. It is **not permanently deleted** and can be restored at any time.

Format:

```
archive EMAIL
```

Examples:

* `archive sg.sales@cytron.io`

<box type="info" seamless>

**Expected output:** The contact will no longer appear on the home page. However, it will still be available in the archived vendors page.

</box>

<box type="tip" seamless>

**Tip:** Archive a vendor you no longer work with, but may need to reference in future. To permanently delete a contact, use [`delete`](#deleting-a-contact-delete).

</box>

<panel header="How do I view or recover archived contacts?" type="seamless">

Use [`restore`](#restoring-an-archived-contact-restore) without any parameters to view all archived contacts. Then, use `restore EMAIL` to return the contact to active state.

</panel>
<br>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-archive-contact) below.


<div style="height: 30px;"></div>

#### Restoring an archived contact : `restore`

Unhides a previously archived contact.

Format:

```
restore [EMAIL]
```

Examples:

* `restore`: shows all archived contacts.
* `restore sg.sales@cytron.io`

<box type="info" seamless>

**Expected output (`restore`):**<br>
![restore](images/v1.5/restore.png =500x)

</box>

<box type="tip" seamless>

If `EMAIL` is omitted, all archived contacts will be displayed so you can find what you want to restore.

</box>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-restore-contact) below.

<div style="height: 30px;"></div>

#### Deleting a contact : `delete`

Removes a vendor contact.
You will be prompted to confirm the deletion.

Format:

```
delete EMAIL
```

Examples:

* `delete support@adafruit.com`

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-delete-contact)
below.

<div style="height: 30px;"></div>

#### Clearing all contacts: `clear`

Removes all contacts permanently.
You will be prompted to confirm the deletion.

Format:
```
clear
```

<box type="info" seamless>

**Expected output:**<br>
![clear](images/v1.5/clear.png =500x)

</box>

<div style="height: 30px;"></div>

### Managing Inventory

#### Adding a product: `addproduct`

Adds a product to the inventory.

Format:
```
addproduct id/IDENTIFIER n/NAME [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]
```

Examples:

* `addproduct id/SKU-288 n/HP LaserJet (M428fdw) q/17 th/15`
* `addproduct id/DE/5 n/PlayStation e/sg.sales@cytron.io`

<box type="info" seamless>

**Expected output:**<br>
![addprod](images/v1.5/addprod.png =1000x)

</box>

<box type="info" seamless>

If quantity is omitted, it will default to 0.
If restock threshold is omitted, it will default to your [configured default threshold](#changing-default-threshold-threshold).
If vendor email is omitted, product will not be associated with a vendor.

</box>

<box type="tip" seamless>

**Tip:** You can set your own [default threshold](#changing-default-threshold-threshold).

</box>

<panel header="What products are considered duplicates?" type="seamless" id="faq-duplicate-products">

A product is considered a duplicate if it has the **same identifier (id) as an existing product**. For example:

* `addproduct id/SKU-1003 n/Arduino Uno R4`
* `addproduct id/SKU-1003 n/Raspberry Pi 5`

</panel>

<br>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-addproduct) below.

<div style="height: 30px;"></div>

#### Listing all products : `listproduct`

Shows a list of all **active** products in the inventory.

Format:

```
listproduct
```

<box type="tip" seamless>

Long names may be truncated in the list view. Use [`findproduct`](#locating-products-findproduct-coming-soon) to view a product's full details.

</box>

<div style="height: 30px;"></div>

#### Editing a product : `editproduct`

Edits a product with the given identifier. Only the fields you specify will be updated, all other fields stay the same.

Format:
```
editproduct IDENTIFIER [id/NEW_IDENTIFIER] [n/NAME] [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]
```

Examples:

* `editproduct SKU-288 id/SKU-299 n/HP LaserJet (M140w) q/35`
* `editproduct DE/5 e/hello@synapse.sg`

<box type="info" seamless>

**Expected output:** The product's updated fields are reflected immediately in the product list. For example, editing `q/` will show the updated quantity, and editing `e/` will show the new associated vendor email.

</box>

<panel header="How do I remove the vendor email from a product?" type="seamless">

Simply type `editproduct EMAIL e/` without specifying any email.

</panel><br>


The same rules for email that apply to add also apply to edit. For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-editproduct) below.

<div style="height: 30px;"></div>

#### Locating products : `findproduct`

Displays products with name matching any of the given keyword(s).

Format:

```
findproduct KEYWORD [MORE_KEYWORDS]
```

Examples:
* `findproduct camera`

<box type="info" seamless>

**Expected output:**<br>
![findprod](images/v1.5/findprod.png =500x)

</box>

<div style="height: 30px;"></div>

#### Archiving a product : `archiveproduct`

Hides a product from the home page. It is **not permanently deleted** and can be restored at any time.

Format:

```
archiveproduct IDENTIFIER
```

Examples:

* `archiveproduct DE/5`

<box type="info" seamless>

**Expected output:** The product will no longer appear on the home page. However, it will still be available in the archived products page.

</box>

<box type="tip" seamless>

**Tip:** Archive a product you no longer have, but may bring back in future. To permanently delete a product, use `deleteproduct`.

</box>

<panel header="How do I view or recover archived products?" type="seamless">

Use [`restoreproduct`](#restoring-an-archived-product-restoreproduct) without any parameters to view all archived products. Then, use `restoreproduct IDENTIFIER` to return the product to active state.

</panel><br>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-archiveproduct) below.


<div style="height: 30px;"></div>

#### Restoring an archived product : `restoreproduct`

Unhides a previously archived product.

Format:

```
restoreproduct [IDENTIFIER]
```

Examples:

* `restoreproduct`: shows all archived products.
* `restoreproduct DE/5`

<box type="info" seamless>

**Expected output:** The product reappears in the active product list. If no identifier is given, all archived products are displayed so you can pick what to restore.

</box>

<box type="tip" seamless>

If `IDENTIFIER` is omitted or invalid, all archived products will be displayed, so you can find what you want to restore.

</box>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-restoreproduct) below.

<div style="height: 30px;"></div>

#### Deleting a product : `deleteproduct`

Removes a product.
You will be prompted to confirm the deletion.

Format:

```
deleteproduct PRODUCT_IDENTIFIER
```

Examples:

* `deleteproduct DE/5`

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-deleteproduct)
below.

<div style="height: 30px;"></div>

#### Clearing all products : `clearproduct`

Removes all products permanently.
You will be prompted to confirm the deletion.

Format:

```
clearproduct
```

<box type="info" seamless>

**Expected output:** Once confirmed, all products are permanently removed. The product list on the home page will now be empty.

</box>

<div style="height: 30px;"></div>

### Utility Commands

#### Viewing help : `help`

Opens a window with summary of commands.

Format:
```
help
```

<div style="height: 30px;"></div>

#### Adding a command shortcut : `alias`

Creates a shortcut to an existing command (excluding `alias`).

Format:
```
alias [ORIGINAL_COMMAND] [ALIAS]
```

Example:
* `alias` list all current aliases.
* `alias list ls` maps `ls` as an alias for the `list` command.

<box type="info" seamless>

**Expected output (`alias`):**<br>
![alias](images/v1.5/alias.png =500x)

</box>

<div style="height: 30px;"></div>

#### Deleting a command shortcut : `deletealias`

Removes an existing shortcut.

Format:
```
deletealias ALIAS
```

Example:
* `deletealias ls` removes `ls` as an alias

<div style="height: 30px;"></div>

#### Changing default threshold : `threshold`

Changes the default restock threshold used when adding products.

Format:
```
threshold DEFAULT_THRESHOLD
```

Examples:

* `threshold 5`

<box type="info" seamless>

This change is saved across app sessions.

</box>

<div style="height: 30px;"></div>

#### Undoing the previous change : `undo`

Undoes the last change to contacts or products. You can repeat the command to undo multiple changes in the current session.

Format:

```
undo
```

<box type="info" seamless>

You can undo or redo actions like `add`, `edit`, `delete`, `clear`, `archive`, `restore` for vendors and products. Settings changes such as `alias` or `threshold` cannot be undone or redone.

</box>

<div style="height: 30px;"></div>

#### Redoing the previous undone change : `redo`

Redoes the last change you undid on contacts or products. You can repeat the command to redo multiple changes in the current session.

Format:

```
redo
```

<box type="info" seamless>

**Expected output:**<br>
![undo](images/v1.5/undo.png =700x)

<br>

![redo](images/v1.5/redo.png =700x)

</box>

<div style="height: 30px;"></div>

#### Listing all contacts and products : `listall`

Shows a list of all **active** contacts and products at once.

Format:
```
listall
```

<box type="info" seamless>

**Expected output:**<br>
![listall](images/v1.5/listall.png =1000x)

</box>

<div style="height: 30px;"></div>

#### Exiting the app : `exit`

Exits VendorVault.

Format:
```
exit
```

<div style="height: 30px;"></div>

--------------------------------------------------------------------------------------------------------------------

<br>

## Command Summary

### Contact Commands

| Action              | Command                                                                | Example                                                                                                    | What it does                                                       |
|---------------------|------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| **Add Contact**     | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`                | `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore t/electronics` | Adds a contact                                                     |
| **Edit Contact**    | `edit EMAIL [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​` | `edit sales@techsource.com n/TechSource p/61234568`                                                        | Edits a contact's details                                          |
| **Delete Contact**  | `delete EMAIL`                                                         | `delete sales@techsource.com`                                                                              | Deletes a contact                                                  |
| **List Contacts**   | `list`                                                                 | &nbsp;                                                                                                     | Lists active contacts                                              |
| **Find Contacts**   | `find [n/NAME]... [t/TAG]...`                                          | `find n/TechSource <br/>t/electronics`                                                                     | Displays contacts with matching name and/or tag                    |
| **Archive Contact** | `archive EMAIL`                                                        | `archive sales@techsource.com`                                                                             | Archives a contact                                                 |
| **Restore Contact** | `restore [EMAIL]`                                                      | `restore sales@techsource.com`                                                                             | Restores an archived contact; lists all archived if no email given |
| **Clear Contacts**  | `clear`                                                                | &nbsp;                                                                                                     | Clears all contacts                                                |

<div style="height: 30px;"></div>

### Product Commands

| Action              | Command                                                                                                    | Example                                                                     | What it does                                                            |
|---------------------|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|-------------------------------------------------------------------------|
| **Add Product**     | `addproduct id/IDENTIFIER n/NAME [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]`                     | `addproduct id/SKU-1003 n/Arduino Uno R4 q/50 th/10 e/sales@techsource.com` | Adds a product                                                          |
| **Edit Product**    | `editproduct IDENTIFIER [id/NEW_IDENTIFIER] [n/NAME] [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]` | `editproduct SKU-1003 n/Arduino Mega q/35`                                  | Edits a product's details                                               |
| **Delete Product**  | `deleteproduct PRODUCT_IDENTIFIER`                                                                         | `deleteproduct SKU-1003`                                                    | Deletes a product                                                       |
| **List Products**   | `listproduct`                                                                                              | &nbsp;                                                                      | Lists active products                                                   |
| **Find Products**   | `findproduct KEYWORD [MORE_KEYWORDS]`                                                                      | `findproduct uno`                                                           | Displays products with matching name.                                   |
| **Archive Product** | `archiveproduct IDENTIFIER`                                                                                | `archiveproduct SKU-1003`                                                   | Archives a product                                                      |
| **Restore Product** | `restoreproduct [IDENTIFIER]`                                                                              | `restoreproduct SKU-1003`                                                   | Restores an archived product; lists all archived if no identifier given |
| **Clear Products**  | `clearproduct`                                                                                             | &nbsp;                                                                      | Clears all products                                                     |

<div style="height: 30px;"></div>

### General Commands

| <div style="width:100px">Action</div> | <div style="width:200px">Command</div> | <div style="width:200px">Example</div> | What it does                                                    |
|---------------------------------------|----------------------------------------|----------------------------------------|-----------------------------------------------------------------|
| **Alias**                             | `alias [ORIGINAL_COMMAND] [ALIAS]`     | `alias list ls`                        | Adds a new alias; lists all aliases if no arguments given       |
| **Delete Alias**                      | `deletealias ALIAS`                    | `deletealias ls`                       | Deletes an existing alias                                       |
| **Change Default Threshold**          | `threshold DEFAULT_THRESHOLD`          | `threshold 5`                          | Changes the default restock threshold |
| **Undo**                              | `undo`                                 | &nbsp;                                 | Undoes previous change                                          |
| **Redo**                              | `redo`                                 | &nbsp;                                 | Redoes last undone change                                       |
| **List All**                          | `listall`                              | &nbsp;                                 | Lists all active contacts and products                          |
| **Help**                              | `help`                                 | &nbsp;                                 | Shows a summary of commands                                     |
| **Exit**                              | `exit`                                 | &nbsp;                                 | Exits VendorVault                                               |

--------------------------------------------------------------------------------------------------------------------

<br>

## FAQ

<panel header="How do I back up my data?" id="faq-backup-data" type="seamless">

* Open the folder where VendorVault's `.jar` file is located.
* Inside, locate the `data` folder, which contains `.json` files.
* Copy the `data` folder to a secure location of your choice

</panel>

<panel header="How do I edit my data directly?" type="seamless">

* Open the folder where VendorVault's `.jar` file is located.
* Inside, locate the `data` folder, which contains `.json` files.

<box type="warning" seamless>

Please follow this format carefully. Files that do not adhere to the required format will be considered invalid.

</box>

<panel header="`addressbook.json`: stores contact details" type="seamless">

```json
{
  "persons" : [ {
    "name" : NAME,
    "phone" : PHONE_NUMBER,
    "email" : EMAIL,
    "address" : ADDRESS,
    "tags" : [ TAGS ]
  } ]
}
```

</panel>

<panel header="`inventory.json`: stores product details" type="seamless">

```json
{
  "products" : [ {
    "identifier" : IDENTIFIER(string),
    "name" : NAME(string),
    "quantity" : QUANTITY(integer),
    "threshold" : THRESHOLD(interger),
    "vendorEmail" : VENDOR_EMAIL(email),
    "isArchived" : BOOLEAN(true/false)
  } ]
}
```

</panel>

<panel header="`aliases.json`: stores alias details" type="seamless">

```json
{
  "aliasList" : [ {
    "alias" : ALIAS,
    "originalCommand" : ORIGINAL_COMMAND
  } ]
}
```

</panel>

</panel>

<panel header="I edited the data file directly and now VendorVault is not working. What should I do?" type="seamless">

You can try the following steps:

1. Restore from backup: If you made a backup of the data file before editing, you can restore the original data file by replacing the edited data files in the data folder with the backup.
2. Start with a new data file: If you do not have a backup, you can delete the existing data file (or move it to a different location for safekeeping) and start VendorVault again. After entering a valid command, this will create a new, sample data file.

</panel>

<panel header="How do I transfer my data to another computer?" type="seamless">

1. [Install VendorVault](#quick-start) on the new computer.
2. On the old computer, open the folder where VendorVault's `.jar` file is located.
3. Look for the `data` folder and copy it to an external/cloud storage.
4. When you launch VendorVault on the new computer, a new `data` folder is created. Replace it with the old computer's version.
5. Relaunch VendorVault and you should see your data appear exactly as before.

</panel>

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Troubleshooting

<box type="important" seamless>

**Error** messages mean the command **did not succeed**.
<br>
**Warning** messages mean the command **succeeded**, but VendorVault is flagging a **possible issue**.

</box>

### Managing contacts
<div style="height: 30px;"></div>

#### Troubleshooting `add` contact

Use this section when `add` fails or returns a warning.

| Scenario                                                       | Message shown                                                                        | How to fix                                                                   |
|----------------------------------------------------------------|--------------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| Missing one or more required prefixes (`n/`, `p/`, `e/`, `a/`) | `Missing required field(s): ...`                                                     | Include all required prefixed fields in your command.                        |
| No prefixes at all                                             | `Invalid Command format! ...`                                                        | Use the full prefixed format, e.g. `add n/... p/... e/... a/...`.            |
| Text appears before the first prefix                           | `No non-prefix characters before prefix(es) is allowed, ...`                         | Remove any text before `n/`.                                                 |
| Same single-value field repeated (e.g. two `n/` or two `e/`)   | `Multiple values specified for the following single-valued field(s): ...`            | Keep only one value for each of `n/`, `p/`, `e/`, `a/`.                      |
| Name is blank                                                  | `Name should not be blank.`                                                          | Provide a non-empty name after `n/`.                                         |
| Name is too long                                               | `Name should be at most 256 characters.`                                             | Shorten the name.                                                            |
| Phone is blank                                                 | `Phone number should not be blank.`                                                  | Ensure each phone entry is not blank.                                        |
| Phone is too short                                             | `Phone number(s) must be at least 3 digits ...`                                      | Ensure each phone entry has at least 3 digits.                               |
| Email is blank                                                 | `Email should not be blank.`                                                         | Provide a non-empty email after `e/`.                                        |
| Email format is invalid                                        | `Email should be a valid format ...`                                                 | Use a [valid email format](#contact-email-format) (e.g. `sales@vendor.com`). |
| Email is too long                                              | `Email should be at most 320 characters.`                                            | Shorten the email.                                                           |
| Address is blank                                               | `Address can take any values, and it should not be blank`                            | Provide a non-empty address after `a/`.                                      |
| Address is too long                                            | `Address should be at most 500 characters.`                                          | Shorten the address.                                                         |
| Tag is blank                                                   | `Tag names should not be blank`                                                      | Provide a non-empty tag name after each`t/`.                                  |
| Tag is too long                                                | `Tag names should be at most 50 characters`                                          | Shorten the tags that are too long.                                          |
| Contact duplicates an existing contact by same email           | `This vendor contact already exists with the same email (name: NAME, email: EMAIL).` | Change the email address, or edit the existing contact instead.              |

<panel header="What's considered a valid Contact Email?" type="seamless" id="contact-email-format">

Email should be of the format local-part@domain and adhere to the following constraints:
* The local-part should only contain alphanumeric characters and these special characters, excluding the parentheses, (`+_.-`). The local-part may not start or end with any special characters.
* This is followed by a `@` and then a domain name. The domain name is made up of domain labels separated by periods.
  The domain name must:
    - End with a domain label at least 2 characters long
    - Have each domain label start and end with alphanumeric characters
    - Have each domain label consist of alphanumeric characters, separated only by hyphens, if any.

</panel>

<div style="height: 30px;"></div>

Common `add` warnings:

| Warning trigger                             | Warning shown                                                                                                                         | What it means                                                                                                                                                                            |
|---------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name has unusual symbols                    | `⚠ Warning: Name contains unusual symbols, is this intentional?`                                                                      | Name is accepted, but [looks unusual](#contact-name-format). You can verify if you entered the correct name.                                                                             |
| Phone includes unusual symbols/format       | `⚠ Warning: Phone number contains unusual symbols, is this intentional?`                                                              | Phone is accepted, but [format may be unintended](#contact-phone-format). You can safely ignore it if you're providing labels eg. `61234567 (Office)`                                    |
| Email is unusually long                     | `⚠ Warning: Email address is unusually long, is this intentional?`                                                                    | Email is accepted, but more than 256 characters. You can verify if the email entered is correct.                                                                                         |
| Similar name to an existing contact         | `⚠ Warning: There's a contact with a similar name (name: <similar-name>), is this intentional?`                                       | Possible duplicate by similar name. You can check if the name in the warning message is the same vendor as what you were about to add.                                                   |
| Similar phone number to an existing contact | `⚠ Warning: There's a contact with a similar phone number (name: <name>, phone number: <similar-phone-number>)`                       | Possible duplicate by similar phone number. You can check if the name in the warning message is the same vendor as what you were about to add.                                           |
| Similar address to an existing contact      | `⚠ Warning: There's a contact with a similar address (name: <name>, address: <similar-address>), is this intentional?`                | Possible duplicate/related location by address similarity. You can check if the name and address in the warning message belongs as what you were about to add.                           |

<box type="tip" seamless>

**Tip:** If multiple warnings apply, VendorVault shows each type of them (one per line) together with the success message.

</box>

<div style="height: 30px;"></div>

#### Troubleshooting `edit` contact

Use this section when `edit` fails or returns a warning.

<box type="info" seamless>

Most errors and warnings in `add` also occur in `edit`, except the first three errors and `Tag is blank` error. For these shared errors, refer to the [troubleshooting add contact](#troubleshooting-add-contact) guide.

</box>


| Scenario                                                              | Message shown                                    | How to fix                                                              |
|-----------------------------------------------------------------------|--------------------------------------------------|-------------------------------------------------------------------------|
| Missing/invalid target email (or extra non-prefixed text after email) | `Invalid command format! ...`                    | Follow the syntax `edit EMAIL [n/...] [p/...] [e/...] [a/...] [t/...]`. |
| No fields specified to edit                                           | `At least one field to edit must be provided.`   | Include at least one of `n/`, `p/`, `e/`, `a/`, or `t/`.                |
| Target email not found                                                | `No contact with the specified email was found.` | Check the contact exists and re-run with the correct existing email.    |

<box type="tip" seamless>

**Tip:** Unlike `add`, edit command warnings only appear for **fields you are actually editing**. For example, if you edit only the phone number and there's a similar name in the database, you won't see a name warning.

</box>

<div style="height: 30px;"></div>

#### Troubleshooting `find` contact

Use this section when `find` fails.

| Scenario                             | Message shown                                                | How to fix                              |
|--------------------------------------|--------------------------------------------------------------|-----------------------------------------|
| No prefixes at all                   | `Invalid Command format! ...`                                | Use the full prefixed format.           |
| Text appears before the first prefix | `No non-prefix characters before prefix(es) is allowed, ...` | Remove any text before the prefix.      |
| Name keyword is blank                | `Name keyword should not be blank.`                          | Provide a non-empty keyword after `n/`. |
| Tag keyword is blank                 | `Tag keyword should not be blank.`                           | Provide a non-empty keyword after `t/`. |

<div style="height: 30px;"></div>

#### Troubleshooting `archive` contact

Use this section when `archive` fails.

| Scenario                             | Message shown                                                  | How to fix                                                                                        |
|--------------------------------------|----------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| No email provided                    | `Invalid Command format! ...`                                  | Provide the vendor's email: `archive EMAIL`.                                                      |
| Email format is invalid              | `Email should be a valid format (e.g. user@example.com).`      | Ensure the email is correctly formatted, e.g. `archive sg.sales@cytron.io`.                       |
| Email does not match any contact     | `No vendor found with the specified email.`                    | Check the email is correct and that the contact exists in the active list. Use `list` to verify.  |
| Contact is already archived          | `This vendor is already archived. Did you want to restore it?` | Use `restore EMAIL` instead to bring the contact back to the active list.                         |

<div style="height: 30px;"></div>

#### Troubleshooting `restore` contact

Use this section when `restore` fails.

| Scenario                                        | Message shown                                                                               | How to fix                                                                                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| Invalid email format provided                   | `Email should be a valid format (e.g. user@example.com).`                                   | Ensure the email is correctly formatted, e.g. `restore sg.sales@cytron.io`.                           |
| No archived contacts exist (no email given)     | `No vendors are currently archived. Use 'archive EMAIL' to archive a vendor first.`         | Archive a contact first using `archive EMAIL` before attempting to restore.                           |
| Email provided but no matching archived contact | `No archived vendor found with the specified email.` (archived list is shown)               | Check the email is correct. The archived contacts panel will be shown to help you find the right one. |

<div style="height: 30px;"></div>

#### Troubleshooting `delete` contact

Use this section when `delete` fails.

| Scenario                               | Message shown                                            | How to fix                                                         |
|----------------------------------------|----------------------------------------------------------|--------------------------------------------------------------------|
| No email is provided                   | `Invalid Command format! ...`                            | Provide the vendor's email: `delete EMAIL`.                        |
| Email Format is invalid                | `Email should be a valid format (e.g. user@example.com).` | Provide the correct vendor's email: `delete EMAIL`.                |
| Email provided but no matching contact | `No contact with the specified email was found.`         | Ensures the vendor exists in the active list. Use `list` to check. |

<br>

Common `delete` warnings:

| Warning trigger                             | Warning shown                                            | What it means                                        |
|---------------------------------------------|----------------------------------------------------------|------------------------------------------------------|
| Contact with existing product(s) is deleted | `... product(s) became unassociated from contact (...).` | The product(s) will not have a corresponding vendor. |

<div style="height: 30px;"></div>

### Managing inventory
<div style="height: 30px;"></div>

#### Troubleshooting `addproduct`

Use this section when `addproduct` fails or returns a warning.

| Scenario                                            | Message shown                                                             | How to fix                                                                      |
|-----------------------------------------------------|---------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| Missing one or more required prefixes (`id/`, `n/`) | `Missing required field(s): ...`                                          | Include all required prefixed fields in your command.                           |
| No prefixes at all                                  | `Invalid command format! ...`                                             | Use the full prefixed format, e.g. `addproduct id/... n/...`.                   |
| Text appears before the first prefix                | `No non-prefix characters before prefix(es) is allowed, ...`              | Remove any text before `id/`.                                                   |
| Same single-value field repeated (e.g. two `q/`)    | `Multiple values specified for the following single-valued field(s): ...` | Keep only one value for each of `id/`, `n/`, `q/`, `th/`, `e/`.                 |
| Identifier is blank                                 | `Identifier should not be blank.`                                         | Provide a non-empty identifier after `id/`.                                     |
| Identifier is too long                              | `Identifier should be at most 120 characters.`                            | Shorten the identifier.                                                         |
| Name is blank                                       | `Name should not be blank.`                                               | Provide a non-empty name after `n/`.                                            |
| Name is too long                                    | `Name should be at most 120 characters.`                                  | Shorten the name.                                                               |
| Quantity is invalid                                 | `Quantity should be a non-negative valid integer.`                        | Ensure it is a whole number between 0 and 2,147,483,647.                        |
| Threshold is invalid                                | `Restock threshold should be a non-negative valid integer.`               | Ensure it is a whole number between 0 and 2,147,483,647.                        |
| Product duplicates an existing product by same identifier | `This product already exists with the same identifier (identifier: IDENTIFIER, name: NAME).` | Change the identifier, or edit the existing product instead.             |
| Product's vendor does not exist                     | `Vendor email EMAIL does not match any existing contact.`                 | Check that the email matches an existing contact's email, or add a new contact. |

<br>

Common `addproduct` warnings:

| Warning trigger                     | Warning shown                                                                                                                                   | What it means                                                                                                                  |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `q/` omitted                        | `⚠ Warning: Quantity missing, defaulted to 0.`                                                                                                  | Quantity was not provided so it defaults to 0. Edit it later with `editproduct` if needed.                                     |
| `th/` omitted                       | `⚠ Warning: Restock threshold missing, defaulted to N.`                                                                                         | Threshold was not provided so it defaults to the configured value. Edit with `editproduct` if needed.                          |
| `e/` omitted                        | `⚠ Warning: Vendor email missing, product will not be associated with a vendor.`                                                                | No vendor is linked to the product. You can assign one later using `editproduct IDENTIFIER e/EMAIL`.                           |
| Identifier/Name has unusual symbols | `⚠ Warning: Identifier contains unusual symbols, is this intentional?`<br><br/>`⚠ Warning: Name contains unusual symbols, is this intentional?` | Identifier/Name is accepted, but [looks unusual](#product-name-format). You can verify if you entered it correctly.            |
| Similar name to an existing product | `⚠ Warning: There's a product with a similar name (name: <similar-name>), is this intentional?`                                                 | Possible duplicate by similar name. You can check if the name in the warning message is the same as what you were about to add. |

<div style="height: 30px;"></div>

#### Troubleshooting `editproduct`

Use this section when `editproduct` fails or returns a warning.

<box type="info" seamless>

If you assign a vendor email to a product, the contact **must already exist** in VendorVault. If the contact is later deleted, the product will retain the email but the vendor will be unassociated. To fix this, either re-add the contact or clear the vendor email using `editproduct IDENTIFIER e/`.

</box>

| Scenario                                             | Message shown                                                             | How to fix                                                                               |
|------------------------------------------------------|---------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| No fields specified to edit                          | `At least one field to edit must be provided.`                            | Include at least one of `id/`, `n/`, `q/`, `th/`, or `e/`.                               |
| Identifier does not match any active product         | `No product found with the specified identifier.`                         | Ensure the product exists in the active list. Use `listproduct` to check.                |
| New identifier is already used by another product    | `This product already exists with the same identifier.`                   | Choose a unique identifier.                                                              |
| Vendor email does not match any existing contact     | `No contact with the specified email was found.`                          | Check that the email matches an existing contact, or add the contact first using `add`.  |

<br>

Common `editproduct` warnings:

| Warning trigger                           | Warning shown                                                                                             | What it means                                                                                            |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| Edited name is similar to another product | `⚠ Warning: There's a product with a similar name (id: <id>, name: <similar-name>), is this intentional?` | Possible duplicate by similar name. Check if the flagged product is the same as the one you are editing. |
| Edited quantity is at or below threshold  | `⚠ Warning: Product stock is below threshold.`                                                            | The product's stock has fallen to or below its restock threshold. Consider restocking.                   |

<box type="tip" seamless>

**Tip:** Warnings only appear for **fields you are actually editing**. For example, if you edit only the vendor email and the quantity is already below threshold, you will not see a stock warning. This prevents unnecessary alerts for unchanged fields.

</box>

<box type="tip" seamless>

**Tip:** If multiple warnings apply, VendorVault shows all of them (one per line) together with the success message.

</box>

<div style="height: 30px;"></div>

#### Troubleshooting `findproduct`

Use this section when `findproduct` fails.

| Scenario               | Message shown                 | How to fix          |
|------------------------|-------------------------------|---------------------|
| No keyword is provided | `Invalid command format! ...` | Provide keyword(s). |

<div style="height: 30px;"></div>

#### Troubleshooting `archiveproduct`

Use this section when `archiveproduct` fails.

| Scenario                                 | Message shown                                                   | How to fix                                                                                              |
|------------------------------------------|-----------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| No identifier provided                   | `Invalid Command format! ...`                                   | Provide the product identifier: `archiveproduct IDENTIFIER`.                                            |
| Identifier does not match any product    | `No product found with the specified identifier.`               | Check the identifier is correct and that the product exists in the active list. Use `listproduct`.      |
| Product is already archived              | `This product is already archived. Did you want to restore it?` | Use `restoreproduct IDENTIFIER` instead to bring the product back to the active list.                   |

<div style="height: 30px;"></div>

#### Troubleshooting `restoreproduct`

Use this section when `restoreproduct` fails.

| Scenario                                             | Message shown                                                                                    | How to fix                                                                                                  |
|------------------------------------------------------|--------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| No archived products exist (no identifier given)     | `No products are currently archived. Use 'archiveproduct IDENTIFIER' to archive a product first.` | Archive a product first using `archiveproduct IDENTIFIER` before attempting to restore.                    |
| Identifier provided but no matching archived product | `No archived product found with the specified identifier.` (archived list shown)                 | Check the identifier is correct. The archived products panel will be shown to help you find the right one. |

<div style="height: 30px;"></div>

#### Troubleshooting `deleteproduct`

Use this section when `deleteproduct` fails.

| Scenario                              | Message shown                                     | How to fix                                                                |
|---------------------------------------|---------------------------------------------------|---------------------------------------------------------------------------|
| No identifier provided                | `Invalid Command format! ...`                     | Provide the product identifier: `deleteproduct IDENTIFIER`.               |
| Identifier does not match any product | `No product found with the specified identifier.` | Ensure the product exists in the active list. Use `listproduct` to check. |

<div style="height: 30px;"></div>

### General commands
<div style="height: 30px;"></div>

#### Troubleshooting `alias`

Use this section when `alias` fails.

| Scenario                         | Message shown                                                  | How to fix                                                             |
|----------------------------------|----------------------------------------------------------------|------------------------------------------------------------------------|
| No new alias provided            | `Invalid Command format! ...`                                  | Provide the new alias: `alias ORIGINAL_COMMAND NEW_ALIAS`              |
| Original Command does not exists | `The original command (ORIGINAL COMMAND) does not exists. ...` | Ensure the original command is in the `help` page.                     |
| Alias has been used              | `This alias already exists. ...`                               | Provide a new alias, or use `deletealias` to delete the current alias. |

<div style="height: 30px;"></div>

#### Troubleshooting `deletealias`

Use this section when `deletealias` fails.

| Scenario                     | Message shown                       | How to fix                                                        |
|------------------------------|-------------------------------------|-------------------------------------------------------------------|
| No alias provided            | `Invalid Command format! ...`       | Provide an alias to delete: `deletealias ALIAS`                   |
| No alias given in alias list | `No alias found in alias list. ...` | Find the correct alias using `alias` then use `deletealias ALIAS` |

<div style="height: 30px;"></div>

#### Troubleshooting `threshold`

Use this section when `threshold` fails.

| Scenario              | Message shown                                               | How to fix                                                      |
|-----------------------|-------------------------------------------------------------|-----------------------------------------------------------------|
| No threshold provided | `Invalid command format! ...`                               | Provide a threshold.                                            |
| Threshold is invalid  | `Restock threshold should be a non-negative valid integer.` | Ensure threshold is a whole number between 0 and 2,147,483,647. |

<div style="height: 30px;"></div>

### Why am I seeing warnings?

Warnings are shown when the command succeeds, but the provided information does not meet the recommended format. This is to help you catch possible mistakes or unintended data formats. You can choose to ignore the warning if the data is correct as intended.

<panel header="Recommended contact name format" type="seamless" id="contact-name-format">

Name is recommended to meet the following guidelines, otherwise you will see a warning:
- It can contain letters, numbers and spaces

</panel>

<panel header="Recommended contact phone number format" type="seamless" id="contact-phone-format">

Phone number(s) is recommended to meet the following guidelines, otherwise you will see a warning:
* It can contain only digits, spaces, '+' or '-' in the number part.
* Multiple phone numbers should be separated by commas.
Example: 12345678, 62345678

</panel>

<panel header="Recommended product identifier format" type="seamless" id="product-id-format">

Product identifier is recommended to meet the following guidelines, otherwise you will see a warning:
- It can contain letters, numbers, spaces, and symbols.
- Symbols include: `/` `-`

</panel>

<panel header="Recommended product name format" type="seamless" id="product-name-format">

Product name is recommended to meet the following guidelines, otherwise you will see a warning:
- It can contain letters, numbers, spaces, and symbols.
- Symbols include: `.` `,` `&` `+` `(` `)` `/` `\` `-` `'`

</panel>

<panel header="Why am I seeing warnings for possible duplicates?" type="seamless" id="duplicate-warnings">

**Contact and Product Names** warnings appear when a new name **shares words** with an existing one. For example, "Cytron Technologies" and "Cytron T.".

**Contact Phone Numbers** warnings appear when a new phone number **shares at least 3 consecutive digits** with an existing one. For example, "91245678" and "91234783".

**Contact Addresses** warnings appear when one address **fully contains** the other. For example, "123 Main Street" and "123 Main St".

</panel>

<div style="height: 30px;"></div>
