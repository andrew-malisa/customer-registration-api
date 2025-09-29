package com.vodacom.customerregistration.api.domain;

import com.vodacom.customerregistration.api.domain.enumeration.AgentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;

/**
 * A Agent.
 */
@Entity
@Table(name = "agent")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "agent")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GenericGenerator(name = "snowflake", strategy = "com.vodacom.customerregistration.api.util.SnowflakeJpaIdGenerator")
    @GeneratedValue(generator = "snowflake")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Pattern(regexp = "^(\\+255|0)[67]\\d{8}$")
    @Column(name = "phone_number", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private AgentStatus status;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;


    @Size(max = 100)
    @Column(name = "region", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String region;

    @Size(max = 100)
    @Column(name = "district", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String district;

    @Size(max = 100)
    @Column(name = "ward", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String ward;


    public Long getId() {
        return this.id;
    }

    public Agent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Agent phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AgentStatus getStatus() {
        return this.status;
    }

    public Agent status(AgentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Agent user(User user) {
        this.setUser(user);
        return this;
    }


    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Agent region(String region) {
        this.setRegion(region);
        return this;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Agent district(String district) {
        this.setDistrict(district);
        return this;
    }

    public String getWard() {
        return this.ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public Agent ward(String ward) {
        this.setWard(ward);
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        return getId() != null && getId().equals(((Agent) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
