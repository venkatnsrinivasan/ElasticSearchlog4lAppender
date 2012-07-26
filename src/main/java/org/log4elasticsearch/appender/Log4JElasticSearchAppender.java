package org.log4elasticsearch.appender;

import java.io.IOException;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import static org.elasticsearch.common.settings.ImmutableSettings.*;

import static org.elasticsearch.node.NodeBuilder.*;


public class Log4JElasticSearchAppender extends AppenderSkeleton{


	private LoggingEventJsonSourceBuilder loggingEventJsonSourceBuilder = new ElasticSearchLoggingEventJsonSourceBuilderImpl();
	/**
	 * Indicates the Elastic Search node which maybe local or act as client
	 */
	private Node node;
	/**
	 * The Elasticsearch client which can be embedded or a TransportClient
	 */
	private Client client;
	
	/**
	 * Index where the logevent messages should be created in e.g a Product name
	 */
	private String index;
	/**
	 * Type of the index e,g an application name within the product 
	 */
	private String type;
	/**
	 * Comma seperate list of host:port entries of remote clusters to connect and send
	 * data to e.g 10.20.30.1:9300,10.20.30.1:9301. If specified the appender does not use
	 * a node instance to  act as a client but uses @TransportClient
	 */
	private String remoteClusterHosts;
	/**
	 * Name of the cluster must be specified and match with the cluster name the 'remoteClusterHosts' above
	 * 
	 */
	private String clusterName ;
	private static Logger logger = Logger.getLogger(Log4JElasticSearchAppender.class);
	public Log4JElasticSearchAppender(){
		
	}
	
	
	@Override
	public void activateOptions() {
		if(remoteClusterHosts !=null && remoteClusterHosts.length() >0 && clusterName !=null){
			client = new TransportClient(settingsBuilder().put("client.transport.sniff", true).put("cluster.name", clusterName ));
			String[] clusters = remoteClusterHosts.split(",");
			for(String eachHostAndPort :clusters){
				String[] hostAndPort = eachHostAndPort.split(":");
				((TransportClient)client).addTransportAddress(new InetSocketTransportAddress(hostAndPort[0],Integer.parseInt(hostAndPort[1])));
			}
		}else{
			node = nodeBuilder().client(true).node();
			client = node.client();
		}
	}


	public void close() {
		client.close();
		if(node !=null){
		node.close();
		}
		
	}

	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent loggingEvent) {
		String loggingEventJson =null;
		try {
			loggingEventJson = loggingEventJsonSourceBuilder.buildJsonSource(loggingEvent);
		} catch (IOException e) {
			logger.error(e);
		}
		IndexRequestBuilder indexRequestBuilder;
		if(index !=null && type !=null){
			indexRequestBuilder=client.prepareIndex(index,type);
		}else{
			indexRequestBuilder=client.prepareIndex();
		}
		
		IndexResponse indexResponse =indexRequestBuilder.setSource(loggingEventJson).execute().actionGet();
		if(logger.isDebugEnabled()){
			logger.debug(indexResponse);
		}
	}
	public LoggingEventJsonSourceBuilder getLoggingEventJsonSourceBuilder() {
		return loggingEventJsonSourceBuilder;
	}
	public void setLoggingEventJsonSourceBuilder(
			LoggingEventJsonSourceBuilder loggingEventJsonSourceBuilder) {
		this.loggingEventJsonSourceBuilder = loggingEventJsonSourceBuilder;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRemoteClusterHosts() {
		return remoteClusterHosts;
	}
	public void setRemoteClusterHosts(String remoteClusterHosts) {
		this.remoteClusterHosts = remoteClusterHosts;
	}


	public String getClusterName() {
		return clusterName;
	}


	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	

}
