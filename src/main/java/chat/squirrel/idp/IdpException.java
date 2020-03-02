package chat.squirrel.idp;

import java.io.IOException;

public class IdpException extends IOException {
    private static final long serialVersionUID = 1L;

    public IdpException() {
        super();
    }

    public IdpException(String message) {
        super(message);
    }
}
