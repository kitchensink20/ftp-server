package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class EprtCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments == null || arguments.isEmpty())
            return new FtpResponse(501, "Syntax error in parameters or arguments");

        if(!getProtocol(arguments).equals("1") && !getProtocol(arguments).equals("2"))
            return new FtpResponse(522, "Protocol not supported");

        int port = getPort(arguments);
        return new FtpResponse(229, " Entering Active Mode (|||" + port + "|)");
    }

    private int getPort(String arguments) {
        String[] parts = arguments.split("\\|");
        return Integer.parseInt(parts[3]);
    }

    private String getProtocol(String arguments) {
        String[] parts = arguments.split("\\|");
        return parts[1];
    }

    private String getIpAddress(String arguments) {
        String[] parts = arguments.split("\\|");
        return parts[2];
    }

    public Socket getDataSocket(String arguments) throws IOException {
        String protocol = getProtocol(arguments);
        String ipAddress = getIpAddress(arguments);
        int port = getPort(arguments);
        Socket dataSocket;

        if (protocol.equals("1"))
            dataSocket = new Socket(ipAddress, port);
        else if (protocol.equals("2"))
            dataSocket = new Socket(InetAddress.getByName(ipAddress), port);
        else
            return null;

        return dataSocket;
    }
}
