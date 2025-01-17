package io.micronaut.docs.security.securityRule.secured

import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class SecuredSpec extends Specification {

    @Shared
    Map<String, Object> config = [
            'spec.name': 'docsecured',
            ]

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, config as Map<String, Object>)

    @Shared
    @AutoCleanup
    HttpClient client = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.getURL())

    void "verify you can access an endpoint annotated with @Secured('isAnonymous()') without authentication"() {
        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/anonymous"))

        then:
        noExceptionThrown()
    }

    void "verify you can access an endpoint annotated with @Secured('isAuthenticated()') with an authenticated user"() {
        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/authenticated"))

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException)
        e.status == HttpStatus.UNAUTHORIZED

        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/authenticated").basicAuth("user", "password"))

        then:
        noExceptionThrown()
    }

    void "verify you can access an endpoint annotated with @Secured([\"ROLE_ADMIN\", \"ROLE_X\"]) with an authenticated user with one of those roles"() {
        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/"))

        then:
        HttpClientResponseException e = thrown(HttpClientResponseException)
        e.status == HttpStatus.UNAUTHORIZED

        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/admin")
                .basicAuth("user", "password"))

        then:
        e = thrown(HttpClientResponseException)
        e.status == HttpStatus.FORBIDDEN

        when:
        client.toBlocking().exchange(HttpRequest.GET("/example/admin")
                .basicAuth("admin", "password"))

        then:
        noExceptionThrown()
    }
}
