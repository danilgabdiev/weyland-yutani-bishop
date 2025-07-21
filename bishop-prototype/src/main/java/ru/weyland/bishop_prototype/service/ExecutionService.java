package ru.weyland.bishop_prototype.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.weyland.synthetic.command.Command;
import ru.weyland.synthetic.command.CommandHandler;

@Service
@RequiredArgsConstructor
public class ExecutionService {
    private final CommandHandler commandProcessor;

    public void handleCommand(Command command) {
        commandProcessor.process(command);
    }
}
