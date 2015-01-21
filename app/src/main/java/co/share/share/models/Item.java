package co.share.share.models;

import android.media.Image;

import java.util.List;

public class Item {

    private String title;
    private String description;
    private Image image;
    private List<Transaction> itemTransactions;
    private Transaction itemCurrentTransaction;

    public Item() {

    }
}
