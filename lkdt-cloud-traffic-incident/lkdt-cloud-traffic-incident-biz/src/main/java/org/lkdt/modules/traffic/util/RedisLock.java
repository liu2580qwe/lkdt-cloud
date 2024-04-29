package org.lkdt.modules.traffic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import javax.annotation.Resource;
import java.util.Collections;

@Component
public class RedisLock {

	Logger logger = LoggerFactory.getLogger(this.getClass());


    protected long internalLockLeaseTime = 60000;//锁过期时间



    //SET命令的参数
    SetParams params = SetParams.setParams().nx().px(internalLockLeaseTime);

    private JedisPool jedisPool;


    /**
     * 加锁
     * @param lockKey 锁键
     * @param id	  唯一ID
     * @param timeout	获取锁的超时时间
     * @return
     */
    public boolean lock(String lockKey,String id,long timeout){
        Jedis jedis = jedisPool.getResource();
        Long start = System.currentTimeMillis();
        try{
            for(;;){
                //SET命令返回OK ，则证明获取锁成功
                String lock = jedis.set(lockKey, id, params);
                if("OK".equals(lock)){
                	logger.info("加锁==成功========KEY==="+lockKey);
                    return true;
                }
                //否则循环等待，在timeout时间内仍未获取到锁，则获取失败
                long l = System.currentTimeMillis() - start;
                if (l>=timeout) {
                	logger.info("加锁==等待==超时========KEY==="+lockKey);
                    return false;
                }
                try {
//                	logger.info("加锁==等待==========");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }finally {
            jedis.close();
        }
    }


    /**
     * 解锁
     * @param lockKey 锁键
     * @param id	  唯一ID
     * @return
     */
    public boolean unlock(String lockKey,String id){
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(lockKey),
                                    Collections.singletonList(id));
            if("1".equals(result.toString())){
            	logger.info("解锁==成功==========KEY==="+lockKey);
                return true;
            }
            logger.info("解锁==失败==========KEY==="+lockKey);
            return false;
        }finally {
            jedis.close();
        }
    }


}
