package com.renhai.manage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

@SpringBootApplication
public class SeattleApplication extends Application {
	private ConfigurableApplicationContext springContext;
//	private Parent root;
	private BorderPane root;

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(SeattleApplication.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		root = fxmlLoader.load();

		URL paneOneUrl = getClass().getResource("/fxml/MainView.fxml");
//		AnchorPane mainView = FXMLLoader.load( paneOneUrl );
		AnchorPane mainView = new FXMLLoader(paneOneUrl).load();
		root.setCenter(mainView);
		root.setAlignment(mainView, Pos.CENTER);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("管理");
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}


	public static void main(String[] args) {
		launch(SeattleApplication.class, args);
	}
}
