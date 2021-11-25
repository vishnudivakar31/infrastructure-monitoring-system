package io.vdev.utils;

import io.vdev.model.NetworkMode;
import io.vdev.model.NetworkSpeedTestResult;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Singleton
public class SystemStatsUtil {
    private static final String[] BATTERY_STATUS = {"/bin/sh", "-c", "pmset -g batt"};
    private String[] SPEED_TEST_CMD = {"/bin/sh", "-c", "speedtest-cli"};
    private String[] FIND_ALL_PROCESS_CMD = {"/bin/sh", "-c", "top -l 1 | grep -E thread"};
    private String[] FIND_CPU_USAGES = {"/bin/sh", "-c", "top -l 1 | grep -E ^CPU"};
    private String[] FIND_DISK_USAGES = {"/bin/sh", "-c", "df -h /"};
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
            return line;
        }
        bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        process.destroy();
        return "";
    }

    public List<String> getNetworkAddresses() throws SocketException {
        List<String> result = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while(enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
            Enumeration interfaceEnumeration = networkInterface.getInetAddresses();
            while(interfaceEnumeration.hasMoreElements()) {
                result.add(((InetAddress) interfaceEnumeration.nextElement()).getHostAddress().toString());
            }
        }
        return result;
    }

    public static String getHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

    public List<NetworkSpeedTestResult> getSpeedTestReport() throws IOException {
        List<NetworkSpeedTestResult> result = new LinkedList<>();
        Process process = runtime.exec(SPEED_TEST_CMD);
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = bufferedReader.readLine()) != null) {
            if (line.startsWith("Download")) {
                result.add(NetworkSpeedTestResult.builder()
                        .networkMode(NetworkMode.DOWNLOAD)
                        .speed(line.split(":")[1].trim())
                        .build());
            } else if (line.startsWith("Upload")) {
                result.add(NetworkSpeedTestResult.builder()
                        .networkMode(NetworkMode.UPLOAD)
                        .speed(line.split(":")[1].trim())
                        .build());
            }
        }
        return result;
    }

    public List<String> getDiskUsage() throws IOException {
        List<String> result = new LinkedList<>();
        String line = "";
        Process process = runtime.exec(FIND_DISK_USAGES);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    public List<String> getBatteryStatus() throws IOException {
        List<String> result = new LinkedList<>();
        String line = "";
        Process process = runtime.exec(BATTERY_STATUS);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while((line = bufferedReader.readLine()) != null) {
            result.add(line);
        }
        return result;
    }
}
