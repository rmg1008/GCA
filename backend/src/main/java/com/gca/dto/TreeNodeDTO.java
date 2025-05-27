package com.gca.dto;

import java.util.ArrayList;
import java.util.List;

public class TreeNodeDTO extends BaseDTO {
    private final List<TreeNodeDTO> children = new ArrayList<>();
    @SuppressWarnings("unused")
    private Long parentId;
    private Long templateId;

    public TreeNodeDTO() {
    }

    public TreeNodeDTO(Long id, String name, Long parentId, Long templateId) {
        super.setId(id);
        super.setName(name);
        this.parentId = parentId;
        this.templateId = templateId;
    }

    public List<TreeNodeDTO> getChildren() {
        return children;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }
}