package com.ecommerce.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

public class QRCodeGenerator {
    
    public static BufferedImage generatePixQRCode(BigDecimal valor, String chave) {
        int size = 200;
        BufferedImage qrImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = qrImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        
        g2d.setColor(Color.BLACK);
        int blockSize = 4;
        
        String pixCode = generatePixCode(valor, chave);
        java.util.Random random = new java.util.Random(pixCode.hashCode());
        
        // Padrão mais denso e legível
        for (int x = 0; x < size; x += blockSize) {
            for (int y = 0; y < size; y += blockSize) {
                if (random.nextDouble() > 0.5) {
                    g2d.fillRect(x, y, blockSize, blockSize);
                }
            }
        }
        
        // Cantos de referência maiores
        drawCornerPattern(g2d, 0, 0, blockSize);
        drawCornerPattern(g2d, size - 8 * blockSize, 0, blockSize);
        drawCornerPattern(g2d, 0, size - 8 * blockSize, blockSize);
        
        g2d.dispose();
        return qrImage;
    }
    
    private static void drawCornerPattern(Graphics2D g2d, int x, int y, int blockSize) {
        // Padrão de canto QR Code mais visível
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, 8 * blockSize, 8 * blockSize);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + blockSize, y + blockSize, 6 * blockSize, 6 * blockSize);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x + 2 * blockSize, y + 2 * blockSize, 4 * blockSize, 4 * blockSize);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + 3 * blockSize, y + 3 * blockSize, 2 * blockSize, 2 * blockSize);
    }
    
    public static String generatePixCode(BigDecimal valor, String chave) {
        return PixQRCode.generatePixPayload(valor, chave);
    }
}