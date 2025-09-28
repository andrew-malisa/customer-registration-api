package com.vodacom.customerregistration.api.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test class for SnowflakeIdGenerator
 */
class SnowflakeIdGeneratorTest {

    private SnowflakeIdGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new SnowflakeIdGenerator(1L); // Use machine ID 1 for consistent testing
    }

    @Test
    void shouldGenerateUniqueIds() {
        Set<Long> ids = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            long id = generator.nextId();
            assertThat(ids.add(id)).isTrue(); // Should be unique
            assertThat(id).isPositive(); // Should be positive
        }

        assertThat(ids).hasSize(count);
    }

    @Test
    void shouldGenerateIdsInAscendingOrder() {
        long previousId = 0;
        for (int i = 0; i < 100; i++) {
            long id = generator.nextId();
            assertThat(id).isGreaterThan(previousId);
            previousId = id;
        }
    }

    @Test
    void shouldParseIdCorrectly() {
        long id = generator.nextId();
        SnowflakeIdGenerator.SnowflakeIdInfo info = generator.parseId(id);

        assertThat(info.getMachineId()).isEqualTo(1L);
        assertThat(info.getTimestamp()).isLessThanOrEqualTo(System.currentTimeMillis());
        assertThat(info.getSequence()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void shouldThrowExceptionForInvalidMachineId() {
        assertThatThrownBy(() -> new SnowflakeIdGenerator(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Machine ID must be between 0 and");

        assertThatThrownBy(() -> new SnowflakeIdGenerator(1024L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Machine ID must be between 0 and");
    }

    @Test
    void shouldHandleConcurrentGeneration() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> allIds = new HashSet<>();
        AtomicLong counter = new AtomicLong(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Set<Long> threadIds = new HashSet<>();
                    for (int j = 0; j < idsPerThread; j++) {
                        long id = generator.nextId();
                        threadIds.add(id);
                        counter.incrementAndGet();
                    }
                    
                    synchronized (allIds) {
                        // Check no duplicates with other threads
                        for (Long id : threadIds) {
                            assertThat(allIds.add(id)).isTrue();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(allIds).hasSize(threadCount * idsPerThread);
        assertThat(counter.get()).isEqualTo(threadCount * idsPerThread);
    }

    @Test
    void shouldFitInLongRange() {
        // Generate several IDs and ensure they fit in Long range
        for (int i = 0; i < 1000; i++) {
            long id = generator.nextId();
            assertThat(id).isGreaterThan(0L);
            assertThat(id).isLessThan(Long.MAX_VALUE);
        }
    }
}