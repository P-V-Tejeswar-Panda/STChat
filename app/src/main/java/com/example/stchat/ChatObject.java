package com.example.stchat;

class ChatObject {
    private String name;
    private String phoneNumber;
    private String chatID;

    public ChatObject(String chatID, String name, String phoneNumber) {
        this.chatID = chatID;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
