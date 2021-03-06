package surrealchat.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Handles file save/load operations.
 */
public class FileManagement {
    protected File file;

    /**
     * Creates a new FileManagement instance.
     *
     * @param file File object with specified file path for save/load.
     */
    public FileManagement(File file) {
        this.file = file;
    }

    /**
     * Scans a file to generate list of tasks in the file.
     *
     * @return List of tasks as taken from file.
     * @throws IOException If error arises during file creation process.
     */
    public List<String> loadTaskFile() throws IOException {
        //Create file if not available and scan the file
        file.createNewFile();
        Scanner fileScanner = new Scanner(file);

        //Obtain task lines
        List<String> fileLines = new ArrayList<String>();
        while (fileScanner.hasNext()) {
            String nextTask = fileScanner.nextLine();
            fileLines.add(nextTask);
        }
        return fileLines;
    }

    /**
     * Writes tasks into file to be saved.
     *
     * @param fileTaskList List of tasks to be keyed into the file.
     */
    public void saveTasksToFile(List<String> fileTaskList) {
        try {
            //Write the tasks to file
            FileWriter fw = new FileWriter(file);
            for (String s : fileTaskList) {
                fw.write(String.format("%s\n", s));
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Something went wrong! Not stonks!\n");
        }
    }
}
