package com.example.demo.demo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.Base64;

@Setter
@Getter
public class Ticket {
    // Геттеры и сеттеры
    private long lifetime;
    private LocalDateTime firstActivationDate;
    private LocalDateTime endingDate;
    private Long ownerId;
    private Long deviceId;
    private String digitalSignature;

    // Конструктор
    public Ticket(long lifetime, LocalDateTime firstActivationDate, LocalDateTime endingDate, Long ownerId, Long deviceId, String digitalSignature) {
        this.lifetime = lifetime;
        this.firstActivationDate = firstActivationDate;
        this.endingDate = endingDate;
        this.ownerId = ownerId;
        this.deviceId = deviceId;
        this.digitalSignature = digitalSignature;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "lifetime=" + lifetime +
                ", firstActivationDate=" + firstActivationDate +
                ", endingDate=" + endingDate +
                ", ownerId=" + ownerId +
                ", deviceId=" + deviceId +
                ", digitalSignature='" + digitalSignature + '\'' +
                '}';
    }
}