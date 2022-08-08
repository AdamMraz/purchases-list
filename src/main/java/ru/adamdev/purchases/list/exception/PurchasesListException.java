package ru.adamdev.purchases.list.exception;

import lombok.Getter;

public class PurchasesListException extends Exception {

    @Getter
    private final String type;

    public PurchasesListException(String type, String message) {
        super(message);
        this.type = type;
    }

    @Override
    public String toString() {
        return type + " - " + getMessage();
    }
}
