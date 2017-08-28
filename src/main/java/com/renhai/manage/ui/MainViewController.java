package com.renhai.manage.ui;

import com.renhai.manage.dto.ColumnEnum;
import com.renhai.manage.dto.TesterDto;
import com.renhai.manage.service.TesterService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.IntegerStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @FXML
    public TableView<TesterDto> dataTable;

    @FXML
    public void initialize() {

        dataTable.setItems(getData());
        dataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu menu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem("Remove");
        menu.getItems().add(removeMenuItem);
        dataTable.setContextMenu(menu);
        removeMenuItem.setOnAction(event -> {
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
        });

        for (ColumnEnum columnEnum : ColumnEnum.values()) {
            TableColumn column = new TableColumn<>(columnEnum.getDisplayName());
            column.setCellValueFactory(new PropertyValueFactory(columnEnum.name()));
            column.setOnEditCommit(event -> {
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
            });

            switch (columnEnum) {
                case gender: {
                    ObservableList<String> genderList = FXCollections.observableArrayList("男", "女");
                    column.setCellFactory(ChoiceBoxTableCell.forTableColumn(genderList));
                    break;
                }
                case cnTestDate: {
                    column.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter("yyyy-MM-dd")));
                    break;
                }
                case trainingYear: {
                    column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
                    break;
                }
                default: {
//                    column.setCellFactory(TextFieldTableCell.forTableColumn());
                    break;
                }
            }

            dataTable.getColumns().addAll(column);
        }

    }

    private ObservableList<TesterDto> getData() {
        ObservableList<TesterDto> result = FXCollections.observableArrayList();
        List<TesterDto> data = testerService.getAllTesters();
        result.addAll(data);
        return result;
    }
}
