package com.gca.dto;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeDTO {
    private Long id;
    private String name;
    private List<TreeNodeDTO> children = new ArrayList<>();

    public TreeNodeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public List<TreeNodeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNodeDTO> children) {
        this.children = children;
    }
}