package models;

import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
public class User extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private UserType type;

    @Constraints.Email
    @Constraints.Required
    private String email;

    @Constraints.MinLength(40)
    @Constraints.MaxLength(40)
    private String salt;

    @Constraints.MinLength(40)
    @Constraints.MaxLength(40)
    private String password;
    
    @Constraints.MinLength(40)
    @Constraints.MaxLength(40)
    private String token;

	@Constraints.Required
    private Boolean isActive = true;
	
	@OneToMany
	private List<Address> addresses;

	@UpdatedTimestamp
    private Date updatedAt;

    @CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, User> find = new Finder<Integer, User>(Integer.class, User.class);

    public Integer getId() {
        return id;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
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

    /**
	 * @return the addresses
	 */
	public List<Address> getAddresses() {
		return addresses;
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

    @SuppressWarnings("deprecation")
	public void setPassword(String password) {
        this.salt = DigestUtils.shaHex(RandomStringUtils.randomAscii(40));
        this.password = DigestUtils.shaHex(this.salt + password);        
        this.token = DigestUtils.shaHex(this.salt + password + this.email);
    }

    @SuppressWarnings("deprecation")
	public boolean isPasswordCorrect(String password) {
        return DigestUtils.shaHex(this.salt + password).equals(this.password);
    }
    public String getToken() {
		return token;
	}

}
