package com.gca.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = -2078263980355550273L;
    @Id
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }
}
