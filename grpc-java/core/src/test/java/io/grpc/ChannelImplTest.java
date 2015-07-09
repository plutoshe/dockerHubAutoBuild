/*
 * Copyright 2015, Google Inc. All rights reserved.
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

package io.grpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.grpc.transport.ClientStream;
import io.grpc.transport.ClientStreamListener;
import io.grpc.transport.ClientTransport;
import io.grpc.transport.ClientTransportFactory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Unit tests for {@link ChannelImpl}. */
@RunWith(JUnit4.class)
public class ChannelImplTest {
  private MethodDescriptor<String, Integer> method = MethodDescriptor.create(
      MethodType.UNKNOWN, "/service/method", 1, TimeUnit.SECONDS,
      new StringMarshaller(), new IntegerMarshaller());
  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private ClientTransportFactory mockTransportFactory = mock(ClientTransportFactory.class);
  private ChannelImpl channel = new ChannelImpl(mockTransportFactory, executor);
  @SuppressWarnings("unchecked")
  private ClientCall.Listener<Integer> mockCallListener = mock(ClientCall.Listener.class);
  @SuppressWarnings("unchecked")
  private ClientCall.Listener<Integer> mockCallListener2 = mock(ClientCall.Listener.class);
  @SuppressWarnings("unchecked")
  private ClientCall.Listener<Integer> mockCallListener3 = mock(ClientCall.Listener.class);
  private ArgumentCaptor<ClientTransport.Listener> transportListenerCaptor =
      ArgumentCaptor.forClass(ClientTransport.Listener.class);
  private ArgumentCaptor<ClientStreamListener> streamListenerCaptor =
      ArgumentCaptor.forClass(ClientStreamListener.class);

  @After
  public void tearDown() {
    executor.shutdownNow();
  }

  @Test
  public void timeoutTest() {
    ChannelImpl.TimeoutMarshaller marshaller =
        new ChannelImpl.TimeoutMarshaller();
    assertEquals("1000u", marshaller.toAsciiString(1000L));
    assertEquals(1000L, (long) marshaller.parseAsciiString("1000u"));

    assertEquals("100000m", marshaller.toAsciiString(100000000L));
    assertEquals(100000000L, (long) marshaller.parseAsciiString("100000m"));

    assertEquals("100000S", marshaller.toAsciiString(100000000000L));
    assertEquals(100000000000L, (long) marshaller.parseAsciiString("100000S"));

    // 1,666,667 * 60 has 9 digits
    assertEquals("1666666M", marshaller.toAsciiString(100000000000000L));
    assertEquals(60000000000000L, (long) marshaller.parseAsciiString("1000000M"));

    // 1,666,667 * 60 has 9 digits
    assertEquals("1666666H", marshaller.toAsciiString(6000000000000000L));
    assertEquals(3600000000000000L, (long) marshaller.parseAsciiString("1000000H"));
  }

  @Test
  public void shutdownWithNoTransportsEverCreated() {
    verifyNoMoreInteractions(mockTransportFactory);
    channel.shutdown();
    assertTrue(channel.isShutdown());
    assertTrue(channel.isTerminated());
  }

  @Test
  public void twoCallsAndGracefulShutdown() {
    verifyNoMoreInteractions(mockTransportFactory);
    ClientCall<String, Integer> call = channel.newCall(method);
    verifyNoMoreInteractions(mockTransportFactory);

    // Create transport and call
    ClientTransport mockTransport = mock(ClientTransport.class);
    ClientStream mockStream = mock(ClientStream.class);
    Metadata.Headers headers = new Metadata.Headers();
    when(mockTransportFactory.newClientTransport()).thenReturn(mockTransport);
    when(mockTransport.newStream(same(method), same(headers), any(ClientStreamListener.class)))
        .thenReturn(mockStream);
    call.start(mockCallListener, headers);
    verify(mockTransportFactory).newClientTransport();
    verify(mockTransport).start(transportListenerCaptor.capture());
    ClientTransport.Listener transportListener = transportListenerCaptor.getValue();
    verify(mockTransport)
        .newStream(same(method), same(headers), streamListenerCaptor.capture());
    ClientStreamListener streamListener = streamListenerCaptor.getValue();

    // Second call
    ClientCall<String, Integer> call2 = channel.newCall(method);
    ClientStream mockStream2 = mock(ClientStream.class);
    Metadata.Headers headers2 = new Metadata.Headers();
    when(mockTransport.newStream(same(method), same(headers2), any(ClientStreamListener.class)))
        .thenReturn(mockStream2);
    call2.start(mockCallListener2, headers2);
    verify(mockTransport)
        .newStream(same(method), same(headers2), streamListenerCaptor.capture());
    ClientStreamListener streamListener2 = streamListenerCaptor.getValue();
    Metadata.Trailers trailers = new Metadata.Trailers();
    streamListener2.closed(Status.CANCELLED, trailers);
    verify(mockCallListener2, timeout(1000)).onClose(Status.CANCELLED, trailers);

    // Shutdown
    channel.shutdown();
    assertTrue(channel.isShutdown());
    assertFalse(channel.isTerminated());
    verify(mockTransport).shutdown();

    // Further calls should fail without going to the transport
    ClientCall<String, Integer> call3 = channel.newCall(method);
    call3.start(mockCallListener3, new Metadata.Headers());
    ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
    verify(mockCallListener3, timeout(1000))
        .onClose(statusCaptor.capture(), any(Metadata.Trailers.class));
    assertSame(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());

    // Finish shutdown
    transportListener.transportShutdown();
    assertFalse(channel.isTerminated());
    streamListener.closed(Status.CANCELLED, trailers);
    verify(mockCallListener, timeout(1000)).onClose(Status.CANCELLED, trailers);
    assertFalse(channel.isTerminated());

    transportListener.transportTerminated();
    assertTrue(channel.isTerminated());

    verifyNoMoreInteractions(mockTransportFactory);
    verifyNoMoreInteractions(mockTransport);
    verifyNoMoreInteractions(mockStream);
  }

  @Test
  public void transportFailsOnStart() {
    Status goldenStatus = Status.INTERNAL.withDescription("wanted it to fail");

    // Have transport throw exception on start
    ClientCall<String, Integer> call = channel.newCall(method);
    ClientTransport mockTransport = mock(ClientTransport.class);
    when(mockTransportFactory.newClientTransport()).thenReturn(mockTransport);
    doThrow(goldenStatus.asRuntimeException())
        .when(mockTransport).start(any(ClientTransport.Listener.class));
    call.start(mockCallListener, new Metadata.Headers());
    verify(mockTransportFactory).newClientTransport();
    verify(mockTransport).start(any(ClientTransport.Listener.class));
    ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
    verify(mockCallListener, timeout(1000))
        .onClose(statusCaptor.capture(), any(Metadata.Trailers.class));
    assertSame(goldenStatus, statusCaptor.getValue());

    // Have transport shutdown immediately during start
    call = channel.newCall(method);
    ClientTransport mockTransport2 = mock(ClientTransport.class);
    ClientStream mockStream2 = mock(ClientStream.class);
    Metadata.Headers headers2 = new Metadata.Headers();
    when(mockTransportFactory.newClientTransport()).thenReturn(mockTransport2);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) {
        ClientTransport.Listener listener = (ClientTransport.Listener) invocation.getArguments()[0];
        listener.transportShutdown();
        listener.transportTerminated();
        return null;
      }
    }).when(mockTransport2).start(any(ClientTransport.Listener.class));
    Exception ex = new IllegalStateException("Transport shutdown");
    when(mockTransport2.newStream(same(method), same(headers2), any(ClientStreamListener.class)))
        .thenReturn(mockStream2);
    call.start(mockCallListener2, headers2);
    verify(mockTransportFactory, times(2)).newClientTransport();
    verify(mockTransport2).start(any(ClientTransport.Listener.class));
    verify(mockTransport2).newStream(same(method), same(headers2), streamListenerCaptor.capture());
    Metadata.Trailers trailers2 = new Metadata.Trailers();
    streamListenerCaptor.getValue().closed(Status.CANCELLED, trailers2);
    verify(mockCallListener2, timeout(1000)).onClose(Status.CANCELLED, trailers2);

    // Make sure the Channel can still handle new calls
    call = channel.newCall(method);
    ClientTransport mockTransport3 = mock(ClientTransport.class);
    ClientStream mockStream3 = mock(ClientStream.class);
    Metadata.Headers headers3 = new Metadata.Headers();
    when(mockTransportFactory.newClientTransport()).thenReturn(mockTransport3);
    when(mockTransport3.newStream(same(method), same(headers3), any(ClientStreamListener.class)))
        .thenReturn(mockStream3);
    call.start(mockCallListener3, headers3);
    verify(mockTransportFactory, times(3)).newClientTransport();
    verify(mockTransport3).start(transportListenerCaptor.capture());
    verify(mockTransport3).newStream(same(method), same(headers3), streamListenerCaptor.capture());
    Metadata.Trailers trailers3 = new Metadata.Trailers();
    streamListenerCaptor.getValue().closed(Status.CANCELLED, trailers3);
    verify(mockCallListener3, timeout(1000)).onClose(Status.CANCELLED, trailers3);

    // Make sure shutdown still works
    channel.shutdown();
    assertTrue(channel.isShutdown());
    assertFalse(channel.isTerminated());
    verify(mockTransport3).shutdown();
    transportListenerCaptor.getValue().transportShutdown();
    assertFalse(channel.isTerminated());

    transportListenerCaptor.getValue().transportTerminated();
    assertTrue(channel.isTerminated());

    verifyNoMoreInteractions(mockTransportFactory);
    verifyNoMoreInteractions(mockTransport);
    verifyNoMoreInteractions(mockTransport2);
    verifyNoMoreInteractions(mockTransport3);
    verifyNoMoreInteractions(mockStream2);
    verifyNoMoreInteractions(mockStream3);
  }
}
