package gameEngine.commands;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TestCommand implements Command {
    public interface Testeable {
        void test();
    }

    private Testeable receiver;

    public TestCommand(Testeable receiver){
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.test();
    }

    @Override
    public void unExecute() {
        throw new NotImplementedException();
    }
}
