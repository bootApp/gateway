package com.bootapp.gateway.filters.endpoints
import com.bootapp.gateway.rpc.AuthEndpoint
import com.bootapp.service.core.proto.Rpc
import com.google.gson.Gson
import com.google.protobuf.util.JsonFormat
import com.netflix.zuul.filters.http.HttpSyncEndpoint
import com.netflix.zuul.message.http.HttpRequestMessage
import com.netflix.zuul.message.http.HttpResponseMessage
import com.netflix.zuul.message.http.HttpResponseMessageImpl
import com.netflix.zuul.stats.status.StatusCategoryUtils
import com.netflix.zuul.stats.status.ZuulStatusCategory
import org.json.JSONObject
import org.json.simple.parser.JSONParser

class AuthService extends HttpSyncEndpoint {
    @Override
    HttpResponseMessage apply(HttpRequestMessage request) {
        HttpResponseMessage resp = new HttpResponseMessageImpl(request.getContext(), request, 200)
        try {
            Map<String, String> queryParams = request.queryParams.entries().collectEntries {
                [it.getKey(), it.getValue()]
            }
            Rpc.Payload.Builder builder = Rpc.Payload.newBuilder()
            builder.putAllData(queryParams)
            Rpc.Payload payload = builder.build()
            Rpc.Payload.Builder responseBuilder = Rpc.Payload.newBuilder()
            byte[] response
            if (request.hasBody()) {
                Rpc.Payload.Builder bodyBuilder = Rpc.Payload.newBuilder()
                Map<String, String> data = new HashMap<String, String>()
                JSONObject json = (JSONObject) new JSONParser().parse(request.bodyAsText)
                json.keys().each {
                    data.put(it.toString(), json.get(it.toString()).toString())
                }
                bodyBuilder.putAllData(data)
                Rpc.Payload bodyPayload = bodyBuilder.build()
                response = AuthEndpoint.INSTANCE.invoke(request.path, payload.toByteArray(), bodyPayload.toByteArray())
            } else {
                response = AuthEndpoint.INSTANCE.query(request.path, payload.toByteArray())
            }
            responseBuilder.mergeFrom(response)
            resp.setBodyAsText(JsonFormat.printer().print(responseBuilder.build()))
            StatusCategoryUtils.setStatusCategory(request.getContext(), ZuulStatusCategory.SUCCESS)
        } catch (Exception e) {
            print e
        }
        return resp
    }
}