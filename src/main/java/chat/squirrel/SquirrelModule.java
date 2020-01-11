package chat.squirrel;

public abstract class SquirrelModule {
    protected SquirrelServer mainInstance;

    public SquirrelModule(SquirrelServer mainInstance) {
	this.mainInstance = mainInstance;
    }
    
    public abstract void setupRoutes();
}
