/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 *
 * @author arif
 */
public class Chatbot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "6395733810:AAHocLQFZH_aFhjUhg2XZOZ1X4Zvf-85JlQ";

    @Override
    public void onUpdateReceived(Update update) {
        String message=update.getMessage().getText();
        System.out.println(message);
    }

    @Override
    public String getBotUsername() {
        return "arifsaputra_bot";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}