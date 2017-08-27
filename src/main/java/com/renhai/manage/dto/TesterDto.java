package com.renhai.manage.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

/**
 * Created by andy on 8/27/17.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TesterDto {
    private IntegerProperty id;
    private StringProperty name;
}
