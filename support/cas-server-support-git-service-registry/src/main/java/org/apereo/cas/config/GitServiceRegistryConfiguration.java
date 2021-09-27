package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.git.GitRepository;
import org.apereo.cas.git.GitRepositoryBuilder;
import org.apereo.cas.services.GitServiceRegistry;
import org.apereo.cas.services.ServiceRegistry;
import org.apereo.cas.services.ServiceRegistryExecutionPlanConfigurer;
import org.apereo.cas.services.ServiceRegistryListener;
import org.apereo.cas.services.locator.DefaultGitRepositoryRegisteredServiceLocator;
import org.apereo.cas.services.locator.GitRepositoryRegisteredServiceLocator;
import org.apereo.cas.services.locator.TypeAwareGitRepositoryRegisteredServiceLocator;
import org.apereo.cas.services.resource.RegisteredServiceResourceNamingStrategy;
import org.apereo.cas.services.util.RegisteredServiceJsonSerializer;
import org.apereo.cas.services.util.RegisteredServiceYamlSerializer;
import org.apereo.cas.util.CollectionUtils;

import lombok.val;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * This is {@link GitServiceRegistryConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@ConditionalOnProperty(prefix = "cas.service-registry.git", name = "repository-url")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Configuration(value = "gitServiceRegistryConfiguration", proxyBeanMethods = false)
public class GitServiceRegistryConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("serviceRegistryListeners")
    private ObjectProvider<List<ServiceRegistryListener>> serviceRegistryListeners;

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "gitServiceRegistryRepositoryInstance")
    @Autowired
    public GitRepository gitServiceRegistryRepositoryInstance(final CasConfigurationProperties casProperties) {
        val registry = casProperties.getServiceRegistry().getGit();
        return GitRepositoryBuilder.newInstance(registry).build();
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "gitServiceRegistry")
    @Autowired
    public ServiceRegistry gitServiceRegistry(final CasConfigurationProperties casProperties, final ConfigurableApplicationContext applicationContext,
                                              @Qualifier("gitServiceRegistryRepositoryInstance")
                                              final GitRepository gitServiceRegistryRepositoryInstance,
                                              @Qualifier("resourceNamingStrategy")
                                              final RegisteredServiceResourceNamingStrategy resourceNamingStrategy) {
        val properties = casProperties.getServiceRegistry().getGit();
        val gitRepository = gitServiceRegistryRepositoryInstance;
        val locators = new ArrayList<GitRepositoryRegisteredServiceLocator>();
        if (properties.isGroupByType()) {
            locators.add(new TypeAwareGitRepositoryRegisteredServiceLocator(resourceNamingStrategy, gitRepository.getRepositoryDirectory(), properties));
        }
        locators.add(new DefaultGitRepositoryRegisteredServiceLocator(resourceNamingStrategy, gitRepository.getRepositoryDirectory(), properties));
        return new GitServiceRegistry(applicationContext, gitRepository, CollectionUtils.wrapList(new RegisteredServiceJsonSerializer(), new RegisteredServiceYamlSerializer()),
            properties.isPushChanges(), properties.getRootDirectory(), serviceRegistryListeners.getObject(), locators);
    }

    @Bean
    @ConditionalOnMissingBean(name = "gitServiceRegistryExecutionPlanConfigurer")
    @RefreshScope
    public ServiceRegistryExecutionPlanConfigurer gitServiceRegistryExecutionPlanConfigurer(
        @Qualifier("gitServiceRegistry")
        final ServiceRegistry gitServiceRegistry) {
        return plan -> plan.registerServiceRegistry(gitServiceRegistry);
    }
}
