package com.gca.domain;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "device_group")
public class Group implements Serializable {

    @Serial
    private static final long serialVersionUID = 1029882785305430206L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto incrementa el ID
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER carga la entidad relacionada inmediatamente
    @JoinColumn(name = "parent")
    private Group parent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy="parent")
    private Collection<Group> children;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template")
    private Template template;

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

    public Group getParent() {
        return parent;
    }

    public void setParent(Group parent) {
        this.parent = parent;
    }

    public Collection<Group> getChildren() {
        return children;
    }

    public void setChildren(Collection<Group> children) {
        this.children = children;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }
}
