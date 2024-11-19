package com.harvest.bagain.products;

import java.util.List;

import com.harvest.bagain.productsphoto.ProductPhoto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsAddReq {
    @NotNull(message = "상품명은 필수 입력 항목입니다.")
    private String name;
    
    @NotNull(message = "가격은 필수 입력 항목입니다.")
    @Positive(message = "가격은 숫자만 입력 가능합니다.")
    private Integer price;
    
    @NotNull(message = "재고는 필수 입력 항목입니다.")
    @Positive(message = "재고는 숫자만 입력 가능합니다.")
    private Integer inventory;
    
    private String comment;
    private String categoryName; 
    private String photoFilename;
    private String[] commentPhotoFilenames;
    private List<ProductPhoto> productPhotos;
}
