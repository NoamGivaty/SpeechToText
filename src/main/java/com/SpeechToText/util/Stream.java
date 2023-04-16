package com.SpeechToText.util;

import com.SpeechToText.InfiniteStream.InfiniteStreamRecognize;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.SpeechToText.util.ElasticSearch;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class Stream {

    private final SseEmitter emitter = new SseEmitter();
    private static String conversation = "";
    private static String paragraph = "";


    @Autowired
    ElasticSearch elasticSearch;

    public void run()
    {
        new Thread(() -> {
            InfiniteStreamRecognize infiniteStreamRecognize = null;
            try {
                infiniteStreamRecognize = new InfiniteStreamRecognize();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            infiniteStreamRecognize.run();
        }).start();
    }

    public String record(String user, long seconds) throws JsonProcessingException {
        String oldConversation = "";
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        while (elapsedTime < seconds*1000) {
            try {
                Thread.sleep(1000);
                conversation = InfiniteStreamRecognize.getConversation();
                if (!conversation.equals(oldConversation)) {
                    emitter.send(SseEmitter.event().comment(conversation));
                    elasticSearch.addData(user, conversation);
                    oldConversation = conversation;
                    paragraph += conversation + "\n";
                }
                elapsedTime = System.currentTimeMillis() - startTime;
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }
        InfiniteStreamRecognize.shutDownTargetDataLine();
        return paragraph;
    }
}
