package ru.weyland.synthetic.audit;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "synthetic.audit")
@Validated
@Getter
@Setter
public class AuditProperties {
    @NotBlank
    private String mode = "console";

    private String topic = "audit-log";
}