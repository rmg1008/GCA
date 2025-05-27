package com.gca.domain;

import com.gca.util.JsonToMapConverter;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "Template_Command")
public class TemplateCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 1758986836350600479L;
    @EmbeddedId
    private TemplateCommandId id = new TemplateCommandId();

    @ManyToOne
    @MapsId("templateId")
    @JoinColumn(name = "template_id")
    private Template template;

    @ManyToOne
    @MapsId("commandId")
    @JoinColumn(name = "command_id")
    private Command command;

    @Column(name = "execution_order")
    private int executionOrder;

    @Column(name = "parameter_values")
    @Convert(converter = JsonToMapConverter.class)
    private Map<String, String> parameterValues;


    public TemplateCommandId getId() {
        return id;
    }

    public void setId(TemplateCommandId id) {
        this.id = id;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public int getExecutionOrder() {
        return executionOrder;
    }

    public void setExecutionOrder(int executionOrder) {
        this.executionOrder = executionOrder;
    }

    public Map<String, String> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(Map<String, String> parameterValues) {
        this.parameterValues = parameterValues;
    }
}
