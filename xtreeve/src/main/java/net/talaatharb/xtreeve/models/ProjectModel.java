package net.talaatharb.xtreeve.models;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import lombok.Data;

@Data
public class ProjectModel {

	private final String filePath;
	private final JsonNode fileTree;
	private final String rootTag;

	public ProjectModel(String filePath) throws IOException {
		this.filePath = filePath;
		File file = new File(filePath);
		XmlMapper xmlMapper = new XmlMapper();
		String root = null;
		try (JsonParser parser = xmlMapper.getFactory().createParser(file)) {
			root = ((FromXmlParser) parser).getStaxReader().getLocalName();
		}

		rootTag = root != null ? root : "root";
		fileTree = xmlMapper.readTree(file);
	}
}
