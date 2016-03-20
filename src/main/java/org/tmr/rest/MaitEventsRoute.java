package org.tmr.rest;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;

@Startup
@ApplicationScoped
@ContextName("mait")
public class MaitEventsRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		restConfiguration().component("servlet");
		
        rest("/mait")
        	.get("/events/{id}")
        	.produces(MediaType.APPLICATION_JSON)
        	.to("direct:hello");
      
        from("direct:hello")
        	.bean(new MongoBean(), "getIcmEvents(${header.name})");

	}

}
