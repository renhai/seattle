package com.renhai.manage.ui;

import com.google.common.base.Preconditions;
import com.renhai.manage.dto.ColumnEnum;
import com.renhai.manage.dto.TesterDto;
import com.renhai.manage.entity.Tester;
import com.renhai.manage.service.TesterService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hai on 8/28/17.
 */
@Component
@Slf4j
public class TesterAddDialogController {
	@FXML
	public GridPane addForm;

	private Stage dialogStage;

	private TesterDto testerDto;

	@Autowired
	private TesterService testerService;

	private boolean isSaveBtnClicked;

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public TesterDto getTesterDto() {
		return this.testerDto;
	}

	public boolean isSaveBtnClicked() {
		return this.isSaveBtnClicked;
	}

	private Map<ColumnEnum, Control> componentMap = new HashMap<>();

	@FXML
	public void initialize() {
		addForm.setPadding(new Insets(20));
		addForm.setHgap(25);
		addForm.setVgap(15);
//		GridPane.setHalignment(nameLabel, HPos.RIGHT);

		int rowIndex = 0;
		for (ColumnEnum columnEnum : ColumnEnum.values()) {
			Label label = new Label(columnEnum.getDisplayName());
			Control control;
			switch (columnEnum) {
				case id:
				case age:
					continue;
				case gender:{
					control = new ChoiceBox(FXCollections.observableArrayList(Tester.Gender.getTextList()));
					break;
				}
				case level:{
					control = new ChoiceBox(FXCollections.observableArrayList(Tester.Level.getTextList()));
					break;
				}
				case grade:{
					control = new ChoiceBox(FXCollections.observableArrayList(Tester.Grade.getTextList()));
					break;
				}
				case status:{
					control = new ChoiceBox(FXCollections.observableArrayList(Tester.Status.getTextList()));
					break;
				}
				case dob:
				case cnTestDate: {
					control = new DatePicker();
					break;
				}
				default: {
					control = new TextField();
					control.setPrefWidth(350);
					break;
				}
			}
			addForm.add(label, 0, rowIndex);
			addForm.add(control, 1, rowIndex);
			rowIndex ++;
			this.componentMap.put(columnEnum, control);
		}

		HBox box = new HBox();
		box.setAlignment(Pos.CENTER_RIGHT);
		Button saveBtn = new Button("Save");
		Button cancelBtn = new Button("Cancel");
		saveBtn.setOnAction(event -> this.onSaveClick());
		cancelBtn.setOnAction(event -> this.onCancelClick());
		box.setSpacing(15);
		box.getChildren().addAll(saveBtn, cancelBtn);
		addForm.add(box, 1, rowIndex);
	}

	private void checkInputField(Tester tester) {
		Preconditions.checkArgument(StringUtils.isNotBlank(tester.getName()), "请输入姓名");
		Preconditions.checkArgument(StringUtils.isNotBlank(tester.getAccount()), "请输入账号");
		Preconditions.checkArgument(StringUtils.isNotBlank(tester.getBadgeNo()), "请输入工作证编号");
	}


	private void onSaveClick() {
		Tester entity = new Tester();
		for (ColumnEnum key : this.componentMap.keySet()) {
			Object realValue = null;
			Control control = this.componentMap.get(key);
			if (control instanceof ChoiceBox) {
				realValue = ((ChoiceBox) control).getValue();
			} else if (control instanceof DatePicker) {
				LocalDate localDate = ((DatePicker) control).getValue();
				realValue = localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			} else if (control instanceof TextField) {
				String text = ((TextField) control).getText();
				realValue = StringUtils.trimToNull(text);
			}

			String fieldName = key.name();
			try {
				Field field = FieldUtils.getField(Tester.class, fieldName, true);
				if (realValue == null) {
					FieldUtils.writeDeclaredField(entity, fieldName, null, true);
				} else if (field.getType().isEnum()) {
					realValue = MethodUtils.invokeStaticMethod(field.getType(), "fromText", realValue);
					FieldUtils.writeDeclaredField(entity, fieldName, realValue, true);
				} else if (field.getType().equals(Integer.class)) {
					realValue = Integer.parseInt(realValue.toString());
					FieldUtils.writeDeclaredField(entity, fieldName, realValue, true);
				} else if (field.getType().equals(Double.class)) {
					realValue = Double.parseDouble(realValue.toString());
					FieldUtils.writeDeclaredField(entity, fieldName, realValue, true);
				} else {
					FieldUtils.writeDeclaredField(entity, fieldName, realValue, true);
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		try {
			checkInputField(entity);
		} catch (Exception e) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText(e.getMessage());
			alert.show();
			return;
		}
		this.testerDto = testerService.createTester(entity);
		this.isSaveBtnClicked = true;
		this.dialogStage.close();
	}

	private void onCancelClick() {
		this.dialogStage.close();
	}
}
