package net.talaatharb.xtreeve.utils;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XMLUtils {
	private static final XmlMapper xmlMapper = new XmlMapper();

	public static final String jsonNodeToStrippedXmlWithAttributes(JsonNode node, String rootName)
			throws XMLStreamException {
		StringWriter stringWriter = new StringWriter();

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);

		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement(rootName);

		if (node.isObject()) {
			ObjectNode objectNode = (ObjectNode) node;
			objectNode.fields().forEachRemaining(entry -> {
				JsonNode value = entry.getValue();
				if (!value.isContainerNode()) {
					try {
						xmlStreamWriter.writeAttribute(entry.getKey(), value.asText());
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			});
		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndDocument();
		xmlStreamWriter.close();

		return stringWriter.toString().substring(38);
	}

	public static final String jsonNodeToStrippedXml(JsonNode node, String rootName) throws JsonProcessingException {
		ObjectNode strippedNode = stripChildren(node);
		return xmlMapper.writerWithDefaultPrettyPrinter().withRootName(rootName).writeValueAsString(strippedNode);
	}

	private static final ObjectNode stripChildren(JsonNode node) {
		ObjectNode strippedNode = xmlMapper.createObjectNode();

		if (node.isObject()) {
			node.fields().forEachRemaining(entry -> {
				JsonNode value = entry.getValue();
				if (!value.isContainerNode()) {
					strippedNode.set(entry.getKey(), value);
				}
			});
		}
		return strippedNode;
	}
}
