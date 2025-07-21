package ru.weyland.synthetic.queue;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.weyland.synthetic.command.Command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Slf4j
public class TaskQueue {

    private final BlockingQueue<Command> queue;
    private final ExecutorService executor;
    @Setter
    private Consumer<Command> onCommandExecuted = c -> {};
    private boolean started = false;


    public TaskQueue(int queueCapacity) {
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void submit(Command command) throws IllegalStateException {
        if (!queue.offer(command)) {
            throw new IllegalStateException("Command queue is full");
        }

    }

    public int getQueueSize() {
        return queue.size();
    }

    public void startProcessing() {
        if (started) return;
        started = true;

        executor.submit(() -> {
            while (true) {
                try {
                    Command command = queue.take();

                    Thread.sleep(5000);

                    log.info("Executing command: " + command);
                    onCommandExecuted.accept(command);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
