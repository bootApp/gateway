package com.bootapp.gateway.filters.endpoints

import com.netflix.zuul.filters.http.HttpSyncEndpoint
import com.netflix.zuul.message.http.HttpRequestMessage
import com.netflix.zuul.message.http.HttpResponseMessage
import com.netflix.zuul.message.http.HttpResponseMessageImpl
import com.netflix.zuul.stats.status.StatusCategoryUtils
import com.netflix.zuul.stats.status.ZuulStatusCategory

class Healthcheck extends HttpSyncEndpoint {

    @Override
    HttpResponseMessage apply(HttpRequestMessage request) {
        HttpResponseMessage resp = new HttpResponseMessageImpl(request.getContext(), request, 200)
        resp.setBodyAsText("healthy")
        // need to set this manually since we are not going through the ProxyEndpoint
        StatusCategoryUtils.setStatusCategory(request.getContext(), ZuulStatusCategory.SUCCESS)

        return resp
    }
}