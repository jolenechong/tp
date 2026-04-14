---
  layout: default.md
  title: "Jolene's Project Portfolio Page"
---

### Project: VendorVault

VendorVault is a desktop app for managing your vendors and inventory all in one place. It combines the speed of typing commands with the simplicity of a visual interface, allowing you to organise your vendor contacts and track your products efficiently. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java, and has over 25 kLoC.

Given below are my contributions to the project.


* *New Feature*: Added the ability to undo/redo previous commands, with action summary details. (Pull requests [\#66](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/66), [\#154](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/154), [\#160](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/160), [\#169](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/169), [\#292](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/292), [\#393](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/393), [\#345](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/345))
    * What it does: Allows users to undo previous commands step-by-step, with the option to redo them. Success messages also include the change being undone/redone for better clarity.
    * Justification: Provides a convenient way to recover from mistakes and improves usability.
    * Highlights: Applies to existing and future commands. See the Developer Guide for implementation details.

* *New Feature*: Added a history command that allows the user to navigate to previous commands using up/down keys. (Pull requests [\#134](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/134))
    * What it does: Lets users navigate previous commands using the up/down keys, making it easy to repeat commands without retyping.
    * Justification: Improves efficiency and user experience by enabling quick reuse of past commands.

* *Enhancements to existing features*:
    * Add Contact Command
      * Added Error Handling, Warnings and Length Constraints (Pull requests [\#65](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/65), [\#68](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/68), [\#104](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/104), [\#233](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/233), [\#260](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/260), [\#338](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/338), [\#550](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/550), [\#542](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/542))
      * Added Duplicate Errors with similar Email and Warnings with similar Name, Phone Number and Address respectively (Pull requests [\#109](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/109), [\#216](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/216), [\#246](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/246))
      * Fix emails case-sensitivity issue and enhanced duplicate error message to say duplicate contact (Pull requests [\#391](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/391))
      * Fix tags non-alphanumeric character issue (Pull requests [\#342](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/342))
      * Added the scroll to the bottom effect when a new contact is added (Pull requests [\#188](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/188))
    * Edit Contact Command
      * Error and Duplicate Handling (Pull requests [\#152](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/152))
      * Added Email as Identifier to edit instead of Index (Pull requests [\#107](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/170))
      * Added confirmation prompt when clearing all tags (Pull requests [\#197](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/197))
    * Clear Contact Command
      * Added Confirmation Prompt (Pull requests [\#190](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/190))
    * Find Contact Command
      * Added the ability to see products associated to contacts in the find contact results (Pull requests [\#273](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/273))
    * Add Product Command
      * Added Duplicate Handling Errors and Warnings (Pull requests [\#172](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/172))
    * Contact/Name/Phone Number/Address, Product Name Duplicate Warnings
      * Added the ability to detect and warn users of duplicate contact names, phone numbers, addresses and product names with the highest contiguous duplicate contact/product shown (Pull requests [\#305](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/305))
    * GUI
      * Updated the GUI color scheme and Design (Pull requests [\#73](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/73), [\#79](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/79))
      * Updated App to open to Maximised Screen and Increase Minimum Screen Size to accommodate our new GUI (Pull requests [\#120](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/120))

* *Documentation*:
  * Added Developer Guide sections for Undo/Redo and Command History Implementation (Pull requests [\#268](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/268), [\#269](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/269), [\#270](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/270), [\#169](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/169), [\#176](https://github.com/AY2526S2-CS2103T-W08-2/tp/pull/176))
  * Added Developer Guide for relevant features mentioned above and shared sections
  * Added User Guide for relevant features mentioned above and shared sections

* *Code contributed*: [RepoSense link](https://nus-cs2103-ay2526-s2.github.io/tp-dashboard/?search=jolenechong&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=jolenechong&tabRepo=AY2526S2-CS2103T-W08-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

<box type="info" seamless>

**Note:** This portfolio is up to date as of 14 April 2026. For a complete list of my contributions, including specific documentation updates and other minor pull requests not explicitly mentioned here, refer to: [PRs authored by me](https://github.com/AY2526S2-CS2103T-W08-2/tp/pulls?q=is%3Apr+author%3A%40me+sort%3Acreated-desc).

</box>
