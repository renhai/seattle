package com.renhai.manage.dto;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by andy on 8/20/17.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDto {
    private int successfulCount;
    private int failedCount;
    private List<Integer> failedLineNumbers;

    public String toString() {
        return String.format("成功：%d\n失败：%d\n失败行：%s", successfulCount, failedCount, StringUtils.join(failedLineNumbers), ", ");
    }
}
