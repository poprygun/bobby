package io.pivotal.poc.config;


import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
@EnableConfigurationProperties(MQConfiguration.MQProperties.class)
@EnableJms
@Profile({"cluster"})
public class MQClusteredConfiguration {
    @Inject
    MQConfiguration.MQProperties properties;

    @Bean(name = "clusteringJmsTemplate")
    public JmsTemplate provideJmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(hornetQJMSConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setSessionCacheSize(10);
        cachingConnectionFactory.setCacheProducers(false);
        cachingConnectionFactory.setTargetConnectionFactory(hornetQJMSConnectionFactory());
        return cachingConnectionFactory;
    }


    @Bean
    public HornetQJMSConnectionFactory hornetQJMSConnectionFactory() {



        HornetQJMSConnectionFactory connectionFactory = new HornetQJMSConnectionFactory(true, transportConfigurations());
        connectionFactory.setConnectionLoadBalancingPolicyClassName(RoundRobinConnectionLoadBalancingPolicy.class.getName());

        return connectionFactory;
    }

    private TransportConfiguration[] transportConfigurations() {

        List<TransportConfiguration> transportConfigurations = new ArrayList<>();
        for (int i = 0; i < properties.clusterUrls.length; i++) {
            serverDefFrom(transportConfigurations, properties.clusterUrls[i]);
        }
        return transportConfigurations.toArray(new TransportConfiguration[transportConfigurations.size()]);
    }

    private void serverDefFrom(List<TransportConfiguration> transportConfigurations, String clusterUrl) {
        Map<String, Object> map = new HashMap<>();
        String mqServer = clusterUrl;
        String[] serverDef = mqServer.split(":");
        map.put(TransportConstants.HOST_PROP_NAME, serverDef[0]);
        map.put(TransportConstants.PORT_PROP_NAME, serverDef[1]);

        TransportConfiguration server = new TransportConfiguration(NettyConnectorFactory.class.getName(), map);
        transportConfigurations.add(server);
    }
}
