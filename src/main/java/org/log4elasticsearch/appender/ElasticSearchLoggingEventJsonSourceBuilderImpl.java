package org.log4elasticsearch.appender;

import java.io.IOException;
import java.util.Date;


import static org.elasticsearch.common.xcontent.XContentFactory.*;

import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.elasticsearch.common.xcontent.XContentBuilder;

public class ElasticSearchLoggingEventJsonSourceBuilderImpl implements LoggingEventJsonSourceBuilder {

	public String buildJsonSource(LoggingEvent loggingEvent) throws IOException {
		XContentBuilder loggingEventJsonObjectBuilder =jsonBuilder().startObject();
		
		addBasicInfo(loggingEvent,loggingEventJsonObjectBuilder);
		addLocationInfo(loggingEvent,loggingEventJsonObjectBuilder);
		addThrowableInfo(loggingEvent,loggingEventJsonObjectBuilder);
		return loggingEventJsonObjectBuilder.endObject().string();
	}

	private void addThrowableInfo(LoggingEvent loggingEvent,
			XContentBuilder loggingEventJsonObjectBuilder) throws IOException {
		String[] throwableArray = loggingEvent.getThrowableStrRep();
		if(throwableArray!=null && throwableArray.length >0){
			loggingEventJsonObjectBuilder.field("throwables",throwableArray);
		}
		
	}

	private void addLocationInfo(LoggingEvent loggingEvent,
			XContentBuilder loggingEventJsonObjectBuilder) throws IOException {
		if(loggingEvent.locationInformationExists()){
			LocationInfo locationInfo =loggingEvent.getLocationInformation();
			loggingEventJsonObjectBuilder.field("locationInfo");
			loggingEventJsonObjectBuilder.field("fileName",locationInfo.getFileName());
			loggingEventJsonObjectBuilder.field("lineNumber",locationInfo.getLineNumber());
			loggingEventJsonObjectBuilder.field("methodName",locationInfo.getMethodName());
			loggingEventJsonObjectBuilder.field("className",locationInfo.getClassName());
		
		}
		
	}

	private void addBasicInfo(LoggingEvent loggingEvent,
			XContentBuilder loggingEventJsonObjectBuilder) throws IOException {
		
		loggingEventJsonObjectBuilder.field("loggerName",loggingEvent.getLoggerName());
		loggingEventJsonObjectBuilder.field("message",loggingEvent.getMessage());
		loggingEventJsonObjectBuilder.field("level",loggingEvent.getLevel().toString());
		loggingEventJsonObjectBuilder.field("timestamp",new Date(loggingEvent.getTimeStamp()));
		loggingEventJsonObjectBuilder.field("thread",loggingEvent.getThreadName());
		
	}

}
