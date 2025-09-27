package com.vodacom.customerregistration.api.service.mapper;

import com.vodacom.customerregistration.api.domain.Customer;
import com.vodacom.customerregistration.api.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    CustomerDTO toDto(Customer s);

    Customer toEntity(CustomerDTO customerDTO);
}
