package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.FileService;
import service.UserService;

import java.io.*;
import java.util.*;

public class DeleCommandHandler extends BaseCommandHandler{
    private final String currentDirPath;
    private final FileService fileService = FileService.getFileService();

    public DeleCommandHandler(String currentDirPath) {
        this.currentDirPath = currentDirPath;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null || arguments.isEmpty())
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        String fileName = getFileNameFromArgs(arguments);
        File fileToDelete;
        if(arguments.split("\\\\").length == 1)
            fileToDelete = new File(currentDirPath, arguments);
        else {
            fileToDelete = new File(arguments);
        }

        if(!fileToDelete.exists())
            return new FtpResponse(404, "File not found");

        if(fileToDelete.isDirectory())
            return new FtpResponse(550, "Permission denied");

        if (!fileService.fileBelongsToUser(user, fileName))
            return new FtpResponse(550, "Permission denied");

        boolean deletedSuccessfully = fileToDelete.delete();
        if(deletedSuccessfully){
            fileService.deleteFileByUserAndFileName(user, fileName);
            return new FtpResponse(204, "File deleted successfully");
        }
        else
            return new FtpResponse(409, "File could not be deleted");
    }

    private String getFileNameFromArgs(String arguments) {
        if(arguments.split("\\\\").length == 1)
            return arguments;

        String[] argumentsSplit = arguments.split("\\\\");
        return argumentsSplit[argumentsSplit.length - 1];
    }
}
