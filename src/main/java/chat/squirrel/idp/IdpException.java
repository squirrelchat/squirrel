package chat.squirrel.idp;

import java.io.IOException;

public class IdpException extends IOException {
    public IdpException() {
        super();
    }

    public IdpException(String message) {
        super(message);
    }
}
