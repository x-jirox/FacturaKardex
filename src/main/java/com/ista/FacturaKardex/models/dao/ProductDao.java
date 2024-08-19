package com.ista.FacturaKardex.models.dao;


import com.ista.FacturaKardex.models.entity.Product;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDao {
    public List<Product> findAll();
    public void save(Product products);
    public Product findOne(Long id);
    public void delete(Long id);
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    @Query("SELECT DISTINCT p.categoria FROM Product p")
    List<String> findDistinctCategorias();
    List<Product> findProductosByFiltros(String categoria, Boolean estado, String nombre, String talla, Integer cantidad);
}
