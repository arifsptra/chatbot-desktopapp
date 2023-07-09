/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatbot;

/**
 *
 * @author arif
 */
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class CuacaBMKG {
    String cityName; // Ganti dengan nama kota yang diinginkan
    String temperature;
    String humidity;
    
    public CuacaBMKG(String kota) {
        cityName = kota;
        cuaca();
    }
    
    public void cuaca() {
        try {
            // URL dari API BMKG
            String url = "https://data.bmkg.go.id/DataMKG/MEWS/DigitalForecast/DigitalForecast-JawaTengah.xml";

            // Membuat objek URL dari string URL
            URL obj = new URL(url);

            // Parsing respons XML menjadi objek Document
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(obj.openStream());

            // Mendapatkan elemen area berdasarkan nama kota yang diinginkan
            
            NodeList areaList = doc.getElementsByTagName("area");
            Element areaElement = null;

            for (int i = 0; i < areaList.getLength(); i++) {
                Element tempElement = (Element) areaList.item(i);
                String tempCityName = tempElement.getElementsByTagName("name").item(0).getTextContent();

                if (tempCityName.equals(cityName)) {
                    areaElement = tempElement;
                    break;
                }
            }

            // Mendapatkan data cuaca dari elemen area yang dipilih hanya untuk hari ini
            if (areaElement != null) {
                NodeList weatherList = areaElement.getElementsByTagName("parameter");
                for (int i = 0; i < weatherList.getLength(); i++) {
                    Element weatherElement = (Element) weatherList.item(i);
                    String description = weatherElement.getAttribute("description");

                    if (description.equals("Temperature")) {
                        NodeList timeRangeList = weatherElement.getElementsByTagName("timerange");
                        for (int j = 0; j < timeRangeList.getLength(); j++) {
                            Element timeRangeElement = (Element) timeRangeList.item(j);
                            String dateTime = timeRangeElement.getAttribute("datetime");
                            String date = dateTime.substring(0, 8); // Mendapatkan tanggal dari datetime
                            
                            LocalDate currentDate = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                            String currentDateString = currentDate.format(formatter);

                            if (date.equals(currentDateString)) {
                                temperature = timeRangeElement.getElementsByTagName("value").item(0).getTextContent();
                                String unit = timeRangeElement.getElementsByTagName("value").item(0).getAttributes().getNamedItem("unit").getNodeValue();

                                temperature = "Suhu: " + temperature + " °" + unit;
                            }
                        }
                    } else if (description.equals("Humidity")) {
                        NodeList timeRangeList = weatherElement.getElementsByTagName("timerange");
                        for (int j = 0; j < timeRangeList.getLength(); j++) {
                            Element timeRangeElement = (Element) timeRangeList.item(j);
                            String dateTime = timeRangeElement.getAttribute("datetime");
                            String date = dateTime.substring(0, 8); // Mendapatkan tanggal dari datetime
                            
                            LocalDate currentDate = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                            String currentDateString = currentDate.format(formatter);

                            if (date.equals(currentDateString)) {
                                humidity = timeRangeElement.getElementsByTagName("value").item(0).getTextContent();
                                String unit = timeRangeElement.getElementsByTagName("value").item(0).getAttributes().getNamedItem("unit").getNodeValue();

                                humidity = "Kelembapan: " + humidity + " " + unit;
                            }
                        }
                    }
                }
            } else {
                System.out.println("Kota tidak ditemukan.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getData() {
        return temperature + ", " + humidity; 
    }
}

