package src.main.java.ru.yandex.practicum.telemetry.serialization.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Component
public class CollectorAvroSerializer implements Serializer<SpecificRecordBase> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        if (data == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<SpecificRecordBase> datumWriter = new SpecificDatumWriter<>(data.getSchema());

            datumWriter.write(data, encoder);
            encoder.flush();

            byte[] result = outputStream.toByteArray();

            return result;

        } catch (IOException e) {
            throw new SerializationException(
                    "Ошибка сериализации Avro данных для топика [" + topic + "]", e);
        }
    }

    @Override
    public void close() {

    }
}