package com.renhai.manage.ui;

import com.renhai.manage.SeattleApplication;
import com.renhai.manage.dto.ColumnEnum;
import com.renhai.manage.dto.TesterDto;
import com.renhai.manage.entity.Tester;
import com.renhai.manage.service.TesterService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by andy on 8/26/17.
 */
@Component
@Slf4j
public class MainViewController {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    @FXML
    public TextField filterField;

    @Autowired
    private TesterService testerService;

    @Autowired
    private ApplicationContext context;

    private ObservableList<TesterDto> data = FXCollections.observableArrayList();

    @FXML
    public TableView<TesterDto> dataTable;

    @FXML
    public void initialize() {
        initData();
//        dataTable.setItems(this.data);
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu menu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove");
        menu.getItems().add(removeMenuItem);
        dataTable.setContextMenu(menu);
        removeMenuItem.setOnAction(event -> this.removeItems());

        for (ColumnEnum columnEnum : ColumnEnum.values()) {
            TableColumn column = generateColumn(columnEnum);
            dataTable.getColumns().addAll(column);
        }

        FilteredList<TesterDto> filteredData = new FilteredList<>(this.data, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(dto -> {
			if (StringUtils.isBlank(newValue)) {
				return true;
			}

			String lowerCaseFilter = newValue.toLowerCase();
			for (ColumnEnum columnEnum : ColumnEnum.values()) {
				try {
					Object value  = MethodUtils.invokeMethod(dto, "get"+ StringUtils.capitalize(columnEnum.name()));
					if (value != null && value.toString().contains(lowerCaseFilter)) {
						return true;
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

			}
			return false;
		}));
        SortedList<TesterDto> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(dataTable.comparatorProperty());
        dataTable.setItems(sortedData);
    }

    private void initData() {
        data.clear();
        List<TesterDto> result = testerService.getAllTesters();
        data.addAll(result);
    }

    public void onClickAdd(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TesterAddDialog.fxml"));
            loader.setControllerFactory(context::getBean);
            ScrollPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("添加");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(SeattleApplication.primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TesterAddDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveBtnClicked()) {
                this.data.add(controller.getTesterDto());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void removeItems() {
        ObservableList<TesterDto> selectedRows = dataTable.getSelectionModel().getSelectedItems();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认");
        String s = String.format("确认删除这%d行吗？", selectedRows.size());
        alert.setHeaderText(s);

        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            List<Integer> idList = selectedRows.stream().map(dto -> dto.getId()).collect(Collectors.toList());
            testerService.deleteTesters(idList);
            this.data.removeAll(selectedRows);
            dataTable.refresh();
        }
    }

    private void updateField(ColumnEnum columnEnum, Event event) {
        TableColumn.CellEditEvent cellEditEvent = (TableColumn.CellEditEvent) event;
        int position = cellEditEvent.getTablePosition().getRow();
        TesterDto dto = dataTable.getItems().get(position);
        String fieldName = columnEnum.name();
        try {
            TesterDto newDto = testerService.updateTester(dto.getId(), fieldName, cellEditEvent.getNewValue());
            OptionalInt index = IntStream.range(0, this.data.size()).filter(i -> this.data.get(i).getId().equals(dto.getId())).findFirst();
            this.data.set(index.getAsInt(), newDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private TableColumn generateColumn(ColumnEnum columnEnum) {
        TableColumn column = new TableColumn<>(columnEnum.getDisplayName());
        column.setId(columnEnum.name());
        column.setCellValueFactory(new PropertyValueFactory(columnEnum.name()));
        column.setOnEditCommit(event -> this.updateField(columnEnum, event));

        switch (columnEnum) {
            case id:
            case age:
                break;
            case gender: {
                ObservableList<String> genderList = FXCollections.observableArrayList(Tester.Gender.getTextList());
                column.setCellFactory(ChoiceBoxTableCell.forTableColumn(genderList));
                break;
            }
            case level: {
                ObservableList<String> genderList = FXCollections.observableArrayList(Tester.Level.getTextList());
                column.setCellFactory(ChoiceBoxTableCell.forTableColumn(genderList));
                break;
            }
            case grade: {
                ObservableList<String> genderList = FXCollections.observableArrayList(Tester.Grade.getTextList());
                column.setCellFactory(ChoiceBoxTableCell.forTableColumn(genderList));
                break;
            }
            case status: {
                ObservableList<String> genderList = FXCollections.observableArrayList(Tester.Status.getTextList());
                column.setCellFactory(ChoiceBoxTableCell.forTableColumn(genderList));
                break;
            }
            case dob:
            case cnTestDate: {
                column.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter("yyyy-MM-dd")));
                break;
            }
            case testCount:
            case score:
            case termNo:
            case trainingYear: {
                column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                break;
            }
            case cnScore: {
                column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
                break;
            }
            default: {
                column.setCellFactory(TextFieldTableCell.forTableColumn());
                break;
            }
        }
        return column;
    }

    @FXML
    public void handleUpload(ActionEvent actionEvent) {

    }

    @FXML
    public void handleDownload(ActionEvent actionEvent) {
        File excelFile = generateTempFile();
        if (excelFile == null) {
            log.warn("generate excel file failed.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");
        fileChooser.setInitialFileName(String.format("export-%s.xlsx", DATE_FORMAT.format(new Date())));
        File destFile = fileChooser.showSaveDialog(null);
        if (destFile != null) {
            log.info(destFile.getAbsolutePath());
            try {
                FileCopyUtils.copy(excelFile, destFile);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private String[] getVisibleFields() {
        List<String> visibleFields = this.dataTable.getColumns().stream().filter(column -> column.isVisible()).map(column -> column.getId()).collect(Collectors.toList());
        return visibleFields.toArray(new String[visibleFields.size()]);
    }

    private File generateTempFile() {
        try {
            List<TesterDto> exportData;
            ObservableList<TesterDto> selectedRows = dataTable.getSelectionModel().getSelectedItems();
            if (CollectionUtils.isEmpty(selectedRows)) {
                exportData = testerService.getAllTesters();
            } else {
                exportData = new ArrayList<>(selectedRows);
            }

            String[] fields = getVisibleFields();
            Workbook excel = createExcel(fields, exportData);
            File tempFile = File.createTempFile(RandomStringUtils.randomAlphabetic(8), "xlsx");
            tempFile.deleteOnExit();
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            excel.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return tempFile;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private Workbook createExcel(String[] fields, List<TesterDto> testers) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");
        Row header = sheet.createRow(0);
        for (int i = 0; i < fields.length; i ++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            ColumnEnum columnEnum = ColumnEnum.fromName(fields[i]);
            cell.setCellValue(columnEnum.getDisplayName());
        }

        int rowNum = 1;
        for (TesterDto tester : testers) {
            Row row = sheet.createRow(rowNum ++);
            int colNum = 0;
            for (String fieldName : fields) {
                Object value = MethodUtils.invokeMethod(tester, "get"+ org.apache.commons.lang3.StringUtils.capitalize(fieldName));
                if (value == null) {
                    row.createCell(colNum ++).setCellValue(org.apache.commons.lang3.StringUtils.EMPTY);
                } else {
                    row.createCell(colNum ++).setCellValue(value.toString());
                }
            }
        }

        return workbook;
    }
}
