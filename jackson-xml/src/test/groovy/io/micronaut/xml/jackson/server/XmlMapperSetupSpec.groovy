package io.micronaut.xml.jackson.server
/*
 * Copyright 2017-2021 original authors
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
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.ApplicationContext
import io.micronaut.inject.qualifiers.Qualifiers
import spock.lang.Specification

class XmlMapperSetupSpec extends Specification {

    void 'verify can retrieve xml mapper'() {
        when:
        ApplicationContext applicationContext = ApplicationContext.run('test').start()

        then:
        applicationContext.containsBean(ObjectMapper, Qualifiers.byName('xml'))
    }

    void 'verify can encode/decode xml'() {
        when:
        ApplicationContext applicationContext = ApplicationContext.run('test').start()
        ObjectMapper mapper = applicationContext.getBean(ObjectMapper, Qualifiers.byName('xml'))

        then:
        mapper.readTree('<person><name>test</name></person>').get('name').textValue() == 'test'
        mapper.writeValueAsString(['1', '2', '3']) == '<ArrayList><item>1</item><item>2</item><item>3</item></ArrayList>'
    }

    void 'verify can set xml features'() {
        when: "Object Mapper is configured with non-default XML features"
        ApplicationContext applicationContext = ApplicationContext.run('test', 'xml').start()
        ObjectMapper mapper = applicationContext.getBean(ObjectMapper, Qualifiers.byName('xml'))

        then: "parsing an empty element yields an empty string instead of null"
        mapper.readTree('<person><name/></person>').get('name').textValue() == ''

        and: "generated XML is prefixed with XML declaration"
        mapper.writeValueAsString(['1']) == "<?xml version='1.0' encoding='UTF-8'?><ArrayList><item>1</item></ArrayList>"
    }

    void 'verify can disable to use wrapper by default'() {
        when: "Object Mapper is configured with non-default using wrapper"
        ApplicationContext applicationContext = ApplicationContext.run(["jackson.xml.defaultUseWrapper": false], 'test', 'xml').start()
        ObjectMapper mapper = applicationContext.getBean(ObjectMapper, Qualifiers.byName('xml'))

        then: "define class and serialize to XML using mappers"
        Container container = new Container(["A", "B"])
        mapper.writeValueAsString(container) ==
                "<?xml version='1.0' encoding='UTF-8'?><Container><items>A</items><items>B</items></Container>"
    }

    void 'verify can enable to use wrapper by default'() {
        when: "Object Mapper is configured with non-default using wrapper"
        ApplicationContext applicationContext = ApplicationContext.run(["jackson.xml.defaultUseWrapper": true], 'test', 'xml')
        ObjectMapper mapper = applicationContext.getBean(ObjectMapper, Qualifiers.byName('xml'))

        then: "define class and serialize to XML using mappers"
        Container container = new Container(["A", "B"])
        mapper.writeValueAsString(container) ==
                "<?xml version='1.0' encoding='UTF-8'?><Container><items><items>A</items><items>B</items></items></Container>"
    }

    static class Container {
        List<String> items

        Container(List<String> items) {
            this.items = items
        }
    }
}
