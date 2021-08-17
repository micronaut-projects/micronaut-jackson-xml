/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.xml.jackson


import io.micronaut.context.ApplicationContext
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class XmlRequestResponseSpec extends Specification {

    @Shared
    @AutoCleanup
    ApplicationContext context = ApplicationContext.run()

    @Shared
    EmbeddedServer embeddedServer = context.getBean(EmbeddedServer).start()

    @Shared
    XmlClient xmlClient = embeddedServer.getApplicationContext().getBean(XmlClient)

    def 'verify client can parse xml content'() {
        when:
        Publisher<XmlModel> model = xmlClient.getXmlContent()
        then:
        Mono.from(model).block().value == 'test'
    }

    def 'verify client can send xml content'() {
        when:
        def requestModel = new XmlModel()
        requestModel.value = 'test'
        Publisher<XmlModel> model = xmlClient.sendXmlContent(requestModel)

        then:
        Mono.from(model).block().value == 'test'
    }

    def 'verify invalid xml content send'() {
        when:
        Mono.from(xmlClient.sendRawXmlContent("<xmlModel><value></></xmlModel>")).block()

        then:
        HttpClientResponseException exception = thrown()
        exception.response.getBody(Map).get()._embedded.errors[0].message.contains "Failed to convert argument [xmlModel] for value [null] due to: Unexpected character '>' (code 62)"
    }

    @Client('/media/xml/')
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    static interface XmlClient {
        @Get
        @SingleResult
        Publisher<XmlModel> getXmlContent();

        @Post
        @SingleResult
        Publisher<XmlModel> sendXmlContent(@Body XmlModel xmlModel);

        @Post
        @SingleResult
        Publisher<XmlModel> sendRawXmlContent(@Body String xml);
    }

    @Controller('/media/xml/')
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    static class XmlController {

        @Get
        @SingleResult
        Publisher<String> getXmlContent() {
            return Mono.just('<XmlModel><value>test</value></XmlModel>')
        }

        @Post
        @SingleResult
        Publisher<XmlModel> getXmlContent(@Body XmlModel xmlModel) {
            return Mono.just(xmlModel)
        }
    }

    static class XmlModel {
        public String value
    }
}
