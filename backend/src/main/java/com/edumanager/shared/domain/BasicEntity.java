package com.edumanager.shared.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.io.Serializable;

@MappedSuperclass
@Getter
public abstract class BasicEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;


    @Override
    public int hashCode() {
        return id!=null?id.hashCode():super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(!(obj instanceof BasicEntity)) return false;

        BasicEntity that = (BasicEntity)obj;
        if(id!=null && that.id!=null) return id.equals(that.id);

        return super.equals(obj);
    }
}
