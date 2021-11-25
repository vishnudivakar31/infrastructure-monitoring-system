package io.vdev.utils;

import io.vdev.model.*;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.vdev.model.AlarmSeverity.*;
import static io.vdev.producer.SystemStatProducer.*;

@Slf4j
@ApplicationScoped
public class AlarmUtil {

    @Inject
    @Channel("alarmEmitter")
    private Emitter<Alarm> alarmEmitter;

    @Incoming("statCheckEmitter")
    public void checkStatForAlarmCreation(Stat stat) {
        switch (stat.getStatName()) {
            case CPU_USAGE_STATS: checkForCpuAlarm(stat);
                break;
            case NETWORK_SPEED_TEST: checkForNetworkSpeedAlarm(stat);
                break;
            case DISK_USAGE: checkForDiskAlarm(stat);
                break;
            case BATTERY_USAGE_STAT: checkForBatterAlarm(stat);
        }
    }

    private void checkForBatterAlarm(Stat stat) {
        String metrics = ((List<String>)stat.getMeasure()).get(1);
        Pattern regex = Pattern.compile("\\b(?<!\\.)(?!0+(?:\\.0+)?%)(?:\\d|[1-9]\\d|100)(?:(?<!100)\\.\\d+)?%");
        Matcher matcher = regex.matcher(metrics);
        if (matcher.find()) {
            float batteryUsage = Float.parseFloat(matcher.group().replace("%", ""));
            AlarmSeverity alarmSeverity = null;
            String message = "";
            if (batteryUsage < 5) {
                alarmSeverity = DOWN;
                message = "battery is below 5% and almost exhausted. plug-in before system goes down.";
            } else if (batteryUsage < 10) {
                alarmSeverity = FATAL;
                message = "battery is below 10%. plug-in before system goes down.";
            } else if (batteryUsage < 30.0) {
                alarmSeverity = ATTENTION;
                message = "battery is below 30%. plug-in and monitor resource consuming processes.";
            } else if (batteryUsage < 60.0) {
                alarmSeverity = WARNING;
                message = "battery is below 60%. monitor resource consuming processes.";
            }
            if (alarmSeverity != null) {
                alarmEmitter.send(Alarm.builder()
                        .alarmType(AlarmType.BATTERY_ALARM)
                        .alarmSeverity(alarmSeverity)
                        .timestamp(new Date())
                        .message(message)
                        .build());
            }
        }
    }

    private void checkForDiskAlarm(Stat stat) {
        String metrics = ((List<String>)stat.getMeasure()).get(1);
        Pattern regex = Pattern.compile("\\b(?<!\\.)(?!0+(?:\\.0+)?%)(?:\\d|[1-9]\\d|100)(?:(?<!100)\\.\\d+)?%");
        Matcher matcher = regex.matcher(metrics);
        if (matcher.find()) {
            float diskCapacity = 100 - Float.parseFloat(matcher.group().replace("%", ""));
            AlarmSeverity alarmSeverity = null;
            String message = "";
            if (diskCapacity < 10) {
                alarmSeverity = DOWN;
                message = "disk capacity is below 10% and almost exhausted. backup, free some space or add some space.";
            } else if (diskCapacity < 30) {
                alarmSeverity = FATAL;
                message = "disk capacity is below 30%. backup, free some space or add some space.";
            } else if (diskCapacity < 40.0) {
                alarmSeverity = ATTENTION;
                message = "disk capacity is below 40%. backup, free some space or add some space.";
            } else if (diskCapacity < 50.0) {
                alarmSeverity = WARNING;
                message = "battery is below 50%.";
            }
            if (alarmSeverity != null) {
                alarmEmitter.send(Alarm.builder()
                        .alarmType(AlarmType.DISK_ALARM)
                        .alarmSeverity(alarmSeverity)
                        .timestamp(new Date())
                        .message(message)
                        .build());
            }
        }
    }

    private void checkForNetworkSpeedAlarm(Stat stat) {
        List<NetworkSpeedTestResult> metrics = (List<NetworkSpeedTestResult>) stat.getMeasure();
        for (NetworkSpeedTestResult networkSpeedTestResult : metrics) {
            if (networkSpeedTestResult.getNetworkMode().equals(NetworkMode.DOWNLOAD)) generateAlarmForDownload(networkSpeedTestResult.getSpeed());
            else generateAlarmForUpload(networkSpeedTestResult.getSpeed());
        }
    }

    private void generateAlarmForUpload(String speedString) {
        float speed = Float.parseFloat(speedString.replace("Mbit/s", "").trim());
        AlarmSeverity alarmSeverity = null;
        String message = "";
        if (speed < 20) {
            alarmSeverity = DOWN;
            message = "upload speed is less than 20 Mbps. check for network traffic or upgrade the connectivity";
        } else if (speed < 50) {
            alarmSeverity = FATAL;
            message = "upload speed is less than 50 Mbps. check for network traffic";
        } else if (speed < 100) {
            alarmSeverity = ATTENTION;
            message = "upload speed is less than 100 Mbps. might face performance issues";
        } else if (speed < 200) {
            alarmSeverity = WARNING;
            message = "upload speed is less than 200 Mbps.";
        }
        if (alarmSeverity != null) {
            alarmEmitter.send(Alarm.builder()
                    .alarmType(AlarmType.NETWORK_ALARM_UPLOAD)
                    .alarmSeverity(alarmSeverity)
                    .timestamp(new Date())
                    .message(message)
                    .build());
        }
    }

    private void generateAlarmForDownload(String speedString) {
        float speed = Float.parseFloat(speedString.replace("Mbit/s", "").trim());
        AlarmSeverity alarmSeverity = null;
        String message = "";
        if (speed < 50) {
            alarmSeverity = DOWN;
            message = "download speed is less than 50 Mbps. check for network traffic or upgrade the connectivity";
        } else if (speed < 100) {
            alarmSeverity = FATAL;
            message = "download speed is less than 100 Mbps. check for network traffic";
        } else if (speed < 200) {
            alarmSeverity = ATTENTION;
            message = "download speed is less than 200 Mbps. might face performance issues";
        } else if (speed < 350) {
            alarmSeverity = WARNING;
            message = "download speed is less than 350 Mbps.";
        }
        if (alarmSeverity != null) {
            alarmEmitter.send(Alarm.builder()
                    .alarmType(AlarmType.NETWORK_ALARM_DOWNLOAD)
                    .alarmSeverity(alarmSeverity)
                    .timestamp(new Date())
                    .message(message)
                    .build());
        }
    }

    private void checkForCpuAlarm(Stat stat) {
        String[] metrics = stat.getMeasure().toString().split(",");
        Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
        Matcher matcher = regex.matcher(metrics[2]);
        if (matcher.find()) {
            float idle = Float.parseFloat(matcher.group(1));
            AlarmSeverity alarmSeverity = null;
            String message = "";
            if (idle < 10) {
                alarmSeverity = DOWN;
                message = "available cpu less that 10%. close all applications or restart the system";
            } else if (idle < 40) {
                alarmSeverity = FATAL;
                message = "available cpu less that 40%. close un-wanted applications";
            } else if (idle < 80.0) {
                alarmSeverity = ATTENTION;
                message = "available cpu less that 80%. control the cpu resource usages";
            } else if (idle < 95.0) {
                alarmSeverity = WARNING;
                message = "available cpu less that 95%. validate processes running..!!";
            }
            if (alarmSeverity != null) {
                alarmEmitter.send(Alarm.builder()
                        .alarmType(AlarmType.CPU_ALARM)
                        .alarmSeverity(alarmSeverity)
                        .timestamp(new Date())
                        .message(message)
                        .build());
            }
        }
    }
}
