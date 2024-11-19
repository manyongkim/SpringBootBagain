package com.harvest.bagain.productsphoto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harvest.bagain.products.Products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "product_photos")
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Integer photoId;

    @ManyToOne
    @JoinColumn(name = "products_code")
    @JsonBackReference
    private Products product;

    @Column(name = "photo_url")
    private String photoUrl;

}
