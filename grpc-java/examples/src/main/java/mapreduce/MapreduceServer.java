package mapreduce;


import io.grpc.ServerImpl;
import io.grpc.stub.StreamObserver;
import io.grpc.transport.netty.NettyServerBuilder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapreduceServer {
  private static final Logger logger = Logger.getLogger(MapreduceServer.class.getName());

  private int port = 10000;
  private ServerImpl grpcServer;



  /** Start serving requests. */
  public void start() throws IOException {
    grpcServer = NettyServerBuilder.forPort(port)
        .addService(MapperStreamGrpc.bindService(new MapperStreamService()))
        .build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may has been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        MapreduceServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  /** Stop serving requests and shutdown resources. */
  public void stop() {
    if (grpcServer != null) {
      grpcServer.shutdown();
    }
  }

  public MapreduceServer(int port) {
    this.port = port;
  }

  public MapreduceServer() {
    this.port = 10000;
  }

  /** Stop serving requests and shutdown resources. */
  public static boolean isSeperator(char cc) {
    if (cc == '，' || cc == '。' || cc == '？' || cc == '！' || cc == '；') {
      return true;     
    }
    return false;
  }

  /** Stop serving requests and shutdown resources. */
  public static boolean inRange(int x) {
    int llimit = 3;
    int ulimit = 30;
    return (llimit <= x && x <= ulimit);
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws Exception {
    final MapreduceServer server;
    if (args.length == 1) {
      server = new MapreduceServer(Integer.parseInt(args[0]));
    } else {
      server = new MapreduceServer();
    }
    server.start();
  }

  private class MapperStreamService implements MapperStreamGrpc.MapperStream {

    @Override
    public StreamObserver<MapperRequest> getStreamEmitResult(
        final StreamObserver<MapperResponse> responseObserver
    ) {
      return new StreamObserver<MapperRequest>() {
        @Override
        public void onValue(MapperRequest note) {
          List<KvPair> kvs = note.getArrList();
          MapperResponse.Builder w = MapperResponse.newBuilder();
          for (KvPair kv : kvs) {
            String curSentence = kv.getKey();
            curSentence = curSentence + "。";
            char[] ac = curSentence.toCharArray();
            curSentence = "";
            for (int i = 0; i < ac.length; ++i) {
              if (isSeperator(ac[i])) {
                if (inRange(curSentence.length())) {
                  w.addArr(KvPair.newBuilder().setKey(curSentence).setValue("").build());
                }
                curSentence = "";
              } else {
                curSentence += ac[i];
              }
            }
          }
          responseObserver.onValue(w.build());
        }

        @Override
        public void onError(Throwable t) {
          logger.log(Level.WARNING, "Encountered error in routeChat", t);
        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();
        }
      };
    }

  }
}
