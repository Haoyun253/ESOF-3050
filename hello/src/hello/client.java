package hello;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.demo.client.AbstractClient;

public class client extends AbstractClient {

    public client() {
        super("localhost", 5178); // 设置服务器地址和端口
    }
    // 用于保存当前请求的 CompletableFuture
    private CompletableFuture<String> currentRequestFuture;
    // 发送请求到服务器
    @Override
    protected void handleMessageFromServer(Object msg)  {
        System.out.println("Response from server: " + msg);
        // 假设服务器响应的第一个部分是请求ID，用于匹配请求和响应

        if (currentRequestFuture != null) {
            currentRequestFuture.complete(msg.toString()); // 将服务器响应直接传递给 future
            currentRequestFuture = null; // 清空 currentRequestFuture，准备处理下一个请求
        }
    }
    // 发送消息到服务器并返回 CompletableFuture

    // 启动客户端
    public void startClient() {
        try {
            openConnection();
            System.out.println("Client connected to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送消息到服务器
    public CompletableFuture<String> sendMessageToServer(Object message) {
        currentRequestFuture = new CompletableFuture<>();
        try {
            sendToServer(message);
        } catch (IOException e) {
            currentRequestFuture.completeExceptionally(e);
        }
        return currentRequestFuture;
    /*    try {
            sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }

    // 关闭客户端
    public void stopClient() {
        try {
            closeConnection();
            System.out.println("Client disconnected from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}