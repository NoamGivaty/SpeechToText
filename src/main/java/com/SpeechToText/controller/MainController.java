package com.SpeechToText.controller;

import com.SpeechToText.util.Stream;
import com.SpeechToText.InfiniteStream.*;
import com.SpeechToText.util.ElasticSearch;
import com.google.api.client.json.Json;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

@RestController
@RequestMapping("")
public class MainController {

    @Autowired
    Stream stream;
    @Autowired
    ElasticSearch elasticSearch;

    @RequestMapping(value = "Welcome", method = RequestMethod.GET)
    public ResponseEntity<?> Welcome()
    {
        return new ResponseEntity<>("Hi! this is a nice project", HttpStatus.OK);
    }

    @GetMapping(value = "/stream")
    @ResponseBody
    public ResponseEntity<?> Stream(@RequestParam String user, @RequestParam long seconds) throws IOException {
        stream.run();
        return new ResponseEntity<> (stream.record(user,seconds),HttpStatus.OK);
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public ResponseEntity<?> getData(@RequestParam String user,
                                     @RequestParam(required = false) String searchWord)
            throws IOException {

        return new ResponseEntity<>(elasticSearch.getData(user, searchWord), HttpStatus.OK);
    }
}
