package net.talaatharb.xtreeve.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.JsonNode;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.xtreeve.utils.GUIUtils;

@Slf4j
@NoArgsConstructor
public class MainWindowController implements Initializable {

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private TreeView<JsonNode> treeView;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private AnchorPane detailViewPane;

	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private AnchorPane editorPane;
	
	@Setter(value = AccessLevel.PACKAGE)
	@FXML
	private Menu openRecentMenu;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("Main window loaded");

	}

	Parent setupPane(String path, AnchorPane container) {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = null;

		try {
			root = loader.load();
		} catch (final IOException ex) {
			log.error("{} Failed to load!", path);
			log.error(ex.getMessage());
		}
		container.getChildren().add(root);
		GUIUtils.setAnchorAll(root, 5.0);
		return root;
	}
	
	@FXML
	void newClicked() {
		log.debug("New Clicked");
	}
	
	@FXML
	void openClicked() {
		log.debug("Open Clicked");
	}
	
	@FXML
	void closeClicked() {
		log.debug("Close Clicked");
	}
	
	@FXML
	void saveClicked() {
		log.debug("Save Clicked");
	}
	
	@FXML
	void saveAsClicked() {
		log.debug("Save As Clicked");
	}
	
	@FXML
	void resetClicked() {
		log.debug("Reset Clicked");
	}
	
	@FXML
	void preferencesClicked() {
		log.debug("Preferences Clicked");
	}
	
	@FXML
	void quitClicked() {
		log.debug("Quit Clicked");
	}
	
	@FXML
	void aboutClicked() {
		log.debug("About Clicked");
	}
	
	@FXML
	void rollbackClicked() {
		log.debug("Rollback Clicked");
	}
	
	@FXML
	void cutClicked() {
		log.debug("Cut Clicked");
	}
	
	@FXML
	void copyClicked() {
		log.debug("Copy Clicked");
	}
	
	@FXML
	void pasteClicked() {
		log.debug("Paste Clicked");
	}
	
	@FXML
	void deleteClicked() {
		log.debug("Delete Clicked");
	}

}
