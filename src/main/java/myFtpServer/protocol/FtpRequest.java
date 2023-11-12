package myFtpServer.protocol;

public class FtpRequest {
    private final String command;
    private final String arguments;

    public FtpRequest(String userInput) {
        String[] commandParts = userInput.split(" ");
        this.command = commandParts[0].toUpperCase();
        this.arguments = commandParts.length > 1 ? userInput.substring(commandParts[0].length() + 1) : null;
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
}
