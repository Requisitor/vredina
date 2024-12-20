package ru.mtuci.demo.demo;

public class DeviceDto {
    private String name;
    private String macAddress;
    private ApplicationUser user; // Добавлено поле user

    // Геттеры
    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public ApplicationUser getUser() {
        return user;
    }

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setUser(ApplicationUser user) {
        this.user = user;
    }
}