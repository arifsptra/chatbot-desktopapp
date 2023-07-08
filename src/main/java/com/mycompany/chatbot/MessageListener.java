/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatbot;

/**
 *
 * @author arif
 */
public interface MessageListener {
    void onMessageReceived(String chatId, String username, String message);
}

