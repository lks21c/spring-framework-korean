/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cache.annotation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 스프링의 어노테이션 기반 캐시 관리를 활성화 하기 위한 일반적인 구조를 제공하는 {@code @Configuration} abstract 클래스들.
 *
 * @author Chris Beams
 * @author Stephane Nicoll
 * @since 3.1
 * @see EnableCaching
 */
@Configuration
public abstract class AbstractCachingConfiguration<C extends CachingConfigurer> implements ImportAware {

	protected AnnotationAttributes enableCaching;

	protected CacheManager cacheManager;

	protected CacheResolver cacheResolver;

	protected KeyGenerator keyGenerator;

	protected CacheErrorHandler errorHandler;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.enableCaching = AnnotationAttributes.fromMap(
				importMetadata.getAnnotationAttributes(EnableCaching.class.getName(), false));
		Assert.notNull(this.enableCaching,
				"@EnableCaching is not present on importing class " +
						importMetadata.getClassName());
	}

	@Autowired(required = false)
	void setConfigurers(Collection<C> configurers) {
		if (CollectionUtils.isEmpty(configurers)) {
			return;
		}
		if (configurers.size() > 1) {
			throw new IllegalStateException(configurers.size() + " implementations of " +
					"CachingConfigurer were found when only 1 was expected. " +
					"Refactor the configuration such that CachingConfigurer is " +
					"implemented only once or not at all.");
		}
		C configurer = configurers.iterator().next();
		useCachingConfigurer(configurer);
	}

	/**
	 * Extract the configuration from the nominated {@link CachingConfigurer}.
	 */
	protected void useCachingConfigurer(C config) {
		this.cacheManager = config.cacheManager();
		this.cacheResolver = config.cacheResolver();
		this.keyGenerator = config.keyGenerator();
		this.errorHandler = config.errorHandler();
	}

}
