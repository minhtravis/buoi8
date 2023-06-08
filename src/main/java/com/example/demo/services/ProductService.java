package com.example.demo.services;

import com.example.demo.entity.Product;
import com.example.demo.repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private IProductRepository productsRepository;

    public List<Product> getAll()
    {
        return productsRepository.findAll();
    }

    public Product getProductById(Long id){
        Optional<Product> optional = productsRepository.findById(id);
        return optional.orElse(null);
    }
    public void addProduct(Product products){
        productsRepository.save(products);
    }
    public void update(long id, Product editProduct){
        Product findProduct=getProductById(id);
        if(findProduct!=null){
            productsRepository.delete(findProduct);
            productsRepository.save(editProduct);
        }
    }
    public void update(Product editProduct) {
        Optional<Product> optional = productsRepository.findById(editProduct.getId());
        if (optional.isPresent()) {
            Product existingProduct = optional.get();
            existingProduct.setName(editProduct.getName());
            existingProduct.setPrice(editProduct.getPrice());
            existingProduct.setImage(editProduct.getImage());
            existingProduct.setCategory(editProduct.getCategory());
            productsRepository.save(existingProduct); // Cập nhật thông tin sách trong cơ sở dữ liệu
        }
    }
    public void delete(long  id){
        Product findProduct=getProductById(id);
        if(findProduct!=null){
            productsRepository.delete(findProduct);
        }
    }
    public List<Product> search(String name) {
        if (name.isBlank()) {
            return productsRepository.findAll(); // Trả về tất cả sản phẩm nếu không có tên được cung cấp
        }
        return productsRepository.findByNameContaining(name);
    }

}
