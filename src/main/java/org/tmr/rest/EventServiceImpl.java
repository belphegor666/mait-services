package org.tmr.rest;

import java.util.Enumeration;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.rs.security.cors.CorsHeaderConstants;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl;

import org.bson.Document;
import org.json.JSONArray;


import com.mongodb.client.FindIterable;


@CrossOriginResourceSharing(
		allowAllOrigins = true,
        allowOrigins = {
           "http://nowhere"
        }, 
        allowCredentials = true, 
        maxAge = 1
)

@Path("/rest")
public class EventServiceImpl implements EventService {
	
	@Inject
	MongoBean datastore;
	
	@Override
	@GET
	@Path("/events/icm")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIcmEvents() {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}
		
		return Response.ok(datastore.findAllIcmEvents().toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}	
	
	@Override
	@GET
	@Path("/events/icm/{urn}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIcmEventsByUrn(@PathParam("urn") String urn) {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}		
		
		return Response.ok(datastore.findAllIcmEventsByUrn(urn).toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}		

	@Override
	@GET
	@Path("/events/icm/msgid/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIcmEventsByMsgId(@PathParam("id") String id) {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}		
		
		return Response.ok(datastore.findAllIcmEventsByMsgId(id).toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}	
	
	@Override
	@GET
	@Path("/events/ium")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIumEvents() {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}
		
		return Response.ok(datastore.findAllIumEvents().toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}	
	
	@Override
	@GET
	@Path("/events/ium/{urn}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIumEventsByUrn(@PathParam("urn") String urn) {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}		
		
		return Response.ok(datastore.findAllIumEventsByUrn(urn).toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}

	@Override
	@GET
	@Path("/events/ium/msgid/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllIumEventsByMsgId(@PathParam("id") String id) {
		if(datastore == null){
			Response.serverError().entity("MongoDB Handle is null!").build();
		}		
		
		return Response.ok(datastore.findAllIumEventsByMsgId(id).toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}	
	
	@Override
	@GET
	@Path("/events")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getAllEvents() {
		return Response.ok(this.icmTestMsg(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}	
	
	@Override
	@GET
	@Path("/events/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getEvents(@PathParam("id") String id) {
		return Response.ok(id, MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}

	@Override
	@GET
	@Path("/mongotest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response mongoTest() {
		if(datastore.getIcmColl() == null) {
			return Response.serverError().entity("ICM Collection Handle is null").build();
		}
		
		JSONArray results = new JSONArray();
		
		if(datastore.getIcmColl().count() == 0) {
			Document item = new Document();
			item.put("foo","bar");

		    datastore.getIcmColl().insertOne(item);
		    
		    FindIterable<Document> search = datastore.getIcmColl().find();

			for( Document d : search ) {
				d.remove("_id");
				results.put(d);
			}
			//cleanup
			datastore.getIcmColl().deleteOne(item);
		} else {
		    FindIterable<Document> search = datastore.getIcmColl().find();

			for( Document d : search ) {
				d.remove("_id");
				results.put(d);
			}			
		}
				
		return Response.ok(results.toString(), MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}
	
	@Override
	@GET
	@Path("/cfservices")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCfServices() {
		String vcap_services = System.getenv("VCAP_SERVICES");
		
		return Response.ok(vcap_services, MediaType.APPLICATION_JSON)
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_METHODS, "GET")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_CREDENTIALS, "false")
                .header(CorsHeaderConstants.HEADER_AC_ALLOW_ORIGIN, "*")
                .build();
	}

	@Override
	@GET
	@Path("/amqptest")
	@Produces({MediaType.APPLICATION_JSON})
	public Response amqpTest() {
		// mvn -q -P readMessages -Dexec.args="--protocol AMQP+SSL --user bapco-map2-oa --password Password1 
		// 
        try {
			final String ICM_USER = "bapco-map2-oa";
			final String IUM_USER = "bapco-com2-oa";
			final String PASSWORD = "Password1" ;
			
        	ConnectionFactoryImpl cf = new ConnectionFactoryImpl("csms-demo.atos.io",64800,
        			ICM_USER, "Password1", ICM_USER, true);
    		//cf.setKeyStorePath(params.get(Param.KEYSTORE));
    		//cf.setKeyStorePassword(params.get(Param.KEYSTOREPASS));
    		cf.setTrustStorePath("WEB-INF/classes/csms-demo.jks");
    		cf.setTrustStorePassword("secret");
 
// mvn -q -P readMessages -Dexec.args="--protocol AMQP+SSL --user bapco-map2-oa --password Password1 
//            --queue BAPCO-MAP2-MAIT --host csms-demo.atos.io --port 64800 --truststore csms-demo.jks --truststorepass secret"
            Connection connection = cf.createConnection(ICM_USER, PASSWORD);
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();
            
            System.out.println("AMQP: Connection Successful!");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue q = session.createQueue("BAPCO-MAP2-MAIT");
            //Queue q = session.createQueue("BAPCO-COM2-MAIT");
            
            //MessageProducer messageProducer = session.createProducer(queue);
            MessageConsumer messageConsumer = session.createConsumer(q);
            QueueBrowser qBrowser = session.createBrowser(q);

            Enumeration msgs =qBrowser.getEnumeration();
            //TextMessage receivedMessage = (TextMessage) messageConsumer.receive(2000L);
            if ( !msgs.hasMoreElements() ) { 
                System.out.println("No messages in queue");
            } else { 
                while (msgs.hasMoreElements()) { 
                    TextMessage tempMsg = (TextMessage)msgs.nextElement(); 
                    System.out.println("Message: " + tempMsg.getText()); 
                }
            }
/*
            if (receivedMessage != null) {
                System.out.println(receivedMessage.getText());
            } else {
                System.out.println("No message received within the given timeout!");
            }
*/
            connection.close();
        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            //System.exit(1);
        }
		
		return Response.ok("AMQP SUCCESS", MediaType.APPLICATION_JSON).build();
	}	
	
	@Override
	@GET
	@Path("/reset")
	public void resetData() {
		// clear ICM messages
		datastore.getIcmColl().deleteMany(new Document());
		// clear IUM messages
		datastore.getIumColl().deleteMany(new Document());
	}

	private String icmTestMsg() {
		return 
		
		"[{\"OrigIncidentDate\":\"18/03/2016\",\"Description\":\"Test incident at The International Centre Telford\",\"ResourceID\":\"888\",\"Mode\":\"create\",\"OrigIncidentTime\":\"23:35:29\",\"X\":\"888\",\"Y\":\"888\",\"ResourceETA\":\"23:45:29\",\"DestinOrganisation\":\"BAPCO-FIR\",\"OrigIncidentURN\":\"f0266653-2723-4a32-8d9a-b09d2d0838c7\",\"OrigOrganisation\":\"BAPCO-AMB\",\"MessageId\":\"888888\"},"
		+"{\"OrigIncidentDate\":\"18/03/2016\",\"Description\":\"Test incident at The International Centre Telford\",\"ResourceID\":\"888\",\"Mode\":\"create\",\"OrigIncidentTime\":\"23:35:29\",\"X\":\"888\",\"Y\":\"888\",\"ResourceETA\":\"23:45:29\",\"DestinOrganisation\":\"BAPCO-FIR\",\"OrigIncidentURN\":\"f0266653-2723-4a32-8d9a-b09d2d0838c7\",\"OrigOrganisation\":\"BAPCO-AMB\",\"MessageId\":\"888888\"}]";
	}

	private static class MyExceptionListener implements ExceptionListener {
	    @Override
	    public void onException(JMSException exception) {
	        System.out.println("Connection ExceptionListener fired.");
	        exception.printStackTrace(System.out);
	    }
	}	
	
}
