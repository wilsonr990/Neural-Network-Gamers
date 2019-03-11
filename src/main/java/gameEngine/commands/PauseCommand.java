package gameEngine.commands;

public class PauseCommand implements Command {
    public interface Pausable {
        void pause();

        void unPause();
    }

    private Pausable receiver;

    public PauseCommand(Pausable receiver){
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.pause();
    }

    @Override
    public void unExecute() {
        receiver.unPause();
    }
}
