package com.example.gooseapp.service;

public enum Codes {
    AREA_NOT_RESTRICED(101),
    AREA_RESTRICED(102),
    NOT_ALLOWED(201),
    MISSING_DEVICE(202),
    EXIT_MISSING_DEVICE(203),
    WRONG_DEVICE(301),
    HAS_RIGHT_DEVICES(302);

    private int value; //valore associato
    private Codes(int val){
        this.value = val;
    }
    public int getCodeValue(){
        return this.value;
    }
}
