package net.talaatharb.xtreeve.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.talaatharb.xtreeve.utils.GUIUtils;

@Slf4j
@NoArgsConstructor
public class MainWindowController implements Initializable {

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
}
