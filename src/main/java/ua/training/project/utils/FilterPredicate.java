package ua.training.project.utils;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterPredicate {

    private final List<Predicate> predicates = new LinkedList<>();

    public <T> FilterPredicate add(T field, Function<T, Predicate> function){
        if(field != null && !field.toString().isEmpty()){
            predicates.add(function.apply(field));
        }
        return this;
    }

    public Predicate build(){
        return ExpressionUtils.allOf(predicates);
    }

    public static FilterPredicate builder(){
        return new FilterPredicate();
    }

}