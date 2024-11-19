package com.harvest.bagain.products;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.harvest.bagain.users.Users;

@RestController
public class ProductsController {

	private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);
	@Autowired
	private ProductsDAO productsDAO;
	
	// 상품 등록
	@PostMapping("/mypage/userpage/productadd")
	public ResponseEntity<Map<String, Object>> addProduct(@Validated @ModelAttribute ProductsAddReq req,
			@RequestParam(required = false) MultipartFile photo,
			@RequestParam(required = false) MultipartFile[] commentphoto) {

		Map<String, Object> result = productsDAO.addProduct(req, photo, commentphoto);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/products/{productCode}/liked/{userCode}")
	public ResponseEntity<Map<String, Object>> toggleLikeProduct(
	        @PathVariable Integer productCode, 
	        @PathVariable Integer userCode) {
	    try {
	        Optional<Products> productOpt = productsDAO.getProductByCode(productCode);
	        Optional<Users> userOpt = productsDAO.getUserByCode(userCode);

	        if (productOpt.isEmpty()) {
	            return ResponseEntity.status(404).body(Map.of("status", false, "message", "유효하지 않은 상품 코드"));
	        }
	        if (userOpt.isEmpty()) {
	            return ResponseEntity.status(404).body(Map.of("status", false, "message", "유효하지 않은 사용자 코드"));
	        }

	        Map<String, Object> response = productsDAO.toggleLikeProduct(productOpt.get(), userOpt.get());
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("status", false, "message", "서버 내부 오류: " + e.getMessage()));
	    }
	}
}