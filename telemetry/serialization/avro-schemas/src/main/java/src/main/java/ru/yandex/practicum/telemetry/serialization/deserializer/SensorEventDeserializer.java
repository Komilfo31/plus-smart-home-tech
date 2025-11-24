package src.main.java.ru.yandex.practicum.telemetry.serialization.deserializer;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
public class SensorEventDeserializer extends BaseAvroDeserializer<SensorEventAvro> {
    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema(), SensorEventAvro.class);
    }
}
