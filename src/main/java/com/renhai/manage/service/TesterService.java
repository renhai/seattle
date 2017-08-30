package com.renhai.manage.service;

import com.renhai.manage.dto.TesterDto;
import com.renhai.manage.dto.UploadResultDto;
import com.renhai.manage.entity.Tester;
import com.renhai.manage.repository.TesterRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by andy on 8/27/17.
 */
@Service
@Slf4j
public class TesterService {
    private static SimpleDateFormat EXCEL_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public TesterService() {
        log.info("TesterService init");
    }

    @Autowired
    private TesterRepository testerRepository;

    public UploadResultDto uploadExcel(File excelFile) {
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(excelFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);

        Row header = sheet.getRow(0);
        StringBuilder sb = new StringBuilder();
        for (Cell cell : header) {
            sb.append(cell.toString()).append("\t");
        }
        log.info(sb.toString());

        int successfulCount = 0;
        int failedCount = 0;
        List<Integer> failedLineNumbers = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            try {
                Tester tester = Tester.builder()
                        .name(row.getCell(0).getStringCellValue())
                        .account(org.apache.commons.lang3.StringUtils.trimToNull(row.getCell(1).getStringCellValue()))
                        .gender(Tester.Gender.fromText(StringUtils.trimWhitespace(row.getCell(2).getStringCellValue())))
                        .badgeNo(org.apache.commons.lang3.StringUtils.trimToNull(row.getCell(3).getStringCellValue()))
                        .idNo(org.apache.commons.lang3.StringUtils.trimToNull(row.getCell(4).getStringCellValue()))
                        .education(row.getCell(5).getStringCellValue())
                        .jobTitle(row.getCell(6).getStringCellValue())
                        .occupation(row.getCell(7).getStringCellValue())
                        .workUnit(row.getCell(8).getStringCellValue())
                        .zipCode(row.getCell(9).getStringCellValue())
                        .workAddress(row.getCell(10).getStringCellValue())
                        .workPhone(row.getCell(11).getStringCellValue())
                        .homePhone(row.getCell(12).getStringCellValue())
                        .cellPhone(row.getCell(13).getStringCellValue())
                        .telMobile(row.getCell(14).getStringCellValue())
                        .email(org.apache.commons.lang3.StringUtils.trimToNull(row.getCell(15).getStringCellValue()))
                        .dialect(row.getCell(16).getStringCellValue())
                        .cnTestDate(StringUtils.isEmpty(row.getCell(17).getStringCellValue()) ? null : EXCEL_DATE_FORMAT.parse(row.getCell(17).getStringCellValue()))
                        .cnScore(StringUtils.isEmpty(row.getCell(18).getStringCellValue()) ? null : Double.parseDouble(StringUtils.trimWhitespace(row.getCell(18).getStringCellValue())))
                        .level(Tester.Level.fromText(StringUtils.trimWhitespace(row.getCell(19).getStringCellValue())))
                        .grade(Tester.Grade.fromText(StringUtils.trimWhitespace(row.getCell(20).getStringCellValue())))
                        .bankName(row.getCell(21).getStringCellValue())
                        .bankAccount(row.getCell(22).getStringCellValue())
                        .build();
                testerRepository.save(tester);
                successfulCount++;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                failedCount++;
                failedLineNumbers.add(i);
            }
        }
        return new UploadResultDto(successfulCount, failedCount, failedLineNumbers);
    }

    public List<TesterDto> getAllTesters() {
        List<Tester> testers = IteratorUtils.toList(testerRepository.findAll().iterator());
        List<TesterDto> result = testers.stream().map(tester -> new TesterDto(tester)).collect(Collectors.toList());
        return result;
    }

    @Transactional
    public TesterDto updateTester(Integer id, String fieldName, Object value)
        throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ParseException {
        Tester tester = testerRepository.findOne(id);
        Field field = FieldUtils.getField(Tester.class, fieldName, true);

        if (field.getType().isEnum()) {
            Object realValue = MethodUtils.invokeStaticMethod(field.getType(), "fromText", value);
            FieldUtils.writeDeclaredField(tester, fieldName, realValue, true);
        } else {
            FieldUtils.writeDeclaredField(tester, fieldName, value, true);
        }

        Tester entity = testerRepository.save(tester);
        return new TesterDto(entity);
    }

    @Transactional
    public void deleteTesters(List<Integer> ids) {
        for (Integer id : ids) {
            testerRepository.delete(id);
        }
    }

    @Transactional
    public TesterDto createTester(Tester tester) {
        Tester entity = testerRepository.save(tester);
        return new TesterDto(entity);
    }

}
