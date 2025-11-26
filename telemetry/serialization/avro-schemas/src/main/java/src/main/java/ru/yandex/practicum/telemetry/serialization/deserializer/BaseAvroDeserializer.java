package src.main.java.ru.yandex.practicum.telemetry.serialization.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DecoderFactory decoderFactory;
    private final Schema schema;
    private final Class<T> targetType;

    public BaseAvroDeserializer(Schema schema, Class<T> targetType) {
        this(DecoderFactory.get(), schema, targetType);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema, Class<T> targetType) {
        this.decoderFactory = decoderFactory;
        this.schema = schema;
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            SpecificDatumReader<T> datumReader = new SpecificDatumReader<>(targetType);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            Decoder decoder = decoderFactory.binaryDecoder(inputStream, null);

            return datumReader.read(null, decoder);
        } catch (IOException e) {
            throw new SerializationException("Error deserializing Avro message for topic: " + topic, e);
        }
    }

    @Override
    public void close() {
    }
}
