package com.renhai.manage.ui;

import com.renhai.manage.dto.UploadResultDto;
import com.renhai.manage.service.TesterService;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Created by andy on 8/27/17.
 */
@Component
@Slf4j
public class UploadViewController {

    public UploadViewController() {
        log.info("UploadViewController init");
    }

    @Autowired
    private TesterService testerService;

    public void onClickUpload(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Excel File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            log.info(selectedFile.getName());
            UploadResultDto result = testerService.uploadExcel(selectedFile);
            log.info(result.toString());
        } else {
            log.info("no file selected");
        }
    }
}
