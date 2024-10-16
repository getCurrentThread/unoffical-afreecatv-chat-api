package com.github.getcurrentthread.soopapi.client;

import org.junit.Test;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SOOPChatClientTest {

    @Test
    public void testSOOPChatClientConnection() throws Exception {
        // BID를 실제 방송 BID로 변경하세요
        String testBID = "rlatldgus";
        
        System.out.println("Starting test with BID: " + testBID);
        
        SOOPChatClient client = new SOOPChatClient.Builder()
                .bid(testBID)
                .build();

        CountDownLatch latch = new CountDownLatch(100);

        client.addObserver(new IChatMessageObserver() {
            @Override
            public void notify(Map<String, Object> message) {
                System.out.println("Received message: " + message);
                latch.countDown();
            }
        });

        System.out.println("Connecting to chat...");
        client.connectToChat().join();
        System.out.println("Connected to chat. Waiting for messages...");

        // 최대 2분 동안 메시지를 기다립니다
        boolean received = latch.await(2, TimeUnit.MINUTES);

        if (received) {
            System.out.println("Test passed: Message received within timeout.");
        } else {
            System.out.println("Test failed: No message received within timeout.");
        }

        System.out.println("Disconnecting...");
        client.disconnect();
        System.out.println("Test completed.");
    }
}
// package com.github.getcurrentthread.soopapi.client;

// import org.junit.Before;
// import org.junit.Test;
// import static org.junit.Assert.*;

// import java.util.Map;
// import java.util.concurrent.atomic.AtomicReference;
// public class SOOPChatClientTest {

//     private SOOPChatClient client;

//     @Before
//     public void setUp() throws Exception {
//         client = new SOOPChatClient.Builder()
//                 .bid("dummy_bid")
//                 .bno("dummy_bno")
//                 .build();
//     }

//     @Test
//     public void testMessageDecoding() {
//         String testMessage = "\u001b\t000500000500\f5\fHello, World!\fuser123\f(user123)\f0\f0\fnickname123\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(testMessage);

//         assertNotNull(decodedMessage);
//         assertEquals("CHAT_MESSAGE", decodedMessage.get("type"));
//         assertEquals("user123", decodedMessage.get("senderId"));
//         assertEquals("Hello, World!", decodedMessage.get("comment"));
//         assertEquals("nickname123", decodedMessage.get("senderNickname"));
//     }

//     @Test
//     public void testObserverNotification() throws Exception {
//         AtomicReference<Map<String, Object>> receivedMessage = new AtomicReference<>();
        
//         client.addObserver(message -> {
//             receivedMessage.set(message);
//         });

//         String testMessage = "\u001b\t000500000500\f5\fTest notification\fuser123\f(user123)\f0\f0\fnickname123\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(testMessage);
        
//         assertNotNull(decodedMessage);
//         client.notifyObservers(decodedMessage);

//         assertNotNull(receivedMessage.get());
//         assertEquals("Test notification", receivedMessage.get().get("comment"));
//     }

//     @Test
//     public void testSendBalloonDecoding() {
//         String balloonMessage = "\u001b\t001800000900\f18\fbj123\fuser456\fnickname456\f10\f0\f0\f0\fballoon_info\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(balloonMessage);

//         assertNotNull(decodedMessage);
//         assertEquals("SEND_BALLOON", decodedMessage.get("type"));
//         assertEquals("bj123", decodedMessage.get("bjId"));
//         assertEquals("user456", decodedMessage.get("senderId"));
//         assertEquals("nickname456", decodedMessage.get("senderNickname"));
//         assertEquals(10, ((Integer)decodedMessage.get("balloonCount")).intValue());
//         assertEquals("balloon_info", decodedMessage.get("balloonInfo"));
//     }

//     @Test
//     public void testOGQEmoticonDecoding() {
//         String emoticonMessage = "\u001b\t010900001300\f109\fchat123\fmessage\fgroup1\fsub1\fv1\fuser789\fnick789\fflag\fcolor\flang\f1\fext\f12\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(emoticonMessage);

//         assertNotNull(decodedMessage);
//         assertEquals("OGQ_EMOTICON", decodedMessage.get("type"));
//         assertEquals("chat123", decodedMessage.get("chatNo"));
//         assertEquals("message", decodedMessage.get("message"));
//         assertEquals("user789", decodedMessage.get("senderId"));
//         assertEquals("nick789", decodedMessage.get("senderNickname"));
//         assertEquals(1, ((Integer)decodedMessage.get("type")).intValue());
//         assertEquals("12", decodedMessage.get("subscriptionMonth"));
//     }

//     @Test
//     public void testChocolateDecoding() {
//         String chocolateMessage = "\u001b\t003700000500\f37\fbj456\fuser789\fnickname789\f5\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(chocolateMessage);

//         assertNotNull(decodedMessage);
//         assertEquals("CHOCOLATE", decodedMessage.get("type"));
//         assertEquals("bj456", decodedMessage.get("bjId"));
//         assertEquals("user789", decodedMessage.get("senderId"));
//         assertEquals("nickname789", decodedMessage.get("senderNickname"));
//         assertEquals(5, ((Integer)decodedMessage.get("count")).intValue());
//     }

//     @Test
//     public void testLiveCaptionDecoding() {
//         String liveCaptionMessage = "\u001b\t012200000100\f122\fThis is a live caption\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(liveCaptionMessage);

//         assertNotNull(decodedMessage);
//         assertEquals("LIVE_CAPTION", decodedMessage.get("type"));
//         assertEquals("This is a live caption", decodedMessage.get("caption"));
//     }

//     @Test
//     public void testInvalidMessageDecoding() {
//         String invalidMessage = "\u001b\t999900000100\f999\fInvalid message\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(invalidMessage);

//         assertNull(decodedMessage);
//     }

//     @Test
//     public void testMultipleObservers() {
//         AtomicReference<Map<String, Object>> receivedMessage1 = new AtomicReference<>();
//         AtomicReference<Map<String, Object>> receivedMessage2 = new AtomicReference<>();

//         client.addObserver(receivedMessage1::set);
//         client.addObserver(receivedMessage2::set);

//         String testMessage = "\u001b\t000500000500\f5\fTest multiple observers\fuser123\f(user123)\f0\f0\fnickname123\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(testMessage);
        
//         client.notifyObservers(decodedMessage);

//         assertNotNull(receivedMessage1.get());
//         assertNotNull(receivedMessage2.get());
//         assertEquals(receivedMessage1.get(), receivedMessage2.get());
//     }

//     @Test
//     public void testRemoveObserver() {
//         AtomicReference<Map<String, Object>> receivedMessage = new AtomicReference<>();
//         IChatMessageObserver observer = receivedMessage::set;

//         client.addObserver(observer);
//         client.removeObserver(observer);

//         String testMessage = "\u001b\t000500000500\f5\fTest remove observer\fuser123\f(user123)\f0\f0\fnickname123\f";
//         Map<String, Object> decodedMessage = client.decodeMessage(testMessage);
        
//         client.notifyObservers(decodedMessage);

//         assertNull(receivedMessage.get());
//     }

//     @Test
//     public void testGetBid() {
//         assertEquals("dummy_bid", client.getBid());
//     }

//     @Test
//     public void testIsConnected() {
//         assertFalse(client.isConnected());
//     }
// }