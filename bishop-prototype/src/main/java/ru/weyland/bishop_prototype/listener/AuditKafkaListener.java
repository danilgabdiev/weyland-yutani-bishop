package ru.weyland.bishop_prototype.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditKafkaListener {
    @KafkaListener(
            topics = "audit-log",
            groupId = "bishop-consumer"
    )
    public void onMessage(ConsumerRecord<String, String> rec) {
        // Ловим событие аудита из Kafka и выводим в консоль
        System.out.println(String.format(
                "[KAFKA-AUDIT] Message received: %s",
                rec.value()
        ));
    }
}
