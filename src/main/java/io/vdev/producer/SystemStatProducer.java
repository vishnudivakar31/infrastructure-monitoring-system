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

    private static final String NUMBER_OF_PROCESS = "NUMBER_OF_PROCESS";
    private static final String CPU_USAGE_STATS = "CPU_USAGE_STATS";
    private static final String NETWORK_INTERFACE_ADDRESS = "NETWORK_INTERFACE_ADDRESS";
    private static final String NETWORK_SPEED_TEST = "NETWORK_SPEED_TEST";

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

    @Scheduled(every = "60s")
    public void getCurrentNetworkInterfaceAddresses() throws SocketException {
        log.info("getCurrentNetworkInterfaceAddresses starting..");
        systemStatEmitter.send(getNetworkInterfaceAddresses())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getCurrentNetworkInterfaceAddresses emitted successfully");
                    else log.error("getCurrentNetworkInterfaceAddresses emitted failed. {}", failure.getMessage());
                    log.info("getCurrentNetworkInterfaceAddresses stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "10m")
    public void getNetworkSpeed() throws IOException {
        log.info("getNetworkSpeed started..");
        systemStatEmitter.send(getNetworkSpeedReport())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getNetworkSpeed emitted successfully");
                    else log.error("getNetworkSpeed emitted failed. {}", failure.getMessage());
                    log.info("getNetworkSpeed stats emitted at {}", System.currentTimeMillis());
                });
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
}
