/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.xml.jackson.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.MediaType;
import io.micronaut.http.codec.CodecConfiguration;
import io.micronaut.jackson.codec.JacksonMediaTypeCodec;
import io.micronaut.jackson.codec.JacksonFeatures;
import io.micronaut.runtime.ApplicationConfiguration;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * A jackson based {@link io.micronaut.http.codec.MediaTypeCodec} that handles XML requests/responses.
 *
 * @author Sergey Vishnyakov
 * @since 1.0.0
 */
@Named("xml")
@Singleton
@BootstrapContextCompatible
public class XmlMediaTypeCodec extends JacksonMediaTypeCodec {

    public static final String CONFIGURATION_QUALIFIER = "xml";

    /**
     * @param xmlMapper                Object mapper for xml. If null, retrieved from beanContext
     * @param applicationConfiguration The common application configurations
     * @param codecConfiguration       The configuration for the codec
     */
    public XmlMediaTypeCodec(@Named(CONFIGURATION_QUALIFIER) ObjectMapper xmlMapper,
                             ApplicationConfiguration applicationConfiguration,
                             @Named(CONFIGURATION_QUALIFIER) @Nullable CodecConfiguration codecConfiguration) {
        super(xmlMapper, applicationConfiguration, codecConfiguration,
              MediaType.APPLICATION_XML_TYPE);
    }

    @Override
    public JacksonMediaTypeCodec cloneWithFeatures(JacksonFeatures jacksonFeatures) {
        ObjectMapper objectMapper = this.getObjectMapper().copy();
        jacksonFeatures.getDeserializationFeatures().forEach(objectMapper::configure);
        jacksonFeatures.getSerializationFeatures().forEach(objectMapper::configure);

        return new XmlMediaTypeCodec(objectMapper, applicationConfiguration, codecConfiguration);
    }
}
