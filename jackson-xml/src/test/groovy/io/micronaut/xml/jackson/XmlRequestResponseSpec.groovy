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
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.reactivex.Single
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
        Single<XmlModel> model = xmlClient.getXmlContent()
        then:
        model.blockingGet().value == 'test'
    }

    def 'verify client can send xml content'() {
        when:
        def requestModel = new XmlModel()
        requestModel.value = 'test'
        Single<XmlModel> model = xmlClient.sendXmlContent(requestModel)
        then:
        model.blockingGet().value == 'test'
    }

    def 'verify invalid xml content send'() {
        when:
        xmlClient.sendRawXmlContent("<xmlModel><value></></xmlModel>").blockingGet()
        then:
        def exception = thrown(Exception)
        exception.message.contains "Failed to convert argument [xmlModel] for value [null] due to: Unexpected character '>' (code 62)"
    }

    @Client('/media/xml/')
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    static interface XmlClient {
        @Get
        Single<XmlModel> getXmlContent();

        @Post
        Single<XmlModel> sendXmlContent(@Body XmlModel xmlModel);

        @Post
        Single<XmlModel> sendRawXmlContent(@Body String xml);
    }

    @Controller('/media/xml/')
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    static class XmlController {

        @Get
        Single<String> getXmlContent() {
            return Single.just('<XmlModel><value>test</value></XmlModel>')
        }

        @Post
        Single<XmlModel> getXmlContent(@Body XmlModel xmlModel) {
            return Single.just(xmlModel)
        }
    }

    static class XmlModel {
        public String value
    }
}
