package com.vodacom.customerregistration.api.repository;

import com.vodacom.customerregistration.api.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    
    /**
     * Find customers registered by a specific agent (using audit createdBy field)
     */
    Page<Customer> findByCreatedByOrderByCreatedDateDesc(String createdBy, Pageable pageable);
    
    /**
     * Find customers registered by a specific agent with name search
     */
    @Query("SELECT c FROM Customer c WHERE c.createdBy = :createdBy AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.nidaNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY c.createdDate DESC")
    Page<Customer> findByCreatedByAndNameOrNidaContaining(@Param("createdBy") String createdBy, 
                                                          @Param("searchTerm") String searchTerm, 
                                                          Pageable pageable);
}
