package io.vdev.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class NetworkSpeedTestResult {
    private NetworkMode networkMode;
    private String speed;
}

