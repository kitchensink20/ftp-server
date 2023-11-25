package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ListCommandHandler extends BaseCommandHandler{
    private final ServerSocket dataServerSocket;
    private final String currentDirectory;

    public ListCommandHandler(ServerSocket dataServerSocket, String currentDirectory) {
        this.dataServerSocket = dataServerSocket;
        this.currentDirectory = currentDirectory;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        Socket dataClient = dataServerSocket.accept();

        PrintWriter dataOutput = new PrintWriter(dataClient.getOutputStream(), true);

        File directory = new File(currentDirectory);
        File[] files = directory.listFiles();

        for(File file : files)
            dataOutput.println(getFileInfo(file));

        dataOutput.close();
        dataClient.close();

        return new FtpResponse(226, "Transfer complete");
    }

    private String getFileInfo(File file) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        String date = sdf.format(file.lastModified());
        String size = (file.isDirectory() ? "<DIR>" : String.valueOf(file.length()));
        String name = file.getName();

        return String.format("%s %10s %s", date, size, name);
    }
}
