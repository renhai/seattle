package com.renhai.manage.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by hai on 6/26/17.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(
		name = "tester",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_tester_account", columnNames = {"account"}),
				@UniqueConstraint(name = "uk_tester_badgeNo", columnNames = {"badgeNo"}),
				@UniqueConstraint(name = "uk_tester_idNo", columnNames = {"idNo"}),
				@UniqueConstraint(name = "uk_tester_email", columnNames = {"email"}),
		})
public class Tester {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 姓名 */
	@NotNull
	private String name;
	/** 账号 */
	@NotNull
	private String account;
	/** 性别 */
	@Enumerated(EnumType.STRING)
	@Column(length = 1)
	private Gender gender;
	/** 工作证编号 */
	private String badgeNo;
	/** 身份证号 */
	@Column(length = 18)
	private String idNo;
	/** 学历 */
	private String education;
	/** 职称 */
	private String jobTitle;
	/** 职务 */
	private String occupation;
	/** 工作单位 */
	private String workUnit;
	/** 邮编 */
	private String zipCode;
	/** 工作地址 */
	private String workAddress;
	/** 办公电话 */
	private String workPhone;
	/** 工作地址 */
	private String homePhone;
	/** 手机 */
	private String cellPhone;
	/** TelMobile */
	private String telMobile;
	/** 邮箱 */
	private String email;
	/** 方言 */
	private String dialect;
	/** CNTestDate */
	@Temporal(TemporalType.TIMESTAMP)
	private Date cnTestDate;
	/** CNScore */
	private Double cnScore;
	@Enumerated(EnumType.STRING)
	/** 测试员等级 */
	private Level level;
	/** 类别 */
	@Enumerated(EnumType.STRING)
	private Grade grade;
	/** 银行 */
	private String bankName;
	/** 账户 */
	private String bankAccount;


	/** 生日 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date dob;
	/** 培I: 15年, 培II: 16年, 培III: 17年 */
	private Integer trainingYear;
	/** 测试量 */
	private Integer testCount;
	/** 状态 */
	@Enumerated(EnumType.STRING)
	private Status status;
	/** 成绩 */
	private Integer score;
	/** 骨干班次 */
	private String backboneClass;
	/** 测试分中心 */
	private String testCenter;
	/** 期数 */
	private Integer termNo;
	/** 备注1 */
	private String note1;
	/** 备注2 */
	private String note2;
	/** 备注3 */
	private String note3;

	public enum Gender {
		M("男"), F("女");

		@Getter
		private String text;
		Gender(String text) {
			this.text = text;
		}

		public static Gender fromText(String text) {
			for (Gender gender : Gender.values()) {
				if (gender.text.equals(text)) {
					return gender;
				}
			}
			return null;
		}
	}

	public enum Level {
		PROVINCE("省级"), NATIONAL("国家级");

		@Getter
		private String text;
		Level(String text) {
			this.text = text;
		}

		public static Level fromText(String text) {
			for (Level level : Level.values()) {
				if (level.text.equals(text)) {
					return level;
				}
			}
			return null;
		}
	}

	public enum Grade {
		A("A类测试员"), B("B类测试员"), C("C类测试员"), D("D类测试员"), EXPERT("专家测试员");

		@Getter
		private String text;
		Grade(String text) {
			this.text = text;
		}

		public static Grade fromText(String text) {
			for (Grade grade : Grade.values()) {
				if (grade.text.equals(text)) {
					return grade;
				}
			}
			return null;
		}
	}

	public enum Status {
		IDLE("空闲"), BUSY("忙碌");

		@Getter
		private String text;
		Status(String text) {
			this.text = text;
		}

		public static Status fromText(String text) {
			for (Status status : Status.values()) {
				if (status.text.equals(text)) {
					return status;
				}
			}
			return null;
		}
	}
}


