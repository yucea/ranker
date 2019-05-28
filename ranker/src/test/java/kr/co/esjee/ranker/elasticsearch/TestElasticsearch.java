package kr.co.esjee.ranker.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import kr.co.esjee.ranker.webapp.AppConstant;

public abstract class TestElasticsearch implements AppConstant {

	private static final String CLUSTER_NAME = "sjelasticsearch";
	private static final String CLUSTER_NODES = "192.168.0.145:9300,192.168.0.146:9300,192.168.0.147:9300";

	public Client client() throws UnknownHostException {
		return client(CLUSTER_NAME, CLUSTER_NODES);
	}

	public Client client(String clusterName, String clusterNodes) throws UnknownHostException {
		Settings settings = Settings.builder()
				// .put("client.transport.sniff", true)
				.put("cluster.name", clusterName).build();
		TransportClient client = new PreBuiltTransportClient(settings);

		String[] nodes = clusterNodes.split(",");
		for (String node : nodes) {
			String server = node.split(":")[0];
			int port = Integer.parseInt(node.split(":")[1]);
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(server), port));
		}

		return client;
	}

}
