package com.shnok.javaserver.model.network;

public class SessionKey {
    public int playOkID1;
    public int playOkID2;
    public int loginOkID1;
    public int loginOkID2;

    /**
     * Instantiates a new session key.
     *
     * @param loginOK1 the login o k1
     * @param loginOK2 the login o k2
     * @param playOK1  the play o k1
     * @param playOK2  the play o k2
     */
    public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2) {
        playOkID1 = playOK1;
        playOkID2 = playOK2;
        loginOkID1 = loginOK1;
        loginOkID2 = loginOK2;
    }

    @Override
    public String toString() {
        return "PlayOk: " + playOkID1 + " " + playOkID2 + " LoginOk:" + loginOkID1 + " " + loginOkID2;
    }
}
