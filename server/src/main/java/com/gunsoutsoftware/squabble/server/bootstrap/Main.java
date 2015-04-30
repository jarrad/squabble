package com.gunsoutsoftware.squabble.server.bootstrap;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages={"com.gunsoutsoftware.squabble.server.bootstrap.config"})
public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		log.info("Starting Spring Context");
		
		final AnnotationConfigApplicationContext context =  new AnnotationConfigApplicationContext();
		context.getEnvironment().addActiveProfile("squabble-default");
		context.register(Main.class);
		context.refresh();
		context.start();
		
		Thread hook = new Thread(new Runnable() {
			public void run() {
				context.close();
			}
		}, "SpringShutdownHook");
		
		Runtime.getRuntime().addShutdownHook(hook);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					synchronized (this) {
						this.wait();						
					}
				} catch (InterruptedException e) {
					log.trace("KeepAlive thread interrupted", e);
				}
			}
		}, "KeepAlive");
		t.start();
		
	}
	
}
