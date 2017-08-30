package com.renhai.manage.ui;

import com.renhai.manage.SeattleApplication;
import com.renhai.manage.dto.ColumnEnum;
import com.renhai.manage.dto.TesterDto;
import com.renhai.manage.entity.Tester;
import com.renhai.manage.service.TesterService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by andy on 8/26/17.
 */
@Component
@Slf4j
public class MainViewController {

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
        dataTable.setItems(this.data);
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu menu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove");
        menu.getItems().add(removeMenuItem);
        dataTable.setContextMenu(menu);
        removeMenuItem.setOnAction(event -> this.removeItems());

        for (ColumnEnum columnEnum : ColumnEnum.values()) {
            TableColumn column = new TableColumn<>(columnEnum.getDisplayName());
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

            dataTable.getColumns().addAll(column);
        }

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
            dataTable.getItems().removeAll(selectedRows);
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
            dataTable.getItems().set(position, newDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
