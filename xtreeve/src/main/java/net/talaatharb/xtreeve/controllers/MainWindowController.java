package net.talaatharb.xtreeve.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
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

	private ProjectModel model;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Main window loaded");

	}

	private TreeItem<String> createTreeItem(JsonNode node, String name) {
		TreeItem<String> treeItem;

		if (node.isObject()) {
			treeItem = new TreeItem<>(name);

			Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
			while (fields.hasNext()) {
				Map.Entry<String, JsonNode> field = fields.next();
				TreeItem<String> childItem = createTreeItem(field.getValue(), field.getKey());
				if (childItem.getChildren().isEmpty()) {
					childItem.setValue("Attribute: (" + field.getKey() + ": " + field.getValue().asText() + ")");
				}
				treeItem.getChildren().add(childItem);
			}
		} else if (node.isArray()) {
			int size = node.size();
			treeItem = new TreeItem<>(name + " array(" + size + ")");
			int i = 1;
			for (JsonNode arrayElement : node) {
				treeItem.getChildren().add(createTreeItem(arrayElement, name + "[" + i + "]"));
				i++;
			}
		} else {
			treeItem = new TreeItem<>(node.asText());
		}

		return treeItem;
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
			treeView.setRoot(new TreeItem<>("Loading..."));
			System.gc();

			new Thread(() -> {
				try {
					loadXML(absolutePath);
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
		TreeItem<String> treeRoot = createTreeItem(model.getFileTree(), model.getRootTag());
		treeView.setRoot(treeRoot);
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
