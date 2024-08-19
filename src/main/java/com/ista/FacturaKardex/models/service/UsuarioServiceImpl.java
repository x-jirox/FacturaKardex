package com.ista.FacturaKardex.models.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ista.FacturaKardex.models.dao.IUsuarioDao;
import com.ista.FacturaKardex.models.entity.Usuario;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioDao usuarioDao;


    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll() {
        return usuarioDao.findAll();
    }
    @Transactional
    @Override
    public void save(Usuario usuario) {
        try {
            usuarioDao.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
        }
    }
    @Transactional(readOnly = true)
    @Override
    public Usuario findOne(Long id) {
        return usuarioDao.findOne(id);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        usuarioDao.delete(id);
    }
    @Transactional(readOnly = true)
    @Override
    public Usuario findByUsername(String username) {
        return usuarioDao.findByUsername(username);
    }
    @Transactional(readOnly = true)
    @Override
    public Usuario loadUserByUsername(String username) {
        return usuarioDao.loadUserByUsername(username);
    }
    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findByNombreContainingIgnoreCase(String nombres) {
        return usuarioDao.findByNombreContainingIgnoreCase(nombres);
    }
}
