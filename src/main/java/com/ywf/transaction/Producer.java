package com.ywf.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * 事务消息生产者
 * 1. 事务消息不支持延时消息和批量消息。
 * 2. 为了避免单个消息被检查太多次而导致半队列消息累积，我们默认将单个消息的检查次数限制为 15 次，但是用户可以通过 Broker 配置文件的 `transactionCheckMax`参数来修改此限制。如果已经检查某条消息超过 N 次的话（ N = `transactionCheckMax` ） 则 Broker 将丢弃此消息，并在默认情况下同时打印错误日志。用户可以通过重写 `AbstractTransactionCheckListener` 类来修改这个行为。
 * 3. 事务消息将在 Broker 配置文件中的参数 transactionMsgTimeout 这样的特定时间长度之后被检查。当发送事务消息时，用户还可以通过设置用户属性 CHECK_IMMUNITY_TIME_IN_SECONDS 来改变这个限制，该参数优先于 `transactionMsgTimeout` 参数。
 * 4. 事务性消息可能不止一次被检查或消费。
 * 5. 提交给用户的目标主题消息可能会失败，目前这依日志的记录而定。它的高可用性通过 RocketMQ 本身的高可用性机制来保证，如果希望确保事务消息不丢失、并且事务完整性得到保证，建议使用同步的双重写入机制。
 * 6. 事务消息的生产者 ID 不能与其他类型消息的生产者 ID 共享。与其他类型的消息不同，事务消息允许反向查询、MQ服务器能通过它们的生产者 ID 查询到消费者。
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        TransactionMQProducer producer = new TransactionMQProducer("myGroup");
        producer.setNamesrvAddr("192.168.111.129:9876;192.168.111.130:9876");
        // 注册事务监听器
        producer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                if (StringUtils.equals(message.getTags(), "tagA")) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                } else if (StringUtils.equals(message.getTags(), "tagB")) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                System.out.println("check local success");
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();
        String[] tags = new String[]{"tagA", "tagB", "tagC"};

        for (int i = 0; i < 3; i++) {
            Message msg = new Message("transactionTopic", tags[i], ("msg " +i).getBytes());
            TransactionSendResult result = producer.sendMessageInTransaction(msg, null);
            System.out.println("发送结果：" + result);
            TimeUnit.SECONDS.sleep(1);
        }
//        producer.shutdown();
    }
}
