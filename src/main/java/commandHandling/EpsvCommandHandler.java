package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.net.ServerSocket;

public class EpsvCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user, UI ui) throws IOException {
        ServerSocket passiveSocket = new ServerSocket(0);
        int dataPort = passiveSocket.getLocalPort();
        return new FtpResponse(229, " Entering Extended Passive Mode (|||" + dataPort + "|)");
    }
}
