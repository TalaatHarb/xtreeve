package net.talaatharb.xtreeve.controllers;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.scene.control.TreeItem;
import lombok.Getter;

public class JsonTreeItem extends TreeItem<String> {
	@Getter
    private final JsonNode jsonNode;

    public JsonTreeItem(String value, JsonNode jsonNode) {
        super(value);
        this.jsonNode = jsonNode;
    }
}
