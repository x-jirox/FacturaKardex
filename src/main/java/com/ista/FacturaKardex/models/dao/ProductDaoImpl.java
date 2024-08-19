package com.ista.FacturaKardex.models.dao;


import com.ista.FacturaKardex.models.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ProductDaoImpl implements ProductDao{
    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")

    @Override
    public List<Product> findAll() {
        return em.createQuery("from Product", Product.class).getResultList();
    }

    @Override
    public void save(Product products) {
        if (products.getId()!=null && products.getId()>0){
            em.merge(products);
        }else {
            em.persist(products);
        }
    }

    @Override
    public Product findOne(Long id) {
        return em.find(Product.class, id);
    }

    @Override
    public void delete(Long id) {
        em.remove(findOne(id));
    }

    @Override
    public List<Product> findByNombreContainingIgnoreCase(String nombre) {
        String jpql = "SELECT p FROM Product p WHERE LOWER(p.nombre) LIKE LOWER(:nombre)";
        return em.createQuery(jpql, Product.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    @Override
    public List<String> findDistinctCategorias() {
        String jpql = "SELECT DISTINCT p.categoria FROM Product p";
        return em.createQuery(jpql, String.class).getResultList();
    }

    @Override
    public List<Product> findProductosByFiltros(String categoria, Boolean estado, String nombre, String talla, Integer cantidad) {
        String jpql = "SELECT p FROM Product p WHERE " +
                "(:categoria IS NULL OR p.categoria = :categoria) AND " +
                "(:estado IS NULL OR p.estado = :estado) AND " +
                "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(:nombre)) AND " +
                "(:talla IS NULL OR LOWER(p.talla) LIKE LOWER(:talla)) AND " +
                "(:cantidad IS NULL OR p.cantidad = :cantidad)"; // Cambiado a `p.cantidad = :cantidad` para Integer

        return em.createQuery(jpql, Product.class)
                .setParameter("categoria", categoria)
                .setParameter("estado", estado)
                .setParameter("nombre", nombre != null ? "%" + nombre.toLowerCase() + "%" : null) // Ajuste para LIKE
                .setParameter("talla", talla != null ? "%" + talla.toLowerCase() + "%" : null) // Ajuste para LIKE
                .setParameter("cantidad", cantidad)
                .getResultList();
    }


}
