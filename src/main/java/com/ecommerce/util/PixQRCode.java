package com.ecommerce.util;

import java.math.BigDecimal;

public class PixQRCode {
    
    public static String generatePixPayload(BigDecimal valor, String chavePix) {
        StringBuilder payload = new StringBuilder();
        
        // Payload Format Indicator
        payload.append("00020126");
        
        // Merchant Account Information
        String merchantInfo = "0014BR.GOV.BCB.PIX01" + String.format("%02d", chavePix.length()) + chavePix;
        payload.append("26").append(String.format("%02d", merchantInfo.length())).append(merchantInfo);
        
        // Merchant Category Code
        payload.append("52040000");
        
        // Transaction Currency (BRL)
        payload.append("5303986");
        
        // Transaction Amount
        String valorStr = valor.setScale(2).toString();
        payload.append("54").append(String.format("%02d", valorStr.length())).append(valorStr);
        
        // Country Code
        payload.append("5802BR");
        
        // Merchant Name
        String merchantName = "E-COMMERCE LTDA";
        payload.append("59").append(String.format("%02d", merchantName.length())).append(merchantName);
        
        // Merchant City
        String city = "SAO PAULO";
        payload.append("60").append(String.format("%02d", city.length())).append(city);
        
        // Additional Data Field Template
        payload.append("62070503***");
        
        // CRC16
        payload.append("6304");
        String crc = calculateCRC16(payload.toString());
        payload.append(crc);
        
        return payload.toString();
    }
    
    private static String calculateCRC16(String data) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;
        
        byte[] bytes = data.getBytes();
        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        
        return String.format("%04X", crc);
    }
}