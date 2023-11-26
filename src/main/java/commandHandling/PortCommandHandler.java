package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;

public class PortCommandHandler extends BaseCommandHandler{
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
        String[] parts = arguments.split(",");
        String ipAddress = String.join(".", parts[0], parts[1], parts[2], parts[3]);
        int port = Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);

        return port;
    }
}
