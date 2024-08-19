package com.ista.FacturaKardex.models.service;


import com.ista.FacturaKardex.models.dao.ProductDao;
import com.ista.FacturaKardex.models.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductsService{
    @Autowired
    private ProductDao productDao;

    @Transactional(readOnly = true)
    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }
    @Transactional
    @Override
    public void save(Product products) {
        productDao.save(products);
    }
    @Transactional(readOnly = true)
    @Override
    public Product findOne(Long id) {
        return productDao.findOne(id);
    }
    @Transactional
    @Override
    public void delete(Long id) {
        productDao.delete(id);
    }

    @Override
    public List<String> findDistinctCategorias() {
        return productDao.findDistinctCategorias();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Product> findByNombreContainingIgnoreCase(String nombre) {
        return productDao.findByNombreContainingIgnoreCase(nombre);
    }
    @Transactional(readOnly = true)
    @Override
    public List<Product> findProductosByFiltros(String categoria, Boolean estado, String nombre, String talla, Integer cantidad) {
        return productDao.findProductosByFiltros(categoria,estado,nombre,talla,cantidad);
    }
}
