package com.harvest.bagain.liked;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.harvest.bagain.products.Products;
import com.harvest.bagain.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "liked")
public class Liked {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "liked_no")
    private Integer likedNo;

    @ManyToOne
    @JoinColumn(name = "products_code", referencedColumnName = "products_code")
    @JsonBackReference
    private Products product;


    @ManyToOne
    @JoinColumn(name = "users_code", referencedColumnName = "users_code")
    private Users user;

    @Column(name = "liked_status")
    private Boolean likedStatus = false;
}
