package com.ywf.orderly;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 顺序消费
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        // 创建consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group");
        // 设置nameserver
        consumer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        // 设置订阅主题
        consumer.subscribe("Orderly_Topic", "*");

        // 设置消息监听器 一个队列一个线程监听
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                for (MessageExt messageExt : list) {
                    System.out.println("线程：【 " + Thread.currentThread().getName() + " 】," + new String(messageExt.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.out.println("启动消费者");
    }
}
