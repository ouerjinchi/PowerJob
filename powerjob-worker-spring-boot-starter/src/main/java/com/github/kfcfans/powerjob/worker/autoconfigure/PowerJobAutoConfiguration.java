package com.github.kfcfans.powerjob.worker.autoconfigure;

import com.github.kfcfans.powerjob.common.utils.CommonUtils;
import com.github.kfcfans.powerjob.worker.OhMyWorker;
import com.github.kfcfans.powerjob.worker.common.OhMyConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * PowerJob 自动装配
 *
 * @author songyinyin
 * @since 2020/7/26 16:37
 */
@Configuration
@EnableConfigurationProperties(PowerJobProperties.class)
public class PowerJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OhMyWorker initPowerJob(PowerJobProperties properties) {

        // 服务器HTTP地址（端口号为 server.port，而不是 ActorSystem port），请勿添加任何前缀（http://）
        CommonUtils.requireNonNull(properties.getServerAddress(), "serverAddress can't be empty!");
        List<String> serverAddress = Arrays.asList(properties.getServerAddress().split(","));

        // 1. 创建配置文件
        OhMyConfig config = new OhMyConfig();
        // 可以不显式设置，默认值 27777
        config.setPort(properties.getAkkaPort());
        // appName，需要提前在控制台注册，否则启动报错
        config.setAppName(properties.getAppName());
        config.setServerAddress(serverAddress);
        // 如果没有大型 Map/MapReduce 的需求，建议使用内存来加速计算
        // 有大型 Map/MapReduce 需求，可能产生大量子任务（Task）的场景，请使用 DISK，否则妥妥的 OutOfMemory
        config.setStoreStrategy(properties.getStoreStrategy());
        // 启动测试模式，true情况下，不再尝试连接 server 并验证appName
        config.setEnableTestMode(properties.isEnableTestMode());

        // 2. 创建 Worker 对象，设置配置文件
        OhMyWorker ohMyWorker = new OhMyWorker();
        ohMyWorker.setConfig(config);
        return ohMyWorker;
    }
}
