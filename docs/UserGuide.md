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

1. Install Java (one-time setup)
   * VendorVault requires Java `17` or above to run. Full guide for installation [here](https://se-education.org/guides/tutorials/javaInstallation.html)
   * If you are familiar with the installation process, you can download it directly [here](https://www.oracle.com/asean/java/technologies/downloads/).<br>
   *  **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).
<br><br>
1. Download the latest version of VendorVault [here](https://github.com/AY2526S2-CS2103T-W08-2/tp).
    * Specifically, choose to download the `.jar` file.
    * If necessary, move the file to a folder you want to use as the _home folder_ for VendorVault.
<br><br>
1. Open Command Prompt (Windows) or Terminal (Mac/Linux) and run the following commands
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
1. Now, we're ready to use the app! At the top left of the app, you should see a command box with the text `Type a command here...`. This is where you can type in commands to interact with the app. You can also access the list of available commands by clicking on the `Help` menu at the top of the app or by pressing `F1` on your keyboard.
<br><br>
Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore` : Adds a vendor contact named `TechSource Electronics` to VendorVault.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits VendorVault.

1. Refer to the [Features](#features) below for details of each command. Or [Command Summary](#command-summary) for a quick summary of all commands.

<br>

--------------------------------------------------------------------------------------------------------------------

<br>

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

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
* `add n/TechLink Electronics p/61234567, 87654321 e/sales@techlink.com.sg a/10 Ubi Crescent, #05-12, Ubi Techpark, Singapore 408564`
* `add n/CompanyName p/61234567 (Office), 87654321 (HP) e/contact@company.com a/123, Clementi Rd, 1234665 t/business`

<box type="tip" seamless>

**Tip:** Include multiple phone numbers by separating them with commas (eg. `p/61234567, 98765432`).

</box

VendorVault helps to prevent duplicate contacts. Contacts with the same email and phone number as an existing contact will be rejected. For details on what contacts are considered duplicates, refer to this [FAQ](#faq-duplicate-contacts).

For more details on possible warnings and errors when adding a contact, refer to the [troubleshooting guide for add contact](#troubleshooting-add-contact) below.

<div style="height: 30px;"></div>

#### Listing all contacts : `list`

Shows a list of all contacts in the address book.

Format: `list`

<div style="height: 30px;"></div>

#### Editing a contact : `edit`

Edits an existing contact in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the contact at the specified `INDEX`. The index refers to the index number shown in the displayed contact list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the contact will be removed i.e adding of tags is not cumulative.
* You can remove all the contact’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st contact to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd contact to be `Betsy Crower` and clears all existing tags.

<div style="height: 30px;"></div>

#### Locating contacts by name: `find`

Finds contacts whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
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

Deletes the specified contact from the address book.

Format: `delete INDEX`

* Deletes the contact at the specified `INDEX`.
* The index refers to the index number shown in the displayed contact list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd contact in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st contact in the results of the `find` command.

<div style="height: 30px;"></div>

#### Clearing all contacts: `clear`

Clears all entries from the address book.

Format: `clear`

<div style="height: 30px;"></div>

### Managing Inventory

#### Adding a product: `addproduct`

Adds a product to the inventory.

Format: `addproduct id/IDENTIFIER n/NAME [q/QUANTITY]`

<box type="tip" seamless>

**Tip:** If quantity is not specified, it will default to 0.
</box>

Examples:
* `addproduct id/SKU-1003 n/Tray of Eggs q/30`
* `addproduct id/Pr1 n/HP LaserJet (M428fdw) q/5`
* `addproduct id/DE/5 n/PlayStation`

<div style="height: 30px;"></div>

### Undoing the previous command : `undo`

Undoes the previous command that changed the data.

Format: `undo`

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

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

<panel header="I accidentally entered a command that changed the data. Can I undo that?" type="seamless">

Yes, you can undo the previous command that changed the data by using the `undo` command. For example, if you accidentally deleted a contact, simply enter `undo` and the contact will be restored.

</panel>

<panel header="What contacts are considered duplicates?" type="seamless" id="faq-duplicate-contacts">

VendorVault helps prevent accidentally adding the same contact twice with duplicate detection.

How it works:
* The app checks the email and phone numbers when you add a new contact.
* If you try to add a contact with the **same email and phone number as an existing contact**, VendorVault will **reject it**.
* Phone numbers are compared ignoring specifications (like “(Office)” or “(HP)”) and are separated by commas.

For Example, the following commands are considered duplicates of each other because they share the same phone number `61234567` and email `contact@company.com`:<br>
```
add n/CompanyName p/61234567, 98765432 e/contact@company.com a/123, Clementi Rd, 1234665 t/business
add n/CompanyName p/61234567, 12345678 e/contact@company.com a/123, Clementi Rd, 1234665 t/business
```

</panel>

<panel header="I edited the data file directly and now VendorVault is not working. What should I do?" type="seamless">

If you edited the data file and it caused VendorVault to behave unexpectedly, you can try the following steps:
1. Restore from backup: If you made a backup of the data file before editing, you can restore the original data file by replacing the edited data files in the data folder with the backup.
2. Start with a new data file: If you do not have a backup, you can delete the existing data file (or move it to a different location for safekeeping) and start VendorVault again. This will create a new, empty data file.

</panel>

<panel header="How do I transfer my data to another computer?" type="seamless">

You can transfer your VendorVault data by transferring two files:
* Install VendorVault on the new computer (follow the [Quick Start](#quick-start) guide).
* Open the folder where VendorVault's `.jar` file is stored on your old computer.
* Look for the data files created by VendorVault (this file contains all your vendors and inventory).
    * Specifically, look for the `data` folder created by VendorVault, and the data file named `addressbook.json` and `inventory.json` inside that folder.
* Copy this data file to a USB drive or cloud storage (e.g., Google Drive, Dropbox).
* On the new computer, run the app and then open the newly created data folder.
* Replace the empty data file there with the one you copied from your old computer.
* Start VendorVault — your data should now appear exactly as before.

</panel>

--------------------------------------------------------------------------------------------------------------------

## Troubleshooting

### Managing contacts

#### Troubleshooting `add` contact

Use this section when `add` fails or returns a warning.

<box type="warning" seamless>

**Important:**
* **Error** messages mean the contact was **not added**.
* **Warning** messages mean the contact was **added**, but VendorVault is flagging a possible issue.

</box>

| Scenario                                                                         | Message shown                                                                                 | How to fix                                                        |
|----------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|-------------------------------------------------------------------|
| Missing one or more required prefixes (`n/`, `p/`, `e/`, `a/`)                   | `Missing required field(s): ...`                                                              | Include all required prefixed fields in your command.             |
| No prefixes at all                                                               | `All required prefixes are missing, ...`                                                      | Use the full prefixed format, e.g. `add n/... p/... e/... a/...`. |
| Text appears before the first prefix                                             | `No non-prefix characters before prefix(es) is allowed, ...`                                  | Remove any text before `n/`.                                      |
| Same single-value field repeated (e.g. two `n/` or two `e/`)                     | `Multiple values specified for the following single-valued field(s): ...`                     | Keep only one value for each of `n/`, `p/`, `e/`, `a/`.           |
| Name is blank                                                                    | `Name should not be blank.`                                                                   | Provide a non-empty name after `n/`.                              |
| Name is too long                                                                 | `Name should be less than 256 characters.`                                                    | Shorten the name.                                                 |
| Phone is blank/invalid                                                           | `Phone number should not be empty and must be at least 3 digits.`                             | Ensure each phone entry has at least 3 digits.                    |
| Email is blank                                                                   | `Email should not be blank.`                                                                  | Provide a non-empty email after `e/`.                             |
| Email format is invalid                                                          | `Email should be of the format local-part@domain ...`                                         | Use a valid email format (e.g. `sales@vendor.com`).               |
| Address is blank                                                                 | `Address can take any values, and it should not be blank`                                     | Provide a non-empty address after `a/`.                           |
| Address is too long                                                              | `Address should be less than 500 characters.`                                                 | Shorten the address.                                              |
| Tag contains non-alphanumeric characters                                         | `Tag names should be alphanumeric`                                                            | Use letters/numbers only for each `t/` value.                     |
| Contact duplicates an existing contact by same email or overlapping phone number | `This vendor contact already exists in the address book with the same email or phone number.` | Change the phone/email, or edit the existing contact instead.     |

Common `add` warnings:

| Warning trigger                        | Warning shown                                                                                                          | What it means                                                                                                                                                                            |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Name has unusual symbols               | `⚠ Warning: Name contains unusual symbols, is this intentional?`                                                       | Name is accepted, but looks unusual. You can verify if you entered the correct name.                                                                                                     |
| Phone includes unusual symbols/format  | `⚠ Warning: Phone number contains unusual symbols, is this intentional? ...`                                           | Phone is accepted, but format may be unintended. You can safely ignore it if you're providng specifications eg. `61234567 (Office)`                                                      |
| Email is unusually long                | `⚠ Warning: This email address is unusually long, is this intentional?`                                                | Email is accepted, but unusually long. You can verify if the email entered is correct.                                                                                                   |
| Similar name to an existing contact    | `⚠ Warning: There's a contact with a similar name (name: <similar-name>), is this intentional?`                        | Possible duplicate by similar name. You can check if the name in the warning message is the same vendor as what you were about to add.                                                   |
| Similar address to an existing contact | `⚠ Warning: There's a contact with a similar address (name: <name>, address: <similar-address>), is this intentional?` | Possible duplicate/related location by address similarity. You can check if the vendor name and address in the warning message belongs to the same vendor as what you were about to add. |

<box type="tip" seamless>

Tip: If multiple warnings apply, VendorVault shows all of them (one per line) together with the success message.

</box>

--------------------------------------------------------------------------------------------------------------------

## Command summary

| Action             | Command                                                           | Example                                                                                                    | What it does                             |
|--------------------|-------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|------------------------------------------|
| **Add Contact**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​ `          | `add n/TechSource Electronics p/61234567 e/sales@techsource.com a/15 Kallang Way, Singapore t/electronics` | Adds vendor contact                      |
| **Edit Contact**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​` | `edit 2 n/TechSource Electronics p/61234567`                                                               | Edits specified fields of vendor contact |
| **Delete Contact** | `delete INDEX`                                                    | `delete 3`                                                                                                 | Deletes contact at index specified       |
| **List**           | `list`                                                            |                                                                                                            | Lists all contacts                       |
| **Find Contact**   | `find KEYWORD [MORE_KEYWORDS]`                                    | `find TechSource`                                                                                          | Lists all contacts matching `KEYWORD`    |
| **Clear Contacts** | `clear`                                                           |                                                                                                            | Clears all contacts                      |
| **Add product**    | `addproduct id/IDENTIFIER n/NAME [q/QUANTITY]`                    | `addproduct id/SKU-1003 n/Tray of Eggs q/30 `                                                              | Adds product                             |
| **Undo**           | `undo`                                                            |                                                                                                            | Undoes previous command                  |
| **Help**           | `help`                                                            |                                                                                                            |                                          |
