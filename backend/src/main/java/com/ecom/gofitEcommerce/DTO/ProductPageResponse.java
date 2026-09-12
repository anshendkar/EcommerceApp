package com.ecom.gofitEcommerce.DTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPageResponse implements Serializable {

    private List<ProductDto> content;
    private int pageNumber;
    private int totalPages;
    private long totalElements;
}
