package io.github.kimmking.gateway.outbound.okhttp;

import io.github.kimmking.gateway.outbound.httpclient4.NamedThreadFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.*;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class OkhttpOutboundHandler {

    private String backendUrl;

    private ExecutorService proxyService;

    private CloseableHttpClient client;

    public OkhttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
        int cores = Runtime.getRuntime().availableProcessors() * 2;
        long keepAliveTime = 1000;
        int queueSize = 2048;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores, keepAliveTime, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        client = HttpClientBuilder.create().build();
    }

    public void handle(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullHttpRequest.uri();
        proxyService.submit(() -> fetchGet(fullHttpRequest, ctx, url));

    }

    public void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse closeableHttpResponse = null;
        FullHttpResponse response = null;
        try {
            closeableHttpResponse = client.execute(httpGet);
            response = handleResponse(closeableHttpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (response != null) {
                if (!HttpUtil.isKeepAlive(inbound)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    private DefaultFullHttpResponse handleResponse(CloseableHttpResponse closeableHttpResponse) throws IOException {
        HttpEntity entity = closeableHttpResponse.getEntity();
        if (entity != null) {
            byte[] content = EntityUtils.toByteArray(entity);
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", Integer.parseInt(closeableHttpResponse.getFirstHeader("Content-Length").getValue()));
            return response;
        }
        return null;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
