package commandHandling;

import model.File;
import model.User;
import myFtpServer.protocol.FtpResponse;
import service.FileService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorCommandHandler extends BaseCommandHandler{
    private final ServerSocket dataServerSocket;
    private final FileService fileService = FileService.getFileService();

    public StorCommandHandler(ServerSocket dataServerSocket){
        this.dataServerSocket = dataServerSocket;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null || arguments.isEmpty())
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        String filePath = getFilePathFromArgs(arguments, user);

        System.out.println("ekf[wekfwe " + filePath);

        Socket dataSocket = dataServerSocket.accept();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        InputStream inputStream = dataSocket.getInputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, bytesRead);
        }

        bufferedOutputStream.close();
        dataSocket.close();
        dataServerSocket.close();

        createFileRecordInDb(user, filePath);

        return new FtpResponse(200, "File " + arguments + " was received successfully");
    }

    private String getFilePathFromArgs(String arguments, User user) {
        String[] argumentsSplit = arguments.split("\\\\");

        if(argumentsSplit.length == 1)
            return Paths.get(user.getHomeDirectory(), arguments).toAbsolutePath().toString();
        else
            return arguments;
    }

    private void createFileRecordInDb(User user, String filePath) {
        String[] pathSplit = filePath.split("\\\\");
        int lastOccuranceOfDelimiterIndx =  filePath.lastIndexOf("\\");

        String fileName = pathSplit[pathSplit.length - 1];
        String fileLocation = filePath.substring(0, lastOccuranceOfDelimiterIndx);

        fileService.createFile(new File(fileName, fileLocation, user));
    }
}
