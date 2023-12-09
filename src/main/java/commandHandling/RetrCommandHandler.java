package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RetrCommandHandler extends BaseCommandHandler{
    private final Socket dataSocket;

    public RetrCommandHandler(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(dataSocket.getOutputStream());
        FtpResponse ftpResponse;

        try {
            sendFile(arguments, dataOutputStream);
            ftpResponse = new FtpResponse(226, "File transfer complete");
        } catch (FileNotFoundException e) {
            ftpResponse = new FtpResponse(550, "File not found: " + arguments);
        } catch (IOException e) {
            ftpResponse = new FtpResponse(451, "Error sending file: " + arguments);
        } finally {
            dataOutputStream.close();
            dataSocket.close();
        }

        return ftpResponse;
    }

    private static void sendFile(String filename, DataOutputStream dataOutputStream) throws IOException {
        File file = new File(filename);
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = fileIn.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytesRead);
        }

        fileIn.close();
    }
}
