package com.sheva.data;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.sheva.api.providers.json.LocalDateJsonAdapter;
import com.sheva.api.providers.xml.ColorEnumJaxbAdapter;
import com.sheva.api.providers.xml.LocalDateJaxbAdapter;
import com.sheva.api.providers.xml.XmlRootElementCollection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

/**
 * Person POJO class. Entity relates to PERSON table.
 *
 * Created by Sheva on 9/28/2016.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "PERSON", uniqueConstraints = @UniqueConstraint(columnNames = {"firstName", "lastName", "dateOfBirth"}))
@XmlRootElement
@XmlRootElementCollection(name = "people")
@XmlAccessorType(XmlAccessType.FIELD)
@ApiModel(value = "person model class")
@Path(value = "people")
public class Person implements Serializable {

    @Id
    @Column(name = "personId", unique = true, nullable = false)
    @GenericGenerator(name="idGenerator", strategy="increment")
    @GeneratedValue(generator="idGenerator")
    @ApiModelProperty(required = true, value = "unique person identifier")
    private Integer id = 0;

    @Column(nullable = false)
    @ApiModelProperty(required = true)
    private String firstName;

    @Column
    @ApiModelProperty
    private String lastName;

    @Column
    @JsonAdapter(LocalDateJsonAdapter.class)
    @XmlJavaTypeAdapter(LocalDateJaxbAdapter.class)
    @ApiModelProperty(required = true, value = "Information about person date of birth in YYYY-MM-DD",
            allowableValues = "in format YYYY-MM-DD")
    private LocalDate dateOfBirth;

    @Transient
    @ApiModelProperty(value = "Calculated value")
    private Integer age;

    @ElementCollection(targetClass = Color.class, fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    @CollectionTable(name = "PERSON_COLOR_PREFERENCES", joinColumns = @JoinColumn(name = "personId"))
    @Column(name = "color", nullable = false)
    @XmlJavaTypeAdapter(ColorEnumJaxbAdapter.class)
    @Enumerated(EnumType.STRING)
    @SerializedName("favoriteColor")
    @XmlElementWrapper(name = "favoriteColor")
    @ApiModelProperty(value = "Represent collection of favorite color",
            allowableValues = "red, orange, yellow, green, blue, indigo, violet")
    private Set<Color> color = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(
            name="PERSON_FOOD_PREFERENCES",
            joinColumns = @JoinColumn( name="personId"),
            inverseJoinColumns = @JoinColumn( name="foodId")
    )
    @Cascade(CascadeType.ALL)
    @SerializedName("favoriteFood")
    @XmlElementWrapper(name = "favoriteFood")
    @XmlElement(name = "food")
    @ApiModelProperty(value = "Represent collection of favorite food")
    private Set<Food> food = new HashSet<>();

    public Person() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Access(AccessType.PROPERTY)
    @Type(type = "com.sheva.db.LocalDateType")
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setAge();
    }

    private void setAge() {
        this.age = dateOfBirth != null ? Period.between(dateOfBirth, LocalDate.now()).getYears() : null;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public Set<Color> getColor() {
        return color;
    }

    public void setColor(Set<Color> color) {
        this.color = color;
    }

    public Set<Food> getFood() {
        return food;
    }

    public void setFood(Set<Food> food) {
        this.food = food;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", color=" + color +
                ", food=" + food +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id.equals(person.id)) return false;
        if (!firstName.equals(person.firstName)) return false;
        if (lastName != null ? !lastName.equals(person.lastName) : person.lastName != null) return false;
        return dateOfBirth != null ? dateOfBirth.equals(person.dateOfBirth) : person.dateOfBirth == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        return result;
    }
}
