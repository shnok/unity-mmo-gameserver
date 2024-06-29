package com.shnok.javaserver.enums;

public enum GameClientState {
    /** Client has just connected . */
    CONNECTED,
    /** Client has authed but doesn't have character attached to it yet. */
    AUTHED,
    /** Client has selected a character, but it hasn't joined the server yet. */
    JOINING,
    /** Client has selected a char and is in game. */
    IN_GAME
}