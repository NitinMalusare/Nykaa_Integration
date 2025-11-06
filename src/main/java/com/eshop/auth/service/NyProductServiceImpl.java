package com.eshop.auth.service;

import com.eshop.auth.dto.NyProductCreateRequestDto;
import com.eshop.auth.dto.NyProductDto;
import com.eshop.auth.dto.NyProductRequestDto;
import com.eshop.auth.dto.NyProductResponseDto;
import com.eshop.auth.entity.NyProduct;
import com.eshop.auth.repository.NyProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NyProductServiceImpl implements NyProductService {

    private static final Logger logger = LoggerFactory.getLogger(NyProductServiceImpl.class);

    @Autowired
    private NyProductRepository nyProductRepository;

    @Override
    @Transactional
    public NyProductResponseDto createProducts(NyProductCreateRequestDto requestDto, String token) {
        List<NyProductDto> createdProducts = new ArrayList<>();

        // Trim token to handle any whitespace issues
        if (token != null) {
            token = token.trim();
        }

        for (NyProductDto productDto : requestDto.getProducts()) {
            // Check if product already exists with same SKU and token
            Optional<NyProduct> existingProductOpt = nyProductRepository.findBySkuAndToken(productDto.getSku(), token);
            
            NyProduct product;
            boolean isUpdate = false;
            
            if (existingProductOpt.isPresent()) {
                // Product exists, update it
                product = existingProductOpt.get();
                isUpdate = true;
                logger.info("Product with sku='{}' and token='{}' already exists. Updating existing product (id={})", 
                        productDto.getSku(), token, product.getId());
                
                // Update all fields from DTO
                updateProductFromDto(product, productDto);
            } else {
                // Product doesn't exist, create new one
                product = convertToEntity(productDto);
                product.setToken(token);
                logger.info("Creating new product with sku='{}' and token='{}'", productDto.getSku(), token);
            }
            
            NyProduct savedProduct = nyProductRepository.save(product);
            logger.info("{} product with sku='{}' token='{}' id={}", 
                    isUpdate ? "Updated" : "Created", savedProduct.getSku(), token, savedProduct.getId());
            createdProducts.add(convertToDto(savedProduct));
        }

        NyProductResponseDto response = new NyProductResponseDto();
        response.setResponseCode(0);
        response.setResponseMessage("Success");
        response.setProductList(createdProducts);
        response.setToken(token);

        long totalForToken = nyProductRepository.countByToken(token);
        logger.info("After create/update: total products for token='{}' -> {}", token, totalForToken);

        return response;
    }

    @Override
    public NyProductResponseDto fetchProducts(NyProductRequestDto requestDto, String token) {
        List<NyProductDto> allProducts = new ArrayList<>();
        
        // Trim token to handle any whitespace issues
        if (token != null) {
            token = token.trim();
        }
        
        int pageNumber = requestDto.getPageNumber();
        int limit = requestDto.getLimit();

        logger.info("Fetch request received - token='{}' updatedDate='{}' skuCodes='{}' page={} limit={}",
                token, requestDto.getUpdatedDate(), requestDto.getSkuCode(), pageNumber, limit);

        long totalBefore = nyProductRepository.countByToken(token);
        logger.info("Before fetch: total products for token='{}' -> {}", token, totalBefore);
        
        if (totalBefore == 0) {
            logger.warn("No products found for token='{}'. Please verify products were created with this token.", token);
        }

        while (true) {
            PageRequest pageRequest = PageRequest.of(pageNumber - 1, limit);
            Page<NyProduct> productPage;

            if (requestDto.getSkuCode() != null && !requestDto.getSkuCode().isEmpty()) {
                if (requestDto.getUpdatedDate() != null && !requestDto.getUpdatedDate().isEmpty()) {
                    productPage = nyProductRepository.findByUpdatedDateAndSkuCodesAndToken(
                            requestDto.getUpdatedDate(),
                            requestDto.getSkuCode(),
                            token,
                            pageRequest);
                } else {
                    productPage = nyProductRepository.findBySkuCodesAndToken(
                            requestDto.getSkuCode(),
                            token,
                            pageRequest);
                }
            } else if (requestDto.getUpdatedDate() != null && !requestDto.getUpdatedDate().isEmpty()) {
                productPage = nyProductRepository.findByUpdatedDateAndToken(
                        requestDto.getUpdatedDate(),
                        token,
                        pageRequest);
            } else {
                productPage = nyProductRepository.findAllByToken(token, pageRequest);
            }

            List<NyProduct> products = productPage.getContent();
            if (products.isEmpty()) {
                break;
            }

            for (NyProduct product : products) {
                NyProductDto productDto = convertToDto(product);
                allProducts.add(productDto);
            }

            if (products.size() < limit) {
                break;
            }
            pageNumber++;
        }

        NyProductResponseDto response = new NyProductResponseDto();
        response.setResponseCode(0);
        response.setResponseMessage("Success");
        response.setProductList(allProducts);
        response.setToken(token);

        return response;
    }

    private NyProductDto convertToDto(NyProduct product) {
        NyProductDto dto = new NyProductDto();
        dto.setSku(product.getSku());
        dto.setVendorSkuCode(product.getVendorSkuCode());
        dto.setMfgSkuCode(product.getMfgSkuCode());
        dto.setSkuName(product.getSkuName());
        dto.setMrp(product.getMrp());
        dto.setSalePrice(product.getSalePrice());
        dto.setBaseCost(product.getBaseCost());
        dto.setStatus(product.getStatus());
        dto.setLastUpdateDate(product.getLastUpdateDate());
        dto.setBrand(product.getBrand());
        dto.setColor(product.getColor());
        dto.setSize(product.getSize());
        dto.setWeight(product.getWeight());
        dto.setLength(product.getLength());
        dto.setWidth(product.getWidth());
        dto.setHeight(product.getHeight());
        dto.setSerialTracking(product.getSerialTracking());
        dto.setSellerId(product.getSellerId());
        return dto;
    }

    private NyProduct convertToEntity(NyProductDto dto) {
        NyProduct product = new NyProduct();
        product.setSku(dto.getSku());
        product.setVendorSkuCode(dto.getVendorSkuCode());
        product.setMfgSkuCode(dto.getMfgSkuCode());
        product.setSkuName(dto.getSkuName());
        product.setMrp(dto.getMrp());
        product.setSalePrice(dto.getSalePrice());
        product.setBaseCost(dto.getBaseCost());
        product.setStatus(dto.getStatus());
        product.setLastUpdateDate(dto.getLastUpdateDate() != null ? dto.getLastUpdateDate() : 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        product.setBrand(dto.getBrand());
        product.setColor(dto.getColor());
        product.setSize(dto.getSize());
        product.setWeight(dto.getWeight());
        product.setLength(dto.getLength());
        product.setWidth(dto.getWidth());
        product.setHeight(dto.getHeight());
        product.setSerialTracking(dto.getSerialTracking());
        product.setSellerId(dto.getSellerId());
        return product;
    }

    /**
     * Updates an existing product entity with values from the DTO.
     * Preserves the ID and token of the existing product.
     */
    private void updateProductFromDto(NyProduct product, NyProductDto dto) {
        // Don't update SKU and token - they are the unique identifiers
        product.setVendorSkuCode(dto.getVendorSkuCode());
        product.setMfgSkuCode(dto.getMfgSkuCode());
        product.setSkuName(dto.getSkuName());
        product.setMrp(dto.getMrp());
        product.setSalePrice(dto.getSalePrice());
        product.setBaseCost(dto.getBaseCost());
        product.setStatus(dto.getStatus());
        // Update lastUpdateDate if provided, otherwise use current timestamp
        if (dto.getLastUpdateDate() != null && !dto.getLastUpdateDate().isEmpty()) {
            product.setLastUpdateDate(dto.getLastUpdateDate());
        } else {
            product.setLastUpdateDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }
        product.setBrand(dto.getBrand());
        product.setColor(dto.getColor());
        product.setSize(dto.getSize());
        product.setWeight(dto.getWeight());
        product.setLength(dto.getLength());
        product.setWidth(dto.getWidth());
        product.setHeight(dto.getHeight());
        product.setSerialTracking(dto.getSerialTracking());
        product.setSellerId(dto.getSellerId());
    }
}