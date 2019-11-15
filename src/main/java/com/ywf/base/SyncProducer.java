package com.ywf.base;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 发送同步消息
 */
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("baseGroup");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        //3.启动producer
        producer.start();
        for (int i = 0; i < 10; i++) {
            //4.创建消息对象，指定主题Topic、Tag和消息体
            Message msg = new Message("BaseTopic", "Tag-sync", ("hello sync " + i).getBytes());
            //5.发送消息
            SendResult result = producer.send(msg);
            System.out.println("发送结果：" + result);
            TimeUnit.SECONDS.sleep(1);
        }
        //6.关闭生产者producer
        producer.shutdown();
    }
}
