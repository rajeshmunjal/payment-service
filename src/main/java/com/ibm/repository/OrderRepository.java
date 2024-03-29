package com.ibm.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.ibm.model.Order;

@RepositoryRestResource(collectionResourceRel = "orders")
public interface OrderRepository extends PagingAndSortingRepository<Order, String> {
    @RestResource(rel = "by-id", description = @Description("Find order by id"))
    Order findOrderById(@Param("id") String id);

    @SuppressWarnings("unchecked")
	@RestResource(rel = "save", description = @Description("Save order"))
    Order save(@Param("order") Order order);
}
