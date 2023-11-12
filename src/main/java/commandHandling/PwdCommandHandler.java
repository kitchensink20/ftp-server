package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;

public class PwdCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user, UI ui) throws IOException {
        // TO MODIFY (not working properly for now)
        return new FtpResponse(257, user.getHomeDirectory() + "\\ is the current directory");
    }
}
