package net.talaatharb.xtreeve.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.xtreeve.models.ProjectModel;

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

	private ProjectModel model;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Main window loaded");

	}

	private TreeItem<String> createTreeItem(JsonNode node, String name) {
		TreeItem<String> treeItem = new TreeItem<>(name);

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
			handleArrayNode(parent, node, name, 0, ARRAY_PAGINATION_LIMIT);
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
