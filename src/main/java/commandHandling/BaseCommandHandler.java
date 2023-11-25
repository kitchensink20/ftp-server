package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public abstract class BaseCommandHandler {
    public final FtpResponse handleCommand(String arguments, User user) throws IOException {
        if(authorize(user))
            return executeCommand(arguments, user);
        else
            return new FtpResponse(550, "Permission denied");
    }

    protected abstract boolean authorize(User user);

    protected abstract FtpResponse executeCommand(String arguments, User user) throws IOException;
}
