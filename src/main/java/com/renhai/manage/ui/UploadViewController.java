package com.renhai.manage.ui;

import com.renhai.manage.dto.UploadResultDto;
import com.renhai.manage.service.TesterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by andy on 8/27/17.
 */
@Component
@Slf4j
public class UploadViewController {

    @FXML
    public TextField fileNameField;
    @FXML
    public Text successLabel;
    @FXML
    public Text failedLabel;
    @FXML
    public TextArea failedDetail;
    @FXML
    public GridPane uploadResultTable;

    @Autowired
    private TesterService testerService;

    private File selectedFile;

    @FXML
    public void onClickUpload(ActionEvent actionEvent) {
        if (this.selectedFile != null) {
            try {
                UploadResultDto result = testerService.uploadExcel(selectedFile);
                uploadResultTable.setVisible(true);
                successLabel.setText(result.getSuccessfulCount() + "");
                failedLabel.setText(result.getFailedCount() + "");
                failedDetail.setText(StringUtils.join(result.getFailedLineNumbers(), ","));
                log.info(result.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("上传失败!");
                alert.show();
            }
            this.fileNameField.setText(StringUtils.EMPTY);
            this.selectedFile = null;
        }
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
            this.selectedFile = selectedFile;
        } else {
            log.info("no file selected");
        }
	}
}
