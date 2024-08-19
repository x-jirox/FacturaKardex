package com.ista.FacturaKardex.controllers;

import com.ista.FacturaKardex.models.entity.Product;
import com.ista.FacturaKardex.models.service.IProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
public class HomeController {

    @Autowired
    private IProductsService productsService;

    @GetMapping({"/","shop"," "})
    public String home(Model model){
        List<Product> allProducts = productsService.findAll();
        List<Product> limitedProducts = allProducts.stream().limit(12).toList();
        model.addAttribute("producto", limitedProducts);
        return "shop";
    }

}
