package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ListCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user, UI ui) throws IOException {
        // TO MODIFY (not working properly for now)
        Path currentDirectory = Paths.get(user.getHomeDirectory()).toAbsolutePath();
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(currentDirectory)) {
            for(Path file : stream)
                ui.displayDirectoryContent(String.valueOf(file.getFileName()));
            return new FtpResponse(226, "Transfer complete");
        } catch (IOException e) {
            return new FtpResponse(550, "Can't lust directory content");
        }
    }
}
