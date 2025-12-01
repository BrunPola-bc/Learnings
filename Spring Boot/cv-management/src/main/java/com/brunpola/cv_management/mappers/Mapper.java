package com.brunpola.cv_management.mappers;

public interface Mapper<A, B> {

  B mapTo(A a);

  A mapFrom(B b);
}
