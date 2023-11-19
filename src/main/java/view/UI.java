package view;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.*;
import java.util.List;

public class UI {
    private static UI ui;
    private BufferedReader reader;
    private BufferedWriter writer;

    private UI(InputStream inputStream, OutputStream outputStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public static UI getUI(InputStream inputStream, OutputStream outputStream){
        if(ui == null)
            ui = new UI(inputStream, outputStream);
        return ui;
    }

    public static UI getUI() {
        return ui;
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

    public void displayUsersList(List<User> users) throws IOException {
        for(User user : users) {
            writer.write(user.toString());
            writer.newLine();
        }
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
}
