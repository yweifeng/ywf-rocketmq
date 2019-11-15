package com.ywf.filter;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * 过滤消息生产者
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("myGroup");
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        producer.start();

        for (int i = 0; i < 10; i++) {
            Message msg = new Message("filterTopic", "Tag" + i, ("msg " + i).getBytes());
            msg.putUserProperty("i", String.valueOf(i));
            SendResult result = producer.send(msg);
            System.out.println("发送结果：" + result);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }
}
