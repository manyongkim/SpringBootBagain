package com.harvest.bagain.products;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.harvest.bagain.bucket.Bucket;
import com.harvest.bagain.category.Category;
import com.harvest.bagain.liked.Liked;
import com.harvest.bagain.productsphoto.ProductPhoto;
import com.harvest.bagain.reviews.Reviews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "products")
public class Products {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "products_code")
    private Integer pcode;

    @Column(name = "products_name")
    private String name;

    @Column(name = "products_price")
    private Integer price;

    @Column(name = "products_comment")
    private String comment;

    @Column(name = "products_inventory")
    private Integer inventory;

    @Column(name = "products_flag")
    private Byte flag;

    @Column(name = "products_likes_count", nullable = false)
    private Integer likesCount = 0;
    
    @Column(name = "products_photo")
    private String photo;
    
    @CreationTimestamp
    @Column(name = "products_createat")
    private java.sql.Timestamp createAt;

    @UpdateTimestamp
    @Column(name = "products_updateat")
    private java.sql.Timestamp updateAt;
    
    @ManyToOne
    @JoinColumn(name = "category_code", referencedColumnName = "category_code")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Bucket> buckets;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Liked> likedUsers;


    @OneToMany(mappedBy = "product")
    private List<Reviews> reviews;
    
    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductPhoto> productPhotos;
}
