> 整合你上次作业的 httpclient/okhttp
```
io.github.kimmking.gateway.outbound.okhttp.OkhttpOutboundHandler;
```

> 实现过滤器。
```java
package io.github.kimmking.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class MyHttpRequestFilter implements HttpRequestFilter {
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        fullRequest.headers().add("name", "ldq");
    }
}

```

> 实现路由
```java
class MyHttpEndpointRouter implements HttpEndpointRouter {
        @Override
        public String route(List<String> endpoints) {
            if ("ldq".equals(routeRule)) {
                return endpoints.get(RandomUtils.nextInt(2));
            }
            return Endpoint.ENDPINT_DEFAULT;
        }
    }
```