package com.sda.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {

    private Selector selector;
    private InetSocketAddress listenAddress;

    public static void main(String[] args) throws IOException {
        new NIOServer("0.0.0.0", 6666).startServer();
    }

    public NIOServer(String address, int port) {
        listenAddress = new InetSocketAddress(address, port);
    }

    private void startServer() throws IOException {
        System.out.println("Startuje serwer......");
        this.selector = Selector.open();
        ServerSocketChannel serverSocketChannel
                = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(listenAddress);
        serverSocketChannel.register(this.selector,
                SelectionKey.OP_ACCEPT);


        while (true) {
            int count = selector.select();
            System.out.println("Czekaj: " + count);
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    this.accept(key);
                } else if (key.isReadable()) {
                    this.read(key);

                }
            }
        }
    }


    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel =
                (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        System.out.println("ADRES: "
                + socket.getRemoteSocketAddress().toString());

        channel.register(this.selector,SelectionKey.OP_READ);


        }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = BgiyteBuffer.allocate(2048);
        int read = -1;
        read = channel.read(buffer);
        if (read == -1) {
            Socket socket = channel.socket();
            System.out.println("Klient wyszedl: " +socket.getRemoteSocketAddress().toString());
            channel.close();
            key.cancel();
            return;
        }
        else{
        byte[] data = new byte[read];
        System.arraycopy(buffer.array(), 0, data, 0, read);
        System.out.println(new String(data));
    }}
}



