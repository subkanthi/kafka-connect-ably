package com.ably.kafka.connect.mapping;

import com.ably.kafka.connect.config.ChannelSinkConnectorConfig;
import com.ably.kafka.connect.config.ConfigValueEvaluator;
import com.ably.kafka.connect.utils.KafkaExtrasExtractor;
import io.ably.lib.types.Message;
import io.ably.lib.types.MessageExtras;
import io.ably.lib.util.JsonUtils;
import org.apache.kafka.connect.sink.SinkRecord;

import javax.annotation.Nonnull;

import static com.ably.kafka.connect.config.ChannelSinkConnectorConfig.MESSAGE_CONFIG;

public class DefaultMessageSinkMapping implements MessageSinkMapping {

    private final ChannelSinkConnectorConfig sinkConnectorConfig;
    private final ConfigValueEvaluator configValueEvaluator;

    public DefaultMessageSinkMapping(@Nonnull ChannelSinkConnectorConfig config, @Nonnull ConfigValueEvaluator configValueEvaluator) {
        this.sinkConnectorConfig = config;
        this.configValueEvaluator = configValueEvaluator;
    }

    @Override
    public Message getMessage(SinkRecord record) {
        final String messageName = configValueEvaluator.evaluate(record, sinkConnectorConfig.getString(MESSAGE_CONFIG));
        Message message = new Message(messageName, record.value());
        message.id = String.format("%d:%d:%d", record.topic().hashCode(), record.kafkaPartition(), record.kafkaOffset());

        JsonUtils.JsonUtilsObject kafkaExtras = KafkaExtrasExtractor.createKafkaExtras(record);
        if (kafkaExtras.toJson().size() > 0) {
            message.extras = new MessageExtras(JsonUtils.object().add("kafka", kafkaExtras).toJson());
        }
        return message;
    }


}