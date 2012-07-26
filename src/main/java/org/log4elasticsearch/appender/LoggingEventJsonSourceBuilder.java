package org.log4elasticsearch.appender;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.spi.LoggingEvent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public interface LoggingEventJsonSourceBuilder {

	public String buildJsonSource(LoggingEvent loggingEvent) throws IOException;

}
