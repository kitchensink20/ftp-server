package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;

public abstract class BaseCommandHandler {
    public final FtpResponse handleCommand(String arguments, User user, UI ui) throws IOException {
        if(authorize(user))
            return executeCommand(arguments, user, ui);
        else
            return new FtpResponse(550, "Permission denied");
    }

    protected abstract boolean authorize(User user);

    protected abstract FtpResponse executeCommand(String arguments, User user, UI ui) throws IOException;
}
