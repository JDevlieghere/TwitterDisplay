package main;

import serial.SerialConnection;
import twitter.QueryProducer;
import twitter.TwitterConsumer;
import twitter.UserProducer;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws TwitterException, IOException {

        // Create a queue for storing statusses
        BlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);

        // Create the timeline producer
        UserProducer p = new UserProducer(queue);
        p.start();

        // Add another producer
        QueryProducer q = new QueryProducer(queue);
        FilterQuery fq = new FilterQuery();
        String keywords[] = {"Belgium"};
        fq.track(keywords);
        q.start(fq);

        // Connect to Serial Port
        SerialConnection sc = new SerialConnection();
        sc.initialize("COM8");

        // Create consumer
        TwitterConsumer c = new TwitterConsumer(queue, sc.getSerialPort(), 5);
        new Thread(c).start();

    }

}