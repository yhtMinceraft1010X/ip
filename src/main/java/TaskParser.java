package surrealchat.task;

import surrealchat.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import java.time.LocalDate;

public class TaskParser {
    public TaskManagement taskManagement;
    public static final int TASK_UNDONE = 0;

    public TaskParser() {
        this.taskManagement = new TaskManagement(new ArrayList<Task>());
    }

    public boolean parseIsDoneInt(int isDone) {
        switch(isDone) {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new InputMismatchException("Int has to be 0 or 1. Not stonks!");
        }
    }

    public Pair<String, List<Task>> parseFileLines(List<String> fileLines) {
        for (int i = 0; i < fileLines.size(); i++) {
            String[] taskComponents = fileLines.get(i).split("/split/");
            String taskType = taskComponents[0];
            int taskDone = Integer.valueOf(taskComponents[1]);
            String description = taskComponents[2];
            switch(taskType) {
                case "T":
                    this.addToDo(description, taskDone);
                    break;
                case "D":
                    this.addDeadline(description, taskDone);
                    break;
                case "E":
                    this.addEvent(description, taskDone);
                    break;
                default:
                    throw new InputMismatchException("The task type scanned from file is invalid. Not Stonks!");
            }
        }
        List<Task> taskList = this.taskManagement.getTaskList();
        return new Pair<String, List<Task>>("fileTasksAdded", taskList);
    }

    public int getNumberOfTasks() {
        return this.taskManagement.getTaskList().size();
    }

    public Pair<String, Pair<Task, Integer>> parseUserTaskInput(String command, String taskDescription) {
        if (taskDescription.isEmpty()) {
            throw new InputMismatchException("Empty " + command + " task description. Not stonks!");
        } else {
            Task addedTask;
            switch(command) {
                case "todo":
                    addedTask = this.addToDo(taskDescription, this.TASK_UNDONE);
                    break;
                case "deadline":
                    addedTask = this.addDeadline(taskDescription, this.TASK_UNDONE);
                    break;
                case "event":
                    addedTask = this.addEvent(taskDescription, this.TASK_UNDONE);
                    break;
                default:
                    throw new InputMismatchException("Somehow, a wrong command was entered. " +
                            "Command has to be task type. Not stonks!");
            }
            int numberOfTasks = this.getNumberOfTasks();
            return new Pair<String, Pair<Task, Integer>>("userTaskAdded",
                    new Pair<Task, Integer>(addedTask, numberOfTasks));
        }

    }

    public ToDoTask addToDo(String taskDescription, int isDone) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty todo task description. Not stonks!");
        } else {
            ToDoTask newTask = new ToDoTask(taskDescription.trim(), this.parseIsDoneInt(isDone));
            this.taskManagement.addTask(newTask);
            return newTask;
        }
    }

    public DeadlineTask addDeadline(String taskDescription, int isDone) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty deadline task description. Not stonks!");
        } else {
            String[] descriptionSplitArray = taskDescription.split("/by");
            try {
                DeadlineTask newTask = new DeadlineTask(descriptionSplitArray[0].trim(),
                        LocalDate.parse(descriptionSplitArray[1].trim()), this.parseIsDoneInt(isDone));
                this.taskManagement.addTask(newTask);
                return newTask;
            } catch (ArrayIndexOutOfBoundsException e) { //Happens if split does not occur
                throw new ArrayIndexOutOfBoundsException("Wrong formatting. Did you forget to put '/by'? Not stonks!");
            }
        }
    }

    public EventTask addEvent(String taskDescription, int isDone) {
        if (taskDescription.isEmpty()) {
            throw new NoSuchElementException("Empty event task description. Not stonks!");
        } else {
            String[] descriptionSplitArray = taskDescription.split("/at");
            try {
                EventTask newTask = new EventTask(descriptionSplitArray[0].trim(),
                        LocalDate.parse(descriptionSplitArray[1].trim()), this.parseIsDoneInt(isDone));
                this.taskManagement.addTask(newTask);
                return newTask;
            } catch (ArrayIndexOutOfBoundsException e) { //Happens if split does not occur
                throw new ArrayIndexOutOfBoundsException("Wrong formatting. Did you forget to put '/at'? Not stonks!");
            }
        }
    }

    public boolean checkInvalidTaskNumber(int taskNumber) {
        return ((taskNumber <= 0) || (taskNumber > this.taskManagement.getTaskList().size()));
    }

    public Pair<String, Task> markAsDone(int taskNumber) {
        if (this.checkInvalidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException("Invalid task number. Not stonks!");
        } else {
            Task doneTask = this.taskManagement.markAsDone(taskNumber);
            return new Pair<String, Task>("markDone", doneTask);
        }
    }

    public Pair<String, Task> markAsUndone(int taskNumber) {
        if (this.checkInvalidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException("Invalid task number. Not stonks!");
        } else {
            Task undoneTask = this.taskManagement.markAsUndone(taskNumber);
            return new Pair<String, Task>("markUndone", undoneTask);
        }
    }

    public Pair<String, Pair<Task, Integer>> deleteTask(int taskNumber) {
        if (this.checkInvalidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException("Invalid task number. Not stonks!");
        } else {
            Task deletedTask = this.taskManagement.deleteTask(taskNumber);
            Pair<String, Pair<Task, Integer>> deletePair = new Pair<String, Pair<Task, Integer>>("deleteTask",
                    new Pair<Task, Integer>(deletedTask, this.getNumberOfTasks()));
            return deletePair;
        }
    }

    public List<String> convertTasksForFile() {
        List<Task> rawTaskList = this.taskManagement.getTaskList();
        List<String> fileTaskList = new ArrayList<String>();
        for (int i = 0; i < rawTaskList.size(); i++) {
            fileTaskList.add(rawTaskList.get(i).saveTask());
        }
        return fileTaskList;
    }

    public Pair<String, List<Task>> sendListToPrint() {
        List<Task> rawTaskList = this.taskManagement.getTaskList();
        return new Pair<String, List<Task>>("printTaskList", rawTaskList);
    }
}
