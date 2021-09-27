package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.mongo.MongoDbConnectionFactory;
import org.apereo.cas.util.crypto.CipherExecutor;
import org.apereo.cas.webauthn.MongoDbWebAuthnCredentialRepository;
import org.apereo.cas.webauthn.storage.WebAuthnCredentialRepository;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.net.ssl.SSLContext;

/**
 * This is {@link MongoDbWebAuthnConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Configuration(value = "mongoDbWebAuthnConfiguration", proxyBeanMethods = false)
public class MongoDbWebAuthnConfiguration {

    @RefreshScope
    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @RefreshScope
    @Bean
    @Autowired
    public MongoTemplate mongoWebAuthnTemplate(final CasConfigurationProperties casProperties,
                                               @Qualifier("sslContext")
                                               final SSLContext sslContext) {
        val mongo = casProperties.getAuthn().getMfa().getWebAuthn().getMongo();
        val factory = new MongoDbConnectionFactory(sslContext);
        val mongoTemplate = factory.buildMongoTemplate(mongo);
        MongoDbConnectionFactory.createCollection(mongoTemplate, mongo.getCollection(), mongo.isDropCollection());
        return mongoTemplate;
    }

    @RefreshScope
    @Bean
    @Autowired
    public WebAuthnCredentialRepository webAuthnCredentialRepository(final CasConfigurationProperties casProperties,
                                                                     @Qualifier("mongoWebAuthnTemplate")
                                                                     final MongoTemplate mongoWebAuthnTemplate,
                                                                     @Qualifier("webAuthnCredentialRegistrationCipherExecutor")
                                                                     final CipherExecutor webAuthnCredentialRegistrationCipherExecutor) {
        return new MongoDbWebAuthnCredentialRepository(mongoWebAuthnTemplate, casProperties, webAuthnCredentialRegistrationCipherExecutor);
    }
}
