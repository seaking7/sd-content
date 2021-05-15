package com.uplus.sdcontent.jpa;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="contents")
public class ContentEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false)
    private String contentId;

    @Column(nullable = false)
    private String contentName;

}
