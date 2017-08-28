package com.renhai.manage.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by andy on 8/27/17.
 */
@Component
@Slf4j
public class RootLayoutController {

    @Autowired
    private ApplicationContext context;

    @FXML
    public BorderPane root;

    @FXML
    public void onManageClick(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            loader.setControllerFactory(context::getBean);
            AnchorPane childView = loader.load();
            root.setCenter(childView);
            root.setAlignment(childView, Pos.CENTER);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @FXML
    public void onClickUpload(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UploadView.fxml"));
            loader.setControllerFactory(context::getBean);
            AnchorPane childView = loader.load();
            root.setCenter(childView);
            root.setAlignment(childView, Pos.CENTER);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
