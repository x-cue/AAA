package com.xcue.lib.events.chat;

public class PlayerMessage {
    private final String server;
    private final String sender;
    private final String msg;

    public PlayerMessage(String server, String sender, String msg) {
        this.server = server;
        this.sender = sender;
        this.msg = msg;
    }

    /**
     *
     * @return What server the sender was on
     */
    public String getServer() {
        return this.server;
    }

    /**
     *
     * @return Who sent the message
     */
    public String getSender() {
        return this.sender;
    }

    /**
     *
     * @return The message
     */
    public String getMessage() {
        return this.msg;
    }
}
