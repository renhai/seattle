package com.renhai.manage.ui;

import com.renhai.manage.dto.UploadResultDto;
import com.renhai.manage.service.TesterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by andy on 8/27/17.
 */
@Component
@Slf4j
public class UploadViewController {

    @FXML
    public TextField fileNameField;
    @FXML
    public TextArea uploadResultArea;

    @Autowired
    private TesterService testerService;


    @FXML
    public void onClickUpload(ActionEvent actionEvent) {
        try {
            File selectedFile = new File(StringUtils.trim(this.fileNameField.getText()));
            if (!selectedFile.exists()) {
                throw new FileNotFoundException("File not found");
            }
            UploadResultDto result = testerService.uploadExcel(selectedFile);
            uploadResultArea.setVisible(true);
            uploadResultArea.setText(result.toString());
            log.info(result.toString());
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(e.getMessage());
            alert.show();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("上传失败!");
            alert.show();
        }
        this.fileNameField.setText(StringUtils.EMPTY);
    }

    @FXML
	public void onClickBrowse(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            log.info(selectedFile.getName());
            this.fileNameField.setText(selectedFile.getAbsolutePath());
        } else {
            log.info("no file selected");
        }
	}
}
