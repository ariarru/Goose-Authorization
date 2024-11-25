package com.example.gooseapp.service;

public enum Codes {
    NEED_CHECK_BLE(10),
    AREA_NOT_RESTRICED(21),
    AREA_RESTRICED(22),
    NOT_ALLOWED(23),
    MISSING_DEVICE(30),
    EXIT_MISSING_DEVICE(31),
    WRONG_DEVICE(32),
    HAS_RIGHT_DEVICES(33);



    private int value; //valore associato
    private Codes(int val){
        this.value = val;
    }
    public int getCodeValue(){
        return this.value;
    }

    //USA NUMERI E BASTA
}
