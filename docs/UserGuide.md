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
3. Open a terminal for your OS and launch VendorVault:

<tabs>
  <tab header="Windows">

Open **Command Prompt** and run:

```bash
cd PATH_TO_FOLDER_CONTAINING_JAR_FILE
java -jar vendorvault.jar
```

For example, if you placed the `.jar` file in your Downloads folder:

```bash
cd C:\Users\John\Downloads
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
   ![Ui](images/Ui.png)
<br><br>
4. At the top left of the app, you should see a box where you can start typing commands. For more information, you can access the list of available commands with [`help`](#viewing-help-help).

Some example commands you can try:

* `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore` : Adds a vendor contact named `TechSource Electronics`.

* `delete sales@techsource.com` : Deletes `TechSource Electronics`.

* `addproduct id/SKU-1003 n/Arduino Uno R4 q/50 th/10 e/sales@techsource.com`: Adds a product `Arduino Uno`.

* `deleteproduct SKU-1003`: Deletes `Arduino Uno`.  

5\. Refer to the [Features](#features) below for details of each command, or [Command Summary](#command-summary) for a quick summary of all commands.

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Features

### Before you begin

<box type="definition" seamless>
<!-- <box type="important" seamless> -->


VendorVault keeps your data in one of three states:

| State        | What it means                                  | Related commands                                          |
|--------------|------------------------------------------------|-----------------------------------------------------------|
| **Active**   | Visible on the home page        | `listall`                    |
| **Archived** | Hidden but recoverable           | `archive` / `archiveproduct`                              |
| **Deleted**  | Permanently gone          | `delete` / `deleteproduct` / `clear` / `clearproduct`     |

When in doubt, **archive, don't delete.**

</box>

<box type="info" seamless>

**Note about destructive commands:**

* You can use undo to restore the data only **within the same app session**.
* If you need the contact or product again, consider using [`archive`](#archiving-a-contact-archive) / [`archiveproduct`](#archiving-a-product-archiveproduct).

</box>

<box type="info" seamless>

**Notes about the command format:**

* Words in `UPPER_CASE` are the parameters to be supplied.
* Items in square brackets such as `[t/TAG]` are optional.
* Parameters can be supplied in any order.
* To skip the confirmation prompt, use the `-y` flag: `deleteproduct -y PRODUCT_IDENTIFIER`

</box>

<div style="height: 20px;"></div>

### Managing Vendor Contacts

<div style="height: 10px;"></div>

#### Adding a contact: `add`

Adds a vendor contact.

Format:

```
add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​
```

Examples:

* `add n/Adafruit Industries p/64601234 e/support@adafruit.com a/151 Varick St, New York, NY 10013, USA`
* `add n/Cytron Technologies Pte. Ltd. p/65480668 (Office), 91234567 (Sales) e/sg.sales@cytron.io a/09 Collyer Quay t/electronics t/technology`

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

#### Locating contacts: `find` (coming soon)

**This feature is currently in progress** and will be available in a future release. `find` will allow you to view a contact details in **full**.

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
restore EMAIL
```

Examples:

* `restore`: shows all archived contacts.
* `restore sg.sales@cytron.io`

<box type="tip" seamless>

If `EMAIL` is omitted or invalid, all archived contacts will be displayed, so you can find what you want to restore.

</box>

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

<div style="height: 30px;"></div>

#### Clearing all contacts: `clear`

Removes all contacts permanently.
You will be prompted to confirm the deletion.

Format: 
```
clear
```

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

If quantity and/or threshold is omitted, it will default to 0. 
If vendor email is omitted, product will not be associated with a vendor.

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

<div style="height: 30px;"></div>

#### Editing a product : `editproduct`

Edits a product with the given identifier. Only the fields you specify will be updated, all other fields stay the same.

Format:
```
editproduct IDENTIFIER [id/NEW_IDENTIFIER] [n/NAME] [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]
```

Examples:

* `editproduct SKU-1003 id/SKU-1002 n/Arduino Uno R4 q/50`
* `editproduct SKU-1003 e/support@adafruit.com`

<panel header="How do I remove the vendor email from a product?" type="seamless">

Simply type `editproduct EMAIL e/` without specifying any email.

</panel><br>


The same rules for email that apply to add also apply to edit. For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-editproduct) below.

<div style="height: 30px;"></div>

#### Locating products : `findproduct` _(coming soon)_

<box type="info" seamless>

**This feature is currently in progress** and will be available in a future release. `findproduct` will allow you to view a product details in **full**.

</box>

<div style="height: 30px;"></div>

#### Archiving a product : `archiveproduct`

Hides a product from the home page. It is **not permanently deleted** and can be restored at any time.

Format:

```
archiveproduct IDENTIFIER
```

Examples:

* `archiveproduct SKU-1003`
* `archiveproduct SKU-2048`


<box type="tip" seamless>

**Tip:** Tip: Archive a product you no longer have, but may bring back in future. To permanently delete a product, use `deleteproduct`.

</box>

<panel header="How do I view or recover archived products?" type="seamless">

Use [`restoreproduct`](#restoring-an-archived-product-restoreproduct) without any parameters to view all archived products. Then, use `restore IDENTIFIER` to return the product to active state.

</panel><br>

For more details on possible warnings and errors, refer to the [troubleshooting guide](#troubleshooting-archiveproduct) below.


<div style="height: 30px;"></div>

#### Restoring an archived product : `restoreproduct`

Unhides a previously archived product.

Format:

```
restoreproduct IDENTIFIER
```

Examples:

* `restoreproduct`: shows all archived products.
* `restoreproduct SKU-1003`

<box type="tip" seamless>

If `IDENTIFIER` is omitted or invalid, all archived products will be displayed, so you can find what you want to restore.

</box>

<div style="height: 30px;"></div>

#### Deleting a product : `deleteproduct`

Removes a product.
You will be prompted to confirm the deletion.

Format:

```
deleteproduct PRODUCT_IDENTIFIER
```

Examples:

* `deleteproduct SKU-1003`

<div style="height: 30px;"></div>

#### Clearing all products : `clearproduct`

Removes all products permanently.
You will be prompted to confirm the deletion.

Format: 

```
clearproduct
```

<div style="height: 30px;"></div>

### Utility Commands

#### Viewing help : `help`

Shows a message (tbd) explaining how to access the help page.

Format:
```
help
```

<div style="height: 30px;"></div>

#### Add a command alias : `alias`

Creates a shortcut to an existing command (excluding `alias`).

Format:
```
alias ORIGINAL_COMMAND ALIAS
```

Example:
* `alias` list all current aliases.
* `alias list ls` maps `ls` as an alias for the `list` command.

<div style="height: 30px;"></div>

#### Undoing the previous change : `undo`

Undoes the last change to contacts or products. You can repeat the command to undo multiple changes in the current session.

Format:

```
undo
```

<div style="height: 30px;"></div>

#### Redoing the previous undone change : `redo`

Redoes the last change you undid on contacts or products. You can repeat the command to redo multiple changes in the current session.

Format:

```
redo
```

<div style="height: 30px;"></div>

#### Listing all contacts and products : `listall`

Shows a list of all **active** contacts and products at once. 

Format:
```
listall
```

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

| Action             | Command                                                                | Example                                                                                                    | What it does                             |
|--------------------|------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|------------------------------------------|
| **Add Contact**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`               | `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore t/electronics` | Adds a contact                      |
| **Edit Contact**   | `edit EMAIL [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​` | `edit sales@techsource.com n/TechSource p/61234568`                                                        | Edits a contact's details |
| **Delete Contact** | `delete EMAIL`                                                         | `delete sales@techsource.com`                                                                              | Deletes a contact                |
| **List Contacts**           | `list`                                                                 |                                                                                                            | Lists active contacts                       |
| **Find Contacts**   | `find KEYWORD [MORE_KEYWORDS]`                                         | `find TechSource`                                                                                          | Lists all contacts matching `KEYWORD`    |
| **Archive Contact**   | `archive EMAIL`                                         | `archive sales@techsource.com`                                                                                          | Archives a contact    |
| **Restore Contact**   | `restore [EMAIL]`                                         | `restore sales@techsource.com`                                                                                          | Restores an archived contact; lists all archived if no email given    |
| **Clear Contacts** | `clear`                                                                |                                                                                                            | Clears all contacts                      |

<div style="height: 30px;"></div>

### Product Commands

| Action              | Command                                                                                | Example                                                                     | What it does                                                         |
|---------------------|----------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|----------------------------------------------------------------------|
| **Add Product**     | `addproduct id/IDENTIFIER n/NAME [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]` | `addproduct id/SKU-1003 n/Arduino Uno R4 q/50 th/10 e/sales@techsource.com` | Adds a product                                                         |
| **Edit Product**    | `editproduct IDENTIFIER [id/NEW_IDENTIFIER] [n/NAME] [q/QUANTITY] [th/RESTOCK_THRESHOLD] [e/VENDOR_EMAIL]` | `editproduct SKU-1003 n/Arduino Uno R4 q/50` | Edits a product's details  |
| **Delete Product**  | `deleteproduct PRODUCT_IDENTIFIER`    | `deleteproduct SKU-1003`  | Deletes a product |
| **List Products**   | `listproduct`                                                                          |                                                                             | Lists active products                                            |
| **Find Products**    | `findproduct` _(coming soon)_                                                          |                                                                             |
| **Archive Product** | `archiveproduct IDENTIFIER`                                                            | `archiveproduct SKU-1003`                                                   | Archives a product                               |
| **Restore Product** | `restoreproduct [IDENTIFIER]`                                                          | `restoreproduct SKU-1003`                                                   | Restores an archived product; lists all archived if no identifier given |
| **Clear Products**  | `clearproduct`                                                                         |                                                                             | Clears all products                                       |

<div style="height: 30px;"></div>

### General Commands
| Action    | Command | What it does               |
|-----------|---------|----------------------------|
| **Alias** | `alias [ORIGINAL_COMMAND] [ALIAS] ` | Add a new alias; List all aliases if original command and alias are not given            |
| **Undo**  | `undo`  | Undoes previous changes    |
| **Redo**  | `redo`  | Redoes last undone changes |
| **List All** | `listall` | Lists all active contacts and products |
| **Help**  | `help`  | Shows help message         |
| **Exit**  | `exit`  | Exits VendorVault          |

--------------------------------------------------------------------------------------------------------------------

<br>

## FAQ

<panel header="I edited the data file directly and now VendorVault is not working. What should I do?" type="seamless">

If you edited the data file and it caused VendorVault to behave unexpectedly, you can try the following steps:

1. Restore from backup: If you made a backup of the data file before editing, you can restore the original data file by replacing the edited data files in the data folder with the backup.
2. Start with a new data file: If you do not have a backup, you can delete the existing data file (or move it to a different location for safekeeping) and start VendorVault again. This will create a new, empty data file.

</panel>

<panel header="How do I transfer my data to another computer?" type="seamless">

Follow these steps:

* Install VendorVault on the new computer (see [Quick Start](#quick-start)).
* On the old computer, open the folder where VendorVault's `.jar` file is located.
* Look for the `data` folder and copy it to an external/cloud storage.
* When you launch VendorVault on the new computer, a new `data` folder is created. Replace it with the old
  computer's folder.
* Relaunch VendorVault and you should see your data appear exactly as before.

</panel>

<panel header="How do I back up my data?" id="faq-backup-data" type="seamless">

* Open the folder where VendorVault's `.jar` file is located.
* Inside, locate the `data` folder, which contains `.json` files.
  * `addressbook.json`: stores contact details
  * `inventory.json`: stores product details
  * `aliases.json`: stores alias details
* Copy the `data` folder to a secure location of your choice

</panel>

<panel header="How do I edit my data directly?" type="seamless">

* Open the folder where VendorVault's `.jar` file is located.
* Inside, locate the `data` folder, which contains `.json` files.

<box type="warning" seamless>

Please follow this format carefully. Files that do not adhere to the required format will be considered invalid.

</box>

<panel header="`addressbook.json`: stores contact details" type="seamless">

This is the json for address book:

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

This is the json for inventory:

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

This is the json for aliases:

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

| Scenario                                                       | Message shown                                                             | How to fix                                                                   |
|----------------------------------------------------------------|---------------------------------------------------------------------------|------------------------------------------------------------------------------|
| Missing one or more required prefixes (`n/`, `p/`, `e/`, `a/`) | `Missing required field(s): ...`                                          | Include all required prefixed fields in your command.                        |
| No prefixes at all                                             | `All required prefixes are missing, ...`                                  | Use the full prefixed format, e.g. `add n/... p/... e/... a/...`.            |
| Text appears before the first prefix                           | `No non-prefix characters before prefix(es) is allowed, ...`              | Remove any text before `n/`.                                                 |
| Same single-value field repeated (e.g. two `n/` or two `e/`)   | `Multiple values specified for the following single-valued field(s): ...` | Keep only one value for each of `n/`, `p/`, `e/`, `a/`.                      |
| Name is blank                                                  | `Name should not be blank.`                                               | Provide a non-empty name after `n/`.                                         |
| Name is too long                                               | `Name should be at most 256 characters.`                                  | Shorten the name.                                                            |
| Phone is blank/too short                                       | `Phone number should not be empty and must be at least 3 digits.`         | Ensure each phone entry has at least 3 digits.                               |
| Email is blank                                                 | `Email should not be blank.`                                              | Provide a non-empty email after `e/`.                                        |
| Email format is invalid                                        | `Email should be of the format local-part@domain ...`                     | Use a valid email format (e.g. `sales@vendor.com`) less than 320 characters. |
| Address is blank                                               | `Address can take any values, and it should not be blank`                 | Provide a non-empty address after `a/`.                                      |
| Address is too long                                            | `Address should be at most 500 characters.`                               | Shorten the address.                                                         |
| Tag contains non-alphanumeric characters                       | `Tag names should be alphanumeric`                                        | Use letters/numbers only for each `t/` value.                                |
| Contact duplicates an existing contact by same email.          | `This vendor contact already exists with the same email.`                 | Change the email address, or edit the existing contact instead.              |

Common `add` warnings:

| Warning trigger                             | Warning shown                                                                                                                                 | What it means                                                                                                                                                                            |
|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name has unusual symbols                    | `⚠ Warning: Name contains unusual symbols, is this intentional?`                                                                              | Name is accepted, but [looks unusual](#contact-name-format). You can verify if you entered the correct name.                                                                             |
| Phone includes unusual symbols/format       | `⚠ Warning: Phone number contains unusual symbols, is this intentional?`                                                                      | Phone is accepted, but [format may be unintended](#contact-phone-format). You can safely ignore it if you're providing labels eg. `61234567 (Office)`                                    |
| Email is unusually long                     | `⚠ Warning: This email address is unusually long, is this intentional?`                                                                       | Email is accepted, but more than 256 characters. You can verify if the email entered is correct.                                                                                         |
| Similar name to an existing contact         | `⚠ Warning: There's a contact with a similar name (name: <similar-name>), is this intentional?`                                               | Possible duplicate by similar name. You can check if the name in the warning message is the same vendor as what you were about to add.                                                   |
| Similar phone number to an existing contact | `⚠ Warning: There's a contact with a similar phone number (name: <name>, phone number: <similar-phone-number>), is this intentional?`         | Possible duplicate by similar phone number. You can check if the name in the warning message is the same vendor as what you were about to add.                                           |
| Similar address to an existing contact      | `⚠ Warning: There's a contact with a similar address (name: <name>, address: <similar-address>), is this intentional?`                        | Possible duplicate/related location by address similarity. You can check if the vendor name and address in the warning message belongs to the same vendor as what you were about to add. |

<box type="tip" seamless>

Tip: If multiple warnings apply, VendorVault shows all of them (one per line) together with the success message.

</box>
<div style="height: 30px;"></div>

#### Troubleshooting `edit` contact

Use this section when `edit` fails or returns a warning.

<box type="info" seamless>

Many errors that occur in `add` also apply to `edit`, specifically, all except the first three errors listed in the add contact section above also apply. Similarly, all warnings from `add` apply to `edit` as well. For these shared errors, refer to the [Troubleshooting add contact](#troubleshooting-add-contact) guide, as they behave the same way in edit contact commands.

</box>


| Scenario                                                              | Message shown                                    | How to fix                                                              |
|-----------------------------------------------------------------------|--------------------------------------------------|-------------------------------------------------------------------------|
| Missing/invalid target email (or extra non-prefixed text after email) | `Invalid command format! ...`                    | Follow the syntax `edit EMAIL [n/...] [p/...] [e/...] [a/...] [t/...]`. |
| No fields specified to edit                                           | `At least one field to edit must be provided.`   | Include at least one of `n/`, `p/`, `e/`, `a/`, or `t/`.                |
| Target email not found                                                | `No contact with the specified email was found.` | Check the contact exists and re-run with the correct existing email.    |

<box type="tip" seamless>

Tip: Unlike `add`, edit command warnings only appear for **fields you are actually editing**. For example, if you edit only the phone number and there's a similar name in the database, you won't see a name warning. This prevents unnecessary alerts for unchanged fields.

</box>

<div style="height: 30px;"></div>

#### Troubleshooting `archive` contact

Use this section when `archive` fails.

| Scenario                           | Message shown                             | How to fix                                                                     |
|------------------------------------|-------------------------------------------|--------------------------------------------------------------------------------|
| No email provided                  | `Email must be provided.`                 | Provide the vendor's email: `archive EMAIL`.                                   |
| Email does not match any contact   | `No vendor found with email: EMAIL`       | Check the email is correct and that the contact exists in the active list.     |

<div style="height: 30px;"></div>

#### Troubleshooting `restore` contact

Use this section when `restore` fails.

| Scenario                                        | Message shown                                                         | How to fix                                                                         |
|-------------------------------------------------|-----------------------------------------------------------------------|------------------------------------------------------------------------------------|
| Email provided but no matching archived contact | `No archived vendor found with email: EMAIL` (archived list is shown) | Check the email is correct. The archived contacts panel will be shown to help you. |

<div style="height: 30px;"></div>

#### Troubleshooting `delete` contact

Use this section when `delete` fails.

| Scenario                               | Message shown                                         | How to fix                                  |
|----------------------------------------|-------------------------------------------------------|---------------------------------------------|
| No email provided                      | `Email should not be blank.`                          | Provide the vendor's email: `delete EMAIL`. |
| Email Format is invalid                | `Email should be of the format local-part@domain ...` | Check the email if its correct.             |
| Email provided but no matching contact | `No contact with the specified email was found.`      | Check the email if its correct.             |

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
| No prefixes at all                                  | `All required prefixes are missing, ...`                                  | Use the full prefixed format, e.g. `addproduct id/... n/...`.                   |
| Text appears before the first prefix                | `No non-prefix characters before prefix(es) is allowed, ...`              | Remove any text before `id/`.                                                   |
| Same single-value field repeated (e.g. two `q/`)    | `Multiple values specified for the following single-valued field(s): ...` | Keep only one value for each of `id/`, `n/`, `q/`, `th/`, `e/`.                 |
| Identifier is blank                                 | `Identifier should not be blank.`                                         | Provide a non-empty identifier after `id/`.                                     |
| Identifier is too long                              | `Identifier should be at most 120 characters.`                            | Shorten the identifier.                                                         |
| Name is blank                                       | `Name should not be blank.`                                               | Provide a non-empty name after `n/`.                                            |
| Name is too long                                    | `Name should be at most 120 characters.`                                  | Shorten the name.                                                               |
| Product is a duplicate                              | `This product already exists with the same identifier.`                   | Change the identifier, or edit the existing product instead.                    |
| Quantity is invalid                                 | `Quantity should be a non-negative valid integer.`                        | Ensure it is a whole number between 0 and 2,147,483,647.                        |
| Threshold is invalid                                | `Restock threshold should be a non-negative valid integer.`               | Ensure it is a whole number between 0 and 2,147,483,647.                        |
| Product's vendor does not exist                     | `Vendor email ... does not match any existing contact.`                   | Check that the email matches an existing contact's email, or add a new contact. |

<br>

Common `addproduct` warnings:

| Warning trigger                     | Warning shown                                                                                                                                   | What it means                                                                                                                  |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------|
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

| Warning trigger                          | Warning shown                                                                                                        | What it means                                                                                                                              |
|------------------------------------------|----------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| Edited name is similar to another product | `⚠ Warning: There's a product with a similar name (id: <id>, name: <similar-name>), is this intentional?`           | Possible duplicate by similar name. Check if the flagged product is the same as the one you are editing.                                   |
| Edited quantity is at or below threshold  | `⚠ Warning: Product stock is below threshold.`                                                                       | The product's stock has fallen to or below its restock threshold. Consider restocking.                                                     |

<box type="tip" seamless>

Tip: Warnings only appear for **fields you are actually editing**. For example, if you edit only the vendor email and the quantity is already below threshold, you will not see a stock warning. This prevents unnecessary alerts for unchanged fields.

</box>

<box type="tip" seamless>

Tip: If multiple warnings apply, VendorVault shows all of them (one per line) together with the success message.

</box>

<div style="height: 30px;"></div>

#### Troubleshooting `archiveproduct`

Use this section when `archiveproduct` fails.

| Scenario                              | Message shown                                  | How to fix                                                                      |
|---------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------|
| No identifier provided                | (usage message shown)                          | Provide the product identifier: `archiveproduct IDENTIFIER`.                    |
| Identifier does not match any product | `No product found with identifier: IDENTIFIER` | Check the identifier is correct and that the product exists in the active list. |

<div style="height: 30px;"></div>

#### Troubleshooting `restoreproduct`

Use this section when `restoreproduct` fails.

| Scenario                                             | Message shown                                                                 | How to fix                                                                                            |
|------------------------------------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| Identifier provided but no matching archived product | `No archived product found with identifier: IDENTIFIER` (archived list shown) | Check the identifier is correct. The archived products panel will be shown to help you identify the right one. |

<div style="height: 30px;"></div>

#### Troubleshooting `deleteproduct`

Use this section when `deleteproduct` fails.

| Scenario                              | Message shown                                     | How to fix                                                                |
|---------------------------------------|---------------------------------------------------|---------------------------------------------------------------------------|
| No identifier provided                | `Invalid command format! ...`                     | Provide the product identifier: `deleteproduct IDENTIFIER`.               |
| Identifier does not match any product | `No product found with the specified identifier.` | Ensure the product exists in the active list. Use `listproduct` to check. |

<div style="height: 30px;"></div>

### Why am I seeing warnings?

Warnings are shown when the command succeeds, but the provided information does not meet the recommended format. This is to help you catch possible mistakes or unintended data formats. You can choose to ignore the warning if the data is correct as intended.

<panel header="Recommended contact name format" type="seamless" id="contact-name-format">

Name is recommended to meet the following guidelines, otherwise you will see a warning:
- It can contain letters, numbers and spaces

</panel>

<panel header="Recommended contact phone number format" type="seamless" id="contact-phone-format">

Phone number(s) is recommended to meet the following guidelines, otherwise you will see a warning:
* It should contain only digits, spaces, '+' or '-' in the number part.
* Multiple phone numbers should be separated by commas.
Example: 12345678, 62345678

</panel>

<panel header="Recommended product identifier format" type="seamless" id="product-id-format">

Product identifier is recommended to meet the following guidelines, otherwise you will see a warning:
- It should contain only letters, numbers, spaces, and symbols.
- Symbols include: `/` `-`

</panel>

<panel header="Recommended product name format" type="seamless" id="product-name-format">

Product name is recommended to meet the following guidelines, otherwise you will see a warning:
- It should contain only letters, numbers, spaces, and symbols.
- Symbols include: `.` `,` `&` `+` `(` `)` `/` `\` `-` `'`

</panel>

<panel header="Why am I seeing warnings for possible duplicates?" type="seamless" id="duplicate-warnings">

**Contact and Product Names** warnings appear when a new name **shares words** with an existing one. For example, “Cytron Technologies” and “Cytron T.”.

**Contact Addresses** warnings appear when one address **fully contains** the other. For example, “123 Main Street” and “123 Main St”.

</panel>

<div style="height: 30px;"></div>
