package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Date;

@Entity
@UpdateMode(updateChangesOnly=false)
public class Image extends Model {

    @Id
    private Integer id;

    @ManyToOne
    private Product product;

    @Constraints.Required
    @Constraints.MaxLength(255)
    private String name;

    @Column
    private String description;
    
    @Constraints.Required
    @Constraints.MaxLength(255)
    private String extension;

	@CreatedTimestamp
    private Date createdAt;

    public static Finder<Integer, Image> find = new Finder<Integer, Image>(Integer.class, Image.class);

    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
