import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import surrealchat.command.Command;
import surrealchat.command.DeadlineCommand;
import surrealchat.command.DeleteCommand;
import surrealchat.command.DoneCommand;
import surrealchat.command.EditCommand;
import surrealchat.command.EventCommand;
import surrealchat.command.FindCommand;
import surrealchat.command.ListCommand;
import surrealchat.command.ScronchCommand;
import surrealchat.command.SortCommand;
import surrealchat.command.ToDoCommand;
import surrealchat.easteregg.EasterEgg;
import surrealchat.easteregg.HandEasterEgg;
import surrealchat.easteregg.OrangEasterEgg;
import surrealchat.easteregg.VegetalEasterEgg;
import surrealchat.file.FileManagement;
import surrealchat.help.HelpMode;
import surrealchat.task.Task;
import surrealchat.task.TaskManagement;

/**
 * Handles logic of SurrealChat.
 */
public class SurrealChat {
    protected static final String TASK_FILE_PATH = "tasks.txt";
    protected final TaskManagement taskManagement;
    protected final FileManagement fileManagement;
    protected final String fileLoadOutput;

    private SurrealChat(TaskManagement taskManagement,
                        FileManagement fileManagement) {
        assert taskManagement != null : "Null taskManagement! Not stonks!\n";
        assert fileManagement != null : "Null fileManagement! Not stonks!\n";
        this.taskManagement = taskManagement;
        this.fileManagement = fileManagement;
        fileLoadOutput = loadFile();
    }

    /**
     * Creates new SurrealChat instance.
     *
     * @param filePath Path of file for save/load.
     * @return SurrealChat instance.
     */
    public static SurrealChat initSurrealChat(File filePath) {
        assert filePath != null : "Null filePath. File path needed for save/load. Not stonks!\n";
        TaskManagement taskManagement = new TaskManagement(new ArrayList<Task>());
        FileManagement fileManagement = new FileManagement(filePath);
        return new SurrealChat(taskManagement, fileManagement);
    }

    private String[] splitString(String inputString) {
        return inputString.split(" ");
    }

    private void checkExcessArguments(String excess) {
        if (!excess.isEmpty()) {
            throw new InputMismatchException("Excessive inputs for a no-input command. Not stonks!\n");
        }
    }

    /**
     * Generates output to be printed based on what command is executed.
     *
     * @param inputString The entire command, inclusive of arguments if any.
     * @return Output to be printed.
     */
    public String commandLogic(String inputString) {
        if (inputString.isEmpty()) {
            return "Nothing was typed in! Not stonks!\n";
        }
        String[] separatedWords = splitString(inputString);
        String userCommand = separatedWords[0];
        String restOfInput = "";

        //Reorganise the remainder of input.
        for (int i = 1; i < separatedWords.length; i++) {
            restOfInput += String.format("%s ", separatedWords[i]);
        }
        restOfInput = restOfInput.trim();
        try {
            return executeCommand(userCommand, restOfInput);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String executeCommand(String command, String restOfInput) {
        switch(command) {
        case "help":
            return HelpMode.displayHelp(restOfInput);
        case "list":
            checkExcessArguments(restOfInput);
            Command listCommand = new ListCommand();
            String outputList = listCommand.execute(taskManagement);
            return outputList;
        case "todo":
            Command addToDoCommand = new ToDoCommand(restOfInput);
            String outputString = addToDoCommand.execute(taskManagement);
            return outputString;
        case "deadline":
            Command addDeadlineCommand = new DeadlineCommand(restOfInput);
            outputString = addDeadlineCommand.execute(taskManagement);
            return outputString;
        case "event":
            Command addEventCommand = new EventCommand(restOfInput);
            outputString = addEventCommand.execute(taskManagement);
            return outputString;
        case "edit":
            Command editCommand = new EditCommand(restOfInput);
            outputString = editCommand.execute(taskManagement);
            return outputString;
        case "done":
            Command doneCommand = new DoneCommand(restOfInput);
            outputString = doneCommand.execute(taskManagement);
            return outputString;
        case "delete":
            Command deleteCommand = new DeleteCommand(restOfInput);
            outputString = deleteCommand.execute(taskManagement);
            return outputString;
        case "scronch":
            checkExcessArguments(restOfInput);
            Command scronchCommand = new ScronchCommand();
            outputString = scronchCommand.execute(taskManagement);
            return outputString;
        case "find":
            Command findCommand = new FindCommand(restOfInput);
            outputString = findCommand.execute(taskManagement);
            return outputString;
        case "sort":
            Command sortCommand = new SortCommand(restOfInput);
            outputString = sortCommand.execute(taskManagement);
            return outputString;
        case "orang":
            checkExcessArguments(restOfInput);
            EasterEgg orangEasterEgg = new OrangEasterEgg();
            outputString = orangEasterEgg.execute();
            return outputString;
        case "vegetal":
            checkExcessArguments(restOfInput);
            EasterEgg vegetalEasterEgg = new VegetalEasterEgg();
            outputString = vegetalEasterEgg.execute();
            return outputString;
        case "icandoit":
            //Fallthrough to aikendueet
        case "aikendueet":
            checkExcessArguments(restOfInput);
            EasterEgg handEasterEgg = new HandEasterEgg();
            outputString = handEasterEgg.execute();
            return outputString;
        default:
            return "Command not recognised. Not stonks!\n";
        }
    }

    /**
     * Loads the tasks from file.
     */
    public String loadFile() {
        try {
            List<String> fileLines = fileManagement.loadTaskFile();
            return taskManagement.parseFileLines(fileLines);
        } catch (IOException e) {
            return "File loading error. Not stonks!\n";
        }
    }

    /**
     * Saves the tasks into file.
     *
     * @return A string indicating that tasks are being saved.
     */
    public String saveFile() {
        List<String> fileTaskList = taskManagement.convertTasksForFile();
        fileManagement.saveTasksToFile(fileTaskList);
        return "Saving tasks now...\n";
    }
}
