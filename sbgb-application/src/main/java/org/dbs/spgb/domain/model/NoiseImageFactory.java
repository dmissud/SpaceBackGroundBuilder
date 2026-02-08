package org.dbs.spgb.domain.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoiseImageFactory {
    public NoiseImageBuilder createPerlinNoiseImage() {
        return new PerlinNoiseImabeBuilder();
    }
}
