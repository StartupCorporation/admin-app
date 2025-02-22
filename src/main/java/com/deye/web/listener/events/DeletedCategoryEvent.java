package com.deye.web.listener.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeletedCategoryEvent {
    private UUID categoryId;
    private String fileName;
}
