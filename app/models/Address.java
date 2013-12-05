package models;

import play.db.ebean.*;
import play.data.validation.*;

import com.avaje.ebean.annotation.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.*;

@Entity
@UpdateMode(updateChangesOnly=false)
public class Address extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private User user;

    @Constraints.MaxLength(45)
    private String firstname;

    @Constraints.MaxLength(45)
    private String lastname;

    @Constraints.MaxLength(45)
    private String street;

    @Constraints.MaxLength(20)
    private String zip;

    @Constraints.MaxLength(45)
    private String place;

    @Constraints.MaxLength(45)
    private String phone;

    @Constraints.MaxLength(45)
    @Constraints.Email
    private String email;

    @Constraints.Required
    private Boolean isActive;


	@UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Address> find = new Finder<Integer, Address>(Integer.class, Address.class);

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
