[comment]: # (@@author A0162011A)
# ToLuist - Developer Guide

By : `Team ToLuist`  &nbsp;&nbsp;&nbsp;&nbsp; Since: `Jan 2017`  &nbsp;&nbsp;&nbsp;&nbsp; Licence: `MIT`

1. [Introduction](#1-introduction)
2. [Setting Up](#2-setting-up)
3. [Design](#3-design)
4. [Implementation](#4-implementation)
5. [Testing](#5-testing)
6. [Dev Ops](#6-dev-ops)

* [Appendix A: User Stories](#appendix-a--user-stories)
* [Appendix B: Use Cases](#appendix-b--use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c--non-functional-requirements)
* [Appendix D: Glossary](#appendix-d--glossary)
* [Appendix E : Product Survey](#appendix-e--product-survey)


## 1. Introduction

Welcome to ToLuist's Developer Guide.

By going through this document, you will learn how to set up the project, understand the architecture of the 
application, as well as know how to troubleshoot some common development issues.

We have organized the guide in a top-down manner so that, as a new developer, you can look at the big picture of the project
 before zooming in on specific components.

## 2. Setting up

### 2.1. Prerequisites

Here are some tasks you should complete before diving into the project.

1. Install **JDK `1.8.0_60`**  or later<br>

    > Having any Java 8 version is not enough. This app will not work with earlier versions of Java 8.

2. Download and install **Eclipse** Integrated Development Environment on your computer.
3. Install **e(fx)clipse** plugin for Eclipse. 
(Proceed from step 2 
onwards on [this page](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious))
4. Visit Eclipse Marketplace in Eclipse, search for **Buildship Gradle 
Integration** plugin and install it.
5. Also from Eclipse Marketplace, install **Checkstyle Plug-in** plugin.


### 2.2. Importing the project into Eclipse

1. Fork this repo, and clone the fork to your computer.
2. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse** and **buildship** plugins as given
   in the prerequisites above).
3. Click `File` > `Import`.
4. Click `Gradle` > `Gradle Project` > `Next` > `Next`.
5. Click `Browse`, then locate the project's directory.
6. Click `Finish`.

  > * If you are asked whether to 'keep' or 'overwrite' config files, choose to 'keep'.
  > * Depending on your connection speed and server load, it can take up to 30 minutes for the set up to finish.
      This is because Gradle downloads library files from servers during the project set up process.
  > * If Eclipse auto-changed any settings files during the import process, you can discard those changes.

### 2.3. Configuring Checkstyle

1. Click `Project` -> `Properties` -> `Checkstyle` -> `Local Check Configurations` -> `New...`.
2. Choose `External Configuration File` under `Type`.
3. Enter an arbitrary configuration name. E.g. toluist
4. Import checkstyle configuration file found at `config/checkstyle/checkstyle.xml`.
5. Click OK once, go to the `Main` tab, use the newly imported check configuration.
6. Tick and select `files from packages`, click `Change...`, and select the `resources` package.
7. Click OK twice. Rebuild project if prompted.

> You should click on the `files from packages` text after ticking in order to enable the `Change...` button.

### 2.4. Troubleshooting project setup

**Problem: Eclipse reports compile errors after new commits are pulled from Git**

* Reason: Eclipse fails to recognize new files that appeared due to the Git pull.
* Solution: Refresh the project in Eclipse:<br>
  Right click on the project (in Eclipse package explorer), choose `Gradle` -> `Refresh Gradle Project`.

**Problem: Eclipse reports some required libraries missing**

* Reason: Required libraries may not have been downloaded during the project import.
* Solution: [Run tests using Gradle](UsingGradle.md) once (to refresh the libraries).

[comment]: # (@@author A0131125Y)
## 3. Design

### 3.1. Architecture

<img src="images/Architecture.png" width="600"><br>
**Figure 3.1**: Architecture Diagram

The **_Architecture Diagram_** given above explains the high-level design of ToLuist.
Given below is a quick overview of each component.

`Main` has only one class called [`MainApp`](../src/main/java/seedu/toluist/MainApp.java). It is responsible for:

* Initializing the components in the correct sequence, and connecting them with each other during app launch.
* Shutting down the components and invoking cleanup methods where necessary when exiting the app.

[**`Commons`**](#38-common-classes) represents a collection of classes used by many other components.
Two of those classes play important roles at the architecture level.

* `EventsCenter` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of **Event Driven** 
  design).
* `LogsCenter` : This class is used by many classes to write log messages to the App's log file.

The rest of the App consists of five components:

* [**`UI`**](#33-ui-component) renders the GUI of the app.
* [**`Dispatcher`**](#34-dispatcher-component) invokes a suitable command executor.
* [**`Controller`**](#35-controller-component) executes the command.
* [**`Model`**](#36-model-component) holds the data of the application in the memory.
* [**`Storage`**](#37-storage-component) reads data from, and writes data to, the hard disk.

Our architecture follows the **Model-View-Controller** (MVC) Pattern. `UI` displays data and interacts with the user. Commands are passed through the `Dispatcher` and routed to a suitable `Controller`. The `Controller` receives requests from the `Dispatcher` and acts as the 
bridge between the `UI` and the `Model`. The `Model` & `Storage` store and maintain the data. 
Inspirations for this design came from MVC architectures used by web MVC frameworks such as [Ruby on Rails](http://paulhan221.tumblr.com/post/114731592051/rails-http-requests-for-mvc) and [Laravel](http://laravelbook.com/laravel-architecture/).

The sections below explain each component in more details.

### 3.2. Event-Driven Approach

ToLuist applies **Event-Driven Approach** where applicable to reduce coupling between different 
components.

For example, consider what happens after a `ExitAppRequest` is posted. `EventsCenter` will notify all 
subscribers about this event. `MainApp`, which is a subscriber, will respond and stop the app.

<img src="images/Event-Driven.png" width="600"><br>
**Figure 3.2**: `MainApp` is informed when `ExitAppRequest` is posted

### 3.3. UI component

<img src="images/UiClassDiagram.png" width="600"><br>
**Figure 3.3**: Structure of the UI Component

**API** : [`Ui.java`](../src/main/java/seedu/toluist/ui/Ui.java)

**JavaFX** is used for the UI. `MainWindow` holds all the views that make up the different parts of the UI. These views inherit from the abstract `UiView` class, while `MainWindow` itself inherits from the abstract `UiPart` class.

#### 3.3.1. UiView

**API** : [`UiView.java`](../src/main/java/seedu/toluist/ui/view/UiView.java)

`UiView` is the building block for the UI. Each `UiView` should preferably be responsible for only one UI functionality.

Some of the key properties of a `UiView` are described below.

#### Associated with a FXML
Each `UiView` class is associated with a FXML file. For example, `TaskView` is associated with `TaskView.fxml` file. The corresponding FXML file will be loaded automatically when a new `UiView` instance is created.

#### Attachable to one single parent node
You can attach a `UiView` to a parent node. At any point of time, a `UiView` should always have a maximum of one parent. The parent node must be an object of `Pane` class or any of its subclasses.

#### Rendered idempotently

After attaching a `UiView` to a parent node, you can render it by invoking its public method `render()`. Each render call is guaranteed to be idempotent, i.e. subsequent calls to `render()` will render the same UI, as long as the data do not change.

#### Able to load subviews

Each `UiView` has a mini lifecycle. `viewDidLoad` is run after `render` is called. There are a few uses of `viewDidLoad`:

- Control UI-specific properties which cannot be done in FXML.
- Set UI component values (e.g. using `setText` on an FXML `Text` object).
- Attach subviews and propagate the chain.

#### 3.3.2. UiStore ####

**API** : [`UiStore.java`](../src/main/java/seedu/toluist/ui/UiStore.java)

`UiStore` holds the data to be used by `Ui`. An example would be the task data displayed to the user.
 
In essence, `UiStore` acts as a **View Model** for the `Ui`. The reason why `UiStore` is separate from the 
`Model` is because a lot of the states used in `UiStore` are ui-specific states. Keeping them separate 
allows a clear separation of concern between ui states and domain states.

Since `UiStore` acts as a single universal state container for `Ui`, it also implements the **Singleton** 
pattern, to restrict the instantiation of the class to one object.

#### 3.3.3. Reactive nature of the UI ####

To keep `UI` predictable and to reduce the number of lines of codes used to dictate how the `UI` should 
change based on state changes, we make use of reactive programming.
 
You can declare how a `UiView` should be rendered based solely on the states held by the `UiStore`. `UiView` 
and `UiStore` together implement the **Observer-Observable** pattern where the `UiView` will listen directly
 to changes in `UiStore` and re-render automatically.

To make an `UiView` object listen to changes to from a state in `UiStore`, you can use the `bind` API 
provided by `UiStore`. For example, `ResultView` is bound to the observable property 
`observableCommandResult` of `UiStore` using `uiStore.bind(resultView, uiStore.getObservableCommandResult())
`. This way, whenever the command result changes in `UiStore`, the result view will re-render itself to 
show the updated result text.

The diagram below shows how `UI` reacts when an add command is called. `UI` 
simply needs to display all the tasks available in the `UiStore`, without knowing what was the exact change.

<img src="images/UiSequence.png" width="600"><br>
**Figure 3.2.3**: Interactions inside the UI for the `add study` command.

The reactive approach is borrowed from modern Javascript front-end frameworks such as [React.js](https://facebook.github.io/react/) and [Vue.js](https://vuejs.org/v2/guide/reactivity.html).

### 3.4. Dispatcher component

<img src="images/DispatcherClassDiagram.png" width="600"><br>
**Figure 3.4**: Structure of the Dispatcher Component

**API** : [`Dispatcher.java`](../src/main/java/seedu/toluist/dispatcher/Dispatcher.java)

The `Dispatcher` acts like a router in a traditional Web MVC architecture. On receiving new input from `UI`, `Dispatcher` decides which `Controller` is the best candidate to handle the input, then instantiates and asks the `Controller` object to execute the command.
 
In effect, `Dispatcher` is implementing the **Facade** pattern, shielding the command logic from `UI`.

### 3.5. Controller component

<img src="images/ControllerClassDiagram.png" width="600"><br>
**Figure 3.5**: Structure of the Controller Component

**API** : [`Controller.java`](../src/main/java/seedu/toluist/controller/Controller.java)

`Controller` has an `execute` method to execute the command passed by `Dispatcher`. The command execution
 can affect the `Model`, the `Storage` (e.g. adding a task) and/or raise events. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to `Dispatcher`. After every `execute` invocation, the `Controller` can optionally set new states in the `UiStore`, which subsequently trigger a UI re-render.

Each command is represented by a different `Controller` class, which all extends from the abstract 
`Controller` class. The `Controller` is implementing the **Command** pattern, where each `Controller` class 
carries the functionality of a different command.

Some other interesting properties of `Controller` are described below.

#### 3.5.1. Responsible for its own tokenization

As opposed to having a central `Tokenizer` or `Parser` class to decide how to tokenize all the different 
commands, each `Controller` provides its own implementation for `tokenize`. This is more modular than 
having a single `Tokenizer` class, as different commands can have very different formats, leading to very 
different tokenization logic in the corresponding `Controller` classes (Though if the logic is similar, 
they can be shared through a helper class).

In effect, this is applying **Open Closed Principle**, as `Dispatcher` does not need 
to be aware of how the various `Controller` classes do their tokenization, and only interact with each 
`Controller` through the common API `tokenize`. For new commands with vastly different formats, you can 
then easily add a new `Controller` with its own `tokenize` implementation.

#### 3.5.2. Responsible for its own parameter suggestions

To support parameter suggestion, each `Controller` must also provide a list of parameters for each command,
 any fixed options specific to a parameter, as well as any keywords that should not appear together in the 
 command. 
 
Again, there was a conscious decision to let this be provided individually by each `Controller`. Though an 
alternative approach is to have a single class that stores all the keywords used by all commands, this 
approach quickly grows out of hand when there are different commands having the same parameter, but used for 
different purposes. 

Again, ToLuist applies **Open Closed Principle** here, where a new `Controller` can be added 
easily while the 
implementation for getting suggested keywords inside `Dispatcher` can remain unchanged.

#### 3.5.3. Controller common classes

Classes used by multiple components in the controllers are in the `seedu.toluist.controller.commons` package.<br>

Each `Controller` will execute the command by separating the command word and the index(es) (if it exist) from the rest of the description.

##### 3.5.3.1. IndexParser

The index(es) is/are passed through `IndexParser` class to obtain a list of indexes, so that the command action can be applied to the task with these indexes.

E.g. For the command `delete - 3, 5, 7-9, 10 -`, the indexes string `- 3, 5, 7-9, 10 -` is passed into `IndexParser`, and the `DeleteTaskController` specifies that there are at most `11` tasks. `IndexParser` will then return a list of indexes `1, 2, 3, 5, 7, 8, 9, 10, 11` so that the `delete` action can be applied to each of these tasks that correspond to each index.

##### 3.5.3.2. KeywordTokenizer

The description is passed through `KeywordTokenizer` class to obtain a dictionary of parameter-value tokens, where each of the token is handled separatedly by the `Controller` itself.

E.g. For the command `update 1 buy milk /tags strong bones /by friday`, the description `buy milk /tags strong bones /by friday` is passed into `KeywordTokenizer`, and `UpdateTaskController` specifies that the default keyword for unmatched strings is `description`. `KeywordTokenizer` will then return a dictionary of tokens `tags -> strong bones`, `by -> friday` and `description -> buy milk`, where each token will be handled separately in `UpdateTaskController`.

### 3.6. Model component

**API** : [`TodoList.java`](../src/main/java/seedu/toluist/model/TodoList.java)

The `Model` component stores the task data for the app inside the memory.

The todo list data in ToLuist is represented by `TodoList` class. `TodoList` applies **Singleton** pattern,
 so all `Controller` classes can access the same `TodoList` instance when manipulating data.

Each `ToLuist` instance holds many `Task` objects. Depending on whether the properties `startDateTime` and 
`endDateTime` are set, `Task` can represent floating task, task with deadline or event.

The state diagram below represents the relationships between different types of tasks.

<img src="images/TaskStateDiagram.png" width="600"><br>
**Figure 3.6**: Relationships between different task types

### 3.7. Storage component

**API** : [`TodoListStorage.java`](../src/main/java/seedu/toluist/storage/TodoListStorage.java)

The `Storage` component:
- acts like a database in the application.
- provides read/write funcionalities to the `Model`, encapsuling all the inner implementation details.
- saves the task data in json format and reads it back.
- holds the history of all data changes

#### Undoable History

`undoHistoryStack` holds the serialized json strings of the task list data. The minimum size of this stack is 
always 1. The json string at the top of the stack is the serialization of the current todo list data.

To undo the most recent changes, we simply pop the irrelevant strings in `undoHistoryStack` and then deserialize the json string at the top of the stack into task list data.

An alternative approach to implementing undoable history is to create a `unexecute` method for each mutating command, and have a local 
history of the data changes in each `Controller` instance. Compared to this alternative, storing a centralized history of data 
changes in `Storage` is much more robust, as we can avoid checking every `Controller` instance to 
get the previous data state. An additional benefit is that the integrity of the data change order is guaranteed in 
the `undoHistoryStack`, and we do not need to keep track of the previous mutating commands.

[comment]: # (@@author A0162011A)
### 3.8. Common classes

Classes used by multiple components are in the `seedu.toluist.commons` package.

## 4. Implementation

### 4.1. Logging

We are using the `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels
and logging destinations.

* You can control the logging level by using the `logLevel` setting in the configuration file
  (See [Configuration](#42-configuration)).
* You can obtain the `Logger` for a class by using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level.
* Currently, log messages are output through `Console` and to a `.log` file.

**Logging Levels**

* `SEVERE` : A critical problem is detected, which may possibly cause the termination of the application.
* `WARNING` : Caution is advised.
* `INFO` : Information showing noteworthy actions by ToLuist.
* `FINE` : Details that are not usually noteworthy but may be useful in debugging.

### 4.2. Configuration

You can control certain properties of the application (e.g logging level) through the configuration file
(default: `config.json`).

## 5. Testing

You can find the tests in the `./src/test/java` folder.

**In Eclipse**:

* To run all tests, right-click on the `src/test/java` folder and choose
  `Run as` > `JUnit Test`.
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.

**Using Gradle**:

* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.

We have two types of tests:

1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI.
   These are in the `guitests` package.

2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.toluist.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units
     (those code units are assumed to be working).<br>
      e.g. `seedu.toluist.storage.JsonStorageTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as
      how the are connected together.<br>
      e.g. `seedu.toluist.controller.AddControllerTest`

#### Headless GUI Testing
Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in _headless_ mode.
 In headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.

## 6. Dev Ops

### 6.1. Build Automation

See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.

### 6.2. Continuous Integration

We use [Travis CI](https://travis-ci.org/) and [AppVeyor](https://www.appveyor.com/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) and [UsingAppVeyor.md](UsingAppVeyor.md) for more details.

### 6.3. Publishing Documentation

See [UsingGithubPages.md](UsingGithubPages.md) to learn how to use GitHub Pages to publish documentation to the
project site.

### 6.4. Making a Release

Here are the steps to create a new release.

 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file).
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Create a new release using GitHub](https://help.github.com/articles/creating-releases/)
    and upload the JAR file you created.

### 6.5. Managing Dependencies

A project often depends on third-party libraries. For example, ToLuist depends on the
[Jackson library](https://github.com/FasterXML/jackson) for JSON parsing. Managing these dependencies
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which
is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size).<br>
b. Require developers to download those libraries manually (this creates extra work for developers).<br>

[comment]: # (@@author A0127545A)
## Appendix A : User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`


Priority | As a ... | I want to ... | So that I can...
-------- | :-------- | :--------- | :-----------
`* * *` | new user | see usage instructions | refer to instructions when I forget how to use the App
`* * *` | user | add a new task | remind myself of things I have to do
`* * *` | user | add a new task with completion date | remind myself of things I have to do and the deadlines to meet
`* * *` | user | add a new [recurring task](#recurring-task) | prevent the need of keying the same cyclical task every period
`* * *` | user | see all tasks | have an overview of all the things I need to do and decide what I should do first
`* * *` | user | update a task | change entries that are errorneous or outdated
`* * *` | user | mark a task as completed/incompleted | focus on the tasks I have still not cleared
`* * *` | user | delete a task | remove entries that I no longer need
`* * *` | user | add a new event | remind myself of things I have to attend
`* * *` | user | see all events | have an overview of all the things I need to attend
`* * *` | user | update an event | change entries that are errorneous or outdated
`* * *` | user | delete an event | remove entries that I no longer need
`* * *` | user | find a task by name | locate details of task without having to go through the entire list
`* * *` | user | find a task by tag | locate details of task without having to go through the entire list
`* * *` | user | add a tag to a task | group my tasks to provide more context
`* * *` | user | update a tag for a task | change entries that are errorneous or outdated
`* * *` | user | remove a tag from a task | remove entries that I no longer need
`* * *` | user | undo previous command(s) | recover gracefully from making mistakes
`* * *` | user | redo previously undone command(s) | recover gracefully from  wrongly resolving a mistake
`* * *` | user | see my [command history](#command-history) | identify what I have to recover if I had accidentally performed some wrong commands.
`* * *` | user | change my storage file path | decide where I want to save my files for my own use
`* * *` | user | exit the program | gracefully shut down the program when I don't need to use it
`* *` | experienced user | add an [alias](#alias) for a command | customise my own keyboard shortcuts to improve my efficiency
`* *` | experienced user | update an alias for a command | change entries that are errorneous or outdated
`* *` | experienced user | view all alias for commands | review the alias in case I forget what I set for them
`* *` | experienced user | delete an alias for a command | remove entries that I no longer need
`* *` | experienced user | remove multiple tasks at once | reduce the number of commands I use to get the job done
`*` | user | clear all tasks | start afresh with a new task list fast
`*` | user with many tasks | sort task by [priority level](#priority-level) then by end date | figure out which task should be cleared first
`*` | user with many tasks | see statistics for my number of tasks undone, doing, completed | figure out how much work I have left

## Appendix B : Use Cases


(For all use cases below, the **System** is `ToLuist` and the **Actor** is the `user`, unless specified otherwise)

#### Use case 1: View usage instructions

**MSS**

1. Actor requests to see usage instructions.
2. System displays the usage instructions for all the commands.

#### Use case 2: Add a task/event

**MSS**

1. Actor requests to add a task/event with `description` in the input box.
2. System adds the task/event. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. `description` is not provided.

> 2a1. System shows an error message with the correct format example.<br>
> Use case resumes at step 1

#### Use case 3: Update a task/event

**MSS**

1. Actor requests to update a task/event with `index` number in the input box.
2. System finds the task/event and updates it. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. `index` number given is invalid or cannot be found (i.e. `index` number is not a positive integer, or an out-of-range positive integer).

> 2a1. System shows an error message with the correct format example.
> Use case resumes at step 1

#### Use case 4: Delete a task/event

**MSS**

1. Actor requests to delete a task/event with `index` number in the input box.
2. System finds the task/event and deletes it. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. `index` number given is invalid or cannot be found (i.e. `index` number is not a positive integer, or an out-of-range positive integer).

> 2a1. System shows an error message with the correct format example.
> Use case resumes at step 1

#### Use case 5: Mark a task/event as completed/incomplete

**MSS**

1. Actor requests to mark a task/event with `index` number as completed or incomplete.
2. System finds the task/event and updates its status. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. `index` number given is invalid or cannot be found (i.e. `index` number is not a positive integer, or an out-of-range positive integer).

> 2a1. System shows an error message with the correct format example.
> Use case resumes at step 1

#### Use case 6: Undo previous mutated command

**MSS**

1. Actor requests to `undo` action in the input box.
2. System finds the most recent command that mutates the list and undoes it. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. No previous mutated command to undo

> 2a1. System does nothing since there is nothing to undo.
> Use case ends


#### Use case 7: Redo previous undone command

**MSS**

1. Actor requests to `redo` action in the input box.
2. System finds the most recent undone command that mutates the list and redoes it. System shows a feedback message and displays the updated list.<br>
Use case ends.

**Extensions**

2a. No previous undone command to redo

> 2a1. System does nothing since there is nothing to redo.
> Use case ends

#### Use case 8: Add alias for a command

**MSS**

1. Actor requests to `alias` a command in the input box with a `new alias name`.
2. System finds the command and alias it. System shows a feedback message.<br>
Use case ends.

**Extensions**
2a. The alias is already reserved for other commands.

> 2a1. System updates the alias name to refer to the new command.<br>
> Use case resumes at step 3.

#### Use case 9: Remove an alias

**MSS**

1. Actor requests to `unalias` an `alias` in the input box.
2. System finds the alias and removes it. System shows a feedback message.<br>
Use case ends.

**Extensions**

2a. No such existing alias exist.

> 2a1. System shows a feedback message "There is no such existing alias".<br>
> Use case ends

#### Use case 10: View existing aliases

**MSS**

1. Actor requests to display all the existing aliases in the system.
2. System displays all existing aliases.<br>
Use case ends.

#### Use case 11: Set data storage file path

**MSS**

1. Actor requests to save data to a `new file path`.
2. System saves task list to the new data storage file path and delete the old file. System shows a message.<br>
Use case ends.

**Extensions**

2a. File path is not in the correct format.

> 2a1. System shows an error message with the correct format example.<br>
> Use case resumes at step 1.

2b. File path already exist.

> 2b1. System shows an error message.<br>
> Use case resumes at step 1.

#### Use case 12: View command history

**MSS**

1. Actor requests to display the history of all commands in the current session.
2. System displays the command history.<br>
Use case ends.

#### Use case 13: Exit the program

**MSS**

1. Actor requests to exit the program.
2. System exits.<br>
Use case ends.

## Appendix C : Non Functional Requirements

1. Should work on any [mainstream OS](#mainstream-os) as long as it has Java `1.8.0_60` or higher installed.
2. Should be able to hold up to 1000 tasks without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands)
   should be able to accomplish most of the tasks faster using commands than by using a mouse.
4. Commands should be reasonably fluid and flexible (i.e. look like written English) so it is more intuitive for users.
5. The stored data should never, ever be destroyed unless that is what the user wants.
6. Should have nice UI/UX so the user will have a pleasant experience using ToLuist.
7. Each command should finish executing in less than 2 seconds.
8. Should have automated unit tests.
9. Should use Continuous Integration.
10. Should be kept open source.
11. Should be a free software.
12. Source code should be well-documented.
13. Application should be easy to set up (i.e. no installer required, no assistance required other than a user guide).


## Appendix D : Glossary

##### Mainstream OS

> Windows, Linux, Unix, OS-X

##### Recurring task

> A task that has to be done every fixed length of time.

##### Command history

> A list of commands that the user has entered.

##### Alias

> An alternative name for a phrase.

##### Priority level

> The relative importance or urgency of a task compared to other tasks.

## Appendix E : Product Survey

[comment]: # (@@author A0127545A)
**Google Calendar**

Pros:

* Support recurring events.
* Monthly, weekly, daily calendar view is useful for users to visualise their schedule.
* Support fuzzy search for events.
* Certain operations does not require clicking (e.g. add event).
* Support sharing of calendar with other users through export to CSV or iCal.
* Can be used offline.

Cons:

* Does not support task without deadline.
* Does not support marking task as complete/incomplete.
* Certain operations requires clicking (e.g. update task), which is not what Jim wants.

[comment]: # (@@author A0131125Y)
**Trello**

Pros:

* Geared towards task management.
* Intuitive UI that gives an overview of a different task in a pipeline.
* Support collaboration on the task board.

Cons:

* Does not have good calendar integration.
* Cannot be used offline.
* Certain operations requires clicking (e.g. dragging and dropping tasks between list), which is not what Jim wants.

[comment]: # (@@author A0162011A)
**Google Tasks**

Pros:

* Fast to set up and use.
* Seamless integration with Google Mail and Calendar.
* Easy to sync across multiple devices.
* Small footprint.

Cons:

* Cannot be used offline.
* Inflexible design.
* Lacking in additional features.
