package client;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import server.Server;
import server.ServerGUI;

/**
 * Created by Michael on 2016-05-07.
 */
public class ClientTest {

    private Client client;
    private int port = 8765;
    private Server server;

    private SecureChatUI mockSecureChatUI = Mockito.mock(SecureChatUI.class);
    private ServerGUI mockServerGUI = Mockito.mock(ServerGUI.class);

    @Before
    public void init(){
        // Create a Client.
        client = new Client("localhost", port, "Michael", "password", mockSecureChatUI);
    }

    @Test
    public void startClientTest(){
        // Create a Server.
        server = new Server(port, mockServerGUI);
        // Assert that ServerSocket exist.
        Assert.assertTrue(server.getServerSocket().isBound());

        // Assert that there is no socket yet. And that Input- and Outputstreams are null.
        Assert.assertTrue(client.getSocket() == null);
        Assert.assertTrue(client.getsInput() == null);
        Assert.assertTrue(client.getsOutput() == null);

        // Start the client.
        Boolean success = client.start();

        // Assert that socket now exist. And that Input- and Outputstreams are created.
        Assert.assertTrue(client.getSocket().isBound());
        Assert.assertTrue(client.getsInput() != null);
        Assert.assertTrue(client.getsOutput() != null);

        // Assert that ServerListener class is started.
        Assert.assertTrue(client.getServerListener() != null);

        // Assert that the start method has completed and sent back a "true".
        Assert.assertTrue(success);
    }

    @Test
    public void createSymmetricKeyTest(){
        // Chose a password
        String password = "password";
        // This is the expected result.
        String expectedSymmetricKeyAsBase64 = "wAZ9SvTofwDbrGO2FWgoI3BZFy0bvqxnQnNF1qn9pIQ=";
        // Test the method.
        byte[] actualSymmetricKeyAsByteArray = client.createSymmetricKey(password);
        // Assert equals.
        Assert.assertEquals(expectedSymmetricKeyAsBase64, Base64.toBase64String(actualSymmetricKeyAsByteArray));
    }

    @Test
    public void sendMessageTest(){
        // Create the three different chatmessages that are possible.
        ChatMessage chatMessageNormal = new ChatMessage(0, "This is a message");
        ChatMessage chatMessageLogout = new ChatMessage(1, "This is a logout message");
        ChatMessage chatMessageLobby = new ChatMessage(2, "This is a lobby message");

        // Create a mock of LobbyGUI to handle nullpointerexception.
        LobbyGUI mockLobbyGUI = Mockito.mock(LobbyGUI.class);
        Mockito.when(mockSecureChatUI.getLobbyGUI()).thenReturn(mockLobbyGUI);

        // Start the client.
        client.start();
        // Assert that the outputstream is created.
        Assert.assertTrue(client.getsOutput() != null);

        // Try and send one of each chatmessage to see that no error is thrown.
        client.sendMessage(chatMessageNormal);
        client.sendMessage(chatMessageLogout);
        client.sendMessage(chatMessageLobby);
    }

    @Test
    public void disconnectTest(){
        // Start the client.
        client.start();

        // Assertions
        Assert.assertTrue(client.getSocket().isBound());
        Assert.assertTrue(client.getsOutput() != null);
        Assert.assertTrue(client.getsInput() != null);

        // Disconnect the socket and close the streams.
        client.disconnect();

        // Assert that the socket is closed.
        // There is no easy way to test for closed stream because the stream itself may not know if it is closed on the other end.
        Assert.assertTrue(client.getSocket().isClosed());
    }



}
