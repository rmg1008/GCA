package com.gca.domain;

import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clase que representa la clave primaria compuesta para la entidad TemplateCommand.
 * Utilizada para mapear las relaciones entre Template y Command.
 */
@Embeddable
public class TemplateCommandId implements Serializable {

    @Serial
    private static final long serialVersionUID = -8463603087678125738L;
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
