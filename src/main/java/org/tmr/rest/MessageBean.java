package org.tmr.rest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
//import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.amqp_1_0.jms.impl.ConnectionFactoryImpl;
import org.bson.Document;
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;

@Startup
@Singleton
@DependsOn("MongoBean")
public class MessageBean {
	
	private final String ICM = "ICM";
	private final String IUM = "IUM";
	
	private Thread icmThread;
	private Thread iumThread;
	
	private Connection icmConn;
	private Connection iumConn;
	
	@Inject
	MongoBean datastore;
	
	public MessageBean() {
		super();
	}
	
	@PostConstruct
	public void setup() {
		startIcmLoad();
		startIumLoad();		
	}
	
	public void startIcmLoad() {
        try {
			final String ICM_USER = "bapco-map2-oa";
			final String PASSWORD = "Password1" ;
			
        	ConnectionFactoryImpl cf = new ConnectionFactoryImpl("csms-demo.atos.io",64800,
        			ICM_USER, "Password1", ICM_USER, true);

    		cf.setTrustStorePath("WEB-INF/classes/csms-demo.jks");
    		cf.setTrustStorePassword("secret");
 
            icmConn = cf.createConnection(ICM_USER, PASSWORD);
            icmConn.setExceptionListener(new MyExceptionListener());
            icmConn.start();
            
            System.out.println("AMQP: ICM Connection Successful!");

            Session session = icmConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue q = session.createQueue("BAPCO-MAP2-MAIT");
            
            MessageConsumer messageConsumer = session.createConsumer(q);

            icmThread = new Thread(ICM){
            	private volatile boolean running = true;
            	
            	public void terminate(){
            		this.running = false;
            	}  
            	
            	@Override
            	public void run() {
            		while(running && (datastore != null)){
            			try{          		
				            TextMessage receivedMessage = (TextMessage) messageConsumer.receive(2000L);
				
				            if (receivedMessage != null) {
				                System.out.println(receivedMessage.getText());
				                Document icmJSON = Document.parse(receivedMessage.getText());
				                datastore.addIcm(icmJSON);
				            } else {
				                //System.out.println("No ICM message received within the given timeout!");
				            }
            			} catch (Exception e) {
			                System.out.println("Exception in ICM Thread!");
			                e.printStackTrace(System.out);  	           				
            			}
            		}
            	}
            	
            	@Override
            	public void interrupt() {
            		super.interrupt();
            		this.terminate();
            	}
            };
            icmThread.start();

            //connection.close();
        } catch (Exception exp) {
            System.out.println("Exception in ICM Load Messages!");
            exp.printStackTrace(System.out);
         }
	
	}
	
	
	public void startIumLoad() {
        try {
			final String IUM_USER = "bapco-com2-oa";
			final String PASSWORD = "Password1" ;
			
        	ConnectionFactoryImpl cf = new ConnectionFactoryImpl("csms-demo.atos.io",64800,
        			IUM_USER, "Password1", IUM_USER, true);

    		cf.setTrustStorePath("WEB-INF/classes/csms-demo.jks");
    		cf.setTrustStorePassword("secret");
 
            iumConn = cf.createConnection(IUM_USER, PASSWORD);
            iumConn.setExceptionListener(new MyExceptionListener());
            iumConn.start();
            
            System.out.println("AMQP: IUM Connection Successful!");

            Session session = iumConn.createSession(false, Session.AUTO_ACKNOWLEDGE);

             Queue q = session.createQueue("BAPCO-COM2-MAIT");
            
            MessageConsumer messageConsumer = session.createConsumer(q);

            iumThread = new Thread(IUM){
            	private volatile boolean running = true;
            	
            	public void terminate(){
            		this.running = false;
            	}
            	
            	@Override
            	public void run() {
            		while(running && (datastore != null)){
            			try{
				            TextMessage receivedMessage = (TextMessage) messageConsumer.receive(2000L);
				
				            if (receivedMessage != null) {
				                System.out.println(receivedMessage.getText());
				                Document iumJSON = Document.parse(receivedMessage.getText());
				                datastore.addIum(iumJSON);
				            } else {
				                //System.out.println("No IUM message received within the given timeout!");
				            }
			            } catch (Exception e) {
			                System.out.println("Exception in IUM Thread!");
			                e.printStackTrace(System.out);  	
			            }
		            }
            	}
            	
            	@Override
            	public void interrupt() {
            		super.interrupt();
            		this.terminate();
            	}
            };
            iumThread.start();

            //connection.close();
        } catch (Exception exp) {
            System.out.println("Exception in IUM Load Messages!");
            exp.printStackTrace(System.out);
        }	
		
	}	
	
	@PreDestroy
	private void cleanup() {
		stopMessageLoad();
	}
	
	private void stopMessageLoad() {
		//TODO
		icmThread.interrupt();
		try {
			icmConn.close();
		} catch (JMSException e) {
			icmConn = null;
		}
		//stopIcmLoad();
		iumThread.interrupt();
		try {
			iumConn.close();
		} catch (JMSException e) {
			iumConn = null;
		}		
		//stopIumLoad();
	}
	
	private static class MyExceptionListener implements ExceptionListener {
	    @Override
	    public void onException(JMSException exception) {
	        System.out.println("Connection ExceptionListener fired.");
	        exception.printStackTrace(System.out);
	    }
	}	

}
