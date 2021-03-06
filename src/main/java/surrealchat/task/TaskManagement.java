package surrealchat.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import surrealchat.exception.SurrealException;

/**
 * Handles storing of tasks and file loading/unloading operations.
 */
public class TaskManagement {
    protected final List<Task> taskList; //Protect taskList from being changed to null.

    /**
     * Creates instance of TaskManagement object.
     *
     * @param taskList List of tasks.
     */
    public TaskManagement(List<Task> taskList) {
        assert taskList != null : "Null taskList passed in! Not stonks!\n"; //Protection against null
        this.taskList = taskList;
    }

    /**
     * Returns List of tasks for further processing.
     *
     * @return List of tasks.
     */
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * Returns number of tasks stored.
     *
     * @return Number of tasks stored.
     */
    public int getNumberOfTasks() {
        return getTaskList().size();
    }

    /**
     * Adds task to internal task list.
     *
     * @param task Task to be added.
     */
    public void addTask(Task task) {
        taskList.add(task);
    }

    /**
     * Obtains the relevant task.
     *
     * @param taskNumber The number associated with task.
     * @return The associated task.
     */
    public Task getTask(int taskNumber) {
        return taskList.get(taskNumber - 1);
    }

    /**
     * Replaces the task at a certain position.
     *
     * @param taskNumber Position at which to replace task.
     * @param task The replacement task.
     */
    public void replaceTask(int taskNumber, Task task) {
        taskList.set(taskNumber - 1, task);
    }

    /**
     * Toggles a task designated by number between done and undone.
     *
     * @param taskNumber Position number of task (starting from 1) to be marked as done/undone.
     * @return Task that has been marked as done/undone.
     */
    public Task markAsDone(int taskNumber) {
        Task doneTask = taskList.get(taskNumber - 1).markAsDone();
        taskList.set(taskNumber - 1, doneTask);
        return doneTask;
    }


    /**
     * Deletes a task from the list.
     *
     * @param taskNumber Position number of task (starting from 1) to be deleted.
     * @return Deleted task.
     */
    public Task deleteTask(int taskNumber) {
        return taskList.remove(taskNumber - 1);
    }

    /**
     * Deletes all tasks from the list.
     *
     * @throws SurrealException If list was empty to begin with.
     */
    public void deleteAllTasks() throws SurrealException {
        if (taskList.isEmpty()) {
            throw new SurrealException("List is already empty. Not stonks!\n");
        }
        taskList.clear();
    }

    private String spellTaskType(String taskType) {
        assert taskType != null : "Somehow there was a null taskType. Not stonks!\n";
        switch(taskType) {
        case TaskCode.TODO_TYPE:
            return "todo";
        case TaskCode.DEADLINE_TYPE:
            return "deadline";
        case TaskCode.EVENT_TYPE:
            return "event";
        default:
            throw new InputMismatchException("The task type in task is invalid. Not Stonks!\n");
        }
    }

    private String printFileLoadOutput(List<Task> taskList) {
        String outputString = "";
        int total = taskList.size();
        outputString = taskList.stream().reduce("", (x, y) -> {
            String printTaskType = spellTaskType(y.getType());
            x += String.format("Meme Man has added %s task from file: %s\n", printTaskType, y);
            return x;
        }, (x, y) -> x + y);
        outputString += String.format("Total number of tasks loaded from file: %s\n", total);
        return outputString;
    }

    private void convertToTasks(String taskType, String description, boolean taskDone, TaskPriority taskPriority) {
        assert taskType != null : "Somehow there was a null taskType. Not stonks!\n";
        assert description != null : "Somehow, description was empty. Not stonks!\n";
        switch(taskType) {
        case TaskCode.TODO_TYPE:
            addToDoFromFile(description, taskDone, taskPriority);
            return;
        case TaskCode.DEADLINE_TYPE:
            addDeadlineFromFile(description, taskDone, taskPriority);
            return;
        case TaskCode.EVENT_TYPE:
            addEventFromFile(description, taskDone, taskPriority);
            return;
        default:
            throw new InputMismatchException("The task type scanned from file is invalid. Not Stonks!\n");
        }
    }

    private void parseTaskFromFile(String fileLine) {
        String[] taskComponents = fileLine.split("/split/");
        String taskType = taskComponents[0];
        boolean taskDone = parseDoneFromInt(Integer.valueOf(taskComponents[1]));
        TaskPriority taskPriority = TaskPriority.getPriorityType(Integer.valueOf(taskComponents[2]));
        String description = taskComponents[3];

        //Convert to Task objects
        convertToTasks(taskType, description, taskDone, taskPriority);
    }

    /**
     * Parses lines that were loaded form file into tasks.
     *
     * @param fileLines Lines from the loaded file.
     * @return String of tasks successfully loaded from files.
     */
    public String parseFileLines(List<String> fileLines) {
        fileLines.stream().forEach(t -> parseTaskFromFile(t));
        //Obtain list for printing
        List<Task> taskList = getTaskList();
        return printFileLoadOutput(taskList);
    }

    private boolean parseDoneFromInt(int doneInt) {
        switch(doneInt) {
        case 1:
            return true;
        case 0:
            return false;
        default:
            throw new InputMismatchException(
                    "doneInt is not correct. Check the file to see if doneInt is 0 or 1. Not stonks!\n");
        }
    }

    private ToDoTask addToDoFromFile(String taskDescription, boolean isDone, TaskPriority taskPriority) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty todo task description. Not stonks!\n");
        }

        ToDoTask newTask = ToDoTask.loadToDoTaskFromFile(isDone, taskDescription.trim(), taskPriority);
        addTask(newTask);
        return newTask;
    }

    private LocalDateTime parseDate(String dateString) {
        try {
            return LocalDateTime.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Input date time format is incorrect. Not stonks!\n");
        }
    }

    private DeadlineTask addDeadlineFromFile(String taskDescription, boolean isDone, TaskPriority taskPriority) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty deadline task description. Not stonks!\n");
        }

        //Split the description into description and deadline
        String[] descriptionSplitArray = taskDescription.split("/by");
        try {
            LocalDateTime deadlineDateTime = parseDate(descriptionSplitArray[1].trim());

            //Create Deadline task
            DeadlineTask newTask = DeadlineTask.loadDeadlineTaskFromFile(isDone, descriptionSplitArray[0].trim(),
                    taskPriority, deadlineDateTime);
            addTask(newTask);
            return newTask;
        } catch (ArrayIndexOutOfBoundsException e) { //Happens if split does not occur
            throw new ArrayIndexOutOfBoundsException("Wrong formatting. Did you forget to put '/by'? Not stonks!\n");
        }
    }

    private EventTask addEventFromFile(String taskDescription, boolean isDone, TaskPriority taskPriority) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty event task description. Not stonks!\n");
        }

        //Split the description into description and event
        String[] descriptionSplitArray = taskDescription.split("/at");
        try {
            LocalDateTime eventDateTime = parseDate(descriptionSplitArray[1].trim());

            //Create Event task
            EventTask newTask = EventTask.loadEventTaskFromFile(isDone, descriptionSplitArray[0].trim(),
                    taskPriority, eventDateTime);
            addTask(newTask);
            return newTask;
        } catch (ArrayIndexOutOfBoundsException e) { //Happens if split does not occur
            throw new ArrayIndexOutOfBoundsException("Wrong formatting. Did you forget to put '/at'? Not stonks!\n");
        }
    }

    /**
     * Generates list of tasks for saving into a file.
     *
     * @return List of tasks in file string format.
     */
    public List<String> convertTasksForFile() {
        List<Task> rawTaskList = getTaskList();
        List<String> fileTaskList = rawTaskList.stream().map(x -> x.saveTask()).collect(Collectors.toList());
        return fileTaskList;
    }

    /**
     * Converts list of tasks into string form for printing.
     *
     * @return List of tasks in print string format.
     * @throws SurrealException If list is empty.
     */
    public String listOutTasks() throws SurrealException {
        List<Task> rawTaskList = getTaskList();
        if (rawTaskList.isEmpty()) {
            throw new SurrealException("I have nothing to print. Not stonks!\n");
        }
        String outputTasks = "";
        for (int i = 1; i <= rawTaskList.size(); i++) {
            outputTasks += String.format("%d. %s\n", i, rawTaskList.get(i - 1));
        }
        return outputTasks;
    }

    /**
     * Obtains the tasks with keyword and corresponding numbers.
     *
     * @param keyword The keyword for which to search.
     * @return List of tasks with their corresponding numbers in string form.
     */
    public List<String> getSearchResults(String keyword) {
        assert keyword != null : "Null keyword somehow bypassed FindCommand exception. Not stonks!\n";
        List<String> searchResults = new ArrayList<String>();
        for (int i = 1; i <= taskList.size(); i++) {
            Task task = taskList.get(i - 1);
            if (task.toString().toLowerCase().contains(keyword.toLowerCase())) {
                String taskString = String.format("%d. %s\n", i, task);
                searchResults.add(taskString);
            }
        }
        return searchResults;
    }

    /**
     * Sorts the task list in level of priority order.
     *
     * @param sortBy The criteria by which to sort taskList.
     * @throws SurrealException If no sort criteria given, taskList is empty or sort criteria is unsupported.
     */
    public void sort(String sortBy) throws SurrealException {
        if (sortBy.isEmpty()) {
            throw new SurrealException("No sorting criteria given! Not stonks!\n");
        } else if (taskList.isEmpty()) {
            throw new SurrealException("I have nothing to sort. Not stonks!\n");
        }
        taskList.sort(TaskSort.getComparator(sortBy));
    }
}
