package com.gca.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class Command implements Serializable {

    @Serial
    private static final long serialVersionUID = 8842605980901364514L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incrementa el ID
    private Long id;

    private String name;

    private String description;

    @Lob
    @Column(name = "command_value")
    private String value;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
