package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CdupCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    // TO MODIFY (not working properly for now)
    @Override
    protected FtpResponse executeCommand(String arguments, User user, UI ui) throws IOException {
        Path currentDirectory = Paths.get(user.getHomeDirectory()).toAbsolutePath();
        Path parentDirectory = currentDirectory.getParent();
        if(parentDirectory != null) {
            currentDirectory = parentDirectory;
            return new FtpResponse(200, "Changed to parent directory");
        } else
            return new FtpResponse(550, "Failed to change directory: No parent directory");
    }
}
