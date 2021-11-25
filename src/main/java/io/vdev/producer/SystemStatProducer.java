package io.vdev.producer;

import io.quarkus.scheduler.Scheduled;
import io.vdev.model.NetworkSpeedTestResult;
import io.vdev.model.Stat;
import io.vdev.utils.SystemStatsUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.SocketException;
import java.util.Date;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SystemStatProducer {

    public static final String NUMBER_OF_PROCESS = "NUMBER_OF_PROCESS";
    public static final String CPU_USAGE_STATS = "CPU_USAGE_STATS";
    public static final String NETWORK_INTERFACE_ADDRESS = "NETWORK_INTERFACE_ADDRESS";
    public static final String NETWORK_SPEED_TEST = "NETWORK_SPEED_TEST";
    public static final String DISK_USAGE = "DISK_USAGE";
    public static final String BATTERY_USAGE_STAT = "BATTERY_USAGE_STAT";

    @Inject
    private SystemStatsUtil systemStatsUtil;

    @Inject
    @Channel("system-stats")
    private Emitter<Stat> systemStatEmitter;


    @Scheduled(every = "60s")
    public void getCpuStats() throws IOException {
        log.info("getCpuStats at {}", System.currentTimeMillis());
        systemStatEmitter.send(generateCpuStat())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getCpuStats emitted successfully");
                    else log.error("getCpuStats emitted failed. {}", failure.getMessage());
                    log.info("getCpuStats stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "60s")
    public void getNumberOfProcessesStats() throws IOException {
        log.info("getNumberOfProcessesStats at {}", System.currentTimeMillis());
        systemStatEmitter.send(generateNumberOfProcessStat())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getNumberOfProcessesStats emitted successfully");
                    else log.error("getNumberOfProcessesStats emitted failed. {}", failure.getMessage());
                    log.info("getNumberOfProcessesStats stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "60m")
    public void getCurrentNetworkInterfaceAddresses() throws SocketException {
        log.info("getCurrentNetworkInterfaceAddresses starting..");
        systemStatEmitter.send(getNetworkInterfaceAddresses())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getCurrentNetworkInterfaceAddresses emitted successfully");
                    else log.error("getCurrentNetworkInterfaceAddresses emitted failed. {}", failure.getMessage());
                    log.info("getCurrentNetworkInterfaceAddresses stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "5m")
    public void getNetworkSpeed() throws IOException {
        log.info("getNetworkSpeed started..");
        systemStatEmitter.send(getNetworkSpeedReport())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getNetworkSpeed emitted successfully");
                    else log.error("getNetworkSpeed emitted failed. {}", failure.getMessage());
                    log.info("getNetworkSpeed stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "10m")
    public void getDiskUsages() throws IOException {
        log.info("getDiskUsages at {}", System.currentTimeMillis());
        systemStatEmitter.send(getDiskUsageStat())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getDiskUsages emitted successfully");
                    else log.error("getDiskUsages emitted failed. {}", failure.getMessage());
                    log.info("getDiskUsages stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "60s")
    public void getBatteryStatus() throws IOException {
        log.info("getBatteryStatus at {}", System.currentTimeMillis());
        systemStatEmitter.send(checkBatteryStatus())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getBatteryStatus emitted successfully");
                    else log.error("getBatteryStatus emitted failed. {}", failure.getMessage());
                    log.info("getBatteryStatus stats emitted at {}", System.currentTimeMillis());
                });
    }

    private Stat getDiskUsageStat() throws IOException {
        List<String> diskUsages = systemStatsUtil.getDiskUsage();
        return Stat.builder()
                .statName(DISK_USAGE)
                .statUnit("")
                .measure(diskUsages)
                .timestamp(new Date())
                .build();
    }

    private Stat getNetworkSpeedReport() throws IOException {
        List<NetworkSpeedTestResult> results = systemStatsUtil.getSpeedTestReport();
        return Stat.builder()
                .timestamp(new Date())
                .statName(NETWORK_SPEED_TEST)
                .statUnit("")
                .measure(results)
                .build();
    }

    private Stat getNetworkInterfaceAddresses() throws SocketException {
        List<String> ipAddresses = systemStatsUtil.getNetworkAddresses();
        return Stat.builder()
                .timestamp(new Date())
                .statName(NETWORK_INTERFACE_ADDRESS)
                .statUnit("")
                .measure(ipAddresses)
                .build();
    }

    private Stat generateNumberOfProcessStat() throws IOException {
        String numberOfProcess = systemStatsUtil.findNumberOfProcess();
        return Stat.builder()
                .timestamp(new Date())
                .statName(NUMBER_OF_PROCESS)
                .statUnit("nos")
                .measure(numberOfProcess)
                .build();
    }

    private Stat generateCpuStat() throws IOException {
        String cpuStats = systemStatsUtil.findCpuUsages();
        return Stat.builder()
                .timestamp(new Date())
                .statName(CPU_USAGE_STATS)
                .statUnit("")
                .measure(cpuStats)
                .build();
    }

    private Stat checkBatteryStatus() throws IOException {
        List<String> batteryStatus = systemStatsUtil.getBatteryStatus();
        return Stat.builder()
                .timestamp(new Date())
                .statName(BATTERY_USAGE_STAT)
                .statUnit("")
                .measure(batteryStatus)
                .build();
    }
}
