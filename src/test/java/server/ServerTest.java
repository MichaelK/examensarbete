package server;

import client.Client;
import client.SecureChatUI;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

/**
 * Created by Michael on 2016-05-07.
 */
public class ServerTest {

    private int port = 6789;
    private Server server;
    private Client client;
    private ServerGUI mockServerGUI = Mockito.mock(ServerGUI.class);
    private SecureChatUI mockSecureChatUI = Mockito.mock(SecureChatUI.class);


    @Test
    public void startServerTest(){
        // Create a server.
        server = new Server(port, mockServerGUI);
        // Assert that ServerSocket is created.
        Assert.assertTrue(server.getServerSocket().isBound());

        // Create a client.
        client = new Client("localhost", port, "Michael", "password", mockSecureChatUI);

        // Start a client.
        client.start();

        // Assert that socket and thread is created.
        Assert.assertTrue(client.getSocket() != null);
        Assert.assertTrue(server.getThread() != null);

        // Try and close the SeverSocket
        server.stop();
        // Assert that the ServerSocket is closed.
        Assert.assertTrue(server.getServerSocket().isClosed());
    }
}
