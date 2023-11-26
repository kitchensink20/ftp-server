package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public class EprtCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override // !!! do not work for now
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null || arguments.isEmpty())
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        int port = getPort(arguments);
        return new FtpResponse(229, " Entering Active Mode (|||" + port + "|)");
    }

    private static int getPort(String arguments) {
        String[] parts = arguments.split("\\|");
        String ipAddress = parts[1];
        int port = Integer.parseInt(parts[2]);
        return port;
    }
}
