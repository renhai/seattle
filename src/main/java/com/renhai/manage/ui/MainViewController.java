package com.renhai.manage.ui;

import com.renhai.manage.dto.TesterDto;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by andy on 8/26/17.
 */
@Component
@Slf4j
public class MainViewController {

    @FXML
    public TableView<TesterDto> dataTable;
    @FXML
    public TableColumn<TesterDto, Integer> idColumn;
    @FXML
    public TableColumn<TesterDto, String> nameColumn;

    private ObservableList<TesterDto> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        data.add(TesterDto.builder().id(new SimpleIntegerProperty(1)).name(new SimpleStringProperty("hren")).build());
        data.add(TesterDto.builder().id(new SimpleIntegerProperty(2)).name(new SimpleStringProperty("qian")).build());
        data.add(TesterDto.builder().id(new SimpleIntegerProperty(3)).build());
        dataTable.setItems(data);

        idColumn.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());

    }
}
