package fileHandling;

import java.util.*;
import java.io.*;

public class FileHandler {
    private static final String LOG_FILE_PATH = "ServerFiles\\LogFile.txt";
    private static final String USER_DIRS_STORAGE_DIR_PATH = "C:\\ftp-server-project\\DataStorage";

    public static List<String> getLogFileContent() throws IOException {
        File logFile = new File(LOG_FILE_PATH);
        BufferedReader fileReader = new BufferedReader(new FileReader(logFile));
        List<String> fileContent = new ArrayList<>();
        String newLine;
        while((newLine = fileReader.readLine()) != null) {
            fileContent.add(newLine);
        }
        fileReader.close();

        return fileContent;
    }
}
