package myFtpServer;

import controller.FtpServerController;
import logger.Logger;
import model.User;
import myFtpServer.ftpServerStates.FtpServerState;
import myFtpServer.ftpServerStates.NotLoggedInServerState;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpServer {
    private static final int PORT = 21;
    private static final int MAX_CONNECTION_NUM = 10;
    private FtpServerController controller;
    private UI ui;
    private Logger logger;
    private User currentUser;
    private FtpServerState serverState;

    public FtpServer() throws IOException {
        logger = new Logger();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("FTP server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ui = new UI(clientSocket.getInputStream(), clientSocket.getOutputStream());
                controller = new FtpServerController(ui);
                serverState = new NotLoggedInServerState(this, clientSocket);
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            if(!controller.newUserCanConnect(MAX_CONNECTION_NUM)){
                ui.displayFtpResponse(new FtpResponse(421, "Connection limit reached, please try again later"));
                return;
            }

            ui.displayFtpResponse(new FtpResponse(220, "Successfully connected"));

            currentUser = new User();
            while(true) {
                String userInput = ui.acceptUserInput();
                FtpRequest ftpRequest = new FtpRequest(userInput);
                FtpResponse ftpResponse = handleCommands(currentUser, ftpRequest);
                ui.displayFtpResponse(ftpResponse);
                if(ftpResponse.getStatusCode() == 221)
                    break;
            }
            System.out.println(currentUser.getUsername());
        } catch (IOException e) {
            logger.writeErrorEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername(), e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(currentUser != null) {
                    System.out.println(currentUser.getUsername());
                    controller.deactivateSessionIfActive(currentUser.getUserId());
                    logger.writeLogOutEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername());
                }
                clientSocket.close();
                ui.closeStreams();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FtpResponse handleCommands(User currentUser, FtpRequest ftpRequest) throws IOException {
        return serverState.handleCommand(currentUser, ftpRequest);
    }

    public void setState(FtpServerState ftpServerState) {
        this.serverState = ftpServerState;
    }

    public Logger getLogger() {
        return logger;
    }

    public FtpServerController getController() {
        return controller;
    }
}
