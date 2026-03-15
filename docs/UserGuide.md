---
  layout: default.md
  title: "User Guide"
  pageNav: 5
---

# VendorVault User Guide

VendorVault is a **desktop app for managing your vendors and inventory all in one place**. It combines the speed of typing commands with the simplicity of a visual interface, allowing you to update products, their quantities, track vendors, and organise their contacts quickly and efficiently, all optimised for use via a Command Line Interface (CLI).

Spend less time searching through spreadsheets and switching between apps. VendorVault keeps your business information organised so you can focus on what matters most: growing your business.

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
   
     **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).
     
   </box>

2. Download the latest version of VendorVault [here](https://github.com/AY2526S2-CS2103T-W08-2/tp).
    * Specifically, choose to download the `.jar` file.
    * If necessary, move the file to a folder you want to use as the _home folder_ for VendorVault.
<br><br>
3. Open Command Prompt (Windows) or Terminal (Mac/Linux) and run the following commands
    ```
   cd PATH_TO_FOLDER_CONTAINING_JAR_FILE
   java -jar vendorvault.jar
    ```
   Replace `PATH_TO_FOLDER_CONTAINING_JAR_FILE` with the actual path to the folder you put the jar file in. For example, if you put the jar file in `C:\Users\John\Downloads`, you would run:
   ```
   cd C:\Users\John\Downloads
   java -jar vendorvault.jar
   ```
   If you are using a Mac and have the jar file in your Downloads folder, you can run:
   ```
   cd ~/Downloads
   java -jar vendorvault.jar
   ```
   VendorVault should start up and you should see a GUI similar to the below in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)
<br><br>
4. Now, we're ready to use the app! At the top left of the app, you should see a command box with the text `Type a 
command here...`. This is where you can type in commands to interact with the app. You can also access the list of available commands by clicking on the `Help` menu at the top of the app or by pressing `F1` on your keyboard.
<br><br>
Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore` : Adds a vendor contact named `TechSource Electronics` to VendorVault.

   * `delete sales@techsource.com` : Deletes `TechSource Electronics`.

   * `clear` : Deletes all contacts.

   * `exit` : Exits VendorVault.

5. Refer to the [Features](#features) below for details of each command. Or [Command Summary](#command-summary) for a quick summary of all commands.

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g. `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

<div style="height: 20px;"></div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`

<div style="height: 30px;"></div>

### Managing Vendor Contacts

<div style="height: 10px;"></div>

#### Adding a contact: `add`

Adds a contact to VendorVault.

Format:
```
add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​
```

Examples:
* `add n/Adafruit Industries p/64601234 e/support@adafruit.com a/151 Varick St, New York, NY 10013, USA`
* `add n/Cytron Technologies Pte. Ltd. p/65480668 (Office), 91234567 (Sales) e/sg.sales@cytron.io a/09 Collyer Quay t/electronics` 

<box type="tip" seamless>

**Tip:** A contact can have any number of tags or none at all.

</box>

<panel header="How can I include multiple phone numbers?" type="seamless">

To include multiple phone numbers for a contact, you can **separate them with commas** in the `p/` parameter.

For example, the following command adds a contact with two phone numbers: `61234567` and `87654321`:

```
add n/DigiKey Singapore p/61234567, 87654321 e/sg.sales@digikey.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951
```

</panel>

<panel header="What contacts are considered duplicates?" type="seamless" id="faq-duplicate-contacts">

A contact is considered a duplicate if:
* It has the **same email and phone number as an existing contact** in VendorVault.
* Phone numbers are compared while ignoring labels (such as “(Office)” or “(HP)”). Multiple phone numbers should be separated by commas.

For example, these contacts are considered duplicates because they share the same phone number `61234567` and email `contact@company.com`:<br>
```
add n/DigiKey Singapore p/61234567, 98765432 e/contact@company.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951
add n/DigiKey Singapore p/61234567, 12345678 e/contact@company.com a/71 Ayer Rajah Crescent, #05-18, Singapore 139951
```

</panel>

<br>

For more details on possible warnings and errors when adding a contact, refer to the [troubleshooting guide for add contact](#troubleshooting-add-contact) below.

<div style="height: 30px;"></div>

#### Listing all contacts : `list`

Shows a list of all contacts in the VendorVault.

Format:
```
list
```

<box type="tip" seamless>

**Tip:** Want to start with sample data? `list` will insert sample data if there are no contacts or archived contacts. 
</box>

<div style="height: 30px;"></div>

#### Editing a contact : `edit`

Edits a contact using the given email. Only the fields you specify will be updated, all others stay the same.

Format:
```
edit EMAIL [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​
```

Examples:
*  `edit support@adafruit.com p/98196742 a/New York, USA` Updates the phone number and address for `support@adafruit.com`. The name, email, and tags remain unchanged.  
*  `edit sg.sales@cytron.io n/Cytron t/` Updates the name to Cytron for `sg.sales@cytron.io` and clears all existing tags.

<panel header="What happens to a contact's existing tags when I edit them?" type="seamless">

The existing tags are **replaced with the new tags you specified**, adding new tags is not cumulative. 

For example, if a contact has existing tags `t/electronics t/supplier` and you edit it with `edit EMAIL t/wholesale`, the contact's tags will be updated to only have `t/wholesale` and the previous tags will be removed.

</panel>

<panel header="How do I remove all tags from a contact?" type="seamless">

Simply type `t/` without specifying any tags.

For example, `edit EMAIL t/` will remove all tags from the contact with the specified email.

</panel>

<br>

The same rules for multiple phone numbers and duplicates that apply to `add` also apply to `edit`.
For more details on possible warnings and errors when editing a contact, refer to the [troubleshooting guide for edit contact](#troubleshooting-edit-contact) below.

<div style="height: 30px;"></div>

#### Locating contacts by name: `find`

Finds contacts whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g. `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only the name is searched.
* Only full words will be matched e.g. `Han` will not match `Hans`
* contacts matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

<div style="height: 30px;"></div>

#### Deleting a contact : `delete`

Removes a contact from the address book using their email address as the *unique identifier*
You will be prompted to confirm the deletion before any changes are made.

Format: `delete EMAIL`

Examples:
* `delete sales@digikey.com` deletes the contact associated with the email `sales@digikey.com`.

**Tip:** To skip the confimation prompt, use the `-y` flag: `delete -y EMAIL`

<panel header="Why can't I delete using other fields?" type="seamless">

Email addresses serve as the *unique identifier* for each contact, so the `delete` command only accepts email as input to ensure the correct contact is targeted.

</panel>

<br>

<div style="height: 30px;"></div>

#### Clearing all contacts: `clear`

Permanently removes all contacts from the address book.

Format: `clear`

<box type="important"> 

*This action is irreversible*. Ensure you have backed up your data before proceeding, as `clear` will delete all stored contact information.

For instructions on how to backup your data, check out [How do I backup my data](#faq-backup-data)

</box>

<div style="height: 30px;"></div>

### Managing Inventory

#### Adding a product: `addproduct`

Adds a product to the inventory.

Format: `addproduct id/IDENTIFIER n/NAME [q/QUANTITY] [th/RESTOCK_THRESHOLD]`

<box type="tip" seamless>

If quantity is not specified, it will default to 0. 
<br>
If threshold is not specified, it will default to 0.

</box>

Examples:
* `addproduct id/Pr1 n/HP LaserJet (M428fdw) q/50 th/10`
* `addproduct id/DE/5 n/PlayStation`

<panel header="What products are considered duplicates?" type="seamless" id="faq-duplicate-products">

A product is considered a duplicate if it has the **same identifier (id) as an existing product** in VendorVault. For example, these products have the same identifier `SKU-1003`:
```
addproduct id/SKU-1003 n/Arduino Uno R4 Development Board
addproduct id/SKU-1003 n/Raspberry Pi 5 (8GB RAM)
```

</panel>

<div style="height: 30px;"></div>

### Undoing the previous command : `undo`

Undoes the previous command that changed the data.

Format:
```
undo
```

<div style="height: 30px;"></div>

### Redoing the previous undone command : `redo`

Redoes the previous undone command that changed the data.

Format: 
```
redo
```

<div style="height: 30px;"></div>

### Exiting the program : `exit`

Exits the program.

Format: `exit`

<div style="height: 30px;"></div>

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

<div style="height: 30px;"></div>

### Editing the data file

AddressBook data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, AddressBook will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

<div style="height: 30px;"></div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Command Summary

| Action             | Command                                                                | Example                                                                                                    | What it does                             |
|--------------------|------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|------------------------------------------|
| **Add Contact**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​ `               | `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore t/electronics` | Adds vendor contact                      |
| **Edit Contact**   | `edit EMAIL [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​` | `edit sales@techsource.com n/TechSource p/61234568`                                                        | Edits specified fields of vendor contact |
| **Delete Contact** | `delete EMAIL`                                                         | `delete sales@techsource.com`                                                                              | Deletes contact by email                 |
| **List**           | `list`                                                                 |                                                                                                            | Lists all contacts                       |
| **Find Contact**   | `find KEYWORD [MORE_KEYWORDS]`                                         | `find TechSource`                                                                                          | Lists all contacts matching `KEYWORD`    |
| **Clear Contacts** | `clear`                                                                |                                                                                                            | Clears all contacts                      |
| **Add product**    | `addproduct id/IDENTIFIER n/NAME [q/QUANTITY] [th/RESTOCK_THRESHOLD]`  | `addproduct id/Pr1 n/HP LaserJet (M428fdw) q/50 th/10 `                                                    | Adds product                             |
| **Undo**           | `undo`                                                                 |                                                                                                            | Undoes previous command                  |
| **Help**           | `help`                                                                 |                                                                                                            |                                          |

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## FAQ

<panel header="I accidentally entered a command that changed the data. Can I undo that?" type="seamless">

Yes, you can undo the previous command that changed the data by using the `undo` command. For example, if you accidentally deleted a contact, simply enter `undo` and the contact will be restored.

</panel>

<panel header="I edited the data file directly and now VendorVault is not working. What should I do?" type="seamless">

If you edited the data file and it caused VendorVault to behave unexpectedly, you can try the following steps:
1. Restore from backup: If you made a backup of the data file before editing, you can restore the original data file by replacing the edited data files in the data folder with the backup.
2. Start with a new data file: If you do not have a backup, you can delete the existing data file (or move it to a different location for safekeeping) and start VendorVault again. This will create a new, empty data file.

</panel>

<panel header="How do I transfer my data to another computer?" type="seamless">

Follow these steps:
* Install VendorVault on the new computer (see [Quick Start](#quick-start)).
* On the old computer, open the folder where VendorVault's `.jar` file is located.
* Look for the `data` folder, which contain the files `addressbook.json` and `inventory.json`.
* Copy the folder to an external or cloud storage.
* When you launch VendorVault on the new computer, a new `data` folder is created. Replace it with the old 
  computer's folder.
* Relaunch VendorVault and you should see your data appear exactly as before.

</panel>

<panel header="How do I back up my data?" id="faq-backup-data">

* Open the folder where VendorVault's `.jar` file is located.
* Inside, locate the `data` folder, which contains `.json` files.
  * `addressbook.json`: stores contact details
  * `inventory.json`: stores product details
  * `aliases.json`: stores alias details
* Copy the `data` folder to a secure location of your choice

</panel>

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Troubleshooting

<box type="important" seamless>

**Error** messages mean the command **did not succeed**.
<br>
**Warning** messages mean the command **succeeded**, but VendorVault is flagging a possible issue.

</box>

### Managing contacts

#### Troubleshooting `add` contact

Use this section when `add` fails or returns a warning.


| Scenario                                                                         | Message shown                                                             | How to fix                                                        |
|----------------------------------------------------------------------------------|---------------------------------------------------------------------------|-------------------------------------------------------------------|
| Missing one or more required prefixes (`n/`, `p/`, `e/`, `a/`)                   | `Missing required field(s): ...`                                          | Include all required prefixed fields in your command.             |
| No prefixes at all                                                               | `All required prefixes are missing, ...`                                  | Use the full prefixed format, e.g. `add n/... p/... e/... a/...`. |
| Text appears before the first prefix                                             | `No non-prefix characters before prefix(es) is allowed, ...`              | Remove any text before `n/`.                                      |
| Same single-value field repeated (e.g. two `n/` or two `e/`)                     | `Multiple values specified for the following single-valued field(s): ...` | Keep only one value for each of `n/`, `p/`, `e/`, `a/`.           |
| Name is blank                                                                    | `Name should not be blank.`                                               | Provide a non-empty name after `n/`.                              |
| Name is too long                                                                 | `Name should be at most 256 characters.`                                  | Shorten the name.                                                 |
| Phone is blank/too short                                                         | `Phone number should not be empty and must be at least 3 digits.`         | Ensure each phone entry has at least 3 digits.                    |
| Email is blank                                                                   | `Email should not be blank.`                                              | Provide a non-empty email after `e/`.                             |
| Email format is invalid                                                          | `Email should be of the format local-part@domain ...`                     | Use a valid email format (e.g. `sales@vendor.com`).               |
| Address is blank                                                                 | `Address can take any values, and it should not be blank`                 | Provide a non-empty address after `a/`.                           |
| Address is too long                                                              | `Address should be at most 500 characters.`                               | Shorten the address.                                              |
| Tag contains non-alphanumeric characters                                         | `Tag names should be alphanumeric`                                        | Use letters/numbers only for each `t/` value.                     |
| Contact duplicates an existing contact by same email or overlapping phone number | `This vendor contact already exists with the same email or phone number.` | Change the phone/email, or edit the existing contact instead.     |

Common `add` warnings:

| Warning trigger                        | Warning shown                                                                                                          | What it means                                                                                                                                                                            |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name has unusual symbols               | `⚠ Warning: Name contains unusual symbols, is this intentional?`                                                       | Name is accepted, but looks unusual. You can verify if you entered the correct name.                                                                                                     |
| Phone includes unusual symbols/format  | `⚠ Warning: Phone number contains unusual symbols, is this intentional?`                                               | Phone is accepted, but format may be unintended. You can safely ignore it if you're providing labels eg. `61234567 (Office)`                                                             |
| Email is unusually long                | `⚠ Warning: This email address is unusually long, is this intentional?`                                                | Email is accepted, but unusually long. You can verify if the email entered is correct.                                                                                                   |
| Similar name to an existing contact    | `⚠ Warning: There's a contact with a similar name (name: <similar-name>), is this intentional?`                        | Possible duplicate by similar name. You can check if the name in the warning message is the same vendor as what you were about to add.                                                   |
| Similar address to an existing contact | `⚠ Warning: There's a contact with a similar address (name: <name>, address: <similar-address>), is this intentional?` | Possible duplicate/related location by address similarity. You can check if the vendor name and address in the warning message belongs to the same vendor as what you were about to add. |

<box type="tip" seamless>

Tip: If multiple warnings apply, VendorVault shows all of them (one per line) together with the success message.

</box>

#### Troubleshooting `edit` contact

Use this section when `edit` fails or returns a warning.

| Scenario                                                              | Message shown                                    | How to fix                                                              |
|-----------------------------------------------------------------------|--------------------------------------------------|-------------------------------------------------------------------------|
| Missing/invalid target email (or extra non-prefixed text after email) | `Invalid command format! ...`                    | Follow the syntax `edit EMAIL [n/...] [p/...] [e/...] [a/...] [t/...]`. |
| No fields specified to edit                                           | `At least one field to edit must be provided.`   | Include at least one of `n/`, `p/`, `e/`, `a/`, or `t/`.                |
| Target email not found                                                | `No contact with the specified email was found.` | Check the contact exists and re-run with the correct existing email.    |

<box type="info" seamless>

Many errors that occur in `add` also apply to `edit`, specifically, all except the first three errors listed in the add contact section above also apply. Similarly, all warnings from `add` apply to `edit` as well. For these shared errors, refer to the [Troubleshooting add contact](#troubleshooting-add-contact) guide, as they behave the same way in edit contact commands.

</box>

<box type="tip" seamless>

Tip: Unlike `add`, edit command warnings only appear for **fields you are actually editing**. For example, if you edit only the phone number and there's a similar name in the database, you won't see a name warning. This prevents unnecessary alerts for unchanged fields.

</box>

#### Troubleshooting `addproduct` contact

Use this section when `addproduct` fails or returns a warning.

| Scenario                                            | Message shown                                                             | How to fix                                                    |
|-----------------------------------------------------|---------------------------------------------------------------------------|---------------------------------------------------------------|
| Missing one or more required prefixes (`id/`, `n/`) | `Missing required field(s): ...`                                          | Include all required prefixed fields in your command.         |
| No prefixes at all                                  | `All required prefixes are missing, ...`                                  | Use the full prefixed format, e.g. `addproduct id/... n/...`. |
| Text appears before the first prefix                | `No non-prefix characters before prefix(es) is allowed, ...`              | Remove any text before `id/`.                                 |
| Same single-value field repeated (e.g. two `q/`)    | `Multiple values specified for the following single-valued field(s): ...` | Keep only one value for each of `id/`, `n/`, `q/`, `th/`.     |
| Identifier is blank                                 | `Identifier should not be blank.`                                         | Provide a non-empty identifier after `id/`.                   |
| Name is too long                                    | `Name should be at most 120 characters.`                                  | Shorten the name.                                             |
| Product is a duplicate                              | `This product already exists with the same identifier.`                   | Change the identifier, or edit the existing product instead.  |

| Warning trigger                     | Warning shown                                                                                                                                   | What it means                                                                                                                   |
|-------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| Identifier/Name has unusual symbols | `⚠ Warning: Identifier contains unusual symbols, is this intentional?`<br><br/>`⚠ Warning: Name contains unusual symbols, is this intentional?` | Identifier/Name is accepted, but looks unusual. You can verify if you entered it correctly.                                     |
| Similar name to an existing product | `⚠ Warning: There's a product with a similar name (name: <similar-name>), is this intentional?`                                                 | Possible duplicate by similar name. You can check if the name in the warning message is the same as what you were about to add. |
