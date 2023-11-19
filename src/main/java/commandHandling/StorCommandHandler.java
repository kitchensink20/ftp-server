package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;

public class StorCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        // TO DO
        return null;
    }
}
