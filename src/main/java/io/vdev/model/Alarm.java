package io.vdev.model;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class Alarm {
    private AlarmType alarmType;
    private AlarmSeverity alarmSeverity;
    private Date timestamp;
    private String message;
}
