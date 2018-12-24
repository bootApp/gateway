package com.bootapp.gateway.filters

import com.bootapp.gateway.filters.endpoints.AuthService
import com.bootapp.gateway.filters.endpoints.Healthcheck
import com.netflix.zuul.context.SessionContext
import com.netflix.zuul.filters.http.HttpInboundSyncFilter
import com.netflix.zuul.message.http.HttpRequestMessage
import com.netflix.zuul.netty.filter.ZuulEndPointRunner
class Routes extends HttpInboundSyncFilter {

    @Override
    int filterOrder() {
        return 0
    }

    @Override
    boolean shouldFilter(HttpRequestMessage httpRequestMessage) {
        return httpRequestMessage.hasBody()
    }

    @Override
    boolean needsBodyBuffered(HttpRequestMessage request) {
        return shouldFilter(request)
    }

    @Override
    HttpRequestMessage apply(HttpRequestMessage request) {
        SessionContext context = request.getContext()
        String path = request.getPath()
        String host = request.getOriginalHost()
        if (path.equalsIgnoreCase("/healthcheck")) {
            context.setEndpoint(Healthcheck.class.getCanonicalName())
        } else if (path.startsWith("/auth")) {
            context.setEndpoint(AuthService.class.getCanonicalName())
        } else {
            context.setEndpoint(ZuulEndPointRunner.PROXY_ENDPOINT_FILTER_NAME);
            context.setRouteVIP("api")
        }
        return request
    }
}
