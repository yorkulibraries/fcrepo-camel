package org.fcrepo.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Fedora producer.
 */
public class FedoraProducer extends DefaultProducer {
    
    private static final Logger logger  = LoggerFactory.getLogger(FedoraProducer.class);

    private volatile FedoraEndpoint endpoint;
    private volatile String type;
    private volatile String path;

    public FedoraProducer(final FedoraEndpoint endpoint, final String path, final String type) {
        super(endpoint);
        this.endpoint = endpoint;
        this.type = type;
        this.path = path;
    }

    public void process(final Exchange exchange) throws Exception {
        final Message in = exchange.getIn();
        final FedoraClient client = new FedoraClient();

        String url = "http://" + this.path;

        if(in.getHeader(endpoint.HEADER_BASE_URL) != null &&
                in.getHeader(endpoint.HEADER_IDENTIFIER) != null) {
            url = in.getHeader(endpoint.HEADER_BASE_URL, String.class)
                + in.getHeader(endpoint.HEADER_IDENTIFIER, String.class);
        }

        if (in.getBody() == null || in.getBody(String.class).isEmpty()) { 
            exchange.getIn().setBody(client.get(url, this.type));
        } else {
            exchange.getIn().setBody(client.post(url, in.getBody(String.class), this.type));
        }
        client.stop();
    }
}
