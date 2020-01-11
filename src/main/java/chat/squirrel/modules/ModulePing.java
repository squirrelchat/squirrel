package chat.squirrel.modules;

import chat.squirrel.SquirrelModule;
import chat.squirrel.SquirrelServer;
import chat.squirrel.SquirrelVersion;

public class ModulePing extends SquirrelModule {

    public ModulePing(SquirrelServer mainInstance) {
        super(mainInstance);
    }

    @Override
    public void setupRoutes() {
        mainInstance.getRouter().get("/squirrelPing").handler(r -> {
            r.response().end("Squirrel " + SquirrelVersion.VERSION);
        });
    }

}
