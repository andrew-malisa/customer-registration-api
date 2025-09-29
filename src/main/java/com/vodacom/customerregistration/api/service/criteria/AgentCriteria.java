package com.vodacom.customerregistration.api.service.criteria;

import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.vodacom.customerregistration.api.domain.Agent} entity. This class is used
 * in {@link com.vodacom.customerregistration.api.web.rest.AgentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /agents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AgentStatus
     */
    public static class AgentStatusFilter extends Filter<AgentStatus> {

        public AgentStatusFilter() {}

        public AgentStatusFilter(AgentStatusFilter filter) {
            super(filter);
        }

        @Override
        public AgentStatusFilter copy() {
            return new AgentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter phoneNumber;

    private AgentStatusFilter status;

    private UUIDFilter userId;

    private LongFilter customerId;

    private StringFilter region;

    private StringFilter district;

    private StringFilter ward;

    private Boolean distinct;

    public AgentCriteria() {}

    public AgentCriteria(AgentCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.phoneNumber = other.optionalPhoneNumber().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AgentStatusFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(UUIDFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.region = other.optionalRegion().map(StringFilter::copy).orElse(null);
        this.district = other.optionalDistrict().map(StringFilter::copy).orElse(null);
        this.ward = other.optionalWard().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AgentCriteria copy() {
        return new AgentCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public Optional<UUIDFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public UUIDFilter id() {
        if (id == null) {
            setId(new UUIDFilter());
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<StringFilter> optionalPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            setPhoneNumber(new StringFilter());
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AgentStatusFilter getStatus() {
        return status;
    }

    public Optional<AgentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AgentStatusFilter status() {
        if (status == null) {
            setStatus(new AgentStatusFilter());
        }
        return status;
    }

    public void setStatus(AgentStatusFilter status) {
        this.status = status;
    }

    public UUIDFilter getUserId() {
        return userId;
    }

    public Optional<UUIDFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public UUIDFilter userId() {
        if (userId == null) {
            setUserId(new UUIDFilter());
        }
        return userId;
    }

    public void setUserId(UUIDFilter userId) {
        this.userId = userId;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public StringFilter getRegion() {
        return region;
    }

    public Optional<StringFilter> optionalRegion() {
        return Optional.ofNullable(region);
    }

    public StringFilter region() {
        if (region == null) {
            setRegion(new StringFilter());
        }
        return region;
    }

    public void setRegion(StringFilter region) {
        this.region = region;
    }

    public StringFilter getDistrict() {
        return district;
    }

    public Optional<StringFilter> optionalDistrict() {
        return Optional.ofNullable(district);
    }

    public StringFilter district() {
        if (district == null) {
            setDistrict(new StringFilter());
        }
        return district;
    }

    public void setDistrict(StringFilter district) {
        this.district = district;
    }

    public StringFilter getWard() {
        return ward;
    }

    public Optional<StringFilter> optionalWard() {
        return Optional.ofNullable(ward);
    }

    public StringFilter ward() {
        if (ward == null) {
            setWard(new StringFilter());
        }
        return ward;
    }

    public void setWard(StringFilter ward) {
        this.ward = ward;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentCriteria that = (AgentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(status, that.status) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(region, that.region) &&
            Objects.equals(district, that.district) &&
            Objects.equals(ward, that.ward) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNumber, status, userId, customerId, region, district, ward, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalPhoneNumber().map(f -> "phoneNumber=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalRegion().map(f -> "region=" + f + ", ").orElse("") +
            optionalDistrict().map(f -> "district=" + f + ", ").orElse("") +
            optionalWard().map(f -> "ward=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
