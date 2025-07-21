package ru.weyland.bishop_prototype.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.weyland.bishop_prototype.service.ExecutionService;
import ru.weyland.synthetic.audit.WeylandWatchingYou;
import ru.weyland.synthetic.command.Command;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class CommandController {

    private final ExecutionService executionService;

    @PostMapping
    @WeylandWatchingYou
    public ResponseEntity<String> submitCommand(@RequestBody @Valid Command command) {
        if (command.getTime() == null || command.getTime().isBlank()) {
            String now = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_INSTANT);
            command.setTime(now);
        }

        executionService.handleCommand(command);

        return ResponseEntity.ok("Command accepted");
    }
}
