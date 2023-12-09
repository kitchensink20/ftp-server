package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.Socket;

public class PortCommandHandler extends BaseCommandHandler {
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null || arguments.isEmpty())
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        int port = getPort(arguments);
        return new FtpResponse(229, " Entering Active Mode (|||" + port + "|)");
    }

    public Socket getDataSocket(String arguments) throws IOException {
        String ipAddress = getIpAddress(arguments);
        int port = getPort(arguments);
        return new Socket(ipAddress, port);
    }

    private int getPort(String arguments) {
        String[] parts = arguments.split(",");
        return Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]);
    }

    private String getIpAddress(String arguments) {
        String[] parts = arguments.split(",");
        return parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
    }
}
