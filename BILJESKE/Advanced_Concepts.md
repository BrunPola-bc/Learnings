# Module 1: Advanced SQL
## JOIN
- Tablica je lijeva (LEFT) ili desna (RIGHT) ovisno o tome s koje strane `JOIN` keyworda se nalazi
- `... FROM left_table [NEKI] JOIN right_table ON ...`
### (INNER) JOIN
- `JOIN` defaulta u `INNER JOIN`
- pokazuje retke samo kad je uvjet `ON` uvjet potpuno ispunjen (obje vrijednosti postoje)
- Primjer:
```sql
SELECT o.Name AS Owner_Name, p.Name AS Pet_Name
FROM Owners AS o
INNER JOIN Pets AS p
ON p.ID = o.Pet_ID
```
- Pokazuje samo retke s vlasnicima koji imaju kućnog ljubimca i kućnim ljubimcima koji imaju vlasnike
- **Side Note**: `AS` je opcionalno u gornjem primjeru na svim mjestima
### LEFT JOIN
- pokazuje sve retke lijeve tablice + relevantne informacije iz desne kad je `ON` uvjet ispunjen
- ako ne postoji *match* (npr. u lijevoj talici je `Pet_ID` `null` ili se vrijednost `o.Pet_ID` ne poklapa s niti jednim `p.ID`) očekivane vrijednosti desne tablice iz `SELECT` dijela se postavljaju na `null` 
### RIGHT JOIN
- suprotno od LEFT JOIN: pokazuje sve retke desne tablice + relevantne informacije iz lijeve kad je uvjet `ON` ispunjen
- ako ne postoji *match*, vrijednosti lijeve tablice se postavljaju na `null`
### FULL JOIN
- sadrži sve retke koje bi sadržavao `LEFT JOIN` **ILI** `RIGHT JOIN`
- dakle sve za koje POSTOJI *match* (kao `INNER JOIN`)
-   \+ sve retke lijeve tablice za koje ne postoji *match* u desnoj  (vrijednosti iz desne  tablice se postavljaju na `null`)
-   \+ sve retke desne  tablice za koje ne postoji *match* u lijevoj (vrijednosti iz lijeve tablice se postavljaju na `null`)

## UNION
- `JOIN` povezuje tablice **"horizontalno"**, tj. spaja dva retka iz različitih tablica u jedan redak nove tablice.
- `UNION` radi to **"vertikalno"**, tj. konkatenira retke u nastavku
- Primjer:
```sql
SELECT Age FROM Pets
UNION ALL
SELECT Age FROM Owners
```
- imena stupaca NE moraju biti ista
### UNION ALL
- Može sadržavati duplikate
### UNION DISTINCT
- Sadržava svaku vrijednost samo jednom

## Analytic Functions
- dokumentacija: https://docs.cloud.google.com/bigquery/docs/reference/standard-sql/window-function-calls
- Primjer za tablicu koja govori koliko je svaka osoba (`id` stupac) na koji dan (`date`) uložila minuta (`time`) za trening:
```sql
SELECT *, AVG(time) OVER(
                        PARTITION BY id
                        ORDER BY date
                        ROWS BETWEEN 1 PRECEDING AND CURRENT ROW
                    ) AS avg_time
FROM train_time
```
- Analitičke funkcije koriste podskup redaka za analitički izračun
- Za razliku od Agregacijskih, Analitičke funkcija daju rezultat za svaki red u originalnoj tablici

### OVER clause
- pomoću ključne riječi `OVER` definiramo skup redaka koji se koristi u izračunu
- 3 opcionalna dijela:
#### PARTITION BY
- Određuje po čemu se dijele redovi (u gornjem primjeru po `id` osobe)
- Paralela `GROUP BY` naredbi, ali zbog korištenja `PARTITION BY` dobivamo output za **SVAKI** redak umjesto za svaku grupu
#### ORDER BY
- Kojim redom se računa analitička funkcija (utječe na ***window frame***)
#### ROWS BETWEEN ...
- Određuje s kojim redcima se računa analitička funkcija
- Primjeri:
  - `ROWS BETWEEN 1 PRECEDING AND CURRENT ROW` (2 reda)
  - `ROWS BETWEEN 3 PRECEDING AND 1 FOLLOWING` (5 redaka)
  - `ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING` (svi redovi u particiji)

## Nested and Repeated Data
- Ako se ispravno koristi moeže zamijeniti skupe `JOIN` queryje
### Nested Data
- Jedan stupac može imati više podataka, tad za njega kažemo da je *Nested Column* (ugniježdeni stupac??)
- Nested stupac može imati više polja (*fields nested inside the column*)
- Takav stupac je tipa STRUCT ili RECORD
```python
SchemField('ID', 'INTEGER', 'NULLABLE', None, () ),
SchemField('Name', 'STRING', 'NULLABLE', None, () ),
SchemField('Age', 'INTEGER', 'NULLABLE', None, () ),
SchemField('Animal', 'STRING', 'NULLABLE', None, () ),
SchemField('Toy', 'RECORD', 'NULLABLE', None, (
    SchemField('Name', 'STRING', 'NULLABLE', None, () ),
    SchemField('Type', 'STRING', 'NULLABLE', None, () )
) )
```
- Ekvivalent u SQL-u (ovo možda updateam kasnije):
```sql
CREATE TABLE pets_and_toys (
    ID INT64,
    Name STRING,
    Age INT64,
    Animal STRING,
    Toy STRUCT<
        Name STRING,
        Type STRING
    >
);
```
- Do tih polja dolazimo koristeći `Toy.Name` ili `Toy.Type`

### Repeated Data
- Python: `SchemaField('Toys', 'STRING', 'REPEATED', None, ())`
- SQL: `Toys ARRAY<STRING>`
- Tip stupca je `ARRAY`
- Možemo koristiti `UNNEST()` keyword da "raspakiramo" array, ali to stvara po jedan redak za svaki član arraya
- Npr: 
```sql
SELECT *, toy_type
FROM pets_and_toys
UNNEST(Toys) AS toy_type
```
- gornji query daje ovakve stupce:
```
ID  | Name          | Age | Animal  | Toys                | toy_type
1   | Zulu          | 1   | Dog     | [Ball, Stick, Rope] | Ball
1   | Zulu          | 1   | Dog     | [Ball, Stick, Rope] | Stick
1   | Zulu          | 1   | Dog     | [Ball, Stick, Rope] | Rope
2   | Someone Else  | ...
```
- Dakle, za **svaki član** arraya stvara po **jedan redak** (!!! row explosion)

### Nested and Repeated at the same time
- Python:
```python
SchemField('Toys', 'RECORD', 'NULLABLE', None, (
    SchemField('Name', 'STRING', 'NULLABLE', None, () ),
    SchemField('Type', 'STRING', 'NULLABLE', None, () )
) )
```
- SQL:
```sql
Toy ARRAY<STRUCT<
    Name STRING,
    Type STRING
>>
```
- Primjer korištenja (`UNNEST` + dohvaćanje polja `.`-notacijom):
```sql
SELECT  Name AS Pet_Name,
        t.Name AS Toy_Name,
        t.Type AS Toy_Type
FROM Pets_and_Toys
  UNNEST(Toys) AS t
```

## Pisanje efikasnih queryja
- Strategija 1: SELECT samo stupce koje trebamo, izbjegavati `SELECT *`
- Strategija 2: *Read Less Data*
  - npr. ako imamo `SELECT A.name, B.name` i u istom queryju `WHERE A.id = B.id`, a znamo da je korespondencija name:id = 1:1,
    bolje da koristimo WHERE `A.name = B.name` jer ne čitamo stupac `id` samo za `WHERE` clause
  - ovo dosta ovisi o količini podataka koji se čitaju i o tome da li je stupac `name` indeksiran.
    Ako nije možda je ipak bolje koristiti `A.id = B.id` 
- Strategija 3: Izvbjegavaj N:N `JOIN`-ove 

## CASE
- SQL inačica `if` ili `switch` naredbe
- Npr. za dodati novi stupac ovisno o nekoj drugoj varijabli:
```sql
SELECT Name, Age,
    CASE
        WHEN Age > 39 THEN '40s+'
        WHEN Age > 29 THEN '30s'
        WHEN Age > 19 THEN '20s'
        ELSE 'Teen or younger'
    END AS Age_Group
FROM table_name
```
- Gornjim queryjem dobijemo tablicu sa 3 stupca, u zadnjem (`Age_Group`) su navedene vrijednosti definirane u `CASE`-u
- `CASE` završavamo sa `END`

## HAVING
- Ne možemo koristiti `WHERE` s agregatnim funkcija: ~~`WHERE AVG(Salary) > 45000`~~
- Za to korstimo `HAVING` keyword: `HAVING AVG(Salary) > 45000`
- **MORA** ići direktno **poslije `GROUP BY`** (i prije `ORDER BY`)

## CTE (Common Table Expression)
- **`WITH`**
- **Privremene tablice** koje traju samo za vrijeme izvršavanja **TOG** queryja
- tehnički nije niti tablica nego imenovani query result
- Ako želimo rezultat nekog queryja koristiti kao tablicu u drugom queryju, možemo koristiti CTE:
```sql
WITH CTE_some_cte_table AS (
    SELECT
    -- Query s kojim stvaramo CTE tablicu
)
SELECT
    -- Glavni query koji izvršavamo
FROM CTE_some_cte_table
```
- Ne moramo nužno koristiti CTE u `FROM` dijelu queryja nego u bilo kojem *subqueryju*
- CTE možemo `JOIN`-ati s drugim tablicam
- Možemo definirati **više** CTE-ova (odvojeno zarezom) prije glavnog querya

## Temp Tables
- **Privremene tablice** koje traju za vrijeme **SESSIONA**, ali ipak više od jednog queryja
- fizički postoji u memoriji ili temp storage
- Sintaksa (ovisno o dijalektu):
    - `CREATE TEMP TABLE ...`
    - `CREATE TEMPORARY TABLE ...`
    - `CREATE TABLE #temp_table_name ...` (ovdje je samo `#` prije imena ključ)
- Ostatak deklaracije kao za običnu tablicu
- Može se puniti pomoću SELECT-a iz druge tablice
```sql
INSERT INTO #temp_table_name (id, column_name)
SELECT regular_id, other_column_name
FROM regular_table_name
...
```
- Praktičan za skripte koje koriste slične tablice uviše queryja - korištenje temp tablica mijenja ponavljanje izvođenja queryja

## String Functions
- MySQL dokumentacija: https://dev.mysql.com/doc/refman/8.4/en/string-functions.html
- Googla ostale, al vecina je vjerojatno ista
- TRIM, LTRIM, RTRIM
- REPLACE
- SUBSTRING
- UPPER, LOWER (case)

## Stored Procedures
- Ekvivalent Funkciji
- Definicija:
```sql
CREATE PROCEDURE procedure_name
AS
-- NEKI QUERY
```
- U nekim dijalektima:
```sql
CREATE PROCEDURE procedure_name()
BEGIN
    -- Query ili drukčija naredba
END
```
- Pozivanje:
```sql
EXEC procedure_name
-- ili (ovisno o dijalektu)
CALL procedure_name
```
- Sprema se u bazi, bilo ko s dopuštenjem ju može koristiti i/ili mijenjati. Kad se promijeni, promijeni se za SVE korisnike
- Može imati i više naredbi i kontrolni flow (IF, LOOP) za razliku od običnog queryja
- Može imati input i output parametre:
```sql
DELIMITER //
-- definicija
CREATE PROCEDURE GetUserName(IN user_id INT, OUT username VARCHAR(100))
BEGIN
    SELECT name INTO username
    FROM users
    WHERE id = user_id;
END //

DELIMITER ;

-- poziv
SET @name = '';
CALL GetUserName(101, @name);
SELECT @name;  -- Returns the user’s name
```
- !!! Provjeriti sintaksu ovisno o sql dijalektu / tipu baze. Ovo je jedna od stvari koja je dosta rzličita

## Subquery
- Primjer:
```sql
SELECT id, salary, (SELECT AVG(salary) FROM table_name) AS alias
FROM same_or_different_table_name
```
- Može biti u `SELECT` dijelu
- Može biti u `FROM` dijelu, ali vjerojatno je CTE ili Temp Tablica bolja opcija za ovaj slučaj
- KORISNO u `WHERE` dijelu - koristimo `IN`. Primjer:
```sql
SELECT id, name
FROM table_name
WHERE id IN (SELECT id
            FROM same_or_different_table_name
            WHERE [neki uvjet ...] )
```
- Npr. kad želimo samo podatke o osobama starijim od X godina, a godine su pohranjene u nekoj drugoj tablici

## Views
- Slično CTE i Temp tablicama, ali je spremljeno "zauvijek"
- Sprema se samo query, tj. *View* je **virtualna tablica**
- U queryjima se koristi kao obična tablica, ali kad se koristi view prije se pokreće query koji ga izgradi
- Primjer:
```sql
CREATE VIEW view_name AS
SELECT
-- ...
```
- Dalje se koristi kao obicna tablica (`SELECT column_name FROM view_name`)

### Materialized view
- Iznimka viewa koja se sprema na disk (ne kreira se queryjem svaki put kad se pozove)
- Ne updatea se sama, nego na poziv `REFRESH MATERIALIZED VIEW materialized_view_name`
- Kreira se uz dodatak ključne riječi `Materialized`:
```sql
CREATE MATERIALIZED VIEW materialzed_view_name AS
SELECT
-- ...
```

## Indexi
- Dobar video: https://www.youtube.com/watch?v=lYh6LrSIDvY
- Podaci iz tablice su spremljeni na disk u blokove
- U jednom bloku može biti više redaka
- Dakle svaki redak ima **indeks bloka** u memoriji i **indeks UNUTAR bloka** (recimo B i I)
- (( OVO SU INDEKSI U SMISLU "rednog broja", NE U SMISLU INDEKSA BAZE PODATAKA  - bolji izraz bi bio **fizička adresa**))
- Redci tablice su na disk spremljeni u "nasumičnom poretku" (vjerojatno onom kojim su dodavani ???)
- Ako često queryjamo po nekom stupcu (npr. `WHERE name = 'some_name'`), svaki put prolazimo kroz SVE retke
  (sve blokove memorije, sve indekse unutar bloka) dok ne nađemo sve *match*eve,
  dakle `O(n)` redaka (za `n` = broj redaka)
- Na takvim stupcima možemo definirati **INDEX**:
```sql
CREATE INDEX index_name
ON table_name (column_name);
```
- **Index** u smislu baze podataka je dodatna struktura (koja zauzima memoriju na disku) koja sadržava sve vrijednosti iz `column_name` stupca
  i indekse bloka (B) i indekse unutar bloka (I) za pripadajući redak 
```
value | B   | I
------|-----|-------
Ante  | 100 | 45
Bante | 31  | 1
Cante | 18  | 6436
...   |
```
- Vrijednosti iz `column_name` stupca su sortirane nekim smislenim redoslijedom, npr.:
  - abecedno ako je String
  - uzlazno ako je numerički tip
  - `...`
- Idući put kad queryjamo po stupcu na kojem je definiran INDEX, prvo prolazimo kroz index **tree traversal** pretraživanjem
  (slično binarnom),
  dakle `O(log n)` vremena (za `n` = broj redaka), i tako nađemo B i I vrijednosti pomoću kojih lakše dolazimo do saih redaka

- **Dakle u INDEX-u su spremljene samo vrijednosti indeksiranog stupca i informacije o memorijskoj lokaciji pojedinog retka**.

- Indexi najčešće nisu tablice kao u gornjj ilustraciji nego najčešće *B-tree*
- Ako indeksiramo stupac neke tablice koji se često mijenja, moramo često mijenjati i strukturu indexa
  (da bi vrijednosti ostale smisleno poredane), a taj proces predstavlja veliki trošak (vremenski, cpu, ...)
- Isto vrijedi i za indeksiranje tablica koje redovito primaju nove retke
- U takvim situacijama treba razmotriti ***cost vs benefit***, u smislu provodi li se taj query dovoljno često da je prednosot indexa
  veća nego trošak za njegovo održavanje

### Composite index
- Višestupčani index
```sql
CREATE INDEX idx_name_role
ON users (name, role);
```
- Redoslijed stupaca je JAKO bitan
- Pomaže za:
  - `WHERE name = 'Ante'`
  - `WHERE name = 'Ante' AND role = 'admin'`
- NE pomaže za:
  - `WHERE role = 'admin'` (jer je role drugi stupac u indexu)
- Interno je i dalje B-Tree, samo je ključ kombinacija stupaca

### Unique index
- Garantira da nema duplikata
```sql
CREATE UNIQUE INDEX idx_users_email
ON users (email);
```

### Covering index
- Velika prednost kad u queryju tražimo samo stupac / stupce koji su u tom indexu jer baza može odgovoriti bez čitanja tablice / diska
```sql
CREATE INDEX idx_name_email
ON users (name, email);
-------------------------
SELECT name, email
FROM users
WHERE name = 'Ante';
```

### B-tree
- Dobra vizualizacija: https://www.youtube.com/watch?v=K1a2Bk8NrYQ
- Slično binarnom stablu, ali svaki čvor može imati VIŠE ključeva.
- Binarno stablo je imalo jedan ključ s kojim smo uspoređivali idemo li u lijevu ili desnu granu
- B-tree ih ima više, recimo N, ali svaki čvor ima N+1 dijete
- Ne biramo lijevo ili desno (2 izbora) nego
  - Manje od 1. ključa? -> najlijevije dijete
  - Između 1. i 2. ključa? -> drugo dijete s lijeva
  - ...
  - Veće od zadnjeg (N-tog) ključa -> najdesnije dijete
```
            [ 30    |    50     |           70 ]
          /         |            \                \
       /            |             \                 \
[10 | 20 | 25]    [35 | 40 | 45]    [55 | 60 | 65]    [75 | 80 | 90]
```
Legenda:
- Root čvor ima ključeve: 30, 50, 70
- Root ima 4 djece (N+1 = 4)
- Svako dijete je leaf čvor s 3 ključa
- Pristup:
    * Tražiš vrijednost 12 -> manja od 30 -> ideš u prvo dijete [10|20|25]
    * Tražiš vrijednost 37 -> između 30 i 50 -> drugo dijete [35|40|45]
    * Tražiš vrijednost 68 -> između 50 i 70 -> treće dijete [55|60|65]
    * Tražiš vrijednost 85 -> veće od 70 -> četvrto dijete [75|80|90]

- Svi listovi su na istoj dubini
- Svi čvorovi imaju broj ključevca između maksimuma koji mi određujemo i minimuma koji je floor(maksimum/2)
- Eventualno Korijen može imati manje od minimuma

---

# Module 2: Spring Data JPA
Već raspisano u SpringBoot.md bilješkama. Kratko ponavljanje:
- `spring.jpa.hibernate.ddl-auto=update` svaki put se shema update (stvaraju nove tablice kad se promijeni ime,
  novi stupci kad se dodaju varijable ...)
- `@Entity` označava klasu kao Entity -> po njoj se stvara nova tablica
- `@Id` označava id (primarni ključ) tablice
- Sa `@Table` možemo dati ime tablici različito od imena java klase (`@Table(name = "tbl_name")`)
- Slično, sa `@Column` možemo dati ime stupca različito od imena varijable (`@Column(name = "column_name")`)
  - Inače je default da se varijable u cammel caseu (`columnName`) pretvaraju u stupce u snake caseu (`column_name`)
- U argumentu `@Column` možemo definirati da stupac ne smije biti prazan (null) sa `@Column(nullable = false)`
  (više argumenata odvajamo zarezom)
Ostatak je dublje razrađen tek u ovom learning pathu

## Sequence
Možemo definirati *sequence* za npr auto incrementirajući ID:
```java
@Id
@SequenceGenerator(
        name = "student_sequence",
        sequenceName = "student_sequence",
        allocationSize = 1
)
@GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "student_sequence"
)
private Long studentId;
```
U jednostavnijim bazamo (gdje je podržan `AUTO_INCREMENT`) možemo i
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
Jedna od razlika između strategije `SEQUENCE` i `IDENTITY` je što SEQUENCE generira hibernate, a IDENTITY tek baza.
S IDENTITYjem ne možemo raditi efikasni batch insert

## Constraints
Npr Unique Constraint, kad želimoda se vrijednosti u nekom stupcu ne ponavljaju definiramo argumentom `uniqueConstraint` `@Table`
anotacije i anotacijom `@UniqueConstraint`:
```java
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "emailid_unique",
                columnNames = "email_address"
        )
)
```

## Indexing
Sklično kao i constraints:
```java
@Table(
    name = "Person",
    indexes = {
        @Index(name = "idx_lastname", columnList = "lastName"),
        @Index(name = "idx_first_last", columnList = "firstName,lastName")
    }
)
```

## Embeded & Embeddable
- Ako želimo neku strukturu koristiti u tablici moramo tu strukturu definirati u zasebnoj klasi označenoj s `@Embeddable` anotacijom
- Unutar Entityja koristimo `@Embeded` anotaciju iznad varijable
- Varijable unutar `@Embeded` objekta se "raspakiraju" u zasebne stupce
- NE KORISTIMO `@Entity` uz `@Embeddable` ako ne želimo da se za tu strukturu također stvori tablica u bazi
- Ako neki stpac koji odgovara varijabli u `@Embeddable` klasi želimo da ima drugačije ime, koristimo `@AttributeOverrides` i `@AtributeOverride` kao u primjeru
- Primjer:
  - `@Embeddable` klasa:
```java
@Embeddable
@AttributeOverrides({
  @AttributeOverride(name = "name", column = @Column(name = "guardian_name")),
  @AttributeOverride(name = "email", column = @Column(name = "guardian_email")),
  @AttributeOverride(name = "mobile", column = @Column(name = "guardian_mobile"))
})
public class Guardian {

  private String name;
  private String email;
  private String mobile;
}
```
- `@Entity` sa `@Embeded` varijablom:
```java
@Entity
@Table(name = "students")
public class Student {

  @Id
  private Long studentId;
  private String firstName;
  private String lastName;
  private String emailId;

  @Embedded
  private Guardian guardian;
}
```
- Rezultat tablica ima stupce: `student_id`, `email_address`, `first_name`, `guardian_email`, `guardian_mobile`, `guardian_name` i `last_name`.


## JPA Query Methods
- Dovoljno je samo definirati u repozitoriju, nije potrebna implementacija ako koristimo ključne riječi
- Dokumentacija: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
- Primjeri:
```java
List<Student> findByFirstNameContaining(String part);
List<Student> findByAgeBetween(Integer lower, Integer higher);
List<Studnet> findByLastNameNotEndingWith(String part);
List<Student> findByFirstNameOrderByAge(String part);
// ...
```

### @Query (JPQL)
- Kad je kompleksniji query i ne možemo ga dobiti kombiniranjem gornjih ključnih riječi
  ili bi ime postalo predugačko
- Koristimo JPQL, a ne SQL. JPQL je baziran na imenima klasa (entiteta) i njihovih varijabli, a ne imenima tablica i stupaca
```java
@Query("select s.emailId from student s where s.age > ?1")
List<String> functionThatFindsEmailsOfStudentsOverTheAgeOfTheFirstArgument(Integer age);
```

### @Query (SQL)
- Možemo pisati i direktno SQL queryje koristeći `@Query` anotaciju
```java
@Query(
  value = "SQL QUERY GOES HERE",
  nativeQuery = true
)
ReturnType functionName(/* ... */);
```
- Ovaj puta query mora koristiti imena tablica i stupaca

### Named parameters
- Umjesto `?1`, `?2`, `...` parametara u JPQL ili SQL queryjima možemo definirati argumente funkcije kao parametre
```java
@Query("SOME QUERY THAT USES PARAMETER :param")
ReturnType functionName(@Param("param") Type argName);
```
- Argument funkcije označimo anotacijom `@Param` i kao argument anotacije damo ime parametra u SQL/JPQL queryju
- U Queryju ga koristimo sa `:namedParam` (koristimo ime definirano u anotaciji !! )
- Ime samog argumenta funkcije `argName` ne mora biti povezano. Query koristi ime definirano u anotacij `@Param`

### @Modifying & @Transactional
- Ako izvršavamo update, insert ili delete query (bilo što što mijenja bazu), koristimo `@Modifying` anotaciju.
- (Ako koristimo samo `.save()` funckiju u `JpaRepository` interfaceu ne trebamo `@Modifying`,
  ali za `@Query("INSERT INTO ..."), nativeQuery=true)` trebamo)

- S `@Transactional` označavamo neku metodu koja poziva više queryja. Tada će se ili svi izvršiti ili niti jedan.
  Na primjer ako imamo 3 queryja u funkciji, dva se izvrše, a treći daje error,
  ako je funkcija označena s `@Transactional` anotacijom neće se izvršiti niti jedan.
  Tako izbjegavamo djelomično izvršavanje promjena baze.

- Možemo sa `@Transactional` označiti i cijelu klasu, tada se anotacija ponaša kao da smo anotirali svaku public funkciju njome

## Relationships & Cascading

### @OneToOne

### @OneToMany

### @ManyToOne

### @ManyToMany

# Module 3: Spring Security
Samo s dodavanjem Spring Security startera:
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
naši endpointi traže login. Defaultni username je `user`, lozinka je generirana i zapisana u terminalu / logovima kao `WARN`.
Lozinka izgleda kao `abdd02bf-2179-4979-b470-1950b4cd4e61`.
Login se može izvršiti i na `/login` endpointu, a logout na `/logout`.

### JWT Authentication