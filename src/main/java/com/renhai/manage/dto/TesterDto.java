package com.renhai.manage.dto;

import com.renhai.manage.entity.Tester;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hai on 8/16/17.
 */
public class TesterDto {

    public static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Tester tester;

    public TesterDto(Tester tester) {
        this.tester = tester;
    }
    public Integer getId() {
        return tester.getId();
    }

    public String getName() {
        return tester.getName();
    }

    public String getAccount() {
        return tester.getAccount();
    }

    public String getGender() {
        return tester.getGender() == null ? "" : tester.getGender().getText();
    }

    public String getBadgeNo() {
        return tester.getBadgeNo();
    }

    public String getIdNo() {
        return tester.getIdNo();
    }

    public String getEducation() {
        return tester.getEducation();
    }

    public String getJobTitle() {
        return tester.getJobTitle();
    }

    public String getOccupation() {
        return tester.getOccupation();
    }

    public String getWorkUnit() {
        return tester.getWorkUnit();
    }

    public String getZipCode() {
        return tester.getZipCode();
    }

    public String getWorkAddress() {
        return tester.getWorkAddress();
    }

    public String getWorkPhone() {
        return tester.getWorkPhone();
    }

    public String getHomePhone() {
        return tester.getHomePhone();
    }

    public String getCellPhone() {
        return tester.getCellPhone();
    }

    public String getTelMobile() {
        return tester.getTelMobile();
    }

    public String getEmail() {
        return tester.getEmail();
    }

    public String getDialect() {
        return tester.getDialect();
    }

    public Date getCnTestDate() {
        return tester.getCnTestDate();
    }

    public Double getCnScore() {
        return tester.getCnScore();
    }

    public String getLevel() {
        return tester.getLevel() == null ? "" : tester.getLevel().getText();
    }

    public String getGrade() {
        return tester.getGrade() == null ? "" : tester.getGrade().getText();
    }

    public String getBankName() {
        return tester.getBankName();
    }

    public String getBankAccount() {
        return tester.getBankAccount();
    }

    public Date getDob() {
        return tester.getDob();
    }

    public Integer getAge() {
        if (tester.getDob() == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tester.getDob());
        LocalDate dob = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        LocalDate now = LocalDate.now();
        return Period.between(dob, now).getYears();
    }

    public Integer getTrainingYear() {
        return tester.getTrainingYear();
    }

    public Integer getTestCount() {
        return tester.getTestCount();
    }

    public String getStatus() {
        return tester.getStatus() == null ? "" : tester.getStatus().getText();
    }

    public Integer getScore() {
        return tester.getScore();
    }

    public String getBackboneClass() {
        return tester.getBackboneClass();
    }

    public String getTestCenter() {
        return tester.getTestCenter();
    }

    public Integer getTermNo() {
        return tester.getTermNo();
    }

    public String getNote1() {
        return tester.getNote1();
    }
    public String getNote2() {
        return tester.getNote2();
    }

    public String getNote3() {
        return tester.getNote3();
    }


}
