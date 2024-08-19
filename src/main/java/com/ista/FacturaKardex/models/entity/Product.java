package com.ista.FacturaKardex.models.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "producto")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nombre;

    @Size(max = 1500)
    private String descripcion;
    private String talla;
    private Double precio;
    private String foto;
    private Integer cantidad=1;
    private String categoria;

    @Column(nullable = false)
    private Boolean estado=true;


}
