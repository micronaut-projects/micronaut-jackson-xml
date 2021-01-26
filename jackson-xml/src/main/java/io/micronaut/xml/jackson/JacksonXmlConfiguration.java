/*
 * Copyright 2017-2021 original authors
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
package io.micronaut.xml.jackson;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.core.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for the Jackson XML parser and generator.
 *
 * @since 2.0.1
 */
@ConfigurationProperties("jackson.xml")
@TypeHint(
    value = {
        LinkedHashMap.class,
    })
public class JacksonXmlConfiguration {
    private Map<FromXmlParser.Feature, Boolean> parser = Collections.emptyMap();
    private Map<ToXmlGenerator.Feature, Boolean> generator = Collections.emptyMap();
    private boolean defaultUseWrapper = JacksonXmlAnnotationIntrospector.DEFAULT_USE_WRAPPER;

    /**
     * @return Settings for the parser
     */
    public Map<FromXmlParser.Feature, Boolean> getParserSettings() {
        return parser;
    }

    /**
     * Sets the parser features to use.
     * @param parser The parser features
     */
    public void setParser(Map<FromXmlParser.Feature, Boolean> parser) {
        if (CollectionUtils.isNotEmpty(parser)) {
            this.parser = parser;
        }
    }

    /**
     * @return Settings for the generator
     */
    public Map<ToXmlGenerator.Feature, Boolean> getGeneratorSettings() {
        return generator;
    }

    /**
     * Sets the generator features to use.
     * @param generator The generator features
     */
    public void setGenerator(Map<ToXmlGenerator.Feature, Boolean> generator) {
        if (CollectionUtils.isNotEmpty(generator)) {
            this.generator = generator;
        }
    }

    /**
     * @return True if default wrapper is used
     * */
    public boolean isDefaultUseWrapper() {
        return defaultUseWrapper;
    }

    /**
     * Define if a wrapper will be used for indexed (List, array) properties or not by default.
     * @param state True if wrapping is used by default
     */
    public void setDefaultUseWrapper(boolean state) {
        this.defaultUseWrapper = state;
    }
}
