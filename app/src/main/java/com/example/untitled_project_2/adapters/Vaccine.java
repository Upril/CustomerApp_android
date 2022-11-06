package com.example.untitled_project_2.adapters;

public class Vaccine {
    private String Id;
    private String VaccineName;
    private String FacilityName;
    private String Address;
    private String Date;
    private Integer VacStatus;

    public String getVaccineName() {
        return VaccineName;
    }

    public void setVaccineName(String vaccineName) {
        VaccineName = vaccineName;
    }

    public String getFacilityName() {
        return FacilityName;
    }

    public void setFacilityName(String facilityName) {
        FacilityName = facilityName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Integer getVacStatus() {
        return VacStatus;
    }

    public void setVacStatus(Integer vacStatus) {
        VacStatus = vacStatus;
    }
}
