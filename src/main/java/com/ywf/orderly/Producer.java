package com.ywf.orderly;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 顺序消费
 * 场景：一条订单经过 创建 支付 推送 完成操作。
 */
public class Producer {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        List<OrderOperator> orderOperators = generatorOperator();

        // 创建producer
        DefaultMQProducer producer = new DefaultMQProducer("myGroup");

        // 设置nameserver
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        // 启动生产者
        producer.start();

        // 各队列顺序发送消息
        for (OrderOperator orderOperator : orderOperators) {
            Message msg = new Message("Orderly_Topic", orderOperator.toString().getBytes());
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object args) {
                    long orderId = (long) args;
                    return list.get((int) (orderId % list.size()));
                }
            }, orderOperator.getOrderId());
            System.out.println("发送结果：" + result.getSendStatus()+ ", queue:" + result.getMessageQueue() );
            TimeUnit.SECONDS.sleep(1);
        }
        // 关闭producer
        producer.shutdown();
    }

    /**
     * 模拟生成三条订单:
     * 订单1：订单编号 1000 创建->支付
     * 订单2：订单编号 1001 创建->支付->推送
     * 订单3：订单编号 1002 创建->支付->推送->完成
     * @return
     */
    private static List<OrderOperator> generatorOperator() {
        List<OrderOperator> operators = new ArrayList<>();
        OrderOperator operator1 = new OrderOperator(1001L,"创建");
        OrderOperator operator2 = new OrderOperator(1001L,"支付");
        OrderOperator operator3 = new OrderOperator(1002L,"创建");
        OrderOperator operator4 = new OrderOperator(1002L,"支付");
        OrderOperator operator5 = new OrderOperator(1002L,"推送");
        OrderOperator operator6 = new OrderOperator(1003L,"创建");
        OrderOperator operator7 = new OrderOperator(1003L,"支付");
        OrderOperator operator8 = new OrderOperator(1003L,"推送");
        OrderOperator operator9 = new OrderOperator(1003L,"完成");
        operators.add(operator1);
        operators.add(operator2);
        operators.add(operator3);
        operators.add(operator4);
        operators.add(operator5);
        operators.add(operator6);
        operators.add(operator7);
        operators.add(operator8);
        operators.add(operator9);
        return operators;
    }
}
