# JDBC Bilješke

## ENTITY
- Napravi klasu za svaku tablicu (1 instanca <-> 1 red tablice)
- Često praktično koristit `record` za jednostavne entitete
- Posebne klase za često korištene kombinacije tablica  
  npr. Osoba + sve njezine vještine

## MAPPER
- Za pretvaranje `ResultSet`-a u entitete

## KOMUNIKACIJA S BAZOM

### Uspostava konekcije
```java
Connection con = DriverManager.getConnection(url, user, password);
```
- Praktično zapakirat u posebnu funkciju ako su `url`, `user`, `password` uvijek isti

### Primjer korištenja konekcije
```java
String sqlQuery = "NEKI QUERY";
try (Connection con = getConnection()) {

    // Deklaracija statement najčešće u try-with-resources bloku
    Statement stmt = con.createStatement(); // BEZ string arg
    // ILI
    PreparedStatement prepStmt = con.prepareStatement(query); // SA string arg (query)

    // Postavljanje parametara (index kreće od 1 !!!)
    prepStmt.setString(1, "Ime"); // index parametra + vrijednost
    prepStmt.setInt(2, 0);

    try (ResultSet rs = stmt.executeQuery(sqlQuery))
    // ILI
    try (ResultSet rs = prepStmt.executeQuery())
    // ILI
    // Isto samo executeUpdate()
    {
        /* Nešto radimo s ResultSet-om */
    }

} catch (SQLException e) {
    /* Riješi exception */
}
```

- Često praktično sve od jednom u istom try-with-resources bloku:
```java
try (Connection con = getConnection();
     Statement stmt = con.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {

    // Radi nešto s rs

} catch (SQLException e) {
    // Riješi exception
}
```

### Execute metode
- `executeQuery` kad je **SELECT** (ništa se ne mijenja) → vraća `ResultSet`
- `executeUpdate` kad je **INSERT, UPDATE, DELETE, TRUNCATE, CREATE / DROP / ALTER** (nešto se mijenja) → vraća `int` (broj promijenjenih / zahvaćenih redova)
- Iz `ResultSet`-a čitamo s:
  - `rs.getString("imeStupca")`
  - `rs.getInt("imeStupca")`  
  i slično

### Generated Keys
- Ako želimo dohvatiti ID novog reda nakon `INSERT`:
- Kod deklaracije preparedStatementa dodajemo argument `Statement.RETURN_GENERATED_KEYS`
```java
PreparedStatement ps = con.prepareStatement(
    "INSERT INTO users(username) VALUES (?)",
    Statement.RETURN_GENERATED_KEYS
);
ps.setString(1, "Alice");
int affected = ps.executeUpdate();

try (ResultSet rs = ps.getGeneratedKeys()) {
    if (rs.next()) {
        long id = rs.getLong(1);
    }
}
```

### Autocommit & Rollback
- JDBC default: **autocommit = true**
- Za transakcije, tj. kad želimo promijeniti SVE ili NIŠTA - djelomični update bi stvorio greške
```java
try{
    con.setAutoCommit(false);
    // više updatea
    con.commit();
    con.setAutoCommit(true);
} catch {
    con.rollback(); //Vrati stanje na prije try bloka
}
```

### CallableStatement
- Koristi se za **pozivanje pohranjenih procedura** kloje su već definirane u bazi:
```java
CallableStatement cs = con.prepareCall("{CALL MyProcedure(?)}");
cs.setString(1, "argument");
ResultSet rs = cs.executeQuery();
```

### Lambda / SqlConsumer
- Korištenje **lambda izraza za postavljanje argumenata PreparedStatementa** može pojednostaviti generičke metode.  
  - Pogledaj primjere u `fetchIfExists` i `insertAndReturnId` metodama u DBcomms klasi.

