/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.xml.jackson.server

import io.micronaut.context.ApplicationContext
import io.micronaut.core.convert.value.ConvertibleValues
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.MediaType
import io.micronaut.http.simple.SimpleHttpHeaders
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class XmlMessageHandlerSpec extends Specification {

    void "XmlMessageHandler is registered for XML media types"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        XmlMessageHandler<?> handler = ctx.getBean(XmlMessageHandler)

        expect:
        handler.isReadable(Argument.of(Map), MediaType.APPLICATION_XML_TYPE)
        handler.isReadable(Argument.of(Map), MediaType.TEXT_XML_TYPE)
        !handler.isReadable(Argument.of(Map), MediaType.APPLICATION_JSON_TYPE)
        handler.isWriteable(Argument.of(Map), MediaType.APPLICATION_XML_TYPE)
        handler.isWriteable(Argument.of(Map), MediaType.TEXT_XML_TYPE)

        cleanup:
        ctx.close()
    }

    void "XmlMessageHandler reads ConvertibleValues from XML streams"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        XmlMessageHandler<?> handler = ctx.getBean(XmlMessageHandler)
        String xml = "<root><name>Micronaut</name><age>5</age></root>"

        when:
        def convertibleValues = (ConvertibleValues<?>) handler.read(
                Argument.of(ConvertibleValues),
                MediaType.APPLICATION_XML_TYPE,
                new SimpleHttpHeaders(),
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
        )

        then:
        convertibleValues.get("name", String).orElse(null) == "Micronaut"
        convertibleValues.get("age", Integer).orElse(null) == 5

        cleanup:
        ctx.close()
    }

    void "XmlMessageHandler can serialize maps to XML"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        XmlMessageHandler<Map<String, Object>> handler = (XmlMessageHandler<Map<String, Object>>) ctx.getBean(XmlMessageHandler)
        Map<String, Object> payload = [name: "Micronaut", version: "5.0"]
        SimpleHttpHeaders outgoing = new SimpleHttpHeaders()
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()

        when:
        handler.writeTo(
                Argument.mapOf(String, Object),
                MediaType.APPLICATION_XML_TYPE,
                payload,
                outgoing,
                outputStream
        )

        then:
        String xml = outputStream.toString(StandardCharsets.UTF_8)
        xml.contains("<name>Micronaut</name>")
        xml.contains("<version>5.0</version>")
        outgoing.get(HttpHeaders.CONTENT_TYPE) == MediaType.APPLICATION_XML

        cleanup:
        ctx.close()
    }
}
