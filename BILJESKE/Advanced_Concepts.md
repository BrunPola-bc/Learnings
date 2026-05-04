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

## Konfiguracija Securityja
Uglavnom se klasa nazove `SecurityConfig` ili `SecurityConfiguration` i sprema u `config` paket, ali jedino je bitno da se označi
sa `@Configuration` anotacijom.

Najčešće se koristi i `@EnableWebSecurity` anotacija, ali ona u Spring Boot projektima NIJE OBAVEZNA jer je automatski uključena
kroz `@SpringBootApplication` anotaciju. Kad je Spring Security starter uključen u projekt, Spring Boot-ov auto-configuration
uključuje i SecurityAutoConfiguration pa u nekom dubljem dijelu dođemo i do `@EnableWebSecurity`.
U čistom Springu, ta anotacija JE POTRBENA jer uključuje potrebne konfiguracije.

U `SecurityConfig` klasi/konfiguraciji želimo definirati ***`@Bean`*** `SecurityFilterChain` kojim definiramo točno kako želimo
da se naša aplikacija zaštiti. Defaultni filter chain je:
```
Security filter chain: [
  DisableEncodeUrlFilter
  WebAsyncManagerIntegrationFilter
  SecurityContextHolderFilter
  HeaderWriterFilter
  CsrfFilter
  LogoutFilter
  UsernamePasswordAuthenticationFilter
  DefaultResourcesFilter
  DefaultLoginPageGeneratingFilter
  DefaultLogoutPageGeneratingFilter
  BasicAuthenticationFilter
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  ExceptionTranslationFilter
  AuthorizationFilter
]
```

Ako definiramo najoskudniji / njasiromašniji `SecurityFilterChain` *Bean*, bez ikakvih konfiguracija:
```java
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http.build();
  }
}
```
taj filter chain "overwritea" defaultni (zapravo se defaultni niti ne stvara nego se stvara ovaj) i postaje:
```
Security filter chain: [
  DisableEncodeUrlFilter
  WebAsyncManagerIntegrationFilter
  SecurityContextHolderFilter
  HeaderWriterFilter
  CsrfFilter
  LogoutFilter
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  ExceptionTranslationFilter
]
```

Objekt `http`, instanca klase `HttpSecurity` je zapravo FilterChain **Builder** kojim možemo
konfigurirati postojeće i dodavati nove filtere. Slijede neke od opcija

### CSRF
```java
http.csrf(csrf -> csrf.disable());
```
- Gornja naredba isključuje **CsrfFilter** iz Chaina
- CSRF je kratica za *Cross-Site Request Forgery*. 

- Primjer:
  - You’re logged into your bank at `bank.com`.
  - You visit a malicious site `evil.com`.
  - `evil.com` has a form that submits a `POST` request to `bank.com/transfer?to=attacker&amount=1000`.
  - Your browser includes your session cookie automatically → bank thinks it’s you → money gets transferred.

- CSRF iskorištava činjenicu da browser automatski šalje cookiese ili podatke za autentikaciju sa zahtjevima
- Spring Security rješava problem na način da svaki (state-changing) HTTP zahtjev mora imati i CSRF token 
- JWT autentikacije i API key autentikacije ga najčešće isključuju jer tokeni koje one šalju već obavljaju (između ostalog)
  i funkcionalnost kojoj bi služio CSRF token
- Također, to su STATELESS sesije koje ionako ne čuvaju *authentication credentials* u cookiesima/sesiji već traže token pri svakom zahtjevu

### `authorizeHttpRequests()`
```java
http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
```
- Ova funkcija uključuje **AuthorizationFilter** u Chain.
- Kao agumen prima lambdu koja definira KAKO se autorizira pristup pojedinom endpointu.
- Definiramo:
  - *KOJI ENDPOINT* pomoću
    - `anyRequest()` - svi endpointi
    - `requestMatchers(...patterns)` - endpointi koji odgovaraju navedenim uzorcima
      - kao prvi argument možemo i postaviti određenu HTTP metodu pa tako razlikovati `GET /endpoint` i `POST /endpoint`
  - *TKO SMIJE DO NJEGA* pomoću:
    - `permitAll()` - SVI smiju do njega
    - `denyAll()` - nitko ne smije do njega
    - `authenticated()` - svi autenticirani
    - `hasRole( role )` - svi s određenim role-om
    - `hasAnyRole( ...roles )` - svi s barem jednim od navedenih role-ova
    - `hasAllRoles( ...roles )` - svi sa svim navedenim role-ova
    - `hasAuthority()` i ostali analogoni funkcijama baziranim na role-ovima
      - *Side note*: Role je zapravo authority s prefixom `ROLE_`
      - Znači `hasRole("ADMIN")` `==` `hasAuthority("ROLE_ADMIN")`
    - `not()` - možemo dodati ispred nekog drugog uvjeta pa dozvoliti pristup npr. svima koji NEMAJU role "BANNED_USER"
      - `http.authorizeHttpRequests(auth -> auth.anyRequest().not().hasRole("BANNED_USER");`
    - `...`

- Kompleksniji primjer definiranja autorizacije:
```java
http.authorizeHttpRequests(
    auth -> {
      auth.requestMatchers("/", "/home", "/auth/**").permitAll();
      auth.requestMatchers("/people", "/people/*").hasAnyRole("USER", "EXTENDED_USER", "ADMIN");
      auth.requestMatchers("/**/extended/*", "/**/extended").hasAnyRole("EXTENDED_USER", "ADMIN");
      auth.requestMatchers("/admin/**").hasRole("ADMIN");
      auth.anyRequest().authenticated();
    });
```
- Mogu se i chainati pozivi:
```java
http.authorizeHttpRequests(
    auth -> {
      auth.requestMatchers("/", "/home", "/auth/**").permitAll()
          .requestMatchers("/people", "/people/*").hasAnyRole("USER", "EXTENDED_USER", "ADMIN")
          .requestMatchers("/**/extended/*", "/**/extended").hasAnyRole("EXTENDED_USER", "ADMIN")
          .requestMatchers("/admin/**").hasRole("ADMIN")
          .anyRequest().authenticated();
    });
```
- **Redoslijed je bitan**: primjenjuje se pravilo prvog matchera koji pogodi određeni endpoint,
  znači `anyRequest()` bi trebao biti zadnji (fallbackl)

### `sessionManagement()`
```java
http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
```
- Uključuje **SessionManagementFilter** u Filter Chain

- Određujemo kad se `HttpSession` sprema u `SecurityContext`: 
  - `SessionCreationPolicy.ALWAYS` - uvijek se sprema čak i kad nije potrebno
  - `SessionCreationPolicy.IF_REQUIRED` - sprema se **AKO** neki dio aplikacije ima potrebu za njom
  - `SessionCreationPolicy.NEVER` - nikad se ne sprema, ali može koristiti ako već postoji
  - `SessionCreationPolicy.STATELESS` - ne stvara **NITI** koristi

### `formLogin()`
```java
import static org.springframework.security.config.Customizer.withDefaults;
// ...
http.formLogin(withDefaults());
```
- Omogućuje login preko HTML forme na `/login` endpointu

- Uključuje 4 filtera:
  - **UsernamePasswordAuthenticationFilter**
    - obrađuje `POST /login` request
    - izdvoji *username* i *password*
    - stvori `UsernamePasswordAuthenticationToken`
    - proslijedi ga `AuthenticationManager`-u
    - po potrebi spream SecurityContext i/ili stvara sesiju
  - **DefaultResourcesFilter**
    - Ovdej su spremljeni statični resursi koje sljedeći filteri koriste za generiranje stranica
  - **DefaultLoginPageGeneratingFilter**
    - Generira defaultni login page / vraća defaultni html login pagea
    - Ne uključuje se ako definiramo vlastiti login page umjesto korištenja defaultnog
    - Forma koju generira šalje login podatke koje koristi `UsernamePasswordAuthenticationFilter`
  - **DefaultLogoutPageGeneratingFilter**
    - Analogno gore, generira logout stranicu / formu
    - Ta forma je samo jedan gumb koji triggera `LogoutFilter`
      - on dalje čisti SecurityContext i isključi sesiju

### `httpBasic()`
```java
import static org.springframework.security.config.Customizer.withDefaults;
// ...
http.httpBasic(withDefaults());
```
- Uključuje **BasicAuthenticationFilter** u Chain
- Omogućuje login preko Basic HTTP authentication
- To je login preko headera HTTP requesta, ne preko html forme
- Uz request šaljemo `Authorization` header s vrijednosti `Basic [encoded to base 64 "user:password"]`
- Npr za username = `user` i password = `b148ca72-3ad5-4d59-9df3-ef8d6ea9dcbe`:
  - imamo string: `user:b148ca72-3ad5-4d59-9df3-ef8d6ea9dcbe`
  - encodiran u base 64: `dXNlcjpiMTQ4Y2E3Mi0zYWQ1LTRkNTktOWRmMy1lZjhkNmVhOWRjYmU=`
  - šaljemo: `Authorization: Basic dXNlcjpiMTQ4Y2E3Mi0zYWQ1LTRkNTktOWRmMy1lZjhkNmVhOWRjYmU=`
- BasicAuthenticationFilter:
  - čita header
  - dekodira base64
  - izdvaja username i password
  - stvara `UsernamePasswordAuthenticationToken`
  - proslijeđuje ga `AuthenticationManageru`
  - po potrebi sprema u `SecurityContext`
- Header je potreban sa svakim requestom

### JWT Authentication

***NOTE:* Većina funkcija vezanih uz JWT dolazi iz jednog od ovih startera / dependancyja** (koji nisu ponuđeni u spring initializeru):
```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.13.0</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.13.0</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.13.0</version>
</dependency>
``` 
*Verzija 13 je najnovija u trenu pisanja ovih bilješki.*

- Ovo je jedan od pristupa autentikaciji i autorizaciji korištenjem Spring Security-a
- Uz svaki HTTP zahtjev šaljemo i JWT token u kojem su enkodirani podaci o korisniku: tko je i koja prava ima (roles i/ili authorities)
- Svaki token ima rok valjanosti i u sebi sadrži informacije kad je generiran i kad mu "istječe rok"

- Da bi implementirali JWT autentikaciju moramo implementirati dvije stvari:
  1. Način na koji User može doći do / zatražiti generiranje njegovog jwt tokena
  2. Validaciju tokena, tj. način na koji server može provjeriti ima li korisnik s ovim tokenom pravo pristupa pojedinim dijelovima aplikacije

Prvo ću definirati usera, zatim generiranje tokena i na kraju dohvaćanje tokena.

#### Security User
`User` tablica u bazama se uglavnom već koristi pa sam ja koristio `SecurityUser` Entity koji implementira `UserDetails` interface. U mojoj implementaciji **EMAIL JE USERNAME**
```java
@Entity // <---
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

  @Id @GeneratedValue private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {

    return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
  }

  // Ovdje još idu ostale implementacije svih potrebnih funkcija iz UserDetails interfacea
}
```

Role je jednostavni enum:
```java
public enum Role {
  USER,
  EXTENDED_USER,
  ADMIN
}

```

Analogno imamo i `SecurityUserService` i `SecurityUserRepository`:
```java
@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {

  private final SecurityUserRepository repository;

  // Obavezna funkcija iz UserDetailsService interfacea
  @Override
  public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
    return repository.findByEmail(username).orElseThrow();
  }
}
```
```java
@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

  Optional<SecurityUser> findByEmail(String email);
}
```

#### Generiranje tokena
Sad kad imamo usera, trebamo način kako da se **za njega** generira token.
U `JwtService` / `JwtUtils` definiramo funkciju za generiranje tokena za danog usera.

*Koristimo funkcije i klase iz `jjwt-api`, `jjwt-impl` i `jjwt-jackson` startera.*

```java
@Service
public class JwtService {

  private static final String SECRET_KEY =
      "DA2FC136AC98A6F1BBC601C5C46627DB19727F68D404C7636AB5012B73F8D95A";

  /***** Signing Key Implementation *****/

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /***** Token Generation Implementation *****/

  // From UserDetails + extra claims
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    JwtBuilder builder =
        Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 sata
            .signWith(getSigningKey(), Jwts.SIG.HS256);

    return builder.compact(); // String
  }

  // From UserDetails only (no extra claims)
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }
}
```

Koristimo `Jwts` klasu da dohvatimo `JwtBuilder` i na kraju sam JwtToken (`String`).
Najvažnije je da definiramo `subject` u kojem se u mojoj implementaciji čuva username (email) korisnika
i `expiration` u kojem se zadaje vrijeme od kad na dalje ovaj token više nije važeći (ovdje 24h).
Možemo imati i dodatne informacije (claims), ali definiramo i pass-through funkciju koja prima samo userDetails za slučajeve kad dodatnih claimova nema.

Svaki JWT token mora sadržavati i potpis (`.signWith(...)`). Token se sastoji od headera, payloada i signatura.
Signature je hashirana vrijednost koja se računa koristeći header, payload i secret key koji zna samo server.
Gore je key definiran u kodu, ali realnije je da bude environment varijabla ili čitan iz propertiesa.

Kad server primi token i želi provjeriti je li token legit ili je mijenjan, ponovo izračuna hash (signature) na temelju headera, payloada i secreta.
Ako se primljeni i izračunati signature podudaraju, token je ispravan. (Kasnije se još provjerava je li valjan u smislu roka trajanja)

Pomoćna funkcija `getSigningKey()` samo pretvara string `SECRET_KEY` u instancu `SecretKey` objekta jer funkcija `.signWith()` očekuje takav argument.

#### Dohvaćanje tokena (registracija i autentikacija)

Imamo jednostavni controller s endpointima na kojima se novi user može registrirati i autenticirati.
Registracija sprema usera u bazu, generira NOVI i vraća token ZA TOG USERA.
Autentikacija služi da postojeći user (već spremljen u bazu) dobije novi token nakon što sizgubi stari ili starom istekne rok trajanja.
```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService authService;

  @PostMapping("/register-user")
  public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request, Role.USER));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));
  }
}
```
Koristimo posebne klase `RegisterRequest`, `AuthenticationRequest` i `AuthenticationResponse` da iskoristimo Spring Web auto-mapping između JSON bodyja u HTTP requestu i objekata (`@RequestBody` i `@ResponseBody`).
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String password;
}

/* Lombok anotacije */
public class AuthenticationRequest {
  private String email;
  private String password;
}

/* Lombok anotacije */
public class AuthenticationResponse {
  private String token;
}
```
Za registraciju korisnika trebamo više podataka, a za autentikaciju su dovoljni username (u ovoj implementaciji to je email) i password.

Slijedi servis s funkcijama koje controller poziva:
```java
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final SecurityUserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request, Role role) {
    SecurityUser user =
        SecurityUser.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(Set.of(role))
            .build();
    repository.save(user);

    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    SecurityUser user =
        repository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));

    String jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }
}
```

Gore navedena verzija `register` funkcije stvara novog usera na temelju podataka navedenih u requestu i dodjeljuje mu jedan role.
(Moguće je prepraviti funkciju da omogući više rolova)
Sprema usera u bazu (ne provjerava da li već postoji) i generira token za njega.
U bazu spremamo encodirani password.
Token vraća u `AuthenticationResponse` objektu.

Funkcija `authenitcate()` provjerava odgovara li password username-u.
Ako da, dohvaća usera iz baze i generira novi token ZA NJEGA.
Token vraća u `AuthenticationResponse` objektu.

Da bi ovo sve funkcioniralo, morali smo konfigurirati Beanove sljedećih Spring Security klasa u konfiguraciji:
```java
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  private final SecurityUserService securityUserService;

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(securityUserService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
  }
}
```

Definiramo `PasswordEncoder` bean da konfiguriramo koji algoritam enkodiranja passworda koristimo.
Definiramo `AuthenticationProvider` bean da koinfiguriramo način na koji autenticiramo korisnika.
Već u konstruktoru mu moramo dati `UserDetailsService` (kod nas `SecuirtyUserService`) da bi znao kako dohvatiti usera iz baze
i moramo postaviti password encoder koji koristimo.
Definiramo `AuthenticationManager` bean da bude dostupan u `AuthenticationService` servisu.
Kreiramo ga preko `AuthenticationConfiguration.getAuthenticationManager()` funkcije, a prilikom toga će `AuthenticationConfiguration` prikupiti sve dostupne Providere.

Alterntivno, provider možemo explicitno definirati u konfiguraciji FilterChaina (u `SecurityFilterChain` beanu):
`http.authenticationProvider(authenticationProvider)`.
Ovo je korisno kad imamo više chainova i svakome zadajemo različite providere.

##### Kako autentikacija funkcionira
Iz requesta stvorimo `UsernamePasswordAuthenticationToken` i zovemo `authenticationManager.authenticate()` da autenticira usera.
Manager u listi providera iteriranjem pronađe onoga koji zna kako autenticirati danu vrstu tokena. Kad ga nađe, proslijedi mu token.
`AuthenticationProvider`, konkretno kod nas `DaoAuthenticationProvider`,
dohvati usera, provjeri odgovara li dobiveni password iz tokena encodianom passwordu iz baze.
Ako u bilo kojem trenu dođe do greške, baca se exception 
(`BadCredentialsException` ili `UsernameNotFoundException`...)
i `authenticate()` funkcija (iz našeg servisa) ne dođe do kraj i ne generira token za usera.

#### Validacija tokena
Do sada smo objasnili kako user može dobiti token, a sada ćemo objasniti kako server postupa s tokenom koji dobije.

Moramo napisati vlastiti filter koji će validirati token i taj filter na kraju ubaciti u naš filter chain
(pomoću `SecurityFilterChain` beana u `SecurityConfig` konfiguraciji)

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final SecurityUserService securityUserService;

  @Override
  protected void doFilterInternal(
      /* @Nonnull */ HttpServletRequest request,
      /* @Nonnull */ HttpServletResponse response,
      /* @Nonnull */ FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwtToken;
    final String username;

    // If this clause is true: this isnt a request with a JWT token
    // => we continue to the next filter (because this one is for JWT authentication)
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    jwtToken = authHeader.substring(7); // After the "Bearer " prefix

    username = jwtService.extractUsername(jwtToken);

    // If username was found in the token and the user is not yet authenticated
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      SecurityUser user = securityUserService.loadUserByUsername(username);

      if (jwtService.isTokenValid(jwtToken, user)) {

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    
    filterChain.doFilter(request, response);
  }
}
```
Za JWT pristup securityju želimo `STATELESS` SessionCreationPolicy
i filter koji extenda `OncePerRequestFilter` jer želimo da se JWT token šalje **sa svakim** zahtjevom.

Ovaj filter iz `request`-a vadi *Authorization* header, gdje se mora nalaziti JWT token.
Taj header mora imati prefix "`Bearer `" što je konvencija za sve autentikacije bazirane na tokenima.
Ako taj header ne postoji ili njegova vrijednost NE započinje s "`Bearer `", nastavlja se izvođenje idućeg filtera u chainu.

Kad vrijednost headera ispravno započinje, iz nje izvlačimo JWT token i iz njega username.
(Koristimo pomoćnu frunkciju `extractUsername(token)` iz `jwtService` / `jwtUtils` klase, o njoj niže).

Ako nije pronađen username ili je usera već autenticirao neki od prijašnjih filtera, nastavljamo na idući filter u chainu.

Inače, dohvaćamo usera iz baze i provjeravamo je li token valjan za tog usera (to je njegov token i nije mu prošao rok).
(Za to ponovo koristimo pomoćnu funkciju iz `jwtService` / `jwtUtils`, o njoj malo niže).

Ako je validan, stvaramo `UsernamePasswordAuthenticationToken` u kojeg spremamo autenticiranog usera.
Mogli smo koristiti i neki drugi token/objekt, Spring-u je bitno da ima NEKU implementaciju `Authentication` interfacea.
(`UsernamePasswordAuthenticationToken` extenda `AbstractAuthenticationToken` koji implementira `Authentication`)

Opcionalno, na token dodajemo još informacija sa `setDetails()` funkcijom,
konkretno sa `new WebAuthenticationDetailsSource().buildDetails(request)` iz requesta vadimo metadata-u:
IP adresu pošiljatelja i sessionID (null jer je STATELESS policy).

Na kraju taj token u kojem je pohranjen user i njegova prava (authorities) spremamo u `SecurityContext`
(preko `SecurityContextHolder`-a)

Na kraju filtera moramo pozvati `filterChain.doFilter(request, response);`
da se nastavi izvođenje filter chaina.

#### JwtService / JwtUtils
Osim generiranja tokena i potpisa koji su opisani u potpoglavlju "Generiranje Tokena",
u ovom servisu imamo još i funkcije za validaciju tokena i izvlačenje podataka iz njega.

```java
@Service
public class JwtService {

  /* Signing key */
  /* Token Generating */

  /***** Implementation of extracting claims from the token *****/

  // Get ALL CLAIMS from the token
  public Claims extractAllClaims(String jwtToken) {
    return Jwts.parser() // JwtParserBuilder
        .verifyWith(getSigningKey()) // still an instance of JwtParserBuilder (just signed now)
        .build() // JwtParser
        .parseSignedClaims(jwtToken) // Jws<Claims>
        .getPayload(); // Claims
  }

  // Get a SPECIFIC CLAIM from the token
  public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolverFunction) {
    final Claims claims = extractAllClaims(jwtToken);
    return claimsResolverFunction.apply(claims);
  }

  // Get USERNAME (the subject) from the token
  public String extractUsername(String jwtToken) {
    return extractClaim(jwtToken, Claims::getSubject);
  }

  // Get EXPIRATION TIME from the token
  public Date extractExpiration(String jwtToken) {
    return extractClaim(jwtToken, Claims::getExpiration);
  }

  /***** Token Validation Implementation *****/

  // We check if the token belongs to the user
  // and if the token is still non expired
  public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
    final String username = extractUsername(jwtToken);
    boolean isUsernameValid = username.equals(userDetails.getUsername());

    final Date expirationTime = extractExpiration(jwtToken);
    boolean isTokenNonExpired = expirationTime.after(new Date());

    return isUsernameValid && isTokenNonExpired;
  }
}
```
*Ponovo koristimo funkcije iz jjwt-api, jjwt-impl i jjwt-jackson startera*, konkretno verzija 0.13.0 (najnovija u vrijeme pisanja)

Za dohvaćanje username i ostalih podataka -- `Claim`-ova -- iz JWT tokena, najbitnija je funkcija `extractAllClaims`:
- `Jwts.parser()` - započinje buid parsera
- `verifyWith( getSigningKey() )` - definiramo kako provjervati validnost potpisa kasnije
- `build()` - daje pareser
- `parseSignedClaims( jwtToken )` - ovdje se validira token pomoću potpisa postavljenog u builderu i veaća `Jws<Claims>` objekt sa svim podacima
- `getPayload()` - vraća Claims (extenda `Map<String,Object>`) objekt sa claimovima iz payloada (bez headera i potpisa)

`Claims` klasa ima ugrađene funkcije za dohvaćanje standardnih JWT claimova, npr.:
- `getSubject()`
- `getExpiration()`
- `getIssuedAt()`
- `...`
Za dohvaćanje jednog od claimova koristimo `extractClaim` funkciju koja prima token
i funkciju koju želimo da se na Claimovima iz tokena primjeni.
Tako za username (koji smo spremili u subject) koristimo `extractClaim(jwtToken, Claims::getSubject)`
i analogno `extractClaim(jwtToken, Claims::getExpiration)` za rok trajanja.

Validnost tokena provjeravamo boolean funkcijom `isTokenValid` koja provjerava pripada li token danom useru
i je li rok trajanja (expiration) u budućnosti.

#### Ubacivanje filtera u chain
U našem `SecurityFilterChain` *Bean*u koristimo funkciju `addFilterBefore()` kojom definiramo koji filter
i prije kojeg filtera želimo ubaciti u chain.
Postoje i druge `addFilter*()` funkcije kojima se određuje gdje želimo dodati filter.
Krajnji `SecurityFilterChain` bean izgleda ovako:
```java
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // Filter chain config
    http.csrf(csrf -> csrf.disable());

    http.authorizeHttpRequests(
        auth -> {
          auth.requestMatchers("/", "/home", "/auth/**").permitAll();
          // auth.requestMatchers("/people", "/people/*").hasAnyRole("USER", "EXTENDED_USER", "ADMIN");
          auth.requestMatchers("/**/extended/*", "/**/extended")
              .hasAnyRole("EXTENDED_USER", "ADMIN");
          auth.requestMatchers("/admin/**").hasRole("ADMIN");
          auth.anyRequest().authenticated();
        });

    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authenticationProvider(authenticationProvider);

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
```

### API key authentication

Malo jednostavniji pristup Securityju korištenjem tokena.
Ideja je da sa svakim HTTP requestom šaljemo token u headeru.
Razlika u odnosu na JWT token je ta što nema registracije korisnika,
generiranja tokena koji pripada samo tom korisniku, isteka roka trajanja
i ostalih informacija unutar tokena. Token je ili prisutan u zahtjevu ili nije.
Server može imati evidenciju (bazu) više različitih tokena s različitim autoritetima,
ali ta se informacija ne može isčitati iz samog tokena i tokeni ne pripadaju pojedinom korisniku.

Pristup demonstriran po uzoru na https://www.baeldung.com/spring-boot-api-key-secret

Započinjemo s pisanjem filtera:
```java
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends GenericFilterBean {

  private final ApiKeyAuthenticationService apiKeyAuthenticationService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    try {

      if (SecurityContextHolder.getContext().getAuthentication() != null) {
        filterChain.doFilter(request, response);
        return;
      }

      Authentication auth =
          apiKeyAuthenticationService.getAuthentication((HttpServletRequest) request);

      if (auth != null) {
        SecurityContextHolder.getContext().setAuthentication(auth);
      }

      filterChain.doFilter(request, response);

    } catch (Exception exp) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
      PrintWriter writer = httpResponse.getWriter();
      writer.print(exp.getMessage());
      writer.flush();
      writer.close();
    }
  }
}
```

Prvo provjeravamo je li zahtjev već autenticiran nekim drugim filterom,
ako jest samo prelazimo an izvršavanje idućeg filtera.

Ako nije, pokušavamo ga autenticirati našim `ApiKeyAuthenticationService`-om.

Ako uspješno autenticiramo zahtjev (dobijemo `Authentication` objekt nazad),
spremamo ga u SecurityContext i nastavljamo s idućim filterom u chainu.

Catch blok je pristup obradi exceptiona i generiranju HTTP Response-a.

Slijedi `ApiKeyAuthenticationService`:
```java
@Service
public class ApiKeyAuthenticationService {

  private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
  private static final String AUTH_TOKEN = "Baeldung";

  public Authentication getAuthentication(HttpServletRequest request) {
    String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

    if (apiKey == null) {
      return null;
    }

    if (!apiKey.equals(AUTH_TOKEN)) {
      // return null;
      throw new BadCredentialsException("Invalid API Key");
    }

    return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
  }
}
```

Ovdje iz requesta vadimo header kojem smo sami definirali ime (za razliku od JWT gdje se koristio header `Authentication`).
U headeru se mora nalaziti token. Ako token ili header s ispravnim nazivom ne postoje, vraćamo filteru `null`.
Na taj način omogućavamo nastavljanje izvršavanja filter chaina za slučaju da nudimo više mogućnosti autentikacije.
Ako header i token postoje, ali nisu ispravni, bacamo exception (vjerojatno je da je neko pokušao lažirati token).

Ako je token ispravan, vraćamo instancu `ApiKeyAuthentication` klase, koja extenda `AbstractAuthenticationToken`,
koja implementira interface `Authentication`.

U ovom primjeru hard-codirane su vrijednosti i ključa i imena headera. Naravno bilo bi bolje da su spremljene u environment varijable,
ali osim toga u ovom servisu možemo definirati i više ključeva.
Na primjer, u bazi imamo tablicu ključeva, i na temelju *lookup*-a odredimo koje autoritete možemo dati klijentu
koji je uz svoj zahtjev poslao konkretan ključ (ako se ključ uopće nalazi u bazi).

```java
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

  private final String apiKey;

  public ApiKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.apiKey = apiKey;
    setAuthenticated(true);
  }

  @Override
  public @Nullable Object getCredentials() {
    // No credentials for API Key authentication
    return null;
  }

  @Override
  public @Nullable Object getPrincipal() {
    return apiKey;
  }
}
```
Ovdje se samo sprema API ključ kao string.

Na kraju, taj filter moramo ubaciti u chain koristeći `SecurityFilterChain` *bean*:
```java
@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth -> {
              auth.requestMatchers("/").permitAll();
              auth.anyRequest().authenticated();
            })
        .httpBasic(withDefaults())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // <--

    return http.build();
  }
}
```


### `securityMatcher()`
Moguće je konfigurirati Spring Security da različiti endpointi imaju različite filter chainove.
Npr. možemo definiriati chain za sve endpointe koji započinju s `/api/` imaju api key autentikaciju,
a svi endpointi koji započinju s `/admin/` da imaju jwt autentikaciju (ili bilo koju drugu).

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  @Order(1)
  SecurityFilterChain apiChain(HttpSecurity http) {

    http.securityMatcher("/api/**")
        // Ostale konfiguracije chaina
        .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  
  @Bean
  @Order(2)
  SecurityFilterChain adminChain(HttpSecurity http) {

    http.securityMatcher("/admin/**")
        // Ostale konfiguracije chaina
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
```

Važno je da `SecurityFilterChain` *bean*ovi budu pravilno orderani anotacijom `@Order()` jer će se na neki request primjeniti
SAMO JEDAN chain i to onaj prvi (u smislu `@Order`) koji matcha endpoint requesta.

# Module 4: Microservices Architecture
