package com.example.stchat.Notification;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message, String heading, String notificationKey){

        notificationKey = "c548bf03-fc83-4515-aecf-2bef2a477817";
        try {
            JSONObject notificationContent = new JSONObject("{'contents' : {'en' : '"+message + " '},"+
                    "'include_player_ids' : ['"+notificationKey+"'], "+
                    "'headings' : { 'en' : '"+heading+"'}}");
            OneSignal.postNotification(notificationContent, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
