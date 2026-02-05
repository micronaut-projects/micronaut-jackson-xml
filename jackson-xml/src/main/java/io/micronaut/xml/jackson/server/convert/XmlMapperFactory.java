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
package io.micronaut.xml.jackson.server.convert;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.dataformat.xml.XmlFactory;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.XmlWriteFeature;
import tools.jackson.module.kotlin.KotlinModule;
import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.reflect.GenericTypeUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.jackson.JacksonConfiguration;
import io.micronaut.jackson.serialize.MicronautDeserializers;
import io.micronaut.xml.jackson.JacksonXmlConfiguration;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

/**
 * Factory bean for creating the Jackson {@link XmlMapper}.
 *
 * The factory mostly duplicates {@link io.micronaut.jackson.ObjectMapperFactory} with the only difference that it creates
 * object mappers dedicated for xml processing and avoid some json specific configuration that might have been done inside
 * of {@link io.micronaut.jackson.ObjectMapperFactory}.
 *
 * @author Sergey Vishnyakov
 * @since 1.0.0
 */
@Factory
@BootstrapContextCompatible
public class XmlMapperFactory {

    @Inject
    protected ConversionService conversionService;

    @Inject
    protected JacksonModule[] jacksonModules = new JacksonModule[0];

    @Inject
    protected ValueSerializer[] serializers = new ValueSerializer[0];

    @Inject
    protected ValueDeserializer[] deserializers = new ValueDeserializer[0];

    @Inject
    protected ValueSerializerModifier[] beanSerializerModifiers = new ValueSerializerModifier[0];

    @Inject
    protected ValueDeserializerModifier[] beanDeserializerModifiers = new ValueDeserializerModifier[0];

    @Inject
    protected KeyDeserializer[] keyDeserializers = new KeyDeserializer[0];

    /**
     * Factory method to create the XmlMapper.
     *
     * @param jacksonConfiguration The general Jackson configuration
     * @param xmlConfiguration The XML-specific configuration
     * @return A configured XmlMapper
     * @implSpec This method creates an XmlMapper using the builder pattern,
     * applies Micronaut-specific modules, and configures serialization/deserialization
     * settings based on the provided configuration beans.
     */
    @Singleton
    @BootstrapContextCompatible
    @Named("xml")
    public XmlMapper xmlMapper(
        @Nullable JacksonConfiguration jacksonConfiguration,
        @Nullable JacksonXmlConfiguration xmlConfiguration) {

        boolean hasJacksonConfig = jacksonConfiguration != null;
        boolean hasXmlConfig = xmlConfiguration != null;

        XmlFactory xmlFactory = XmlFactory.builder().build();
        XmlMapper.Builder builder = XmlMapper.builder(xmlFactory);

        /* ---------- XML-specific config ---------- */

        if (hasXmlConfig) {
            builder.defaultUseWrapper(xmlConfiguration.isDefaultUseWrapper());
        }

        /* ---------- Modules ---------- */
        builder.addModule(new KotlinModule.Builder().build());
        for (JacksonModule module : jacksonModules) {
            builder.addModule(module);
        }

        SimpleModule micronautModule = new SimpleModule("micronaut");
        micronautModule.setDeserializers(new MicronautDeserializers(conversionService));

        /* ---------- Serializers ---------- */

        for (ValueSerializer<?> serializer : serializers) {
            Class<?> type = serializer.getClass();
            Type annotation = type.getAnnotation(Type.class);

            if (annotation != null) {
                for (Class<?> target : annotation.value()) {
                    micronautModule.addSerializer((Class) target, (ValueSerializer) serializer);
                }
            } else {
                Optional<Class<?>> targetType =
                    GenericTypeUtils.resolveSuperGenericTypeArgument(type);
                targetType.ifPresent(t -> micronautModule.addSerializer((Class) t, (ValueSerializer) serializer));
            }
        }

        /* ---------- Deserializers ---------- */

        for (ValueDeserializer<?> deserializer : deserializers) {
            Class<?> type = deserializer.getClass();
            Type annotation = type.getAnnotation(Type.class);

            if (annotation != null) {
                for (Class<?> target : annotation.value()) {
                    micronautModule.addDeserializer((Class) target, (ValueDeserializer) deserializer);
                }
            } else {
                GenericTypeUtils
                    .resolveSuperGenericTypeArgument(type)
                    .ifPresent(t -> micronautModule.addDeserializer((Class) t, (ValueDeserializer) deserializer));
            }
        }

        /* ---------- Trim strings ---------- */

        if (hasJacksonConfig && jacksonConfiguration.isTrimStrings()) {
            micronautModule.addDeserializer(String.class, new ValueDeserializer<String>() {
                @Override
                public String deserialize(JsonParser p, DeserializationContext ctxt)
                    throws JacksonException {
                    return StringUtils.trimToNull(p.getValueAsString());
                }
            });
        }

        /* ---------- Key deserializers ---------- */

        for (KeyDeserializer keyDeserializer : keyDeserializers) {
            Type annotation = keyDeserializer.getClass().getAnnotation(Type.class);
            if (annotation != null) {
                for (Class<?> target : annotation.value()) {
                    micronautModule.addKeyDeserializer(target, keyDeserializer);
                }
            }
        }

        /* ---------- Modifiers ---------- */

        for (ValueSerializerModifier modifier : beanSerializerModifiers) {
            micronautModule.setSerializerModifier(modifier);
        }

        for (ValueDeserializerModifier modifier : beanDeserializerModifiers) {
            micronautModule.setDeserializerModifier(modifier);
        }

        builder.addModule(micronautModule);

        /* ---------- Core features ---------- */

        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        builder.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        builder.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

        /* ---------- Polymorphic typing ---------- */

        if (hasJacksonConfig && jacksonConfiguration.getDefaultTyping() != null) {
            PolymorphicTypeValidator ptv =
                BasicPolymorphicTypeValidator.builder()
                    .allowIfSubType(Object.class)
                    .build();

            builder.activateDefaultTyping(ptv, jacksonConfiguration.getDefaultTyping());
        }

        /* ---------- General Jackson config ---------- */

        if (hasJacksonConfig) {

            JsonInclude.Include include =
                jacksonConfiguration.getSerializationInclusion();
            if (include != null) {
                builder.changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(include));
            }

            String dateFormat = jacksonConfiguration.getDateFormat();
            if (dateFormat != null) {
                builder.defaultDateFormat(new SimpleDateFormat(dateFormat));
            }

            Locale locale = jacksonConfiguration.getLocale();
            if (locale != null) {
                builder.defaultLocale(locale);
            }

            TimeZone timeZone = jacksonConfiguration.getTimeZone();
            if (timeZone != null) {
                builder.defaultTimeZone(timeZone);
            }

            PropertyNamingStrategy namingStrategy =
                jacksonConfiguration.getPropertyNamingStrategy();
            if (namingStrategy != null) {
                builder.propertyNamingStrategy(namingStrategy);
            }
        }

        /* ---------- XML configuration ---------- */

        if (hasXmlConfig) {
            xmlConfiguration.getParserSettings().forEach((feature, enabled) -> {
                if (enabled) {
                    builder.enable(feature);
                } else {
                    builder.disable(feature);
                }
            });
            xmlConfiguration.getGeneratorSettings().forEach((feature, enabled) -> {
                if (enabled) {
                    builder.enable(feature);
                } else {
                    builder.disable(feature);
                }
            });
        } else {
            builder.disable(XmlWriteFeature.WRITE_XML_DECLARATION);
        }

        builder.disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        return builder.build();
    }
}
