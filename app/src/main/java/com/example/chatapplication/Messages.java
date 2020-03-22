package com.example.chatapplication;

public class Messages {
    public Messages() {
    }

    public Messages(String message, String seen, String time, String type) {
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
    }

    private String message;

    public String getMessages() {
        return message;
    }

    public void setMessages(String messages) {
        this.message = messages;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String seen;
    private String time;
    private String type;
}
