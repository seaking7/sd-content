package com.uplus.sdcontent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class KafkaContentDto implements Serializable {
    private Schema schema;
    private Payload payload;
}
