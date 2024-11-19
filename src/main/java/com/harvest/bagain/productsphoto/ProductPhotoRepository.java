package com.harvest.bagain.productsphoto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harvest.bagain.products.Products;

@Repository
public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer> {

	List<ProductPhoto> findByProduct(Products product);
}