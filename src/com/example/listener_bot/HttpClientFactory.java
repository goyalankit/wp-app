package com.example.listener_bot;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

/**
 * Created with IntelliJ IDEA.
 * User: ankit
 * Date: 10/26/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientFactory {

    private static DefaultHttpClient client;

    public synchronized static DefaultHttpClient getThreadSafeClient() {

        if (client != null)
            return client;

        client = new DefaultHttpClient();

        ClientConnectionManager mgr = client.getConnectionManager();

        HttpParams params = client.getParams();
        client = new DefaultHttpClient(
                new ThreadSafeClientConnManager(params,
                        mgr.getSchemeRegistry()), params);

        return client;
    }
}
