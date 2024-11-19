package com.harvest.bagain.category;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.harvest.bagain.products.Products;
import com.harvest.bagain.products.ProductsDAO;

@RestController

public class CategoryController {

	@Autowired
	private ProductsDAO productsDAO;

	// 카테고리 이름으로 상품 목록 조회
	@GetMapping("/category/{categoryName}")
	public ResponseEntity<List<Products>> getProductsByCategory(@PathVariable String categoryName) {
		List<Products> productsList = productsDAO.getProductsByCategoryName(categoryName);
		return ResponseEntity.ok(productsList);
	}

	// 카테고리 이름과 상품 코드로 단일 상품 조회
	@GetMapping("/{categoryName}/products/{pcode}")
	public ResponseEntity<Products> getProductByCategoryAndPcode(@PathVariable String categoryName,
			@PathVariable Integer pcode) {
		Optional<Products> product = productsDAO.getProductByCategoryAndPcode(categoryName, pcode);
		return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}