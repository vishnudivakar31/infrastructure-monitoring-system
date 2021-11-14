package io.vdev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stat {
    private String statName;
    private String statUnit;
    private Date timestamp;
    private Object measure;
}
