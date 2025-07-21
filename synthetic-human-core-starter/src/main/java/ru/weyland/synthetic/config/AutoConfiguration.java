package ru.weyland.synthetic.config;


import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import ru.weyland.synthetic.audit.ActionLogger;
import ru.weyland.synthetic.audit.AuditProperties;
import ru.weyland.synthetic.audit.AuditSwitcherController;
import ru.weyland.synthetic.command.CommandHandler;
import ru.weyland.synthetic.error.GlobalExceptionHandler;
import ru.weyland.synthetic.monitoring.MetricsService;
import ru.weyland.synthetic.queue.TaskQueue;

@Configuration
@EnableConfigurationProperties(AuditProperties.class)
public class AutoConfiguration {

    @Bean
    public TaskQueue commandQueueExecutor() {
        return new TaskQueue(3); // default queue size
        // (для примера взято поменьше чтобы можно было отловить ошибку)
        //также в TaskQueue можно добавить задержку
    }

    @Bean
    public CommandHandler commandProcessor(TaskQueue executor, MetricsService metricsService) {
        executor.setOnCommandExecuted(command -> metricsService.incrementCompletedTasks(command.getAuthor()));
        executor.startProcessing();
        return new CommandHandler(executor, metricsService);
    }

    @Bean
    public MetricsService metricsService(MeterRegistry registry, TaskQueue executor) {
        return new MetricsService(registry, executor::getQueueSize);
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public ActionLogger auditAspect(KafkaTemplate<String, String> kafkaTemplate, AuditProperties props) {
        return new ActionLogger(kafkaTemplate, props);
    }

    @Bean
    public AuditSwitcherController auditSwitcherController(ActionLogger actionLogger) {
        return new AuditSwitcherController(actionLogger);
    }
}
