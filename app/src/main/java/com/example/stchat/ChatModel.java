package com.example.stchat;

import java.util.ArrayList;

class ChatModel {
    String text, sender, messageID;

    ArrayList<String> mediaUrlList;

    public ChatModel(String messageID, String text, String sender,     ArrayList<String> mediaUrlList) {
        this.messageID = messageID;
        this.text = text;
        this.sender = sender;
        this.mediaUrlList = mediaUrlList;
    }

    public String getText() {
        return text;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }

    public void setMediaUrlList(ArrayList<String> mediaUrlList) {
        this.mediaUrlList = mediaUrlList;
    }
}
