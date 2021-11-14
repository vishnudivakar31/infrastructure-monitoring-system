package io.vdev.utils;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Singleton
public class SystemStatsUtil {
    private String[] FIND_ALL_PROCESS_CMD = {"/bin/sh", "-c", "top -l 1 | grep -E thread"};
    private String[] FIND_CPU_USAGES = {"/bin/sh", "-c", "top -l 1 | grep -E ^CPU"};
    private Runtime runtime = Runtime.getRuntime();

    public String findNumberOfProcess() throws IOException {
        String line = "";
        Process process = runtime.exec(FIND_ALL_PROCESS_CMD);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = bufferedReader.readLine()) != null) {
            return line;
        }
        process.destroy();
        return "";
    }

    public String findCpuUsages() throws IOException {
        String line = "";
        Process process = runtime.exec(FIND_CPU_USAGES);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = bufferedReader.readLine()) != null) {
            System.out.println("CPU INFO: " + line);
            return line;
        }
        bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        process.destroy();
        return "";
    }
}
