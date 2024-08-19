package com.ista.FacturaKardex.models.dao;

import com.ista.FacturaKardex.models.entity.Product;
import com.ista.FacturaKardex.models.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UsuarioDaoImpl implements IUsuarioDao {
    @PersistenceContext
    private EntityManager em;



    @SuppressWarnings("undechecked")

    @Override
    public List<Usuario> findAll() {
        return em.createQuery("from Usuario", Usuario.class).getResultList();
    }

    @Override
    public void save(Usuario usuario) {
        if(usuario.getId() != null && usuario.getId()>0){
            em.merge(usuario);
        }else{
            em.persist(usuario);
        }
    }

    @Override
    public Usuario findOne(Long id) {
        return em.find(Usuario.class, id);
    }

    @Override
    public void delete(Long id) {
        em.remove(findOne(id));
    }

    @Override
    public List<Usuario> findByNombreContainingIgnoreCase(String nombres) {
        String jpql = "SELECT u FROM Usuario u WHERE LOWER(u.nombres) LIKE LOWER(:nombres)";
        return em.createQuery(jpql, Usuario.class)
                .setParameter("nombres", "%" + nombres + "%")
                .getResultList();
    }

    @Override
    public Usuario findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public Usuario loadUserByUsername(String username){
        return findByUsername(username);
    }
}
