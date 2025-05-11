package com.dnasearch.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MonitoringConfig {

    @Bean
    public ThreadPoolTaskExecutor monitoredExecutor(MeterRegistry registry) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("monitored-");
        executor.initialize();

        // Register metrics
        registry.gauge("thread.pool.active", executor, ThreadPoolTaskExecutor::getActiveCount);
        registry.gauge("thread.pool.size", executor, ThreadPoolTaskExecutor::getPoolSize);
        registry.gauge("thread.pool.queued", executor, ThreadPoolTaskExecutor::getThreadPoolExecutor, 
            ThreadPoolTaskExecutor::getQueueSize);

        return executor;
    }
} 