package com.gunsoutsoftware.squabble.server.bootstrap.config;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.gunsoutsoftware.squabble.SquabblePublisher;
import com.gunsoutsoftware.squabble.SquabbleResource;

@Configuration
public class HttpServerConfig {

	@Autowired
	Environment env;
	
	@Autowired
	ConfigurableApplicationContext context;
	
	@Autowired
	SquabblePublisher publisher;
	
	@Bean
	SquabbleResource squabbleResource() {
		return new SquabbleResource(publisher);
	}
	
	@Bean(initMethod="start")
	HttpServer httpServer() throws IOException {
		
		String _uri = env.getProperty("http.uri", "http://localhost:3030");
		
		
	    ResourceConfig resourceConfig = new ResourceConfig();
	    resourceConfig.register(squabbleResource());
	    
	    URI uri = UriBuilder.fromUri(_uri).build();
		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, resourceConfig);

		ServerConfiguration config = server.getServerConfiguration();
		config.setHttpServerName("Squabbler");
		config.setHttpServerName("Squabble");
		config.setSessionTimeoutSeconds(3);
		
		Thread hook = new Thread(new Runnable() {
			public void run() {
				server.shutdown();
			}
		}, "GrizzlyShutdownHook");
		Runtime.getRuntime().addShutdownHook(hook);
		
		//server.start();
		
		return server;
	}
	
}
