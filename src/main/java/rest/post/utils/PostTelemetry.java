package rest.post.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;

public class PostTelemetry {
    private String token;
    private JSONObject params;
    private CloseableHttpClient httpclient = HttpClients.createDefault();

    public PostTelemetry(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public void execute() throws IOException {
        String url = "http://localhost:8008/api/v1/" + token + "/telemetry";
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(params.toString());
        httpPost.addHeader("content-type", "application/json");
        httpPost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httpPost);
    }
}
