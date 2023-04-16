package com.SpeechToText.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class ElasticSearch {
    OkHttpClient client =  new OkHttpClient();

    @Value("${elasticsearch.base.url}")
    private String ELASTIC_SEARCH_URL;
    @Value("${elasticsearch.key}")
    private  String API_KEY;
    @Value("${elasticsearch.index}")
    private  String index;


    public String addData(String user, String data) {
        try {
            String auth = new String(Base64.encodeBase64(API_KEY.getBytes()));
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user",user);
            jsonObject.put("data",data);
            RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(ELASTIC_SEARCH_URL + "/" + index + "/doc")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                    .build();
            Response response = client.newCall(request).execute();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getData(String user, String searchWord) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String auth = new String(Base64.encodeBase64(API_KEY.getBytes()));
        String url = ELASTIC_SEARCH_URL + "/" + index + "/doc/_search?_source=data&q=user:"+user;//       _search?q=user:" + user + "&_source=data";
        if (searchWord != null) url += "+AND+data:" + searchWord;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        response.close();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode hits = root.path("hits").path("hits");
        String data = "";
        for (JsonNode hit : hits) {
            data += hit.path("_source").path("data").asText() + "\n";
        }
        return data;
    }
}
