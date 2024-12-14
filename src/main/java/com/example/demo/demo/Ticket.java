package com.example.demo.demo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.Base64;

public class Ticket implements Serializable {

    @Setter
    @Getter
    private LocalDateTime serverTime;
    @Setter
    @Getter
    private long ticketLifetime; // in seconds
    @Setter
    @Getter
    private LocalDateTime firstActivationDate;
    @Setter
    @Getter
    private LocalDateTime endingDate;
    @Setter
    @Getter
    private Long userId;
    @Setter
    @Getter
    private Long deviceId;
    private boolean isBlocked;
    @Setter
    @Getter
    private String digitalSignature;

    // Конструктор
    public Ticket(long ticketLifetime, LocalDateTime firstActivationDate, LocalDateTime endingDate, Long userId, Long deviceId) {
        this.serverTime = LocalDateTime.now();
        this.ticketLifetime = ticketLifetime;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isBlocked = false;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    // Метод для генерации цифровой подписи
    public void generateDigitalSignature(PrivateKey privateKey) throws Exception {
        String data = serverTime + String.valueOf(ticketLifetime) + firstActivationDate + endingDate + userId + deviceId + isBlocked;
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = signature.sign();
        this.digitalSignature = Base64.getEncoder().encodeToString(signatureBytes);
    }

    // Метод для проверки цифровой подписи
    public boolean verifyDigitalSignature(PublicKey publicKey) throws Exception {
        String data = serverTime + String.valueOf(ticketLifetime) + firstActivationDate + endingDate + userId + deviceId + isBlocked;
        byte[] signatureBytes = Base64.getDecoder().decode(this.digitalSignature);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes("UTF-8"));
        return signature.verify(signatureBytes);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "serverTime=" + serverTime +
                ", ticketLifetime=" + ticketLifetime +
                ", firstActivationDate=" + firstActivationDate +
                ", endingDate=" + endingDate +
                ", userId=" + userId +
                ", deviceId=" + deviceId +
                ", isBlocked=" + isBlocked +
                ", digitalSignature='" + digitalSignature + '\'' +
                '}';
    }
}