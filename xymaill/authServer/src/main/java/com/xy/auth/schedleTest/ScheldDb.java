package com.xy.auth.schedleTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

//@EnableScheduling  //开启spring 定时任务注解
@Component
@Slf4j
public class ScheldDb implements SchedulingConfigurer {



    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(

                //1.添加任务内容(Runnable)

                () -> {
                    //执行业务方法 ... 异步 @@EnableAsync @Async
                },

                //2.设置执行周期(Trigger)

                triggerContext -> {

                    //2.1 从数据库获取执行周期getCron 方法() 返回字符串 例子：* * * * * ？

                    String cron = "* * * * 1 * ";

                    //2.2 合法性校验.

                    if (StringUtils.isEmpty(cron)) {

                        // Omitted Code ..

                    }

                    //2.3 返回执行周期(Date)

                    return new CronTrigger(cron).nextExecutionTime(triggerContext);

                }

        );
    }
}
