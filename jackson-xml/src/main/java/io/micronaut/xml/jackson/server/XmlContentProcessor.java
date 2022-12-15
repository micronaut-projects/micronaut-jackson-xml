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
package io.micronaut.xml.jackson.server;

import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.netty.AbstractHttpContentProcessor;
import io.micronaut.http.server.netty.NettyHttpRequest;
import io.micronaut.xml.jackson.server.convert.ByteArrayXmlStreamReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

/**
 * This class will handle subscribing to a Xml stream and binding once the events are complete in a non-blocking
 * manner.
 *
 * @author Sergey Vishnyakov
 * @author James Kleeh
 * @since 1.0.0
 */
public class XmlContentProcessor extends AbstractHttpContentProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(XmlContentProcessor.class);
    private static final int DEFAULT_REQUEST_SIZE = 1024;

    private final ByteArrayOutputStream byteArrayStream;

    /**
     * @param nettyHttpRequest The {@link NettyHttpRequest}
     * @param configuration    The {@link HttpServerConfiguration}
     */
    public XmlContentProcessor(NettyHttpRequest<?> nettyHttpRequest, HttpServerConfiguration configuration) {
        super(nettyHttpRequest, configuration);

        int requestLength = this.advertisedLength != -1 ? (int) this.advertisedLength : DEFAULT_REQUEST_SIZE;
        byteArrayStream = new ByteArrayOutputStream(requestLength);
    }

    @Override
    protected void onData(ByteBufHolder message, Collection<Object> out) throws Throwable {
        ByteBuf content = message.content();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Buffer xml bytes of size {}", content.readableBytes());
        }

        try {
            content.readBytes(byteArrayStream, content.readableBytes());
        } finally {
            content.release();
        }
    }

    @Override
    public void complete(Collection<Object> out) throws Throwable {
        byte[] bytes = byteArrayStream.toByteArray();
        ByteArrayXmlStreamReader byteArrayXmlStreamReader = new ByteArrayXmlStreamReader(bytes);
        out.add(byteArrayXmlStreamReader);
    }
}
