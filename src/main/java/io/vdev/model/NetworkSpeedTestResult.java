package io.vdev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NetworkSpeedTestResult {
    private NetworkMode networkMode;
    private String speed;
}

