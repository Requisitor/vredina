package ru.mtuci.demo.demo;


import java.util.List;

public class ProductDto {
    private Long id;
    private String name;
    private Boolean isBlocked;
    private List<LicenseDto> licenses;


    public ProductDto() {
    }

    public ProductDto(Long id, String name, Boolean isBlocked, List<LicenseDto> licenses) {
        this.id = id;
        this.name = name;
        this.isBlocked = isBlocked;
        this.licenses = licenses;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public List<LicenseDto> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<LicenseDto> licenses) {
        this.licenses = licenses;
    }
}