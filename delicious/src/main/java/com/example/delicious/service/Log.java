package com.example.delicious.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Log {
    public void setLog(){
        log.info("这是INFO信息");
        log.debug("DEBUG");
        log.error("ERROR");
    }
}
