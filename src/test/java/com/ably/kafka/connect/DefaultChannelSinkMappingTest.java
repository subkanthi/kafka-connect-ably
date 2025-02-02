package com.ably.kafka.connect;

import com.ably.kafka.connect.config.ChannelSinkConnectorConfig;
import com.ably.kafka.connect.config.ConfigValueEvaluator;
import com.ably.kafka.connect.config.DefaultChannelConfig;
import com.ably.kafka.connect.mapping.DefaultChannelSinkMapping;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.realtime.Channel;
import io.ably.lib.types.AblyException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultChannelSinkMappingTest {
    private DefaultChannelSinkMapping defaultChannelSinkMapping;

    //dependencies
    private static final String STATIC_CHANNEL_NAME = "sink-channel";
    private static ChannelSinkConnectorConfig STATIC_CHANNEL_CONFIG = new ChannelSinkConnectorConfig(Map.of("channel", STATIC_CHANNEL_NAME, "client.key", "test-key", "client.id", "test-id"));
    private AblyRealtime ablyRealtime;

    @BeforeEach
    void setUp() throws AblyException {
        ablyRealtime = new AblyRealtime(STATIC_CHANNEL_CONFIG.clientOptions);
    }

    @AfterEach
    void tearDown() {
        ablyRealtime.close();
    }

    @Test
    void testGetChannel_static_name_is_exactly_the_same() throws AblyException, ChannelSinkConnectorConfig.ConfigException {
        //given
        defaultChannelSinkMapping = new DefaultChannelSinkMapping(new ConfigValueEvaluator(), new DefaultChannelConfig(STATIC_CHANNEL_CONFIG));
        SinkRecord record = new SinkRecord("topic", 0, null, null, null, null, 0);

        //when
        final Channel channel = defaultChannelSinkMapping.getChannel(record, ablyRealtime);

        //then
        assertEquals(STATIC_CHANNEL_NAME, channel.name);
    }

    @Test
    void testGetChannel_channel_name_is_evaluating_patterns() throws AblyException, ChannelSinkConnectorConfig.ConfigException {
        //given
        SinkRecord record = new SinkRecord("myTopic", 0, null, "myKey".getBytes(), null, null, 0);
        final ChannelSinkConnectorConfig connectorConfig = new ChannelSinkConnectorConfig(Map.of("channel", "channel_#{key}_#{topic}", "client.key", "test-key", "client.id", "test-id"));
        defaultChannelSinkMapping = new DefaultChannelSinkMapping(new ConfigValueEvaluator(), new DefaultChannelConfig(connectorConfig));

        //when
        final Channel channel = defaultChannelSinkMapping.getChannel(record, ablyRealtime);

        //then
        assertEquals("channel_myKey_myTopic", channel.name);
    }
}
