package io.vdev.model;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class Stat {
    private String statName;
    private String statUnit;
    private Date timestamp;
    private Object measure;
}
