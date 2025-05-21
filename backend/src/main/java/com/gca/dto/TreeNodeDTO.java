package com.gca.dto;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeDTO {
    private Long id;
    private String name;
    private List<TreeNodeDTO> children = new ArrayList<>();
    private Long parentId;
    private Long templateId;

    public TreeNodeDTO(Long id, String name, Long parentId, Long templateId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.templateId = templateId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}