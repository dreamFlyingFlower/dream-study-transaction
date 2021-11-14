package com.wy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 全局配置入口
 * 
 * @author 飞花梦影
 * @date 2021-11-13 15:36:10
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@ConfigurationProperties(prefix = "config")
@Getter
@Setter
public class ConfigProperties {

	private HmilyProperties hmily = new HmilyProperties();
}