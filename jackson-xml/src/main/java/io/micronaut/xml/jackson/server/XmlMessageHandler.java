/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.xml.jackson.server;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.convert.value.ConvertibleValues;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.Headers;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyHandler;
import io.micronaut.http.codec.CodecException;
import io.micronaut.xml.jackson.server.convert.ByteArrayXmlStreamReader;
import io.micronaut.xml.jackson.server.convert.XmlStreamConvertibleValues;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
@Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
final class XmlMessageHandler<T> implements MessageBodyHandler<T> {
    private final ObjectMapper objectMapper;
    private final ConversionService conversionService;

    XmlMessageHandler(@Named("xml") ObjectMapper objectMapper, ConversionService conversionService) {
        this.objectMapper = objectMapper;
        this.conversionService = conversionService;
    }

    @Override
    public boolean isReadable(Argument<T> type, MediaType mediaType) {
        return mediaType != null && mediaType.getExtension().equals(MediaType.EXTENSION_XML);
    }

    private static CodecException decorateRead(Argument<?> type, Exception e) {
        return new CodecException("Error decoding XML stream for type [" + type.getName() + "]: " + e.getMessage(), e);
    }

    @Override
    public T read(Argument<T> type, MediaType mediaType, Headers httpHeaders, InputStream inputStream) throws CodecException {
        try {
            if (type.getType() == ConvertibleValues.class) {
                //noinspection unchecked
                return (T) new XmlStreamConvertibleValues<>(new ByteArrayXmlStreamReader(inputStream.readAllBytes()), (XmlMapper) objectMapper, conversionService);
            }

            return objectMapper.readValue(inputStream, constructType(type, objectMapper.getTypeFactory()));
        } catch (IOException | XMLStreamException e) {
            throw decorateRead(type, e);
        }
    }

    @Override
    public boolean isWriteable(Argument<T> type, MediaType mediaType) {
        return mediaType != null && mediaType.getExtension().equals(MediaType.EXTENSION_XML);
    }

    private static CodecException decorateWrite(Object object, IOException e) {
        return new CodecException("Error encoding object [" + object + "] to XML: " + e.getMessage(), e);
    }

    @Override
    public void writeTo(Argument<T> type, MediaType mediaType, T object, MutableHeaders outgoingHeaders, OutputStream outputStream) throws CodecException {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType != null ? mediaType : MediaType.APPLICATION_XML_TYPE);
        try {
            objectMapper.writerFor(constructType(type, objectMapper.getTypeFactory())).writeValue(outputStream, object);
        } catch (IOException e) {
            throw decorateWrite(object, e);
        }
    }

    // from micronaut-jackson-databind

    /**
     * Constructors a JavaType for the given argument and type factory.
     * @param type The type
     * @param typeFactory The type factory
     * @param <T> The generic type
     * @return The JavaType
     */
    public static <T> JavaType constructType(@NonNull Argument<T> type, @NonNull TypeFactory typeFactory) {
        ArgumentUtils.requireNonNull("type", type);
        ArgumentUtils.requireNonNull("typeFactory", typeFactory);
        Map<String, Argument<?>> typeVariables = type.getTypeVariables();
        JavaType[] objects = toJavaTypeArray(typeFactory, typeVariables);
        final Class<T> rawType = type.getType();
        if (ArrayUtils.isNotEmpty(objects)) {
            final JavaType javaType = typeFactory.constructType(
                rawType
            );
            if (javaType.isCollectionLikeType()) {
                return typeFactory.constructCollectionLikeType(
                    rawType,
                    objects[0]
                );
            } else if (javaType.isMapLikeType()) {
                return typeFactory.constructMapLikeType(
                    rawType,
                    objects[0],
                    objects[1]
                );
            } else if (javaType.isReferenceType()) {
                return typeFactory.constructReferenceType(rawType, objects[0]);
            }
            return typeFactory.constructParametricType(rawType, objects);
        } else {
            return typeFactory.constructType(
                rawType
            );
        }
    }

    private static JavaType[] toJavaTypeArray(TypeFactory typeFactory, Map<String, Argument<?>> typeVariables) {
        List<JavaType> javaTypes = new ArrayList<>();
        for (Argument<?> argument : typeVariables.values()) {
            if (argument.hasTypeVariables()) {
                javaTypes.add(typeFactory.constructParametricType(argument.getType(), toJavaTypeArray(typeFactory, argument.getTypeVariables())));
            } else {
                javaTypes.add(typeFactory.constructType(argument.getType()));
            }
        }
        return javaTypes.toArray(new JavaType[0]);
    }
}
