package com.vodacom.customerregistration.api.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.vodacom.customerregistration.api.domain.Customer} entity. This class is used
 * in {@link com.vodacom.customerregistration.api.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter middleName;

    private StringFilter lastName;

    private LocalDateFilter dateOfBirth;

    private StringFilter nidaNumber;

    private InstantFilter registrationDate;

    private StringFilter region;

    private StringFilter district;

    private StringFilter ward;

    private LongFilter registeredById;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.middleName = other.optionalMiddleName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.dateOfBirth = other.optionalDateOfBirth().map(LocalDateFilter::copy).orElse(null);
        this.nidaNumber = other.optionalNidaNumber().map(StringFilter::copy).orElse(null);
        this.registrationDate = other.optionalRegistrationDate().map(InstantFilter::copy).orElse(null);
        this.region = other.optionalRegion().map(StringFilter::copy).orElse(null);
        this.district = other.optionalDistrict().map(StringFilter::copy).orElse(null);
        this.ward = other.optionalWard().map(StringFilter::copy).orElse(null);
        this.registeredById = other.optionalRegisteredById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getMiddleName() {
        return middleName;
    }

    public Optional<StringFilter> optionalMiddleName() {
        return Optional.ofNullable(middleName);
    }

    public StringFilter middleName() {
        if (middleName == null) {
            setMiddleName(new StringFilter());
        }
        return middleName;
    }

    public void setMiddleName(StringFilter middleName) {
        this.middleName = middleName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public LocalDateFilter getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<LocalDateFilter> optionalDateOfBirth() {
        return Optional.ofNullable(dateOfBirth);
    }

    public LocalDateFilter dateOfBirth() {
        if (dateOfBirth == null) {
            setDateOfBirth(new LocalDateFilter());
        }
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateFilter dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public StringFilter getNidaNumber() {
        return nidaNumber;
    }

    public Optional<StringFilter> optionalNidaNumber() {
        return Optional.ofNullable(nidaNumber);
    }

    public StringFilter nidaNumber() {
        if (nidaNumber == null) {
            setNidaNumber(new StringFilter());
        }
        return nidaNumber;
    }

    public void setNidaNumber(StringFilter nidaNumber) {
        this.nidaNumber = nidaNumber;
    }

    public InstantFilter getRegistrationDate() {
        return registrationDate;
    }

    public Optional<InstantFilter> optionalRegistrationDate() {
        return Optional.ofNullable(registrationDate);
    }

    public InstantFilter registrationDate() {
        if (registrationDate == null) {
            setRegistrationDate(new InstantFilter());
        }
        return registrationDate;
    }

    public void setRegistrationDate(InstantFilter registrationDate) {
        this.registrationDate = registrationDate;
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

    public LongFilter getRegisteredById() {
        return registeredById;
    }

    public Optional<LongFilter> optionalRegisteredById() {
        return Optional.ofNullable(registeredById);
    }

    public LongFilter registeredById() {
        if (registeredById == null) {
            setRegisteredById(new LongFilter());
        }
        return registeredById;
    }

    public void setRegisteredById(LongFilter registeredById) {
        this.registeredById = registeredById;
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
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(middleName, that.middleName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(dateOfBirth, that.dateOfBirth) &&
            Objects.equals(nidaNumber, that.nidaNumber) &&
            Objects.equals(registrationDate, that.registrationDate) &&
            Objects.equals(region, that.region) &&
            Objects.equals(district, that.district) &&
            Objects.equals(ward, that.ward) &&
            Objects.equals(registeredById, that.registeredById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            middleName,
            lastName,
            dateOfBirth,
            nidaNumber,
            registrationDate,
            region,
            district,
            ward,
            registeredById,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalMiddleName().map(f -> "middleName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalDateOfBirth().map(f -> "dateOfBirth=" + f + ", ").orElse("") +
            optionalNidaNumber().map(f -> "nidaNumber=" + f + ", ").orElse("") +
            optionalRegistrationDate().map(f -> "registrationDate=" + f + ", ").orElse("") +
            optionalRegion().map(f -> "region=" + f + ", ").orElse("") +
            optionalDistrict().map(f -> "district=" + f + ", ").orElse("") +
            optionalWard().map(f -> "ward=" + f + ", ").orElse("") +
            optionalRegisteredById().map(f -> "registeredById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
