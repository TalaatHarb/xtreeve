package net.talaatharb.xtreeve.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.stream.XMLStreamException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.xtreeve.models.ProjectModel;
import net.talaatharb.xtreeve.utils.GUIUtils;
import net.talaatharb.xtreeve.utils.XMLUtils;

@Slf4j
@NoArgsConstructor
public class MainWindowController implements Initializable {

	private static final String LOAD_MORE = "Load more...";

	private static final int ARRAY_PAGINATION_LIMIT = 25;

	private static final String LOADING = "Loading...";

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private TreeView<String> treeView;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private AnchorPane detailViewPane;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private AnchorPane editorPane;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private Menu openRecentMenu;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private ProgressBar progressBar;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private CheckBox withAttributesCheckbox;

	private TextArea codeArea = new TextArea();

	private ProjectModel model;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Main window loaded");

		codeArea.setWrapText(true);
		codeArea.setEditable(false);
		
		editorPane.getChildren().add(codeArea);
		GUIUtils.setAnchorNoPadding(codeArea);

		treeView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> handleSelectionChange(newValue));

	}

	private void handleSelectionChange(TreeItem<String> newValue) {
		if (newValue != null && newValue instanceof JsonTreeItem jsonTreeItem && (jsonTreeItem.getJsonNode().isObject()
				|| (jsonTreeItem.getJsonNode().isArray() && jsonTreeItem.getJsonNode().get(0).isTextual()))) {
			var tableContent = createJsonNodeTable(jsonTreeItem.getJsonNode());
			detailViewPane.getChildren().clear();
			detailViewPane.getChildren().add(tableContent);
			GUIUtils.setAnchorNoPadding(tableContent);

			try {
				String value = jsonTreeItem.getValue() != null ? jsonTreeItem.getValue() : "root";
				String rootName = value.contains(" ") ? value.split(" ")[0] : value;
				String editorText = withAttributesCheckbox.isSelected()
						? XMLUtils.jsonNodeToStrippedXmlWithAttributes(jsonTreeItem.getJsonNode(), rootName)
						: XMLUtils.jsonNodeToStrippedXml(jsonTreeItem.getJsonNode(), rootName);
				codeArea.setText(editorText);
			} catch (XMLStreamException | JsonProcessingException e) {
				log.error(e.getMessage());
			}
		}else {
			codeArea.setText("");
			detailViewPane.getChildren().clear();
		}
	}

	public static final TableView<JsonNodeEntry> createJsonNodeTable(JsonNode node) {
		// Create a TableView with two columns: one for keys and one for values
		TableView<JsonNodeEntry> tableView = new TableView<>();

		TableColumn<JsonNodeEntry, String> keyColumn = new TableColumn<>("Key");
		keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));

		TableColumn<JsonNodeEntry, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

		tableView.getColumns().add(keyColumn);
		tableView.getColumns().add(valueColumn);

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

		// Populate the table with data from the JsonNode
		ObservableList<JsonNodeEntry> data = FXCollections.observableArrayList();

		if (node.isObject()) {
			// Handle object nodes by iterating over the fields
			Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				JsonNode value = field.getValue();
				if (value.isTextual()) {
					data.add(new JsonNodeEntry(field.getKey(), value.asText()));
				}
			}
		} else if (node.isArray()) {
			// Handle array nodes by iterating over the elements
			int index = 0;
			for (JsonNode arrayElement : node) {
				data.add(new JsonNodeEntry("Index " + index, arrayElement.asText()));
				index++;
			}
		} else {
			// For primitive values (non-object, non-array)
			data.add(new JsonNodeEntry("Value", node.asText()));
		}

		tableView.setItems(data);
		return tableView;
	}

	public static final VBox createJsonNodeTableView(JsonNode node) {
		// Create a VBox to hold the table
		VBox vbox = new VBox();
		TableView<JsonNodeEntry> tableView = createJsonNodeTable(node);
		vbox.getChildren().add(tableView);
		return vbox;
	}

	private TreeItem<String> createTreeItem(JsonNode node, String name) {
		TreeItem<String> treeItem = new JsonTreeItem(name, node);

		// Initially, add an empty child to indicate lazy loading
		treeItem.getChildren().add(new TreeItem<>(LOADING));

		// Handle the expansion event
		treeItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
			if (isNewlyExpanded(treeItem, newValue)) {
				treeItem.getChildren().clear();
				new Thread(() -> loadChildren(treeItem, node, name)).start();
			}

		});

		return treeItem;
	}

	boolean isNewlyExpanded(TreeItem<String> treeItem, Boolean newValue) {
		return newValue.booleanValue() && treeItem.getChildren().size() == 1
				&& treeItem.getChildren().get(0).getValue().equals(LOADING);
	}

	private void loadChildren(TreeItem<String> parent, JsonNode node, String name) {
		Platform.runLater(() -> progressBar.setVisible(true));
		if (node.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				JsonNode value = field.getValue();
				if (value.isTextual()) {
					final TreeItem<String> childItem = new TreeItem<>(
							"Attribute: (" + field.getKey() + ": " + value.asText() + ")");
					Platform.runLater(() -> parent.getChildren().add(childItem));
				} else {
					String itemName = field.getKey();
					if (value.isArray()) {
						itemName += " (" + value.size() + ")";
					}
					final TreeItem<String> childItem = createTreeItem(value, itemName);
					Platform.runLater(() -> parent.getChildren().add(childItem));
				}
			}
		} else if (node.isArray()) {
			Platform.runLater(() -> handleArrayNode(parent, node, name, 0, ARRAY_PAGINATION_LIMIT));
		} else {
			Platform.runLater(() -> parent.getChildren().add(new TreeItem<>(node.asText())));
		}
		Platform.runLater(() -> progressBar.setVisible(false));
	}

	private void handleArrayNode(TreeItem<String> parent, JsonNode node, String name, int start, int count) {
		int size = node.size();
		var parentChildren = parent.getChildren();

		// Load elements from the array
		for (int i = start; i < start + count && i < size; i++) {
			TreeItem<String> childItem = createTreeItem(node.get(i), name + "[" + i + "]");
			parentChildren.add(childItem);
		}

		// Check if there are more elements to load
		if (start + count < size) {
			TreeItem<String> loadMoreItem = new TreeItem<>(LOAD_MORE);
			loadMoreItem.setExpanded(false);

			loadMoreItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.booleanValue()) {
					if (parentChildren.contains(loadMoreItem)) {
						parentChildren.remove(loadMoreItem);
					}
					handleArrayNode(parent, node, name, start + count, ARRAY_PAGINATION_LIMIT);
				}
			});

			if (!parentChildren.contains(loadMoreItem)) {
				parentChildren.add(loadMoreItem);
			}
		}
	}

	@FXML
	void newClicked() {
		log.info("New Clicked");
	}

	@FXML
	void openClicked() {
		FileChooser fileChooser = new FileChooser();

		fileChooser.setTitle("Open XML File");

		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Files", "*.xml"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			String absolutePath = selectedFile.getAbsolutePath();
			log.info("Selected file: " + absolutePath);

			model = null;
			treeView.setRoot(new TreeItem<>(LOADING));

			new Thread(() -> {
				try {
					Platform.runLater(() -> progressBar.setVisible(true));
					loadXML(absolutePath);
					Platform.runLater(() -> progressBar.setVisible(false));
				} catch (IOException e) {
					log.error("Unable to load file");
				}
			}).start();

		} else {
			log.debug("File selection cancelled.");
		}
	}

	void loadXML(String absolutePath) throws IOException {
		model = new ProjectModel(absolutePath);
		Platform.runLater(() -> {
			TreeItem<String> treeRoot = createTreeItem(model.getFileTree(), model.getRootTag());
			treeView.setRoot(treeRoot);
		});
	}

	@FXML
	void closeClicked() {
		log.info("Close Clicked");
	}

	@FXML
	void saveClicked() {
		log.info("Save Clicked");
	}

	@FXML
	void saveAsClicked() {
		log.info("Save As Clicked");
	}

	@FXML
	void resetClicked() {
		log.info("Reset Clicked");
	}

	@FXML
	void preferencesClicked() {
		log.info("Preferences Clicked");
	}

	@FXML
	void quitClicked() {
		log.info("Quit Clicked");
	}

	@FXML
	void aboutClicked() {
		log.info("About Clicked");
	}

	@FXML
	void rollbackClicked() {
		log.info("Rollback Clicked");
	}

	@FXML
	void cutClicked() {
		log.info("Cut Clicked");
	}

	@FXML
	void copyClicked() {
		log.info("Copy Clicked");
	}

	@FXML
	void pasteClicked() {
		log.info("Paste Clicked");
	}

	@FXML
	void deleteClicked() {
		log.info("Delete Clicked");
	}

}
