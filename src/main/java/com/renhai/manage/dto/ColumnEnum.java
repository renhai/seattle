package com.renhai.manage.dto;

import lombok.Getter;

/**
 * Created by hai on 8/13/17.
 */

public enum ColumnEnum {

	id("ID"),
	name("姓名"),
	account("账号"),
	gender("性别"),
	badgeNo("工作证编号"),
	idNo("证件号"),
	education("文化程度"),
	jobTitle("职称"),
	occupation("职务"),
	workUnit("工作单位"),
	zipCode("邮编"),
	workAddress("地址"),
	workPhone("办公电话"),
	homePhone("家庭电话"),
	cellPhone("手机"),
	telMobile("TelMobile"),
	email("邮箱"),
	dialect("Dialect"),
	cnTestDate("CNTestDate"),
	cnScore("CNScore"),
	level("测试员等级"),
	grade("类别"),
	bankName("银行"),
	bankAccount("账户"),
	dob("生日"),
	age("年龄"),
	trainingYear("培训年"),
	testCount("测试量"),
	status("状态"),
	score("成绩"),
	backboneClass("骨干班次"),
	testCenter("测试分中心"),
	termNo("期数"),
	note1("备注1"),
	note2("备注2"),
	note3("备注3");

	@Getter
	private String displayName;

	ColumnEnum(String displayName) {
		this.displayName = displayName;
	}

	public static ColumnEnum fromName(String name) {
		for (ColumnEnum columnEnum : ColumnEnum.values()) {
			if (columnEnum.name().equals(name)) {
				return columnEnum;
			}
		}
		return null;
	}
}
