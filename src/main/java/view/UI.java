package view;

import model.File;
import model.User;
import myFtpServer.protocol.FtpResponse;
import userMemento.UserMemento;

import java.io.*;
import java.util.List;

public class UI {
    private BufferedReader reader;
    private BufferedWriter writer;

    public UI(InputStream inputStream, OutputStream outputStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String acceptUserInput() throws IOException {
        return reader.readLine();
    }

    public void displayFtpResponse(FtpResponse ftpResponse) throws IOException {
        writer.write(ftpResponse.toString());
        writer.newLine();
        writer.flush();
    }

    public void displayDirectoryContent(String fileName) throws IOException {
        writer.write(fileName);
        writer.newLine();
        writer.flush();
    }

    public void closeStreams() throws IOException {
        writer.close();
        reader.close();
    }

    public void displayAdminMenu() throws IOException {
        writer.newLine();
        writer.write("Admin menu:");
        writer.newLine();
        writer.write("1 - View log file");
        writer.newLine();
        writer.write("2 - Create new user");
        writer.newLine();
        writer.write("3 - View the list of users");
        writer.newLine();
        writer.write("4 - View my folder");
        writer.newLine();
        writer.write("5 - View user's folder");
        writer.newLine();
        writer.write("6 - Upload file to my folder");
        writer.newLine();
        writer.write("7 - Download file from my folder");
        writer.newLine();
        writer.write("8 - Delete file from my folder");
        writer.newLine();
        writer.write("exit - Log out");
        writer.newLine();
        writer.flush();
    }

    public void displayUserMenu() throws IOException {
        writer.newLine();
        writer.write("User menu:");
        writer.newLine();
        writer.write("1 - View my folder");
        writer.newLine();
        writer.write("2 - Upload file to my folder");
        writer.newLine();
        writer.write("3 - Download file from my folder");
        writer.newLine();
        writer.write("4 - Delete file from my folder");
        writer.newLine();
        writer.write("5 - Change my username");
        writer.newLine();
        writer.write("6 - Change my password");
        writer.newLine();
        writer.write("7 - Backup in user data to the recent state");
        writer.newLine();
        writer.write("exit - Log out");
        writer.newLine();
        writer.flush();
    }

    public void displayUsersList(List<User> users) throws IOException {
        for(User user : users) {
            writer.write(user.toString());
            writer.newLine();
        }
        writer.flush();
    }

    public void displayFileList(List<File> files) throws IOException {
        if(files.isEmpty()) {
            writer.write("User's folder is empty.");
            writer.newLine();
        } else {
            for(File file : files) {
                writer.write(file.toString());
                writer.newLine();
            }
        }

        writer.flush();
    }

    public void displayLogOutMessage() throws IOException {
        writer.write("Logging out...");
        writer.flush();
    }

    public void displayWrongPasswordMessage() throws IOException {
        writer.write("The password is wrong. Try again.");
        writer.newLine();
        writer.flush();
    }

    public void displayUserNotFoundMessage() throws IOException {
        writer.write("Username was not found. Try again.");
        writer.newLine();
        writer.flush();
    }

    public void displayUserHasActiveSessionMessage() throws IOException {
        writer.write("You already have active session.");
        writer.newLine();
        writer.flush();
    }

    public void displayLogFileContent(List<String> content) throws IOException {
        writer.write("Reading log file...");
        writer.newLine();

        if(content.isEmpty()) {
            writer.write("File is empty.");
            writer.newLine();
        } else {
            for(String line : content) {
                writer.write(line);
                writer.newLine();
            }
        }

        writer.flush();
    }

    public void displayUsernameBusyMessage() throws IOException {
        writer.write("Username is already busy.");
        writer.newLine();
        writer.flush();
    }

    public void displayPasswordTheSameMessage() throws IOException {
        writer.write("New password is the same as previous.");
        writer.newLine();
        writer.flush();
    }

    public void displayNoUserMementoFoundMessage() throws IOException {
        writer.write("Nothing to restore..");
        writer.newLine();
        writer.flush();
    }

    public void displayUserMementoFoundMessage(User user, UserMemento userMemento) throws IOException {
        writer.write("Restoring userdata..");
        writer.newLine();
        if(!user.getUsername().equals(userMemento.getUsername()))
            writer.write("Your username is again - " + userMemento.getUsername() + "." );
        else if(!user.getPassword().equals(userMemento.getPassword()))
            writer.write("Your password has been changed to the previous one.");
        writer.newLine();
        writer.flush();
    }
}
