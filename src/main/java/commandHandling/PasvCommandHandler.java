package commandHandling;

import com.mysql.cj.xdevapi.Client;
import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PasvCommandHandler extends BaseCommandHandler{
    private final Socket clientSocket;
    private ServerSocket passiveSocket;

    public PasvCommandHandler(Socket clientSocket, ServerSocket passiveSocket) {
        this.clientSocket = clientSocket;
        this.passiveSocket = passiveSocket;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        int passivePort = passiveSocket.getLocalPort();

        InetAddress inetAddress = clientSocket.getLocalAddress();
        String ip = inetAddress.getHostAddress().replace('.', ',');

        int p1 = passivePort / 256;
        int p2 = passivePort % 256;

        return new FtpResponse(227, "Entering Passive Mode (" + ip + "," + p1 + "," + p2 + ")");
    }
}
