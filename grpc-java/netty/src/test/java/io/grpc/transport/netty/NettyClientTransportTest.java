/*
 * Copyright 2014, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.grpc.transport.netty;

import static com.google.common.base.Charsets.UTF_8;
import static io.netty.handler.codec.http2.Http2CodecUtil.DEFAULT_WINDOW_SIZE;
import static org.junit.Assert.fail;

import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.SettableFuture;

import io.grpc.Marshaller;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.MethodType;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.testing.TestUtils;
import io.grpc.transport.ClientStream;
import io.grpc.transport.ClientStreamListener;
import io.grpc.transport.ClientTransport;
import io.grpc.transport.ServerListener;
import io.grpc.transport.ServerStream;
import io.grpc.transport.ServerStreamListener;
import io.grpc.transport.ServerTransport;
import io.grpc.transport.ServerTransportListener;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tests for {@link NettyClientTransport}.
 */
@RunWith(JUnit4.class)
public class NettyClientTransportTest {

  @Mock
  private ClientTransport.Listener clientTransportListener;

  private final List<NettyClientTransport> transports = new ArrayList<NettyClientTransport>();
  private NioEventLoopGroup group;
  private InetSocketAddress address;
  private NettyServer server;
  private EchoServerListener serverListener = new EchoServerListener();

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    group = new NioEventLoopGroup(1);
    address = TestUtils.testServerAddress(TestUtils.pickUnusedPort());
  }

  @After
  public void teardown() throws Exception {
    for (NettyClientTransport transport : transports) {
      transport.shutdown();
    }

    if (server != null) {
      server.shutdown();
    }

    group.shutdownGracefully(0, 10, TimeUnit.SECONDS);
  }

  /**
   * Verifies that we can create multiple TLS client transports from the same builder.
   */
  @Test
  public void creatingMultipleTlsTransportsShouldSucceed() throws Exception {
    startServer();

    // Create a couple client transports.
    ProtocolNegotiator negotiator = newNegotiator();
    for (int index = 0; index < 2; ++index) {
      NettyClientTransport transport = newTransport(negotiator);
      transport.start(clientTransportListener);
    }

    // Send a single RPC on each transport.
    final List<Rpc> rpcs = new ArrayList<Rpc>(transports.size());
    for (NettyClientTransport transport : transports) {
      rpcs.add(new Rpc(transport).halfClose());
    }

    // Wait for the RPCs to complete.
    for (Rpc rpc : rpcs) {
      rpc.waitForResponse();
    }
  }

  @Test
  public void bufferedStreamsShouldBeClosedWhenConnectionTerminates() throws Exception {
    // Only allow a single stream active at a time.
    startServer(1);

    NettyClientTransport transport = newTransport(newNegotiator());
    transport.start(clientTransportListener);

    // Send a dummy RPC in order to ensure that the updated SETTINGS_MAX_CONCURRENT_STREAMS
    // has been received by the remote endpoint.
    new Rpc(transport).halfClose().waitForResponse();

    // Create 3 streams, but don't half-close. The transport will buffer the second and third.
    Rpc[] rpcs = new Rpc[] { new Rpc(transport), new Rpc(transport), new Rpc(transport) };

    // Wait for the response for the stream that was actually created.
    rpcs[0].waitForResponse();

    // Now forcibly terminate the connection from the server side.
    serverListener.transports.get(0).channel().pipeline().firstContext().close();

    // Now wait for both listeners to be closed.
    for (Rpc rpc : rpcs) {
      try {
        rpc.waitForClose();
        fail("Expected the RPC to fail");
      } catch (ExecutionException e) {
        // Expected.
      }
    }
  }

  private ProtocolNegotiator newNegotiator() throws IOException {
    File clientCert = TestUtils.loadCert("ca.pem");
    SslContext clientContext = GrpcSslContexts.forClient().trustManager(clientCert)
        .ciphers(TestUtils.preferredTestCiphers(), SupportedCipherSuiteFilter.INSTANCE).build();
    return ProtocolNegotiators.tls(clientContext, address);
  }

  private NettyClientTransport newTransport(ProtocolNegotiator negotiator) {
    NettyClientTransport transport = new NettyClientTransport(address, NioSocketChannel.class,
            group, negotiator, DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW_SIZE);
    transports.add(transport);
    return transport;
  }

  private void startServer() throws IOException {
    startServer(100);
  }

  private void startServer(int maxStreamsPerConnection) throws IOException {
    File serverCert = TestUtils.loadCert("server1.pem");
    File key = TestUtils.loadCert("server1.key");
    SslContext serverContext = GrpcSslContexts.forServer(serverCert, key)
        .ciphers(TestUtils.preferredTestCiphers(), SupportedCipherSuiteFilter.INSTANCE).build();
    server = new NettyServer(address, NioServerSocketChannel.class,
            group, group, serverContext, maxStreamsPerConnection,
            DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW_SIZE);
    server.start(serverListener);
  }

  private static class Rpc {
    static final String MESSAGE = "hello";
    static final MethodDescriptor<String, String> METHOD = MethodDescriptor.create(
            MethodType.UNARY, "/testService/test", 10, TimeUnit.SECONDS, StringMarshaller.INSTANCE,
            StringMarshaller.INSTANCE);

    final ClientStream stream;
    final TestClientStreamListener listener = new TestClientStreamListener();

    Rpc(NettyClientTransport transport) {
      this(transport, new Metadata.Headers());
    }

    Rpc(NettyClientTransport transport, Metadata.Headers headers) {
      stream = transport.newStream(METHOD, headers, listener);
      stream.request(1);
      stream.writeMessage(new ByteArrayInputStream(MESSAGE.getBytes()));
      stream.flush();
    }

    Rpc halfClose() {
      stream.halfClose();
      return this;
    }

    void waitForResponse() throws InterruptedException, ExecutionException, TimeoutException {
      listener.responseFuture.get(10, TimeUnit.SECONDS);
    }

    void waitForClose() throws InterruptedException, ExecutionException, TimeoutException {
      listener.closedFuture.get(10, TimeUnit.SECONDS);
    }
  }

  private static class TestClientStreamListener implements ClientStreamListener {
    private final SettableFuture<Void> closedFuture = SettableFuture.create();
    private final SettableFuture<Void> responseFuture = SettableFuture.create();

    @Override
    public void headersRead(Metadata.Headers headers) {
    }

    @Override
    public void closed(Status status, Metadata.Trailers trailers) {
      if (status.isOk()) {
        closedFuture.set(null);
      } else {
        StatusException e = status.asException();
        closedFuture.setException(e);
        responseFuture.setException(e);
      }
    }

    @Override
    public void messageRead(InputStream message) {
      responseFuture.set(null);
    }

    @Override
    public void onReady() {
    }
  }

  private static class EchoServerListener implements ServerListener {
    final List<NettyServerTransport> transports = new ArrayList<NettyServerTransport>();

    @Override
    public ServerTransportListener transportCreated(final ServerTransport transport) {
      transports.add((NettyServerTransport) transport);
      return new ServerTransportListener() {

        @Override
        public ServerStreamListener streamCreated(final ServerStream stream, String method,
                                                  Metadata.Headers headers) {
          stream.request(1);
          return new ServerStreamListener() {

            @Override
            public void messageRead(InputStream message) {
              // Just echo back the message.
              stream.writeMessage(message);
              stream.flush();
            }

            @Override
            public void onReady() {
            }

            @Override
            public void halfClosed() {
              // Just close when the client closes.
              stream.close(Status.OK, new Metadata.Trailers());
            }

            @Override
            public void closed(Status status) {
            }
          };
        }

        @Override
        public void transportTerminated() {
        }
      };
    }

    @Override
    public void serverShutdown() {
    }
  }

  private static class StringMarshaller implements Marshaller<String> {
    static final StringMarshaller INSTANCE = new StringMarshaller();

    @Override
    public InputStream stream(String value) {
      return new ByteArrayInputStream(value.getBytes(UTF_8));
    }

    @Override
    public String parse(InputStream stream) {
      try {
        return new String(ByteStreams.toByteArray(stream), UTF_8);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
