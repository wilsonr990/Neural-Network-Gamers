package gameEngine.commands;

public class ShowCommand implements Command {
    public interface Showable {
        void show();

        void unShow();
    }

    private Showable receiver;

    public ShowCommand(Showable receiver){
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.show();
    }

    @Override
    public void unExecute() {
        receiver.unShow();
    }
}
