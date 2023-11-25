package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public class ErptCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        return null;
    }
}
