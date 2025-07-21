package ru.weyland.synthetic.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class MetricsService {

    private final Map<String, Counter> authorCounters = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    /**
     * Регистрирует Gauge для отслеживания текущего размера очереди задач.
     *
     * @param meterRegistry      реестр метрик Micrometer
     * @param queueSizeSupplier  поставщик размера очереди
     */
    public MetricsService(MeterRegistry meterRegistry,
                          Supplier<Integer> queueSizeSupplier) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("synthetic.command.queue.size", queueSizeSupplier)
                .description("Current size of task queue")
                .register(meterRegistry);
    }

    /**
     * Увеличивает счётчик выполненных команд для указанного автора.
     *
     * @param author  имя автора команды
     */
    public void incrementCompletedTasks(String author) {
        authorCounters
                .computeIfAbsent(
                        author,
                        key -> Counter.builder("synthetic.commands.completed")
                                .description("Number of completed commands by author")
                                .tag("author", key)
                                .register(meterRegistry)
                )
                .increment();
    }
}
