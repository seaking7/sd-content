package com.uplus.sdcontent.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uplus.sdcontent.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ContentProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    List<Field> fields = Arrays.asList(
            new Field("string", true, "content_id"),
            new Field("string", true, "content_name"),
            new Field("string", true, "url"));

    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("contents")
            .build();


    @Autowired
    public ContentProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public ContentDto send(String topic, ContentDto contentDto){
        Payload payload = Payload.builder()
                .content_id(contentDto.getContentId())
                .content_name(contentDto.getContentName())
                .url(contentDto.getUrl())
                .build();

        KafkaContentDto kafkaContentDto = new KafkaContentDto(schema, payload);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try{
            jsonInString = mapper.writeValueAsString(kafkaContentDto);

        } catch(JsonProcessingException ex){
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Content Producer sent data from the Content microservice:" + kafkaContentDto);
        return contentDto;
    }

}
