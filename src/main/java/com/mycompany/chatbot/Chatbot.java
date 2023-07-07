/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 *
 * @author arif
 */
public class Chatbot extends TelegramLongPollingBot {

    // Telegram Bot Credentials
    private static final String BOT_TOKEN = "6395733810:AAHocLQFZH_aFhjUhg2XZOZ1X4Zvf-85JlQ";
    private static final String BOT_USERNAME = "arifsaputra_bot";
    
    // Database Credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_chatbot";
    private static final String DB_USERNAME = "arif";
    private static final String DB_PASSWORD = "Koentj1@$";
    
    // Flag to track registration status
    private static final String REGISTERED_FLAG = "REGISTERED";
    
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String sChatId = Long.toString(chatId);
            String text = message.getText();
            String username = message.getFrom().getUserName();

            System.out.println(sChatId);
            System.out.println(text);
            System.out.println(username);

            saveUserData(sChatId, username);
            
            boolean isRegistered = isUserRegistered(sChatId);
            if (!isRegistered) {
                if (text.equals("/daftar")) {
                    saveMemberData(sChatId, username);
                    sendResponse(chatId, "Terima kasih telah mendaftar!");
                } else {
                    sendResponse(chatId, "Silakan daftar terlebih dahulu dengan menggunakan perintah /daftar");
                }
            } else {
                if (text.equals("/cuaca")) {
                    sendResponse(chatId, "Cuaca Hari ini Dingin!");
                } else {
                    sendResponse(chatId, "Pesan lain yang diterima setelah pendaftaran.");
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
    
    private boolean isUserRegistered(String chatId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT * FROM member WHERE chat_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            boolean isRegistered = resultSet.next();

            resultSet.close();
            statement.close();

            return isRegistered;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void saveUserData(String chatId, String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO user (chat_id, username) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(chatId)); // Convert chatId to String
            statement.setString(2, username);

            // Check if the user already exists in the database
            String checkUserExistsSql = "SELECT * FROM user WHERE chat_id = ?";
            PreparedStatement checkUserExistsStatement = connection.prepareStatement(checkUserExistsSql);
            checkUserExistsStatement.setString(1, String.valueOf(chatId)); // Convert chatId to String
            ResultSet resultSet = checkUserExistsStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("User already exists in the database.");
            } else {
                // User does not exist, save the new user data
                statement.executeUpdate();
                System.out.println("New user data saved to the database.");
            }

            resultSet.close();
            checkUserExistsStatement.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void saveMemberData(String chatId, String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO member (chat_id, username) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(chatId)); // Convert chatId to String
            statement.setString(2, username);

            // Check if the user already exists in the database
            String checkUserExistsSql = "SELECT * FROM member WHERE chat_id = ?";
            PreparedStatement checkUserExistsStatement = connection.prepareStatement(checkUserExistsSql);
            checkUserExistsStatement.setString(1, String.valueOf(chatId)); // Convert chatId to String
            ResultSet resultSet = checkUserExistsStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Member already exists in the database.");
            } else {
                // User does not exist, save the new user data
                statement.executeUpdate();
                System.out.println("New member data saved to the database.");
            }

            resultSet.close();
            checkUserExistsStatement.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(Long chatId, String response) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    void sendBroadcastAll(String broadcast) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT chat_id FROM member";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String chatId = resultSet.getString("chat_id");
                SendMessage sendMessage;
                sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(broadcast);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}