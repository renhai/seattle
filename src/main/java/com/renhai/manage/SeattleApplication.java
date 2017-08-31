package com.renhai.manage;

import com.renhai.manage.ui.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.util.Locale;

@SpringBootApplication
public class SeattleApplication extends Application {
	private ConfigurableApplicationContext springContext;
//	private Parent root;
	private BorderPane root;
	public static Stage primaryStage;
	private MainViewController mainViewController;

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(SeattleApplication.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		root = fxmlLoader.load();

		FXMLLoader subLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		subLoader.setControllerFactory(springContext::getBean);
		AnchorPane mainView = subLoader.load();
		this.mainViewController = subLoader.getController();
		root.setCenter(mainView);
		root.setAlignment(mainView, Pos.CENTER);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Locale.setDefault(Locale.CHINA);
		SeattleApplication.primaryStage = primaryStage;
		primaryStage.setTitle("管理");
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
			Node source = evt.getPickResult().getIntersectedNode();
			while (source != null && !(source instanceof TableRow)) {
				source = source.getParent();
			}
			if (source == null || (source instanceof TableRow && ((TableRow) source).isEmpty())) {
				this.mainViewController.dataTable.getSelectionModel().clearSelection();
			}
		});
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}


	public static void main(String[] args) {
		launch(SeattleApplication.class, args);
	}
}
