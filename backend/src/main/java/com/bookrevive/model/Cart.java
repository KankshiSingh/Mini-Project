package com.bookrevive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class Cart {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private List<CartItem> buyItems = new ArrayList<>();      // Books to purchase
    private List<CartItem> donateItems = new ArrayList<>();   // Books to receive as donations

    private Double buyTotal = 0.0;
    private Integer itemCount = 0;

    private LocalDateTime updatedAt;
}
