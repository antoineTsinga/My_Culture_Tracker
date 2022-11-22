package com.example.my_culture_tracker.Model;

public class ListItem {
    private int id;
    private String code;
    private String type;

    public ListItem(int id, String code, String type) {
        this.id = id;
        this.code = code;
        this.type = type;
    }
    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

}
