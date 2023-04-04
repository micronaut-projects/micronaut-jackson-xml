package io.micronaut.xml.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Issue
import spock.lang.Specification

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper

@MicronautTest
class XmlDeserializationSpec extends Specification {
    @Client("/")
    static interface XmlDeserializationApi {
        @Get(uri="/xmlContentType")
        @Consumes(MediaType.APPLICATION_XML)
        SimpleObject getXmlListOfStrings();
    }

    @Inject
    XmlDeserializationApi api

    @Issue("https://github.com/micronaut-projects/micronaut-jackson-xml/issues/175")
    void "testContentTypeApi"() {
        when:
        SimpleObject result = api.getXmlListOfStrings();

        then:
        result
        0 == result.values.size()
    }

    @Controller("/")
    static class XmlDeserializationController {

        @Get(uri="/xmlContentType")
        @Produces(MediaType.APPLICATION_XML)
        String getXmlListOfStrings() {
            return "<SimpleObject><values/></SimpleObject>";
        }

        // This code produces the same thing
        @Get(uri="/xmlContentType2")
        @Produces(MediaType.APPLICATION_XML)
        SimpleObject getXmlListOfStrings2() {
            SimpleObject simpleObject = new SimpleObject();
            return simpleObject;
        }
    }

    @Introspected
    static class SimpleObject {
        Set<String> values;

        SimpleObject() {
            this.values = new HashSet<>()
        }

        @JsonProperty("values")
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @XmlElementWrapper(name="values")
        @XmlElement(name="value")
        Set<String> getValues() {
            return values;
        }

        void setValues(Set<String> values) {
            this.values = values;
        }
    }
}
