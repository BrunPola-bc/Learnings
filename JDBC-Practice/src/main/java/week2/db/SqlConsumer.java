package week2.db;

import java.sql.SQLException;

// SqlConsumer is a functional interface similar to java.util.function.Consumer
// but it allows throwing SQLException from the accept method.
//
// This makes fetch[Entity]IfExists methods cleaner
// (setting parameters by Consumer would require try-catch blocks in every lambda)

@FunctionalInterface
public interface SqlConsumer<T> {
  void accept(T t) throws SQLException;
}
