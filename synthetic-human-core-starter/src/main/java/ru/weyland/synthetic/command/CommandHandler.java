package ru.weyland.synthetic.command;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import ru.weyland.synthetic.error.CommandQueueOverflowException;
import ru.weyland.synthetic.monitoring.MetricsService;
import ru.weyland.synthetic.queue.TaskQueue;

@Slf4j
public class CommandHandler {

    private final TaskQueue taskQueue;
    private final MetricsService metricsService;
    private final Validator validator;

    public CommandHandler(TaskQueue taskQueue, MetricsService metricsService) {
        this.taskQueue = taskQueue;
        this.metricsService = metricsService;
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    public void process(Command cmd) {
        if (cmd.getCommandType() == CommandType.CRITICAL) {
            log.info("Immediate execution of CRITICAL command: {}", cmd);
            metricsService.incrementCompletedTasks(cmd.getAuthor());
            return;
        }

        try {
            taskQueue.submit(cmd);
            log.info("Enqueued COMMON command: {}", cmd);
        } catch (IllegalStateException ex) {
            throw new CommandQueueOverflowException(
                    "Task queue capacity reached", ex
            );
        }
    }
}
