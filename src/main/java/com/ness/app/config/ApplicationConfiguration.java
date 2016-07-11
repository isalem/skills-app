package com.ness.app.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ness.app.Application;

@Configuration
@EnableCaching
@ComponentScan(basePackageClasses = Application.class)
public class ApplicationConfiguration {

	@Bean
	public SimpleCacheManager cacheManager() {
		SimpleCacheManager manager = new SimpleCacheManager();
		
		Set<Cache> caches = new HashSet<>();
		caches.add(new ConcurrentMapCache("user"));
		caches.add(new ConcurrentMapCache("knowledge"));
		caches.add(new ConcurrentMapCache("area"));
		
		manager.setCaches(caches);
		
		return manager;
	}
}