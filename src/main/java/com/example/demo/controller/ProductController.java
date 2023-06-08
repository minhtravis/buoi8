package com.example.demo.controller;


import com.example.demo.entity.Item;
import com.example.demo.entity.Product;
import com.example.demo.services.CartService;
import com.example.demo.services.CategoryService;
import com.example.demo.services.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestMapping("/products")
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @Autowired
    public CategoryService categoryService;
    @GetMapping("")
    public String GetListBooks(Model model){
        model.addAttribute("list",productService.getAll());
        model.addAttribute("title","Product List");
        return "product/list";
    }
    /*@GetMapping("/search")
    public String searchProduct(@RequestParam("input") String input, Model model) {
        List<Product> searchResults = productService.search(input);
        model.addAttribute("list", searchResults);
        model.addAttribute("title", "Product " + input);
        return "product/list";
    }*/

    @GetMapping("/search")
    public String search(@RequestParam("searchText") String searchText,Model model) {
        List<Product> products = productService.getAll();
        List<Product> filteredproducts= new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            filteredproducts = products.stream()
                    .filter(product -> product.getName().contains(searchText))
                    .collect(Collectors.toList());
        }
        model.addAttribute("products", filteredproducts);
        return "product/list";
    }
    @GetMapping("/add")
    public String addProduct(Model model){
        model.addAttribute("product",new Product());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("title","Add Product");
        return "product/add";
    }
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") @Valid Product product , BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        // Kiểm tra ràng buộc và đặt thông báo lỗi vào BindingResult
        // @Null @RequestParam MultipartFile imageProduct
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategory());
            return "product/add";
        } else {
            // Nếu không có lỗi, thêm  và chuyển hướng đến trang danh sách sách
            product.setId(Init());
            if(imageProduct != null && imageProduct.getSize()>0)
            {
                try{
                    File saveFile = new ClassPathResource("static/images").getFile();
                    String newImageFile = UUID.randomUUID() + ".png";
                    java.nio.file.Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                    Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    product.setImage(newImageFile);
                }catch (Exception ex){
                    ex.printStackTrace();
                    return "product/add";
                }
            }
            productService.addProduct(product);
            return "redirect:/products";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        Product editproduct = productService.getProductById(id);
        if (editproduct != null) {
            model.addAttribute("product", editproduct);
            model.addAttribute("categories", categoryService.getAllCategory());
            return "product/edit";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, @ModelAttribute("product") @Valid Product editproduct, @RequestParam MultipartFile imageProduct, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategory());
            return "product/edit";
        } else {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct != null) {
                existingProduct.setName(editproduct.getName());
                if(imageProduct != null && imageProduct.getSize()>0)
                {
                    try{
                        File saveFile = new ClassPathResource("static/images").getFile();
                        String newImageFile = UUID.randomUUID() + ".png";
                        java.nio.file.Path path =  Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                        Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                        existingProduct.setImage(newImageFile);
                    }catch (Exception ex){
                        ex.printStackTrace();
                        return "product/edit";
                    }
                }
                existingProduct.setCategory(editproduct.getCategory());
                productService.update(existingProduct); // Lưu thay đổi vào cơ sở dữ liệu
            }
            return "redirect:/products";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id){
        Product product=productService.getProductById(id);
        if(product!=null){
            productService.delete(id);
            return "redirect:/";
        }
        return "not-found";
    }

    private long Init(){
        long maxID=0;
        for (Product b : productService.getAll()) if(maxID<b.getId()) maxID=b.getId();
        return maxID+1;
    }

    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
                            @RequestParam long id,
                            @RequestParam String name,
                            @RequestParam String image,
                            @RequestParam double price,
                            @RequestParam(defaultValue = "1") int quantity)
    {
        var cart = cartService.getCart(session);
        cart.addItems(new Item(id, name, price , image, quantity));
        cartService.updateCart(session, cart);
        return "redirect:/products";
    }
}