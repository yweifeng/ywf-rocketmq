package com.ywf.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 发送批量消息，最大大小为4M
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("baseGroup");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        //3.启动producer
        producer.start();
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("BaseTopic", "Tag-batch", (String.valueOf(i)).getBytes());
            messages.add(msg);
        }
        SendResult result = producer.send(messages);
        System.out.println("发送结果：" + result);
        TimeUnit.SECONDS.sleep(1);
        //6.关闭生产者producer
        producer.shutdown();
    }
}
