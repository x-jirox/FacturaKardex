package com.ista.FacturaKardex.models.service;



import com.ista.FacturaKardex.models.entity.Usuario;

import java.util.List;

public interface IUsuarioService {
    public List<Usuario> findAll();
    public void save(Usuario usuario);
    public Usuario findOne(Long id);
    public void delete(Long id);
    public Usuario findByUsername(String username);
    public Usuario loadUserByUsername(String username);
    List<Usuario> findByNombreContainingIgnoreCase(String nombres);
}
