/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
            
            // Simpan pesan ke dalam database
            saveMessageToDatabase(sChatId, username, text);
            
            boolean isRegistered = isUserRegistered(sChatId);
            if (!isRegistered) {
                if (text.equals("/daftar")) {
                    saveMemberData(sChatId, username);
                    String response = "Terima kasih telah mendaftar!";
                    sendResponse(chatId, response);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);
                } else {
                    String response = "Silakan daftar terlebih dahulu dengan menggunakan perintah /daftar";
                    sendResponse(chatId, response);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);
                }
            } else {
                if (text.equals("/cuaca")) {
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

                    List<InlineKeyboardButton> lists1 = new ArrayList<>();
                    List<InlineKeyboardButton> lists2 = new ArrayList<>();
                    List<InlineKeyboardButton> lists3 = new ArrayList<>();

                    
                    InlineKeyboardButton list1 = new InlineKeyboardButton();
                    InlineKeyboardButton list2 = new InlineKeyboardButton();
                    InlineKeyboardButton list3 = new InlineKeyboardButton();
                    InlineKeyboardButton list4 = new InlineKeyboardButton();
                    InlineKeyboardButton list5 = new InlineKeyboardButton();
                    InlineKeyboardButton list6 = new InlineKeyboardButton();
                    
                    list1.setText("Pati");
                    list1.setCallbackData("Pati");
                    
                    list2.setText("Rembang");
                    list2.setCallbackData("Rembang");
                    
                    list3.setText("Kudus");
                    list3.setCallbackData("Kudus");
                    
                    list4.setText("Demak");
                    list4.setCallbackData("Demak");
                    
                    list5.setText("Semarang");
                    list5.setCallbackData("Semarang");
                    
                    list6.setText("Cilacap");
                    list6.setCallbackData("Cilacap");
                    
                    lists1.add(list1);
                    lists1.add(list2);
                    lists2.add(list3);
                    lists2.add(list4);
                    lists3.add(list5);
                    lists3.add(list6);
                   
                    rowsInline.add(lists1);
                    rowsInline.add(lists2);
                    rowsInline.add(lists3);

                    markup.setKeyboard(rowsInline);
                    String response = "Pilih Kota: ";
                    sendResponseChoose(chatId, response, markup);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);                    
                } else if(text.equals("/berita")) {
                    String response = "Berita Hari ini gk ada!";
                    sendResponse(chatId, response);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);
                } else if(text.equals("/daftar")) {
                    String response = "Anda sudah terdaftar!";
                    sendResponse(chatId, response);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);
                }else {
                    String response = "Pesan lain yang diterima setelah pendaftaran.";
                    sendResponse(chatId, response);
                    saveMessageToDatabase("11520043", BOT_USERNAME, response);
                }
            }
        } else if(update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            
            Long chatId = message.getChatId();
            String sChatId = Long.toString(chatId);
            String username = message.getFrom().getUserName();
            
            saveMessageToDatabase(sChatId, username, data);
            
            if(data.equals("Pati")){
                sendResponseCuaca(chatId, data);
            } else if(data.equals("Rembang")) {
                sendResponseCuaca(chatId, data);

            } else if(data.equals("Kudus")) {
                sendResponseCuaca(chatId, data);

            } else if(data.equals("Demak")) {
                sendResponseCuaca(chatId, data);

            } else if(data.equals("Semarang")) {
                sendResponseCuaca(chatId, data);

            } else if(data.equals("Cilacap")) {
                sendResponseCuaca(chatId, data);

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
    
    private void saveMessageToDatabase(String chatId, String username, String text) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "INSERT INTO pesan (chat_id, username, pesan) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(chatId));
            statement.setString(2, username);
            statement.setString(3, text);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
    
    private void sendResponseChoose(Long chatId, String response, InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(response);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private void sendResponseCuaca(Long chatId, String kota) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        CuacaBMKG bmkg = new CuacaBMKG(kota);
        message.setText(bmkg.getData());
        
        saveMessageToDatabase("11520043", BOT_USERNAME, bmkg.getData());
        try {
            execute(message);
        } catch (TelegramApiException e) {
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
    
    void sendBroadcastTo(String username, String broadcast) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT chat_id FROM member WHERE username=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String sChatId = resultSet.getString("chat_id");
                long chatId = Long.parseLong(sChatId);
                sendResponse(chatId, broadcast);
            }
            saveMessageToDatabase("11520043", BOT_USERNAME, broadcast);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    void sendBroadcastAll(String broadcast) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT chat_id FROM member";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String sChatId = resultSet.getString("chat_id");
                long chatId = Long.parseLong(sChatId);
                sendResponse(chatId, broadcast);
            }
            saveMessageToDatabase("11520043", BOT_USERNAME, broadcast);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}