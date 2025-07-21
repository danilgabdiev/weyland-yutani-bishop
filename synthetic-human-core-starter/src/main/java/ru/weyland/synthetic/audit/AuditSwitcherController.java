package ru.weyland.synthetic.audit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/audit")
public class AuditSwitcherController {

    private final ActionLogger actionLogger;
    private static final Set<String> ALLOWED_MODES = Set.of("console", "kafka");

    public AuditSwitcherController(ActionLogger actionLogger) {
        this.actionLogger = actionLogger;
    }

    @PostMapping("/switch")
    public ResponseEntity<String> switchAuditMode(@RequestParam("mode") String mode) {
        if (!ALLOWED_MODES.contains(mode.toLowerCase())) {
            return ResponseEntity.badRequest().body("Invalid mode. Allowed values: console, kafka");
        }
        actionLogger.switchMode(mode);
        return ResponseEntity.ok("Audit mode changed to: " + mode);
    }
}
