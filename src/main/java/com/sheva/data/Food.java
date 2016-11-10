package com.sheva.data;

import com.sheva.api.providers.xml.XmlRootElementCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Entity represent FOOD table.
 *
 * Created by Sheva on 9/28/2016.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "FOOD")
@XmlRootElement(name = "food")
@XmlRootElementCollection(name = "foodlist")
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel(value = "food model class", parent = Person.class)
public class Food implements Serializable {

    @Id
    @Column(name = "foodId")
    @GenericGenerator(name="idGenerator" , strategy="increment")
    @GeneratedValue(generator="idGenerator")
    @ApiModelProperty(required = true, value = "food entity identifier")
    @XmlElement(required = true)
    private Integer id = 0;

    @Column
    @ApiModelProperty(required = true, value = "main description of food entity")
    @XmlElement(required = true)
    private String name;

    public Food() {}

    public Food(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        return name.equals(food.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
