package org.log4elasticsearch.appender;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import static org.elasticsearch.index.query.FilterBuilders.*;
import static org.elasticsearch.index.query.QueryBuilders.*;

public class TestLog4JElasticSearchAppender {
	
	private static final String ELASTIC_SEARCH = "ElasticSearch";
	private static Logger logger = Logger.getLogger(TestLog4JElasticSearchAppender.class);
	private  Log4JElasticSearchAppender log4jElasticSearchAppender;
	
	private  String index;
	private  String type;
	private  String remoteClusterHosts;
	private  String clusterName;
	private  Client client;
	private  Node node;
	
	@Before
	public  void setup() throws Exception {
		log4jElasticSearchAppender = (Log4JElasticSearchAppender) logger.getRootLogger().getAppender(ELASTIC_SEARCH);
		index = log4jElasticSearchAppender.getIndex();
		type = log4jElasticSearchAppender.getType();
		remoteClusterHosts = log4jElasticSearchAppender.getRemoteClusterHosts();
		clusterName = log4jElasticSearchAppender.getClusterName();
		
		initializeElasticSearchConnections();
	}
	
	private  void initializeElasticSearchConnections() {
		
		if(remoteClusterHosts !=null && remoteClusterHosts.length() >0 && clusterName !=null){
			client = new TransportClient(settingsBuilder().put("client.transport.sniff", true).put("cluster.name", clusterName ));
			String[] clusters = remoteClusterHosts.split(",");
			for(String eachHostAndPort :clusters){
				String[] hostAndPort = eachHostAndPort.split(":");
				((TransportClient)client).addTransportAddress(new InetSocketTransportAddress(hostAndPort[0],Integer.parseInt(hostAndPort[1])));
			}
		}else{
			node = nodeBuilder().settings(settingsBuilder().put("discovery.zen.ping_timeout", 60).put("discovery.zen.ping_retries", 10)).client(true).clusterName(clusterName).node();
			client = node.client();
			
		}
		

		
	}
	
	@Test
	public void TestLevels(){
		logger.debug("This is debug message");
		logger.info("This is info message");
		logger.warn("This is warn message");
		logger.trace("This is trace message");
		SearchResponse response =client.prepareSearch(index).setQuery(termQuery("level", "trace")).execute().actionGet();
		Assert.assertEquals(1L,response.getHits().getTotalHits());
		 response =client.prepareSearch(index).setQuery(termQuery("level", "info")).execute().actionGet();
		Assert.assertEquals(1L,response.getHits().getTotalHits());
		 response =client.prepareSearch(index).setQuery(termQuery("level", "warn")).execute().actionGet();
		Assert.assertEquals(1L,response.getHits().getTotalHits());
		 response =client.prepareSearch(index).setQuery(termQuery("level", "debug")).execute().actionGet();
		Assert.assertEquals(1L,response.getHits().getTotalHits());
	}

	@After
	public void tearDown() throws Exception {
		client.close();
		if(node !=null){
		node.close();
		}
	}

}
