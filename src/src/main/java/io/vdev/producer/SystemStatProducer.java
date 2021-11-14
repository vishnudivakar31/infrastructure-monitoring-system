package io.vdev.producer;

import io.quarkus.scheduler.Scheduled;
import io.vdev.model.Stat;
import io.vdev.utils.SystemStatsUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@ApplicationScoped
public class SystemStatProducer {

    private static final String NUMBER_OF_PROCESS = "NUMBER_OF_PROCESS";
    private static final String CPU_USAGE_STATS = "CPU_USAGE_STATS";

    @Inject
    private SystemStatsUtil systemStatsUtil;

    @Inject
    @Channel("system-stats")
    private Emitter<Stat> systemStatEmitter;

    @Scheduled(every = "10s")
    public void getCpuStats() throws IOException {
        log.info("getCpuStats at {}", System.currentTimeMillis());
        systemStatEmitter.send(generateCpuStat())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getCpuStats emitted successfully");
                    else log.error("getCpuStats emitted failed. {}", failure.getMessage());
                    log.info("getCpuStats stats emitted at {}", System.currentTimeMillis());
                });
    }

    @Scheduled(every = "10s")
    public void getNumberOfProcessesStats() throws IOException {
        log.info("getNumberOfProcessesStats at {}", System.currentTimeMillis());
        systemStatEmitter.send(generateNumberOfProcessStat())
                .whenComplete((success, failure) -> {
                    if (failure == null) log.info("getNumberOfProcessesStats emitted successfully");
                    else log.error("getNumberOfProcessesStats emitted failed. {}", failure.getMessage());
                    log.info("getNumberOfProcessesStats stats emitted at {}", System.currentTimeMillis());
                });
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
