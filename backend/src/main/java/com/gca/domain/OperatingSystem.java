package com.gca.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class OperatingSystem implements Serializable {

    @Serial
    private static final long serialVersionUID = 550816886951063730L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incrementa el ID
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

    public void setName(String name) {
        this.name = name;
    }
}
