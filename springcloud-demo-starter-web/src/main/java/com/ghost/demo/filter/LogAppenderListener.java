package com.ghost.demo.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.alibaba.fastjson.JSON;
import com.ghost.demo.model.GlobalContext;
import com.ghost.demo.util.ContextUtil;
import com.ghost.demo.util.KafkaLogPropertiesUtil;
import com.ghost.demo.util.LocalIpUtil;
import com.ghost.demo.util.UnsafetyIdUtil;
import io.prometheus.client.Counter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @program springcloud-demo
 * @description:
 * @author: jackchow
 * @create: 2022/04/26 21:35
 */
@Component
@Slf4j
public class LogAppenderListener implements ApplicationListener<ApplicationEvent> {

    private static final Counter counter = Counter.build().name("jdh_system_log")
            .help("Logback log statements at various log levels")
            .labelNames("app", "ip", "biz", "level")
            .register();
    private static volatile boolean isInit = false;

    volatile Environment env;

    static volatile Producer<String, String> producer;
    final static String LOCAL_IP = LocalIpUtil.getLocalIp();
    static volatile String appName;

    static volatile Appender<ILoggingEvent> appender;
    static final String APPENDER_NAME = "KafkaAppender";

    public static Producer<String, String> getProducer(){
        return producer;
    }

    public LogAppenderListener(){
        appender = new UnsynchronizedAppenderBase<ILoggingEvent>() {
            @Override
            public void doAppend(ILoggingEvent eventObject) {
                try{
                    counter.labels(appName, LOCAL_IP,
                            getBizType(eventObject.getLoggerName()),
                            eventObject.getLevel().toString()).inc();
                    appendToKafka(eventObject);
                }catch (Throwable e){
                    System.err.println(e);
                }
                super.doAppend(eventObject);
            }
            @Override
            protected void append(ILoggingEvent eventObject) {
            }
            @Override
            public String getName(){
                return APPENDER_NAME;
            }
        };
    }

    @Override
    public synchronized void onApplicationEvent(ApplicationEvent context) {
        if(context instanceof ApplicationPreparedEvent){
            env = ((ApplicationPreparedEvent)context).getApplicationContext().getEnvironment();
        }else if(context instanceof ApplicationContextEvent){
            env = ((ApplicationContextEvent)context).getApplicationContext().getEnvironment();
        }else {
            return;
        }
        if("true".equals(env.getProperty("ci.enable"))){
            return;
        }
        String app = env.getProperty("spring.application.name");
        if(app == null){
            return;
        }
        appName = app;

        initKafka();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger log = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        Logger kafkaLog = lc.getLogger("org.apache.kafka");
        if(kafkaLog != null){
            kafkaLog.setLevel(Level.INFO);
        }
        if(log.getAppender(APPENDER_NAME) == null){
            log.addAppender(appender);
        }
    }



    String getBizType(String loggerName){
        if("digestLogger".equals(loggerName)){
            return "digest";
        }else if("borderLogger".equals(loggerName)){
            return "border";
        }else if("thirdServiceLogger".equals(loggerName)){
            return "third";
        }else if("errorLogger".equals(loggerName)){
            return "error";
        }
        return "biz";
    }

    synchronized void initKafka() {
        if(producer != null){
            return;
        }
        String mqServers = env.getProperty("log.kafka.servers");
        if (StringUtils.isEmpty(mqServers)) {
            log.info("kafka server is empty");
            return;
        }
        log.info("init log kafka server=[{}]", mqServers);
        Properties props= KafkaLogPropertiesUtil.buildLogProperties(mqServers);
        producer = new KafkaProducer<>(props);
    }

    void appendToKafka(ILoggingEvent event) {
        if (producer != null) {
            try{
                StackTraceElement callStackTrance = event.getCallerData().clone()[0];
                IThrowableProxy throwableProxy = event.getThrowableProxy();
                String throwableMsg = null;
                Throwable throwable = null;
                if (throwableProxy instanceof ThrowableProxy) {
                    throwable = ((ThrowableProxy) throwableProxy).getThrowable();
                    throwableMsg = ExceptionUtils.getStackTrace(throwable);
                }
                Log log = new Log();
                log.time = DateFormatUtils.format(System.currentTimeMillis(),
                        "yyyy-MM-dd HH:mm:ss,SSS");
                log.uid = UnsafetyIdUtil.nextId();
                log.ip = LOCAL_IP;
                log.serviceId = appName;
                log.logLevel = event.getLevel().toString();
                log.logType = getBizType(event.getLoggerName());
                log.theadName = event.getThreadName();
                log.traceId = ContextUtil.getTraceId();
                // 兼容异步线程日志
                GlobalContext currentContext = ContextUtil.getCurrentContext();
                if(StringUtils.isEmpty(log.traceId)){
                    log.traceId = (currentContext != null) ? currentContext.getTraceId() : null;
                }
                log.className = callStackTrance.getClassName();
                log.method = callStackTrance.getMethodName();
                log.line = callStackTrance.getLineNumber() + "";
                log.msg = event.getFormattedMessage();
                log.detail = throwableMsg;
//                log.success = ContextUtil.getLogSuccess();
                if(currentContext != null){
                    log.userId = currentContext.getUserId();
                    log.entId = currentContext.getEntId();
                }
                //kafka日志不发送
                if(log.className != null && log.className.startsWith("org.apache.kafka")){
                    return;
                }
                producer.send(new ProducerRecord<>("trace-json-log", null, JSON.toJSONString(log)));
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }
    @Data
    public class Log{
        String group = "jdh";
        Long uid;
        String time;
        String ip;
        String serviceId;
        String logType;
        String logLevel;
        String theadName;
        String traceId;
        String className;
        String method;
        String line;
        String msg;
        String detail;
        String userId;
        String entId;
        String success;
        public Log(){}
    }

}
