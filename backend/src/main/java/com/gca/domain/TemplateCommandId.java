package com.gca.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TemplateCommandId implements Serializable {

    private Long templateId;
    private Long commandId;

    public TemplateCommandId() {}

    public TemplateCommandId(Long templateId, Long commandId) {
        this.templateId = templateId;
        this.commandId = commandId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemplateCommandId that)) return false;
        return Objects.equals(templateId, that.templateId) &&
                Objects.equals(commandId, that.commandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, commandId);
    }
}
