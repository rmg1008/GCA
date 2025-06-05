package com.gca.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Template implements Serializable {

    @Serial
    private static final long serialVersionUID = -8738092240974220724L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incrementa el ID
    private Long id;

    private String name;

    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "os_id")
    private OperatingSystem os;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateCommand> templateCommands;

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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OperatingSystem getOs() {
        return os;
    }

    public void setOs(OperatingSystem os) {
        this.os = os;
    }

    public List<TemplateCommand> getTemplateCommands() {
        return templateCommands;
    }

    public void setTemplateCommands(List<TemplateCommand> templateCommands) {
        this.templateCommands = templateCommands;
    }
}