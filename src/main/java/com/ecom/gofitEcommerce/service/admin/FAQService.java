package com.ecom.gofitEcommerce.service.admin;

import com.ecom.gofitEcommerce.DTO.FAQDto;
import com.ecom.gofitEcommerce.entity.FAQ;
import com.ecom.gofitEcommerce.entity.Product;
import com.ecom.gofitEcommerce.repository.FAQRepository;
import com.ecom.gofitEcommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FAQService {

    private  final FAQRepository faqRepository;

    private final ProductRepository productRepository;

    @Transactional
    public FAQDto postFAQ(Long productId , FAQDto faqDto){
        Optional<Product> optionalproduct = productRepository.findById(productId);
        if(optionalproduct.isPresent()){
            FAQ faq = new FAQ();
            faq.setQuestion(faqDto.getQuestion());
            faq.setAnswer(faqDto.getAnswer());
            faq.setProduct(optionalproduct.get());

            return faqRepository.save(faq).getFAQDto();
        }
        return null;
    }


}
