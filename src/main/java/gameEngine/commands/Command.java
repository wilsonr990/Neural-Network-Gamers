package gameEngine.commands;

public interface Command {
    void execute();

    void unExecute();

    enum Type{
        TEST_ONE,
        PAUSE,
        SHOW_STATS
    }
}
