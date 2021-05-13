package com.power.doc.model.yapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YapiProperty {

    private String type;
    private String description;
    private Mock mock;
    private String $schema = "http://json-schema.org/draft-04/schema#";
    private Map<String, YapiProperty> properties;
    private YapiProperty items;
    private List<String> required = new ArrayList<>();

    public static class Mock {
        private Object mock;

        public Object getMock() {
            return mock;
        }

        public void setMock(Object mock) {
            this.mock = mock;
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Mock getMock() {
        return mock;
    }

    public void setMock(Mock mock) {
        this.mock = mock;
    }

    public String get$schema() {
        return $schema;
    }

    public void set$schema(String $schema) {
        this.$schema = $schema;
    }

    public Map<String, YapiProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, YapiProperty> properties) {
        this.properties = properties;
    }

    public YapiProperty getItems() {
        return items;
    }

    public void setItems(YapiProperty items) {
        this.items = items;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}
