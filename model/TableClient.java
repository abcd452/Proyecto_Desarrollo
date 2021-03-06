package model;

import utilities.ProjectUtilities;

public class TableClient {

    private String fullName;
    private String documentNumber;
    private long phoneNumber;
    private String date;
    private String value;

    public TableClient(String fullName, String documentNumber, long phoneNumber, String date) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.documentNumber = documentNumber;
        this.date = date;
    }

    public TableClient(String fullName, String documentNumber, double value) {
        this.fullName = fullName;
        this.documentNumber = documentNumber;
        this.value = ProjectUtilities.conversion(value);
    }

    public String getFullName() {
        return fullName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }
}
