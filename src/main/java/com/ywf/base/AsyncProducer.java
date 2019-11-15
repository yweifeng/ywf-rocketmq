package com.ywf.base;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 发送异步消息
 */
public class AsyncProducer {
    public static void main(String[] args) throws Exception {
        //1.创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("baseGroup");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        //3.启动producer
        producer.start();
        for (int i = 0; i < 10; i++) {
            //4.创建消息对象，指定主题Topic、Tag和消息体
            Message msg = new Message("BaseTopic", "Tag-async", ("hello sync " + i).getBytes());
            //5.发送消息
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送结果：" + sendResult);
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("发送异常：" + throwable);
                }
            });
            TimeUnit.SECONDS.sleep(1);
        }
        //6.关闭生产者producer
        producer.shutdown();
    }
}
