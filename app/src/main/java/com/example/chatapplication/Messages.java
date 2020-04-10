package com.example.chatapplication;

public class Messages {
    private String message;

    public Messages() {
    }
    private String type,to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public Messages(String message, String type, String to, String from, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.to = to;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private String from;

    private long time;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    private boolean seen;
}
