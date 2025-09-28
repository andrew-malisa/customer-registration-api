package com.vodacom.customerregistration.api.util;

import org.springframework.stereotype.Component;

/**
 * Snowflake ID Generator
 * 
 * Generates unique 64-bit IDs in a distributed system.
 * Format: 1 bit (sign) + 41 bits (timestamp) + 10 bits (machine) + 12 bits (sequence)
 * 
 * This allows for:
 * - ~69 years of timestamps (from epoch)
 * - 1024 different machines/nodes
 * - 4096 IDs per millisecond per machine
 */
@Component
public class SnowflakeIdGenerator {
    
    // Epoch timestamp (January 1, 2023 00:00:00 UTC)
    private static final long EPOCH = 1672531200000L;
    
    // Bit lengths
    private static final long MACHINE_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    
    // Maximum values
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1; // 1023
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 4095
    
    // Bit shifts
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS; // 12
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS; // 22
    
    private final long machineId;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;
    
    public SnowflakeIdGenerator() {
        // Generate machine ID based on system properties or use default
        this.machineId = generateMachineId();
    }
    
    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(
                String.format("Machine ID must be between 0 and %d", MAX_MACHINE_ID)
            );
        }
        this.machineId = machineId;
    }
    
    /**
     * Generate the next unique ID
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();
        
        // Handle clock going backwards
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", 
                    lastTimestamp - timestamp)
            );
        }
        
        // Same millisecond - increment sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            
            // Sequence overflow - wait for next millisecond
            if (sequence == 0) {
                timestamp = waitForNextMillisecond(lastTimestamp);
            }
        } else {
            // New millisecond - reset sequence
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // Combine all parts
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT) |
               (machineId << MACHINE_ID_SHIFT) |
               sequence;
    }
    
    /**
     * Parse a Snowflake ID to extract its components
     */
    public SnowflakeIdInfo parseId(long id) {
        long timestamp = ((id >> TIMESTAMP_SHIFT) + EPOCH);
        long machineId = (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
        long sequence = id & MAX_SEQUENCE;
        
        return new SnowflakeIdInfo(timestamp, machineId, sequence);
    }
    
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    private long waitForNextMillisecond(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    private long generateMachineId() {
        try {
            // Use hash of hostname and process ID
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            long hash = (hostname + processName).hashCode();
            return Math.abs(hash) % (MAX_MACHINE_ID + 1);
        } catch (Exception e) {
            // Fallback to random machine ID
            return (long) (Math.random() * MAX_MACHINE_ID);
        }
    }
    
    /**
     * Information extracted from a Snowflake ID
     */
    public static class SnowflakeIdInfo {
        private final long timestamp;
        private final long machineId;
        private final long sequence;
        
        public SnowflakeIdInfo(long timestamp, long machineId, long sequence) {
            this.timestamp = timestamp;
            this.machineId = machineId;
            this.sequence = sequence;
        }
        
        public long getTimestamp() { return timestamp; }
        public long getMachineId() { return machineId; }
        public long getSequence() { return sequence; }
        
        @Override
        public String toString() {
            return String.format("SnowflakeId{timestamp=%d, machineId=%d, sequence=%d}", 
                timestamp, machineId, sequence);
        }
    }
}