package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdateMode;

import controllers.routes;
import customactions.LogLevel;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Entity
@UpdateMode(updateChangesOnly = false)
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
    
    @Lob
    private byte[] data;

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

    public File getDataAsFile() throws IOException {
        String tmpPathName = play.Play.application().path().getAbsolutePath().concat("/tmp/");
        String fileName    = tmpPathName.concat(this.id.toString()).concat(this.extension);

        File file = new File(fileName);
        if (!file.exists()) {
            //temporary file does NOT exists -> create it
            File   tmpPath     = new File(tmpPathName);
            
            if (!tmpPath.exists()) {
                tmpPath.mkdirs();
            } 
            
            System.out.println(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
                
        return file;
    }

    public void setDataAsFile(File data) throws IOException {
        FileInputStream fileInputStream;

        fileInputStream = new FileInputStream(data);
        byte[] file = IOUtils.toByteArray(fileInputStream);
        fileInputStream.close();
            
        this.data = file;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAssetUrl() {
        return routes.Assets.at(play.Play.application().configuration().getString("eshomo.upload.directory") + getId() + getExtension()).toString();
    }
}
