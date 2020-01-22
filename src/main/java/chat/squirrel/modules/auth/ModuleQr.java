package chat.squirrel.modules.auth;

import chat.squirrel.modules.AbstractModule;

public class ModuleQr extends AbstractModule {
    @Override
    public void initialize() {
        /*
         * QR Auth flow will probably use a websocket, using the following scheme:
         *  - Browser connects to the WS, and the WS issues a unique token, used for QR generation
         *  - Android/iOS app scans the QR code and POSTs it to the API
         *  - API informs the browser about the user who's trying to login through WS
         *  - Android does a 2nd POST to confirm the login
         *  - Desktop receives a new token
         *  - Flow complete.
         *
         * Each user can decide to disable QR code based login individually.
         * A QR code is only valid for 5 minutes and only once.
         */
    }
}
