package ru.weyland.synthetic.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Aspect
@Component
@Slf4j
public class ActionLogger {
    private final KafkaTemplate<String, String> kafka;
    private final AuditProperties props;
    private final AtomicReference<String> currentMode;

    public ActionLogger(KafkaTemplate<String, String> kafka, AuditProperties props) {
        this.kafka = kafka;
        this.props = props;
        this.currentMode = new AtomicReference<>(props.getMode());
    }

    public void switchMode(String mode) {
        this.currentMode.set(mode);
    }

    @Around("@annotation(ru.weyland.synthetic.audit.WeylandWatchingYou)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        var signature = (MethodSignature) pjp.getSignature();
        var methodName = signature.getName();
        var parameters = pjp.getArgs();

        var result = pjp.proceed();
        var message = formatAuditMessage(methodName, parameters, result);

        if (isKafkaMode()) {
            kafka.send(props.getTopic(), message);
        } else {
            log.info("[ACTION] {}", message);
        }

        return result;
    }

    private String formatAuditMessage(String method, Object[] args, Object result) {
        return String.format(
                "Method: %s, Args: %s, Result: %s",
                method,
                Arrays.toString(args),
                result
        );
    }

    private boolean isKafkaMode() {
        return "kafka".equalsIgnoreCase(currentMode.get());
    }
}