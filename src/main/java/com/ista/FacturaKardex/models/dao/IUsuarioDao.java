package com.ista.FacturaKardex.models.dao;


import com.ista.FacturaKardex.models.entity.Product;
import com.ista.FacturaKardex.models.entity.Usuario;

import java.util.List;

public interface IUsuarioDao {

    public List<Usuario> findAll();
    public void save(Usuario usuario);
    public Usuario findOne(Long id);
    public void delete(Long id);
    List<Usuario> findByNombreContainingIgnoreCase(String nombres);
    public Usuario findByUsername(String username);
    public Usuario loadUserByUsername(String username);
}
