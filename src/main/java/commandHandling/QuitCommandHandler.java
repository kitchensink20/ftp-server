package commandHandling;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.AuthenticationService;
import service.SessionService;
import service.UserService;
import java.io.IOException;

public class QuitCommandHandler extends BaseCommandHandler{
    private final AuthenticationService authenticationService = new AuthenticationService(new UserService(), new SessionService());

    @Override
    protected boolean authorize(User user) {
        return true;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        authenticationService.signOut(user.getUsername());
        return new FtpResponse(221, "Service closing control connection");
    }
}
