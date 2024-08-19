package com.ista.FacturaKardex.controllers.android;

import com.ista.FacturaKardex.models.entity.Product;
import com.ista.FacturaKardex.models.service.IProductsService;
import com.ista.FacturaKardex.models.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@RequestMapping("/api/products")
public class ProductoApiController {

    @Autowired
    private IProductsService productsService;


    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/list")
    public ResponseEntity<List<Product>> listarProducts(
            @RequestParam(name = "nombre", required = false, defaultValue = "") String nombre) {
        List<Product> productos = nombre.isEmpty()
                ? productsService.findAll()
                : productsService.findByNombreContainingIgnoreCase(nombre);

        return ResponseEntity.ok(productos);
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<?> verProduct(@PathVariable Long id) {
        try {
            Product producto = productsService.findOne(id);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado");
            }
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener el producto: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> buscarShop(@RequestParam("nombre") String nombre) {
        List<Product> productos = productsService.findByNombreContainingIgnoreCase(nombre);
        return ResponseEntity.ok(productos);
    }

}
