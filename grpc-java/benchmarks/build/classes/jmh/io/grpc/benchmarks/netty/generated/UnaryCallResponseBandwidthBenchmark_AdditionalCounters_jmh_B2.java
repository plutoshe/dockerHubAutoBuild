package io.grpc.benchmarks.netty.generated;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
public class UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2 extends UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B1 {
    public volatile int setupTrialMutex;
    public volatile int tearTrialMutex;
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> setupTrialMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "setupTrialMutex");
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> tearTrialMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "tearTrialMutex");

    public volatile int setupIterationMutex;
    public volatile int tearIterationMutex;
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> setupIterationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "setupIterationMutex");
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> tearIterationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "tearIterationMutex");

    public volatile int setupInvocationMutex;
    public volatile int tearInvocationMutex;
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> setupInvocationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "setupInvocationMutex");
    public final static AtomicIntegerFieldUpdater<UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2> tearInvocationMutexUpdater = AtomicIntegerFieldUpdater.newUpdater(UnaryCallResponseBandwidthBenchmark_AdditionalCounters_jmh_B2.class, "tearInvocationMutex");

}
