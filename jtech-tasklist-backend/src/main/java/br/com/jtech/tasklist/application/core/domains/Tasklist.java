/*
*  @(#)Tasklist.java
*
*  Copyright (c) J-Tech Solucoes em Informatica.
*  All Rights Reserved.
*
*  This software is the confidential and proprietary information of J-Tech.
*  ("Confidential Information"). You shall not disclose such Confidential
*  Information and shall use it only in accordance with the terms of the
*  license agreement you entered into with J-Tech.
*
*/
package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.input.protocols.TasklistRequest;
import br.com.jtech.tasklist.adapters.output.persistence.entities.TaskEntity;
import lombok.*;

import java.util.UUID;
import java.util.List;


/**
* class Tasklist 
* 
* user angelo.vicente 
*/
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Tasklist {

    private String id;

    public static List<Tasklist> of(List<TaskEntity> entities) {
        return entities.stream().map(Tasklist::of).toList();
     }

    public TaskEntity toEntity() {
        return TaskEntity.builder()
            .id(UUID.fromString(getId()))
            .build();
     }

    public static Tasklist of(TaskEntity entity) {
        return Tasklist.builder()
            .id(entity.getId().toString())
            .build();
     }

    public static Tasklist of(TasklistRequest request) {
        return Tasklist.builder()
            .id(request.getId())
            .build();
     }
 }