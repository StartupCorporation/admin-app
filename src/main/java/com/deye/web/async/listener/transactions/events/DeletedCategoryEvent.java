package com.deye.web.async.listener.transactions.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeletedCategoryEvent {
    private UUID categoryId;
    private List<String> filesNamesToRemove;
    private Set<UUID> removedProductsIds;
}
