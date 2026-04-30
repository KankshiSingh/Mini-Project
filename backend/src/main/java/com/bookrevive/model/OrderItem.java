package com.bookrevive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String bookId;
    private String title;
    private String author;
    private Double price;
    private String type;      // SELL or DONATE
    private String category;
    private String condition;
    private Integer quantity;
    private Double subtotal;
}
