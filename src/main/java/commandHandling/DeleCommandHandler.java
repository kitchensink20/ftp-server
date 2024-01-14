package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.FileService;
import visitor.DeleteVisitor;

import java.io.*;
import java.nio.file.Paths;

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

        String filePath = getFilePathFromArgs(arguments);
        File fileToDelete = new File(filePath);

        if(!fileToDelete.exists())
            return new FtpResponse(404, "File not found");

        if(fileToDelete.isDirectory())
            return new FtpResponse(550, "Permission denied");

        boolean deletedSuccessfully = fileToDelete.delete();
        if(deletedSuccessfully){
            model.File fileModel = fileService.getFileByUserAndFileName(user, fileToDelete.getName());
            fileModel.accept(new DeleteVisitor());
            return new FtpResponse(204, "File deleted successfully");
        }
        else
            return new FtpResponse(409, "File could not be deleted");
    }

    private String getFilePathFromArgs(String arguments) {
        String[] argumentsSplit = arguments.split("\\\\");

        if(argumentsSplit.length == 1)
            return Paths.get(currentDirPath, arguments).toAbsolutePath().toString();
        else
            return arguments;
    }
}
