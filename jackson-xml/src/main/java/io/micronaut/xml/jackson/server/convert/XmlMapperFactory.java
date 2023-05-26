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
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import java.io.IOException;
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
    // has to be fully qualified due to JDK Module type
    protected com.fasterxml.jackson.databind.Module[] jacksonModules = new com.fasterxml.jackson.databind.Module[0];

    @Inject
    protected JsonSerializer[] serializers = new JsonSerializer[0];

    @Inject
    protected JsonDeserializer[] deserializers = new JsonDeserializer[0];

    @Inject
    protected BeanSerializerModifier[] beanSerializerModifiers = new BeanSerializerModifier[0];

    @Inject
    protected BeanDeserializerModifier[] beanDeserializerModifiers = new BeanDeserializerModifier[0];

    @Inject
    protected KeyDeserializer[] keyDeserializers = new KeyDeserializer[0];

    /**
     * Builds the core Jackson {@link ObjectMapper} from the optional configuration and {@link com.fasterxml.jackson.core.JsonFactory}.
     *
     * @param jacksonConfiguration The configuration
     * @param xmlConfiguration The XML configuration
     * @return The {@link ObjectMapper}
     */
    @Singleton
    @BootstrapContextCompatible
    @Named("xml")
    public XmlMapper xmlMapper(@Nullable JacksonConfiguration jacksonConfiguration, @Nullable JacksonXmlConfiguration xmlConfiguration) {

        final boolean hasXmlConfiguration = xmlConfiguration != null;
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        if (hasXmlConfiguration) {
            xmlModule.setDefaultUseWrapper(xmlConfiguration.isDefaultUseWrapper());
        }

        XmlMapper objectMapper = new XmlMapper(xmlModule);

        final boolean hasConfiguration = jacksonConfiguration != null;
        if (!hasConfiguration || jacksonConfiguration.isModuleScan()) {
            objectMapper.findAndRegisterModules();
        }
        objectMapper.registerModules(jacksonModules);
        SimpleModule module = new SimpleModule("micronaut");
        module.setDeserializers(new MicronautDeserializers(conversionService));

        for (JsonSerializer serializer : serializers) {
            Class<? extends JsonSerializer> type = serializer.getClass();
            Type annotation = type.getAnnotation(Type.class);
            if (annotation != null) {
                Class<?>[] value = annotation.value();
                for (Class<?> aClass : value) {
                    module.addSerializer(aClass, serializer);
                }
            } else {
                Optional<Class<?>> targetType = GenericTypeUtils.resolveSuperGenericTypeArgument(type);
                if (targetType.isPresent()) {
                    module.addSerializer(targetType.get(), serializer);
                } else {
                    module.addSerializer(serializer);
                }
            }
        }

        for (JsonDeserializer deserializer : deserializers) {
            Class<? extends JsonDeserializer> type = deserializer.getClass();
            Type annotation = type.getAnnotation(Type.class);
            if (annotation != null) {
                Class<?>[] value = annotation.value();
                for (Class<?> aClass : value) {
                    module.addDeserializer(aClass, deserializer);
                }
            } else {
                Optional<Class<?>> targetType = GenericTypeUtils.resolveSuperGenericTypeArgument(type);
                targetType.ifPresent(aClass -> module.addDeserializer(aClass, deserializer));
            }
        }

        if (hasConfiguration && jacksonConfiguration.isTrimStrings()) {
            module.addDeserializer(String.class, new StringDeserializer() {
                @Override
                public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String value = super.deserialize(p, ctxt);
                    return StringUtils.trimToNull(value);
                }
            });
        }

        for (KeyDeserializer keyDeserializer : keyDeserializers) {
            Class<? extends KeyDeserializer> type = keyDeserializer.getClass();
            Type annotation = type.getAnnotation(Type.class);
            if (annotation != null) {
                Class<?>[] value = annotation.value();
                for (Class<?> clazz : value) {
                    module.addKeyDeserializer(clazz, keyDeserializer);
                }
            }
        }
        objectMapper.registerModule(module);

        for (BeanSerializerModifier beanSerializerModifier : beanSerializerModifiers) {
            objectMapper.setSerializerFactory(
                    objectMapper.getSerializerFactory().withSerializerModifier(
                            beanSerializerModifier
                    ));
        }

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);

        if (hasConfiguration) {

            ObjectMapper.DefaultTyping defaultTyping = jacksonConfiguration.getDefaultTyping();
            if (defaultTyping != null) {
                objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), defaultTyping);
            }

            JsonInclude.Include include = jacksonConfiguration.getSerializationInclusion();
            if (include != null) {
                objectMapper.setSerializationInclusion(include);
            }
            String dateFormat = jacksonConfiguration.getDateFormat();
            if (dateFormat != null) {
                objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
            }
            Locale locale = jacksonConfiguration.getLocale();
            if (locale != null) {
                objectMapper.setLocale(locale);
            }
            TimeZone timeZone = jacksonConfiguration.getTimeZone();
            if (timeZone != null) {
                objectMapper.setTimeZone(timeZone);
            }
            PropertyNamingStrategy propertyNamingStrategy = jacksonConfiguration.getPropertyNamingStrategy();
            if (propertyNamingStrategy != null) {
                objectMapper.setPropertyNamingStrategy(propertyNamingStrategy);
            }

            jacksonConfiguration.getSerializationSettings().forEach(objectMapper::configure);
            jacksonConfiguration.getDeserializationSettings().forEach(objectMapper::configure);
            jacksonConfiguration.getMapperSettings().forEach(objectMapper::configure);
            jacksonConfiguration.getParserSettings().forEach(objectMapper::configure);
            jacksonConfiguration.getGeneratorSettings().forEach(objectMapper::configure);
        }

        if (hasXmlConfiguration) {
            xmlConfiguration.getParserSettings().forEach(objectMapper::configure);
            xmlConfiguration.getGeneratorSettings().forEach(objectMapper::configure);
        }

        return objectMapper;
    }
}
