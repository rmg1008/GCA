package com.gca.domain;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "device_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent")
    private Group parent;

    @OneToMany(mappedBy="parent")
    private Collection<Group> children;

    @OneToMany
    private Collection<Device> devices;

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

    public Collection<Device> getDevices() {
        return devices;
    }

    public void setDevices(Collection<Device> devices) {
        this.devices = devices;
    }
}
