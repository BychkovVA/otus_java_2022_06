package ru.otus.jpql.crm.model;


import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "phone")
public class Phone implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "number")
    private String number;

    public Phone() {
    }

    public Phone(String number) {
        this.id = null;
        this.number = number;
    }


    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    @Override
    public String toString() {
        return "phone{" +
                "id=" + id +
                ", number='" + number + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone that = (Phone) o;
        return id.equals(that.id) &&
                number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number);
    }

}
