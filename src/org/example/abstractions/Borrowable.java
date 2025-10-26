package org.example.abstractions;

public interface Borrowable {
    void borrowItem();
    void returnItem();
    boolean isBorrowed();
    String getTitle();
}
