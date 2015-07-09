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

package io.grpc.transport.netty;

import static io.grpc.transport.HttpUtil.Http2Error.CANCEL;
import static io.netty.handler.codec.http2.Http2CodecUtil.DEFAULT_MAX_FRAME_SIZE;
import static io.netty.handler.codec.http2.Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT;
import static io.netty.handler.codec.http2.Http2Stream.State.HALF_CLOSED_LOCAL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameSizePolicy;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.concurrent.ImmediateEventExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

/**
 * Tests for {@link BufferingHttp2ConnectionEncoder}.
 */
@RunWith(JUnit4.class)
public class BufferingHttp2ConnectionEncoderTest {

  private BufferingHttp2ConnectionEncoder encoder;

  private Http2Connection connection;

  @Mock
  private Http2FrameWriter writer;

  @Mock
  private ChannelHandlerContext ctx;

  @Mock
  private Channel channel;

  @Mock
  private ChannelPromise promise;

  /**
   * Init fields and do mocking.
   */
  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    Http2FrameWriter.Configuration configuration = mock(Http2FrameWriter.Configuration.class);
    Http2FrameSizePolicy frameSizePolicy = mock(Http2FrameSizePolicy.class);
    when(writer.configuration()).thenReturn(configuration);
    when(configuration.frameSizePolicy()).thenReturn(frameSizePolicy);
    when(frameSizePolicy.maxFrameSize()).thenReturn(DEFAULT_MAX_FRAME_SIZE);
    when(writer.writeRstStream(eq(ctx), anyInt(), anyLong(), eq(promise))).thenAnswer(
            successAnswer());
    when(writer.writeGoAway(eq(ctx), anyInt(), anyLong(), any(ByteBuf.class), eq(promise)))
        .thenAnswer(successAnswer());

    connection = new DefaultHttp2Connection(false);

    DefaultHttp2ConnectionEncoder defaultEncoder =
        new DefaultHttp2ConnectionEncoder(connection, writer);
    encoder = new BufferingHttp2ConnectionEncoder(defaultEncoder);
    DefaultHttp2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder,
        mock(Http2FrameReader.class), mock(Http2FrameListener.class));

    Http2ConnectionHandler handler = new Http2ConnectionHandler(decoder, encoder);
    // Set LifeCycleManager on encoder and decoder
    when(ctx.channel()).thenReturn(channel);
    when(channel.isActive()).thenReturn(false);
    handler.handlerAdded(ctx);
  }

  @Test
  public void multipleWritesToActiveStream() {
    encoder.writeSettingsAck(ctx, promise);
    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());
    encoder.writeData(ctx, 3, data(), 0, false, promise);
    encoder.writeData(ctx, 3, data(), 0, false, promise);
    encoder.writeData(ctx, 3, data(), 0, false, promise);
    encoderWriteHeaders(3, promise);

    writeVerifyWriteHeaders(times(2), 3, promise);
    verify(writer, times(3))
            .writeData(eq(ctx), eq(3), any(ByteBuf.class), eq(0), eq(false), eq(promise));
  }

  @Test
  public void ensureCanCreateNextStreamWhenStreamCloses() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(1);

    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());

    // This one gets buffered.
    encoderWriteHeaders(5, promise);
    assertEquals(1, connection.numActiveStreams());
    assertEquals(1, encoder.numBufferedStreams());

    // Now prevent us from creating another stream.
    connection.local().maxActiveStreams(0);

    // Close the previous stream.
    connection.stream(3).close();

    // Ensure that no streams are currently active and that only the HEADERS from the first
    // stream were written.
    writeVerifyWriteHeaders(times(1), 3, promise);
    writeVerifyWriteHeaders(never(), 5, promise);
    assertEquals(0, connection.numActiveStreams());
    assertEquals(1, encoder.numBufferedStreams());
  }

  @Test
  public void alternatingWritesToActiveAndBufferedStreams() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(1);

    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());

    encoderWriteHeaders(5, promise);
    assertEquals(1, connection.numActiveStreams());
    assertEquals(1, encoder.numBufferedStreams());

    encoder.writeData(ctx, 3, Unpooled.buffer(0), 0, false, promise);
    writeVerifyWriteHeaders(times(1), 3, promise);
    encoder.writeData(ctx, 5, Unpooled.buffer(0), 0, false, promise);
    verify(writer, never())
            .writeData(eq(ctx), eq(5), any(ByteBuf.class), eq(0), eq(false), eq(promise));
  }

  @Test
  public void bufferingNewStreamFailsAfterGoAwayReceived() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(0);
    connection.goAwayReceived(1, 8, null);

    promise = mock(ChannelPromise.class);
    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());
    verify(promise).setFailure(any(Throwable.class));
  }

  @Test
  public void receivingGoAwayFailsBufferedStreams() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(5);

    int streamId = 3;
    for (int i = 0; i < 9; i++) {
      encoderWriteHeaders(streamId, promise);
      streamId += 2;
    }
    assertEquals(4, encoder.numBufferedStreams());

    connection.goAwayReceived(11, 8, null);

    assertEquals(5, connection.numActiveStreams());
    // The 4 buffered streams must have been failed.
    verify(promise, times(4)).setFailure(any(Throwable.class));
    assertEquals(0, encoder.numBufferedStreams());
  }

  @Test
  public void sendingGoAwayShouldNotFailStreams() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(1);

    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());
    encoderWriteHeaders(5, promise);
    assertEquals(1, encoder.numBufferedStreams());
    encoderWriteHeaders(7, promise);
    assertEquals(2, encoder.numBufferedStreams());

    ByteBuf empty = Unpooled.buffer(0);
    encoder.writeGoAway(ctx, 3, CANCEL.code(), empty, promise);

    assertEquals(1, connection.numActiveStreams());
    assertEquals(2, encoder.numBufferedStreams());
    verify(promise, never()).setFailure(any(GoAwayClosedStreamException.class));
  }

  @Test
  public void endStreamDoesNotFailBufferedStream() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(0);

    encoderWriteHeaders(3, promise);
    assertEquals(1, encoder.numBufferedStreams());

    ByteBuf empty = Unpooled.buffer(0);
    encoder.writeData(ctx, 3, empty, 0, true, promise);

    assertEquals(0, connection.numActiveStreams());
    assertEquals(1, encoder.numBufferedStreams());

    // Simulate that we received a SETTINGS frame which
    // increased MAX_CONCURRENT_STREAMS to 1.
    connection.local().maxActiveStreams(1);
    encoder.writeSettingsAck(ctx, promise);

    assertEquals(1, connection.numActiveStreams());
    assertEquals(0, encoder.numBufferedStreams());
    assertEquals(HALF_CLOSED_LOCAL, connection.stream(3).state());
  }

  @Test
  public void rstStreamClosesBufferedStream() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(0);

    encoderWriteHeaders(3, promise);
    assertEquals(1, encoder.numBufferedStreams());

    verify(promise, never()).setSuccess();
    ChannelPromise rstStreamPromise = mock(ChannelPromise.class);
    encoder.writeRstStream(ctx, 3, CANCEL.code(), rstStreamPromise);
    verify(promise).setSuccess();
    verify(rstStreamPromise).setSuccess();
    assertEquals(0, encoder.numBufferedStreams());
  }

  @Test
  public void bufferUntilActiveStreamsAreReset() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(1);

    encoderWriteHeaders(3, promise);
    assertEquals(0, encoder.numBufferedStreams());
    encoderWriteHeaders(5, promise);
    assertEquals(1, encoder.numBufferedStreams());
    encoderWriteHeaders(7, promise);
    assertEquals(2, encoder.numBufferedStreams());

    writeVerifyWriteHeaders(times(1), 3, promise);
    writeVerifyWriteHeaders(never(), 5, promise);
    writeVerifyWriteHeaders(never(), 7, promise);

    encoder.writeRstStream(ctx, 3, CANCEL.code(), promise);
    assertEquals(1, connection.numActiveStreams());
    assertEquals(1, encoder.numBufferedStreams());
    encoder.writeRstStream(ctx, 5, CANCEL.code(), promise);
    assertEquals(1, connection.numActiveStreams());
    assertEquals(0, encoder.numBufferedStreams());
    encoder.writeRstStream(ctx, 7, CANCEL.code(), promise);
    assertEquals(0, connection.numActiveStreams());
    assertEquals(0, encoder.numBufferedStreams());
  }

  @Test
  public void bufferUntilMaxStreamsIncreased() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(2);

    encoderWriteHeaders(3, promise);
    encoderWriteHeaders(5, promise);
    encoderWriteHeaders(7, promise);
    encoderWriteHeaders(9, promise);
    assertEquals(2, encoder.numBufferedStreams());

    writeVerifyWriteHeaders(times(1), 3, promise);
    writeVerifyWriteHeaders(times(1), 5, promise);
    writeVerifyWriteHeaders(never(), 7, promise);
    writeVerifyWriteHeaders(never(), 9, promise);

    // Simulate that we received a SETTINGS frame which
    // increased MAX_CONCURRENT_STREAMS to 5.
    connection.local().maxActiveStreams(5);
    encoder.writeSettingsAck(ctx, promise);

    assertEquals(0, encoder.numBufferedStreams());
    writeVerifyWriteHeaders(times(1), 7, promise);
    writeVerifyWriteHeaders(times(1), 9, promise);

    encoderWriteHeaders(11, promise);

    writeVerifyWriteHeaders(times(1), 11, promise);

    assertEquals(5, connection.local().numActiveStreams());
  }

  @Test
  public void bufferUntilSettingsReceived() {
    int initialLimit = BufferingHttp2ConnectionEncoder.SMALLEST_MAX_CONCURRENT_STREAMS;
    int numStreams = initialLimit * 2;
    for (int ix = 0, nextStreamId = 3; ix < numStreams; ++ix, nextStreamId += 2) {
      encoderWriteHeaders(nextStreamId, promise);
      if (ix < initialLimit) {
        writeVerifyWriteHeaders(times(1), nextStreamId, promise);
      } else {
        writeVerifyWriteHeaders(never(), nextStreamId, promise);
      }
    }
    assertEquals(numStreams / 2, encoder.numBufferedStreams());

    // Simulate that we received a SETTINGS frame.
    encoder.writeSettingsAck(ctx, promise);

    assertEquals(0, encoder.numBufferedStreams());
    assertEquals(numStreams, connection.local().numActiveStreams());
  }

  @Test
  public void closedBufferedStreamReleasesByteBuf() {
    encoder.writeSettingsAck(ctx, promise);
    connection.local().maxActiveStreams(0);
    ByteBuf data = mock(ByteBuf.class);
    encoderWriteHeaders(3, promise);
    assertEquals(1, encoder.numBufferedStreams());
    encoder.writeData(ctx, 3, data, 0, false, promise);

    ChannelPromise rstPromise = mock(ChannelPromise.class);
    encoder.writeRstStream(ctx, 3, CANCEL.code(), rstPromise);

    assertEquals(0, encoder.numBufferedStreams());
    verify(rstPromise).setSuccess();
    verify(promise, times(2)).setSuccess();
    verify(data).release();
  }

  private void encoderWriteHeaders(int streamId, ChannelPromise promise) {
    encoder.writeHeaders(ctx, streamId, new DefaultHttp2Headers(), 0, DEFAULT_PRIORITY_WEIGHT,
                         false, 0, false, promise);
  }

  private void writeVerifyWriteHeaders(VerificationMode mode, int streamId,
                                       ChannelPromise promise) {
    verify(writer, mode).writeHeaders(eq(ctx), eq(streamId), any(Http2Headers.class), eq(0),
                                      eq(DEFAULT_PRIORITY_WEIGHT), eq(false), eq(0),
                                      eq(false), eq(promise));
  }

  private Answer<ChannelFuture> successAnswer() {
    return new Answer<ChannelFuture>() {
      @Override
      public ChannelFuture answer(InvocationOnMock invocation) throws Throwable {
        ChannelPromise future =
            new DefaultChannelPromise(channel, ImmediateEventExecutor.INSTANCE);
        future.setSuccess();
        return future;
      }
    };
  }

  private static ByteBuf data() {
    ByteBuf buf = Unpooled.buffer(10);
    for (int i = 0; i < buf.writableBytes(); i++) {
      buf.writeByte(i);
    }
    return buf;
  }
}
