package uk.co.epsilontechnologies.surgecheck.configuration;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.config.CassandraCqlClusterFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.web.client.RestTemplate;
import uk.co.epsilontechnologies.surgecheck.gateway.PassThroughResponseErrorHandler;
import uk.co.epsilontechnologies.surgecheck.model.Coordinates;
import uk.co.epsilontechnologies.surgecheck.model.Grid;

import java.math.BigDecimal;

@Configuration
public class AppConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocations(
                new ClassPathResource("config/base.properties"),
                new ClassPathResource("config/"+System.getProperty("env.name")+".properties"));
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public RestTemplate restTemplate(final PassThroughResponseErrorHandler passThroughResponseErrorHandler) {
        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(passThroughResponseErrorHandler);
        return restTemplate;
    }

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser();
    }

    @Bean
    public Grid grid(
            @Value("${grid.southwest.latitude}") final BigDecimal southWestLatitude,
            @Value("${grid.southwest.longitude}") final BigDecimal southWestLongitude,
            @Value("${grid.northeast.latitude}") final BigDecimal northEastLatitude,
            @Value("${grid.northeast.longitude}") final BigDecimal northEastLongitude) {
        return new Grid(
                new Coordinates(southWestLatitude, southWestLongitude),
                new Coordinates(northEastLatitude, northEastLongitude));
    }

    @Bean
    public CassandraClusterFactoryBean cassandraClusterFactoryBean(
            @Value("${cassandra.contactpoints}") final String cassandraContactPoints,
            @Value("${cassandra.port}") final int cassandraPort) {
        final CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(cassandraContactPoints);
        cluster.setPort(cassandraPort);
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMappingContext() {
        return new BasicCassandraMappingContext();
    }

    @Bean
    public CassandraConverter cassandraConverter(final CassandraMappingContext mappingContext) {
        return new MappingCassandraConverter(mappingContext);
    }

    @Bean
    public CassandraSessionFactoryBean cassandraSessionFactoryBean(
            final CassandraConverter cassandraConverter,
            final CassandraCqlClusterFactoryBean clusterFactoryBean,
            @Value("${cassandra.keyspace}") final String cassandraKeyspace) throws Exception {
        final CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(clusterFactoryBean.getObject());
        session.setKeyspaceName(cassandraKeyspace);
        session.setConverter(cassandraConverter);
        session.setSchemaAction(SchemaAction.NONE);
        return session;
    }

    @Bean
    public CassandraOperations cassandraOperations(final CassandraSessionFactoryBean cassandraSessionFactoryBean) throws Exception {
        return new CassandraTemplate(cassandraSessionFactoryBean.getObject());
    }

}