package com.skillsup.lockfree.list;


import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5)
@Fork(2)
public class LockFreeListBenchmark {

    List<String> lockFreeList;
    List<String> copyOnWrite;

    @Setup
    public void setup() {
        lockFreeList = new LockFreeList<>();
        copyOnWrite = new CopyOnWriteArrayList<>();
    }

    @Benchmark
    @Threads(4)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.NANOSECONDS)
    public boolean testLockFreeList() {
        return lockFreeList.add("TestString");
    }

    @Benchmark
    @Threads(4)
    @Measurement(iterations = 5, time = 100, timeUnit = TimeUnit.NANOSECONDS)
    public boolean testCopyOnWrite() {
        return copyOnWrite.add("TestString");
    }
}
