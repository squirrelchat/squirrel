package chat.squirrel;

public class SquirrelVersion {
    public static final String VERSION_MAJOR;
    public static final String VERSION_MINOR;
    public static final String VERSION_REVISION;

    public static final String VERSION;
    public static final String COMMIT;

    static {
	VERSION_MAJOR = "@VERSION_MAJOR@";
	VERSION_MINOR = "@VERSION_MINOR@";
	VERSION_REVISION = "@VERSION_REVISION@";
	COMMIT = "@COMMIT@";

	VERSION = VERSION_MAJOR.startsWith("@") ? "indev"
		: VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION;
    }
}
