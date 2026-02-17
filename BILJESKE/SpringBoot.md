# Spring Boot (3.8 ja mislim, ali definitivno 3.x)
Spring Boot is a framework in Java designed to make it easier to create stand-alone, production-ready applications quickly. It builds on top of the Spring Framework but simplifies the setup by providing default configurations, embedded servers (like Tomcat or Jetty), and a set of conventions that reduce boilerplate code.

Spring Boot je Java framework koji olakšava brzo kreiranje samostalnih, produkcijski spremnih aplikacija. Temelji se na Spring Frameworku, ali pojednostavljuje postavljanje aplikacije tako što nudi zadane konfiguracije, ugrađene servere (poput Tomcata ili Jettyja) i skup konvencija koji smanjuju količinu repetitivnog koda.

**Spring Framework** = Dependancy Injection framework. Jako fleksibilno, puno opcija za konfiguriranje i prilagođavanje,
ALI zato je i puno posla. Spring Boot predstavlja jedan standardni / standardiziran način konfiguriranja projekta. To je jedan "često korišteni default" / inicijalno stanje. 

## Spring initializer 
Brzi način za inicijalzaciju i stvaranje kostura Spring Boot projekta.
https://start.spring.io/ ili postoji ekstenzija za SV Code koja radi isto.

Definiramo:
- Maven, Gradle (Kotlin ili Groovy)
- programski jezik
- verzija jave
- spring boot snapshot (verzija)
- Metadata
- **Dependancies** - neke najčešće korištene:
  - DevTools
  - Lombok
  - Spring Web
  - ...
  - Driver za bazu

Na kraju preuzmemo generiranu `.jar` datoteku gdi nam je predefinirano sve gore navedeno

## Learning Path (SQL & Spring Boot) Module 3: Spring Boot Fundamentals
## **Coding Assignment**: *Create a Spring Boot application that exposes a simple REST API endpoint*

### Najjednostavnije definiranje endpointa
U `src/main/com/example/appName` (kod mene `src/main/com/brunpola/cv_management`) dodamo novi controller (java file) - npr. `HomeController.java`.
Glavni file `[nešto]Application.java` ostavimo kak je, a Home Controller može biti:
```java
package com.brunpola.cv_management.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

  @GetMapping(path = "/home")
  public String homeHello() {
    return "Hello from HOME of CV Management Application!";
  }

  @GetMapping(path = "/")
  public String baseHello() {
    return "Hello from CV Management Application!";
  }
}
```

Kad pokrenemo Spring Boot aplikaciju, ako imamo Spring Web dependany i ako ne mijenjamo ništa u konfiguraciji, pokreće se Tomcat server na portu 8080.
Dakle do naše aplikacije možemo doći u bilo kojem browseru na linku `http://localhost:8080`,
odnosno do konkretnih endpointa na `http://localhost:8080/` i `http://localhost:8080/home`.

Controller označavamo s `@RestController` anotacijom -> Definiramo da se bavi HTTP Requestovima (`@Controller`) i da vraća neke podatke (`@ResponseBody`) - uglavnom JSON (default).
Neke anotacije su alias za skup drugih, tako i ova.

Sa `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping` i `@DeleteMapping` (ili `@RequestMapping` + argument) definiramo kako obrađujemo odgovarajući HTTP request (GET, POST, ...), a s `path` argumentom određujemo URi endpointa.

## **Coding Assignment**: *This API should empower users to retrieve a list of all people from the CV management database (built in Week 2)*

### Baza
Baza je jako jednostavna. Ima 3 tablice: People, Skills, Projects - svaka od njih ima ID i *Name stupce (People ima FirstName, LastName, Skills ima SkillName i Project ima ProjectName).
Imamo još 3 join tablice - PersonProjects, PersonSkills i ProjectSkills koje se sastoje samo od ID-eva, tj. povezuju koje vještine ima koja osoba, na kojem projektu radi i koje su vještine potrebne za koji projekt.

Za svaku bazu (MySQL, PostgreSQL, H2, ...) postoji Driver koji uključujemo kao starter / dependancy u Spring Initializeru.

Ako hoćemo da nam Spring Boot kreira i/ili napuni bazu moramo imati `schema.sql` i `data.sql` fileove u `src/main/resources/` direktoriju.
Schema mora imati naredbe za kreiranje baze, data za popunjavanja.
U konfiguraciji (`application.properties` ili `application.yml`) možemo s varijablom `spring.sql.init.mode` definirati kad želimo da se inicijalizira baza.
Npr. `always` za fresh start svaki put kad pokrenemo aplikaciju ili `never` ako je već inicijalizirana.
Milsil da ne postoji opcija "inicijaliziraj ako još nije". Za schemu možemo koristit  `IF NOT EXISTS`, a za data možda `ON CONFICT DO NOTHING`.

### Controllers
Kad imamo više tablica koje možemo promatrat, logično je grupirati endpointe pa tako koristiti više controllera. Npr `PersonController.java`, `ProjectController.java` i `SkillControler.java`.
Iznad definicije klase možemo dodati anotaciju `@RequestMapping(path = "/people")` pa kad definiramo ostale endpointe ne moramo svaki put navoditi taj dio URi-a.
```java
@RestController
@RequestMapping(path = "/people")
public class PersonController {
  // ... (implementacija)
}
```

Sve controllere odvajamo u posebni `controllers` paket (u mapu `src/main/java/com/brunpola/cv_management/controllers`).

### JDBC Template pristup

[[[Od 2022 Postoji moderniji pristup, **JDBC Client** koji nisam previše razradil u ovoj skripti.
Niže ima kratki pregled generiran GPT-om]]]

Jedan od načina za interakciju s bazom. Praktičan ako trebamo **potpunu kontrolu nad pisanjem SQL Queryja**.

Definiramo template u `DatabaseConfig.java` u `config` paketu (`src/main/java/com/brunpola/cv_management/config`):
```java
package com.brunpola.cv_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
```

Mana JDBC Templatea je da trebamo sami konvertirati između java klasa i result seta koji dobijemo nakon izvršavanja queryja.
Za to se koristi **DAO (Data Access Object)**

#### Domain
Sa svaku tablicu stvorimo odgovarajuću klasu u `domain` peketu, npr.:
```java
package com.brunpola.cv_management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person {

    private Long id;
    private String firstName;
    private String lastName;
}
```

#### DAO
Zatim za svaku klasu napravimo i odgovarajući DAO interface sa CRUD funkcijama u `dao` paketu:
```java
package com.brunpola.cv_management.dao;

public interface PersonDao {

  void create(Person person);
  Optional<Person> findOne(long l);
  List<Person> find();
  void update(long id, Person person);
  void delete(long id);
}
```
I implementaciju u `dao/impl` paketu:
```java
package com.brunpola.cv_management.dao.impl;

import com.brunpola.cv_management.dao.PersonDao;
import com.brunpola.cv_management.domain.Person;
import com.brunpola.cv_management.domain.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class PersonDaoImpl implements PersonDao {

  private final JdbcTemplate jdbcTemplate;

  public PersonDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /* create */

  // read one
  @Override
  public Optional<Person> findOne(long personId) {
    List<Person> results = jdbcTemplate.query(
        "SELECT ID, FirstName, LastName FROM People WHERE ID = ? LIMIT 1",
        new PersonRowMapper(), personId);

    return results.stream().findFirst();
  }

  // Row Mapper
  public static class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
      return Person.builder()
              .id(rs.getLong("ID"))
              .firstName(rs.getString("FirstName"))
              .lastName(rs.getString("LastName"))
              .build();
    }
  }

  // read many
  @Override
  public List<Person> find() {
    return jdbcTemplate.query(
          "SELECT ID, FirstName, LastName FROM People",
          new PersonRowMapper()
    );
  }

  /* update */

  /* delete */
}
```

Dakle, u DAO interfaceu definiramo sve funkcionalnosti koje želimo, a u implementaciji ih implementiramo.
Implementacija mora biti označena kao `@Component` (mislim da može i `@Repository`)

#### Korištenje JDBC Templatea
Konkretno mislimo na `org.springframework.jdbc.core.JdbcTemplate` uključen sa Spring Data JDBC starterom.

Glavna prednost JDBC Template je što imamo potpunu kontrolu nad SQL queryjima, ali mana je što za jednostavne queryje nema potrebe za tim.
Također ima puno setupa.
Prvo smo morali definirati JdbcTemplate u `DatabaseConfig.java`. To smo radili da ga Spring Boot framwork može pronaći kao *Bean*
Drugo, svaka DAO implementacija mora imati instancu `JdbcTemplate` objekta kao privatnu varijablu i morali smo ju uvesti kroz konstrukltor:
```java
  private final JdbcTemplate jdbcTemplate;

  public PersonDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
```
Treće, za svaku klasu koju želimo dohvatiti iz baze moramo pisati Row Mapper kao implementaciju `org.springframework.jdbc.core.RowMapper`.

Nakon toga svega, korištenje je jednostavno. Imamo dvije najkorištenije funkcije:
- `jdbcTemplate.query()`
  - Ima nekoliko signatura, ali najčešće su argumenti:
    1. SQL query u stringu ( u stilu prepared statementa, sa '?' )
    2. Row Mapper
    3. Argumenti koji redom zamijenjuju '?' iz SQL-a
  - Vraća Listu objekata dobivenih iz ResultSet-a pomoću RowMapper-a
  - Koristi se za SELECt, kad se ništa ne mijenja u bazi
- `jdbcTemplate.update()`
  - Najčešća signatura:
    1. SQL u stringu (sa '?')
    2. argumentio koji mijenjaju '?'
  - Vraća broj redaka koje je zahvatio update/promjena
  - Koristi se za INSERT INTO, UPDATE i DELETE (kad god se nešto mijenja)
- `.queryForObject()` kad očekujemo samo 1 red nazad
- `.queryForList()` kad dohvaćamo samo jedan stupac
- `.queryForMap()` kad dohvaćamo jedan redak, dobijemo mapu oblika `Map<columnName, value>`
- `.execute()` kad direktno utječemo na izgled baze (CREATE, DROP, ALTER) ili kad želimo više kontrole (lower level JDBC)

Nakon što smo implementirali DAO, te funkcije možemo pozivati u controlleru - iako bi radi lakšeg razvijanja i nadograđivanja aplikacije bilo poželjno imati i service sloj. (Mislim da ću o ovom više pisat ispod u idućem pristupu)

### Spring Data JPA pristup

Ne trebamo `DatabaseConfig.java`, ne trebamo instancu JdbcTemplate-a i uopće ne trebamo DAO interface niti implementacije.
Sve navedeno može se zamijeniti samo repozitorijima koje spremamo u `repositories` paket (`src/main/java/com/brunpola/cv_management/repositories`). Za to moramo uključiti **Spring Data JPA** starter.

#### Repozitoriji
Repozitorij množe izgledati ovako:
```java
package com.brunpola.cv_management.repositories;

import com.brunpola.cv_management.domain.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {}
```

#### Entity i Hibernate
Osim ove deklaracije moramo još prilagoditi naše objekte iz `domain` paketa u **Entity** objekte (prebaciti u `domain/entities` paket).
Entity je klasa koja odgovara jednom retku tablice iz baze podataka:
```java
package com.brunpola.cv_management.domain.entities;

import com.brunpola.cv_management.domain.join.PersonProject;
import com.brunpola.cv_management.domain.join.PersonSkill;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(exclude = {"skills", "projects"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "People")
public class PersonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // This should work for my 'week1database'
  @Column(name = "ID")
  private Long id;

  @Column(name = "FirstName")
  @NotNull
  private String firstName;

  @Column(name = "LastName")
  @NotNull
  private String lastName;
}
```
Ovdje su najvažnije anotacije. Pomoću njih **Hibernate** framework (uključen sa Spring Data JPA starterom) kreira i održava bazu.
Najvažnije je:
- `@Entity` - ovako označavamo klasu kao entity
- `@Table` - ovime definiramo na koju konkretnu tablicu mislimo
- `@Id` - MORAMO označiti primarni ključ / id
Opcionalno:
- `@Column` - ako želimo definirati ime stupca različito od imena varijable. Ovdje možemo i definirati ako želimo NOT NULL restrikciju na tom stupcu sa `nullable = false` argumentom (sigurno i još toga)
- `@GeneratedValue` - za auto-increment
- `@JoinColumn` - stupac koji povezuje entitete...
- `@NotNull` - za validaciju koju provjeravamo anotacijom `@Valid` kasnije (npr. u signaturi CRUD funkcija). ((Ovaj dio zapravo treba biti u DTO - o tome kasnije - a ne u Entity))
- `@Positive` / `@PositiveOrZero` / `@Negative` - razne validacije za brojeve
- `@Email` - validacija da string izgleda kao email
- `...`

Pomoću ovih anotacija će Hibernate stvarati, updateati i provjeravati ispravnost baze.
U `application.properties` postavljamo vrijednost za `spring.jpa.hibernate.ddl-auto` property:
- `none` - ništa
- `validate` - provjeri odgovaraju li Entity-ji strukturi baze, ako ne - baca grešku
- `update` - dodaj nove stupce da se prilagodi entityju, ali ne briši stare
- `create` - svaki put se izbriše i napravi nova baza (briše se tek pri ponovnom pokretanju)
- `create-drop` - isto ko create, ali izbriše čim se ugasi app (ne možemo naknadno pregledavati stanje)

Ja sam koristil `validate` jer sam bazu definiral unaprijed pomoću data.sql i schema.sql (onak kak sam imal od prijašnjih modula edukacije)
iako to nije bilo potrebno jer bi Hibernate sam napravil bazu

**Samo s ovom deklaracijom `@Repository` interfacea koji extenda `JpaRepository<Entity Class, Id type>` i dodavanjem `@Entity` konteksta pomkoću anotacija dobili smo svu funkcionalnost gore implementiranog DAO-a bez korištenja JdbcTemplate-a.**
Razlika je što se funkcije zove `save`, `findById`, `findAll` i `delete`, gdje se `save` koristi i za Create i za Update.

#### Dodatno
Repozitorij se može dodatno proširiti funkcijama kao:
```java
Iterable<PersonEntity> lastNameContains(String part);
```
ili
```java
// Ovo ne vrijedi za moj primjer jer nemamo atribu / stupac / varijablu 'age'
Iterable<PersonEntity> ageGreaterThan(Long breakPoint);
```
Sama deklaracija će biti dovoljna da *Spring Data JPA* sam shvati što se očekuje **BEZ IMPLEMENTACIJE**.
Prepoznaje imena varijabli i ključne riječi kao `Contains`, `GreaterThan`, `count` pa možemo imati i kompleksnije upite kao
`findByLastNameOrderByFirstNameAsc` i kombinirane upite kao `findByLastNameContainsAndIdGreaterThan`.
(( ChatGPT kaže da je obavezno `findBy` prije naziva, ali meni se čini da mi je radilo i bez ))

Ako želimo više kontrole možemo definirati query koristeći HQL (Hibernate Query Language) kao u primjeru:
```java
@Query("SELECT p from PersonEntity p where p.lastName not like concat( '%', ?1, '%') ")
Iterable<PersonEntity> testMethod(String part);
```
ili ekvivalentni SQL:
```java
// Netestirano, ali ideja je jasna
@Query(
    value = "SELECT * FROM People WHERE LastName NOT LIKE CONCAT('%', ?1, '%')",
    nativeQuery = true
)
Iterable<PersonEntity> testMethod(String part);
```

U ovoj situaciji preferira se HQL jer query ide preko Entitya umjesto baze što znači da u slučaju kad prelazimo na drugu bazu (restrukturiranjem projekta)
ne moramo mijenjati kod uopće već će Spring Data JPA i HQL sami pretvoriti Query u SQL u odgovarajućem dijalektu (u gornjem primjeru MySQL).

---

Dalje, Entity ne mora **POTPUNO** reflektirati jedan redak tablice. U mojem primjeru imamo People tablicu sa supcima ID, FirstName, LastName,
slično Skills tablicu sa stupcima ID, SkillName i imamo join tablicu PersonSkills sa PersonID, SkillID stupcima (kompozitni ključ)
koja označava koja osoba ima koju vještinu. Isto vrijedi i za parove PersonProjects i ProjectSkills.

Gore pokazani `PersonEntity` zapravo sadrži još dvije varijable:
```java
@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
@Builder.Default
private Set<PersonSkill> skills = new HashSet<>();

@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
@Builder.Default
private Set<PersonProject> projects = new HashSet<>();
```
Hibernate pomoću `@OneToMany` anotacije prepoznaje o čemu se radi i sprema `PersonSkill` objekte u Set.
Baz puno daljnjeg objašnjavanja, da bi ovo radilo potrebni su nam:
**PersonSkill** "Entity":
```java
/* importovi */

@Entity
@NoArgsConstructor
@Data
@Table(name = "PersonSkills")
public class PersonSkill {

  @EmbeddedId private PersonSkillId id;

  @ManyToOne
  @MapsId("personId")
  @JoinColumn(name = "PersonID")
  private PersonEntity person;

  @ManyToOne
  @MapsId("skillId")
  @JoinColumn(name = "SkillID")
  private SkillEntity skill;

  public PersonSkill(PersonEntity person, SkillEntity skill) {
    this.person = person;
    this.skill = skill;
    this.id = new PersonSkillId(person.getId(), skill.getId());
  }
}
```
I kompozitni ključ:
```java
/* importovi */

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSkillId implements Serializable {

  private Long personId;
  private Long skillId;
}
```

### Slojevi (layers)
#### Persistance
Do sad smo radili na "**Persistance**" sloju (repozitoriji). Njegova zadaća je bila komunicirati s bazom: konstruirati SQL, izvršiti query i po potrebi
mappirati vraćeni *ResultSet* na instance **ENTITIY** klasa
Vidjet ćemo dalje da po potrebi funkcije repozitorija (persistance sloja) mogu primiti neki Entity (npr. kod `save` funkcije)
i mora iz njega konstruirati pravilni query.

Dakle Persistance sloj dobiva ENTITY, konstruira SQL, od baze dobije ResultSet, od njega radi Entity (ili više njih, npr. `findAll`)
i na kraju vraća Entity (ili više njih) ako to funkcija traži.
(Može i boolean ako koristimo `exists` funkcije ili brojeve, mislim da long, ako koristimo `count` funkcije)

#### Service
Persistance sloj dobiva i vraća Entityje **Service** sloju.
Uloga Service sloja je da razdvoji Persistance i Presentation sloj. Točnije da odvoji logiku za komunikaciju s bazom
od logike za komunikacije s Clientom (browser, postman, neki microservice ...).
Da clientu ne otkrivamo previše informacija o strukturi naše baze, umjesto Entityja koristimo **DTO (Data Transfer Object)**.
DTO nema nikakve anotacije koje govore Hibernatu kako se gradi baza, on služi za prijenos podataka između Controllera (Presentation sloj)
i servisa (Service sloj). Ne mora imati sve varijable kao i Entity (koji je služio za izgradnju baze),
a opcionalno može i neka polja spajati (npr. u Entity imamo firstName, middleName i lastName, dok u DTO možda samo fullName).

Uloga srvisa je da kovertira između DTO i Entityja (Od controllera prima DTO, šalje dalje entity u repozitorij.
Repozitorij vraća Entity, kovertira ga u DTO da vrati controlleru).
Također, servis rješava takozvani "business logic", odnosno održava pravila i procedure kojima je definirano kako aplikacija zapravo radi.
Ponekad je to samo *convert and pass through*, ali ako imamo neke uvjete kao npr. "Korisnik mora biti admin [za izvršiti danu funkciju]"
ili "Korisnik mora imati upisani Email / biti stariji od 18" ... takve stvari se osiguravaju u service layeru.

Izgled DTO:
```java
package com.brunpola.cv_management.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {

  private Long id;
  private String firstName;
  private String lastName;

  // Opcionalno može i
  // private List<SkillDto> skills;
  // private List<ProjectDto> projects;
}
```
Bitno je da ima `@NoArgsConstructor` za *Jackson* konverziju JSON <-> DTO (ali više o tome u Presentation odjeljku).

Za mappiranje sam koristio Model Mapper (https://modelmapper.org/) koju uključujemo dodavanjem
```xml
		<dependency>
			<groupId>org.modelmapper</groupId>
			<artifactId>modelmapper</artifactId>
			<version>3.2.4</version>
		</dependency>
```
u `pom.xml` (nema ga ponuđenog u Spring Initializeru).
Slično kao jdbcTemplate, moramo napraviti `MapperConfig.java` klasu u `config` paketu:
```java
package com.brunpola.cv_management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
```
da bi mapper postavili kao *Bean*.

U `mappers` paketu dodajemo interface:
```java
package com.brunpola.cv_management.mappers;

public interface Mapper<ENTITY, DTO> {

  DTO mapTo(ENTITY entity);

  ENTITY mapFrom(DTO dto);
}
```
Ubuduće predlažem intuitivnija imena kao `toDto` i `toEntity`.

I implementacije raznih mappera u `mappers/impl` paket (ovdje je `PersonMapperImpl.java`):
```java
package com.brunpola.cv_management.mappers.impl;

import com.brunpola.cv_management.domain.dto.PersonDto;
import com.brunpola.cv_management.domain.entities.PersonEntity;
import com.brunpola.cv_management.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PersonMapperImpl implements Mapper<PersonEntity, PersonDto> {

  private final ModelMapper modelMapper;

  public PersonMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public PersonDto mapTo(PersonEntity personEntity) {
    return modelMapper.map(personEntity, PersonDto.class);
  }

  @Override
  public PersonEntity mapFrom(PersonDto personDto) {
    return modelMapper.map(personDto, PersonEntity.class);
  }
}
```
Ovdje model mapper radi samo s funkcijom `.map()` jer DTO i Entity imaju identična imena polja i tipove.
Siguran sam da model mapper ima opcije za prilagodbu dok to nije slučaj.
Evo **netestiranog** primjera koji je ChatGPT generiral za slučaj kad Dto ima `int age`, a Entity `long yearsOld`:
```java
public PersonMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;

    // configure mapping for age ↔ yearsOld
    modelMapper.typeMap(PersonEntity.class, PersonDto.class)
        .addMappings(mapper -> mapper.map(
            src -> (int) src.getYearsOld(),  // cast long → int
            PersonDto::setAge
        ));

    modelMapper.typeMap(PersonDto.class, PersonEntity.class)
        .addMappings(mapper -> mapper.map(
            src -> (long) src.getAge(),  // cast int → long
            PersonEntity::setYearsOld
        ));
}
```
U konstruktoru implementacije možemo dograditi mapper s dodatnim pravilima pa i dalje koristiti samo `.map()`.

Evo kako zapravo može izgledati `PersonService` interface (u `service` paketu):
**NOTE: Youtube guide koji sam pratil (iz learning patha, https://www.youtube.com/watch?v=Nv2DERaMx-4) mappira u kontroleru, ali svi drugi izvori vele da se mappiranje radi u servisu.**
Kod koji imam ga radi ko i on, a ovaj koji pišem u ove bilješke **editam ručno bez testiranja**.
```java
package com.brunpola.cv_management.services;

import com.brunpola.cv_management.domain.dto.PersonDto;
import jakarta.validation.Valid;
import java.util.List;

public interface PersonService {

  PersonDto save(PersonDto person);
  PersonDto update(PersonDto person);
  List<PersonDto> findAll();
  PersonDto findOne(Long id);
  boolean isExists(Long id);
  PersonDto partialUpdate(Long id, PersonDto person);
  void delete(Long id);
}
```

A implementacija (za `findOne` i `findAll`):
```java
/* importovi */

@Service
public class PersonServiceImpl implements PersonService {

  private final PersonRepository personRepository;
  private final Mapper<PersonEntity, PersonDto> personMapper;

  public PersonServiceImpl(PersonRepository personRepository, Mapper<PersonEntity, PersonDto> personMapper) {
    this.personRepository = personRepository;
    this.personMapper = personMapper;
  }

  @Override
  public List<PersonDto> findAll() {
    return StreamSupport.stream(personRepository.findAll().spliterator(), false)
        .map(personMapper::mapTo)     // Tu sam dodal mappiranje, bilo je bez ovog retka
        .collect(Collectors.toList());
  }

  @Override
  public PersonDto findOne(Long id) {
    PersonEntity entity = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
    return personMapper.mapTo(entity); // Tu sam dodal mappiranje
  }
}
```
Nije bilo puno mijenjanja (vidi gore **NOTE**) - samo mapiranje i promjena signature - jer u ovim funkcijama nema argumenata.
Npr. u `save` funkciji bi morali prvo convertat u argument DTO u Entity i onda pozvat repository metodu.

#### Presentation
Ovdje se definiraju endpointi i za to nam je potreban *Spring Web* Starter. **Presentation** layer je zadužen za komunikaciju s klijentom
(browser, postman, neki microservice ...).
Klijentu šalje i od klijenta prima objekte u JSON formatu, dakle zadužen je za konvertiranje JSON-a u DTO.
Zahvaljujući **Jacksonu** koji je uključen preko *Spring Web* startera, ova konverzija je vrlo jednostavna.

Primjer `@RestController` endpointa:
```java
@PostMapping(path = "/")
public ResponseEntity<PersonDto> createPerson(@RequestBody PersonDto personDto) {
  PersonDto savedPersonDto = personService.save(personDto);
  return new ResponseEntity<>(savedPersonDto, HttpStatus.CREATED);
}
```
[[O responseEntity-ju malo kasnije]]
Ovdje imamo anotaciju `@RequestBody` koja kaže Springu da uzme body (u JSON formatu) HTTP requesta i kovertira ga pomoću Jacksona
u objekt tipa `PersonDto`. Ovo će uspjeti bez greške samo ako se ključevi json tijela podudaraju s imenama varijabli `PersonDto` objekta.
Dakle za dto:
```java
public class PersonDto {
  private Long id;
  private String firstName;
  private String lastName;
}
```
json mora izgledati ovako:
```json
{
  "id": 26,
  "firstName": "Brun",
  "lastName": "Pola"
}
```
Ako iznad funkcije dodamo `@ResponseBody` anotaciju, isto će se dogoditi i sa vrijednosti koju vraćamo (pretvara DTO u JSON).
Ako stavimo tu anotaciju iznad cijele klase to će vrijediti za svaku funkciju,
a kako je kontroler označen sa `@RestController` ta anotacija već uključuje anotacije `@Controller` i `@ResponseBody`.

Postoje i Jackson anotacije kao:
- `@JsonProperty` - stavimo ju iznad varijable **U DTO KLASI**, a u argumentu naziv json ključa kojem će ta varijabla odgovarati (kad želimo da su te dvije stvari različite)
- `@JsonIgnore` - ponovo **u dto** iznad varijable koju ne želimo imati u jsonu (ignorira se kod konvertanja json->dto ako postoji, ignorira se u konvertanju dto->json čak i ako je postavljea u dto)
- `@JsonFormat` za format datuma
- `...`

Još jedna važna stvar za koju je zadužen Presentation sloj / Controller je određivanje **HTTP statusa**. Jedan od načina je korištenje
`ResponseEntity<TipObjekta>` kao u gornjem primjeru. Koristili smo:
```java
return new ResponseEntity<>(savedPersonDto, HttpStatus.CREATED);
```
što znači da ćemo kao ResponseBody vratiti **samo** `savedPersonDto` (automatski konvertiran u JSON), a odredili smo i HTTP status.
Najbolje bi bilo nekim `if` grananjem ili `try-catch` blokovima zaključiti koji status zapravo želimo
(npr. kad ne nađemo red u tablici s danim ID-om trebamo vratiti `404 NOT FOUND` a kad nađemo `200 OK`).
Jedna od (najboljih / najprihvaćenijih) opcija je pisanje exceptiona kao što je već prikazano u **service sloju**:
```java
PersonEntity entity = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
```
Gdje je definicija exceptiona označena anotacijom `@ResponseStatus(HttpStatus.NOT_FOUND)`. Ovo djeluje čudno jer pozivamo exception iz
service sloja, a on ne bi trebao "imati pojma" o HTTP-u. Ali upravo zato pozivamo exception (drugu klasu). Primjer:
```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PersonNotFoundException extends NotFoundException {
  public PersonNotFoundException(Long id) {
    super("Person with ID " + id + " not found");
  }
}
```
```java
public abstract class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
```

Još jedan od starera koje koriste Controlleri i DTO je **Validation**. Na DTO i varijable u njemu možemo postaviti restrikcije pomoću
anotacija kao:
- `@NotNull`
- `@Size` s arghumentima min i max
- `@Email`
- `@Pattern` s regexp argumentom
a vakidaciju objekta pokrećemo s `@Valid` anotacijom prije njega. Npr. u argumentu funkcije controllera, uz `@RequestBody` možemo
staviti i `@Valid` anotaciju što znači da će se JSON tjelo konvertirati u DTO, a zatim će se pokrenuti validacija.
U slučaju da DTO nije u skladu s pravilima definiranim u definicij klase, dobijemo error i prestane se izvršavati funkcija.
(Mislim da se dobije i HTTP status `400 bad request`).

((Postoji i `@Validated` anotacija koja radi sličnu stvar, ali nisam previše proučaval. Mislim da je razlika da se može definirati
kad se koje pravilo primjenjuje (npr. za `readOne` funkciju id mora biti `@NotNull` dok za create to nije uvjet)))

Za kraj, jedna od anotacija u argumentima controller funkcija je `@PathVariable` koju koristimo kad jedan od agumenata vadimo
direktno iz endpointa. Npr. za `getPerson` funkciju (`findOne`) imamo endpoint `/people/{id}`, a jedan argument funkcije je
`@PathVariable("id") Long id`, što znači "pročitaj 'id' iz endpointa / uri-a" i spremi ga u varijablu `id` (ovo se moglo i drugačije zvati).

Evo implementacije kontrolera koji čita sve ili jednu osobu (**vidi NOTE kod service implementacije**):
```java
@RestController
@RequestMapping(path = "/people")
public class PersonController {

  private final PersonService personService;

  public PersonController( PersonService personService ) {
    this.personService = personService;
  }

  @GetMapping(path = "/")
  public List<PersonDto> listPeople() {
    return personService.findAll();
  }

  @GetMapping(path = "/{id}")
  public PersonDto getPerson(@PathVariable("id") Long id) {
    return personService.findOne(id);
  }
}
```

#### Slojevi - rezime
Controlleri = Presentation layer:
- definira endpointe
- Prihvati JSON + auto-convert u DTO
- Validacija
- Pozovi service
- Postavi HTTP status
- return DTO (auto-convertan u JSON)

Service = Business layer:
- Prima DTO -> konvertira u ENTITY
- Tu se definiraju dodatna pravila u funkcioniranju aplikacije:
  - ponekad nema potrebe pa je samo passthrough do repozitorija
  - ponekad pravila tipa "provjeri je li user admin", "ima li 18"...
  - kombiniranje fieldova i slično
- Zove repozitorij
- konvertira ENTITY u DTO
- return DTO

Repository/DAO = Persistance Layer:
- prima ENTITY
- konstruira SQL
  - ovo je automatski kad se koristi `JpaRepository<EntityType, IdType>` i eventualni HQL
  - explicitno ako koristimo JDBC
- Komunicira s bazom
- Po potrebi konvertira ResultSet u Entity (ili više njih)
- return
  - ENTITY( ili više njih ) kad je SELECT
  - broj zahvaćenih redaka kad je update ili delete
  - ponekad vraća updateani entitiy

#### Put i tip podatka
1. CLIENT:
  - Šalje JSON
2. Controller:
  - JSON -> DTO
3. Service:
  - DTO -> ENTITY
4. Repository:
  - ENTITY -> SQL
5. BAZA
  - (SQL u ResultSet)
6. Repository:
  - ResultSet -> ENTITY
7. Service:
  - ENTITY -> DTO
8. Controller:
  - DTO -> JSON
9. CLIENT:
  - Prima JSON

## Rest Client
Za pozivanje HTTP metoda. Do sad smo radili kak aplikacija/server reagira kad dobije HTTP Request,
a RestClient se koristi da takav request pošaljemo nekoj aplikaciji/serveru (za to sam do sad koristil Postman).
`RestClient` dolazi sa *Spring Web* starterom.

Primjer:
```java
@Component
public class UserRestClient {

  private final RestClient restClient;

  public UserRestClient(RestClient.Builder builder) {
    this.restClient =
        builder
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .defaultHeader("USERS", "Using Rest Client")
            .build();
  }

  public List<User> findAll() {
    return restClient
        .get()
        .uri("/users")
        .retrieve()
        .body(new ParameterizedTypeReference<List<User>>() {});
  }

  public User findById(Integer id) {
    return restClient.get().uri("/users/{id}", id).retrieve().body(User.class);
  }
}
```

Slično kao i s drugim dependancy injectionima, deklariramo `restClient` kao privatnu varijablu i u konstruktoru je inicijaliziramo (?).
Razlika je što ovaj puta koristimo `RestClient.Builder` umjesto da imamo nešto kao
`public UserRestClient(RestClient restClient) { this.restClient = restClient }`
jer moramo definirati barem base URL pa koristimo `builder.baseUrl("https://jsonplaceholder.typicode.com/").build`.

Kad pozivamo RestClient metode (šaljemo HTTP requestove) koristimo:

`restClient`
  - `.get()` ili `.post()` ili `delete()` ili `...` ovisno o tome koji tip HTTP Requesta želimo
  - `.uri("nešto")` - uri koji se nastavlja na baseUrl postavljen u konstruktoru. Može imati path varijable kao `/users/{id}` pa njihove vrijednosti navodimo u nastavku kao argumente
  - `.retrive()` - "dohvati json response", može i `.exchange()` ako želimo više kontrole
  - `.body()` - "U što da pretvorim response?"
    - najčešće `.body(nekiDto.class)`
    - ili `.body(new ParameterizedTypeReference<List<nekiDto>>() {})` ako želimo listu
    - alternativno, umjesto `.body()` može `.toEntity()` = Status Code + Body (isti argumenti kao za body)
    - ili `.toBodilessEntity()` = samo status code

Pozivanje ovih metoda događa se u controlleru, ali je bitno da se ovako definirani `UserRestClient` deklarira i inicijalizira u kontroleru:
```java
@RestController
public class UserController {

  private final UserRestClient userRestClient;

  public UserController( UserRestClient userRestClient) {
    this.userRestClient = userRestClient;
  }
  
  /* Kontroler metode koje pozivaju funkcije na userRestClient */
}
```

## HTTP Client (HTTP Interface)
Slično kao prijelaz s JdbcTemplate (JdbcClienta) na repozitorij koji extenda `JpaRepository` ili `CrudRepository`,
sad ćemo Resti Client zamijeniti HTTP Clientom.

Sve što trebamo je definrati samo signature funckija u HttpClient interfaceu
i URi definirati preko `@GetExchange`, `@PostExchange`, `...` anotacija:
```java
public interface UserHttpClient {

  @GetExchange("/users")
  List<User> findAll();

  @GetExchange("/users/{id}")
  User findById(@PathVariable Integer id);
}
```
Nije potrebno implementirati metode. Jedini boilerplate koji moramo napisati je
deklaracija HttpClienta kao **Bean**.
U ovom primjeru to sam napravio u glavnoj aplikaciji (klasi označenoj sa `@SprtingBootApplication`):
```java
  @Bean
  UserHttpClient userHttpClient() {
    RestClient restClient = RestClient.create("https://jsonplaceholder.typicode.com/");
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(UserHttpClient.class);
  }
```
Ponovo, moramo inicijalizirati instancu u controlleru:
Pozivanje ovih metoda događa se u controlleru, ali je bitno da se ovako definirani `UserRestClient` deklarira i inicijalizira u kontroleru:
```java
@RestController
public class UserController {

  private final UserHttpClient userHttpClient;

  public UserController( UserHttpClient userHttpClient) {
    this.userHttpClient = userHttpClient;
  }
  
  /* Kontroler metode koje pozivaju funkcije na userHttpClient */
}
```

## Http Client u Spring Boot 4
Otkad je uveden Spring Boot 4 (studeni 2025) -- točnije, *Spring Framework 7* -- Http Client kakav je gore opisan je puno pojednostavljen
(i service koji njegove metode poziva)

### SIDE NOTE
Servisi kakve smo do sada razmatrali prije korištenja RestClienta i HttpClienta su komunicirali s repozitorijem
jer nam je to bio način da dođemo do podataka / spremimo podatke - jer smo cijelu bazu imali lokalno.
Ponekad podatke dohvaćamo od drugih servisa pa zato šaljemo HTTP requestove.
Drugi servis / aplikacija opet mora proći kroz cijeli put od controllera, preko servisa, do konačno repozitorija
(u slučaju da je to krajnja aplikacija i da ne šalje novi request negdje dalje).

---

Novi Service interface sad može izgledati ovako:
```java
/* importovi */

@HttpExchange(url = "https://jsonplaceholder.typicode.com", accept = "application/json")
public interface UserService {

  @GetExchange("/users")
  List<User> getAllUsers();

  @GetExchange("/users/{id}")
  User getUserById(@PathVariable Long id);

  @PostExchange("/users")
  User createUser(User user);

  @PutExchange("/users/{id}")
  User updateUser(@PathVariable Long id, User user);

  @DeleteExchange("/users/{id}")
  void deleteUser(@PathVariable Long id);
}
```
Implementacije **NISU POTREBNE**, Spring Boot 4 sve rješava sam.
Ne moramo niti u `*Application.java` definirati *Bean* (iako još uvijek možemo),
nego taj boilerplate zamijenjuje konfiguracija (u `config` paketu):
```java
@Configuration(proxyBeanMethods = false)
@ImportHttpServices(UserService.class)
public class NewHttpClientConfig {}
```
Dakle sama klasa/konfiguracija nema nikakvu implementaciju. Anotacija `@ImportHttpServices(NekiService.class)` zamijenjuje boilerplate.
Sve što moramo je ponovo u controlleru inicijalizirati instancu:
```java
@RestController
public class UserController {

  private final UserService UserService;

  public UserController( UserService userService) {
    this.userService = userService
  }

  /* Kontroler metode koje pozivaju funkcije na userService */
}
``` 

## Quick notes:

### Testiranje (MockMVC)
Skippam za sad jer sam previše vremena uložil u ovu - ajmo reći - skriptu.

### PATCH / Partial update
`Optiona.ofNullable()`

### Nested Objects
U Service sloju smo definirali konfiguraciju za ModelMapper.
Ako želimo da funkcionira i s nested objects moramo postaviti Matching Strategy na LOOSE:
```java
package com.brunpola.cv_management.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE); // OVO SMO DODALI
    return modelMapper;
  }
}
```
### Pagination
Uključimo s tim da nam repozitorija extenda `PagingAndSortingRepository` (već uključeno u `JpaRepository` koji koristimo).
Primjer, bez puno objašnjavanja (**Pogledaj NOTE kod servicea**):
U service-u overloadamo `findAll`:
```java
Page<PersonDto> findAll(Pageable pageable);
```
Onda u implementaciji:
```java
  @Override
  public Page<PersonDto> findAll(Pageable pageable) {
    Page<PersonEntity> people = personRepository.findAll(pageable);
    return people.map(personMapper::mapTo); //Tu je dodano convertanje
  }
```
((Repozitorij samo mora extendat `PagingAndSortingRepository` kak je gore napisano ))

I još funkcija u controlleru mora primati argument `Pageable pageable` i postaviti ga kad poziva service:
```java
  @GetMapping(path = "/")
  public Page<PersonDto> listPeople(Pageable pageable) {
    return personService.findAll(pageable);
  }
```

### Java Record
Radi s validacijskim anotacijama.
Dobar za DTO, ne za Entity (jer Hibernatu trebaju dodatni podaci za kreiranje baze)

### Spring Modulith
Struktura projekta se ne grupira po tipu *Bean*-a (controllers, services, domain/entities ...)
nego po poslovnim funkcionalnim jedinicama: imamo paket `person` i unutar tog paketa imamo controller, service, entity, repository...

### @ResponseStatus()
anotacija se može koristiti i iznad definicije funkcije controllera (umjesto `ResponseEntity<>`)

### JdbcClient
Modernija alternativa za JdbcTemplate. Moj sažetak:
```java
jdbcClient.sql("SQL U STRINGU") // OBAVEZNO, argument je upit koji se izvršava
          [.param()] // Opcionalno, ako imamo parametre (kao prepared statement) u sql-u
          .query(Neka.class) /*ili*/ .update()  // ovisno o upitu, ako je query onda dajemo kao
                                                // argument klasu na koju mappiramo result set
                                                // nema potrebe za RowMapperom
          .list() /*ili*/ .single() /*ili*/ .first() /*ili*/ .optional(); // Self-explanatory
```

Evo pregleda generiranog ChatGPT-om:
#### JDBC Client (Spring Boot 3+) ((ChatGPT))

`JdbcClient` je moderni API za rad s bazom podataka koristeći čisti SQL, bez ORM-a (za razliku od JPA/Hibernate).

Dolazi sa dependencyjem:
```xml
spring-boot-starter-jdbc
```
Spring automatski kreira bean, pa ga samo injectamo:
```java
@Repository
public class PersonDao {

  private final JdbcClient jdbcClient;

  public PersonDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }
}
```
---
#### Osnovni flow
Svaki upit prati ovaj obrazac:
```java
jdbcClient
    .sql("SQL upit")
    .param("ime", vrijednost)   // opcionalno
    .query(...) ili .update();
```
---
#### .sql()
Obavezna metoda. Definira SQL upit koji se izvršava.
```java
.sql("SELECT * FROM People")
```
ili s parametrima:
```java
.sql("SELECT * FROM People WHERE id = :id")
```

##### Alternativa
Ne postoji prava alternativa — `.sql()` je entry point.

---
#### .param()
Koristi se za prosljeđivanje parametara u SQL upit.
```java
.param("id", 5)
```
SQL mora koristiti named parameter:
```sql
WHERE id = :id
```
Može ih biti više:
```java
.sql("SELECT * FROM People WHERE first_name = :first AND last_name = :last")
.param("first", "Ivan")
.param("last", "Horvat")
```
---

#### .query()
Koristi se za SELECT upite (čitanje podataka).

Primjer:
```java
List<PersonEntity> people = jdbcClient
    .sql("SELECT * FROM People")
    .query(PersonEntity.class)
    .list();
```

Spring automatski mapira stupce → fieldove.

Alternativa (custom mapper):

```java
.query((rs, rowNum) -> new PersonEntity(
    rs.getLong("ID"),
    rs.getString("FirstName"),
    rs.getString("LastName")
))
```

---
#### .update()
Koristi se za:
- INSERT
- UPDATE
- DELETE

Primjer INSERT:
```java
int rowsAffected = jdbcClient
    .sql("INSERT INTO People(FirstName, LastName) VALUES (:first, :last)")
    .param("first", "Ivan")
    .param("last", "Horvat")
    .update();
```
Return value:
```java
int
```
broj pogođenih redaka.

---
#### .single()
Koristi se kada očekujemo točno jedan rezultat.
```java
PersonEntity person = jdbcClient
    .sql("SELECT * FROM People WHERE id = :id")
    .param("id", 1)
    .query(PersonEntity.class)
    .single();
```
Ako:
- nema rezultata → exception
- ima više → exception

---
#### .optional()
Sigurnija alternativa za `.single()`:
```java
Optional<PersonEntity> person = jdbcClient
    .sql("SELECT * FROM People WHERE id = :id")
    .param("id", 1)
    .query(PersonEntity.class)
    .optional();
```
Preporučeno kada rezultat možda ne postoji.

---
#### .list()
Koristi se kada očekujemo više rezultata.
```java
List<PersonEntity> people = jdbcClient
    .sql("SELECT * FROM People")
    .query(PersonEntity.class)
    .list();
```
Ako nema rezultata → vraća praznu listu.

---
#### .stream()
Alternativa za velike datasetove:
```java
Stream<PersonEntity> stream = jdbcClient
    .sql("SELECT * FROM People")
    .query(PersonEntity.class)
    .stream();
```
---

#### .first()
Vraća prvi rezultat, ignorira ostale:
```java
PersonEntity person = jdbcClient
    .sql("SELECT * FROM People")
    .query(PersonEntity.class)
    .first();
```
Ako nema rezultata → exception.

Alternativa:
```java
.optional()
```
---

#### Najčešći primjeri
##### SELECT jedan
```java
Optional<PersonEntity> findById(Long id) {
  return jdbcClient
      .sql("SELECT * FROM People WHERE id = :id")
      .param("id", id)
      .query(PersonEntity.class)
      .optional();
}
```
---

##### SELECT svi
```java
List<PersonEntity> findAll() {
  return jdbcClient
      .sql("SELECT * FROM People")
      .query(PersonEntity.class)
      .list();
}
```
---

##### INSERT
```java
int save(PersonEntity person) {
  return jdbcClient
      .sql("INSERT INTO People(FirstName, LastName) VALUES (:first, :last)")
      .param("first", person.getFirstName())
      .param("last", person.getLastName())
      .update();
}
```
---

##### UPDATE
```java
int update(PersonEntity person) {
  return jdbcClient
      .sql("UPDATE People SET FirstName = :first WHERE id = :id")
      .param("first", person.getFirstName())
      .param("id", person.getId())
      .update();
}
```
---

##### DELETE
```java
int delete(Long id) {
  return jdbcClient
      .sql("DELETE FROM People WHERE id = :id")
      .param("id", id)
      .update();
}
```
---

#### Automatski mapping
Radi ako:

SQL stupac:
```sql
FirstName
```

Java field:
```java
private String firstName;
```
Spring automatski konvertira snake_case / PascalCase → camelCase.

---

#### Sažetak
- `.sql()` → definira SQL
- `.param()` → prosljeđuje parametre
- `.query()` → SELECT
- `.update()` → INSERT / UPDATE / DELETE
- `.single()` → točno jedan rezultat
- `.optional()` → Optional rezultat
- `.list()` → lista rezultata
- `.first()` → prvi rezultat
- `.stream()` → stream rezultata
JdbcClient je preporučeni način za JDBC u Spring Boot 3+.


---
---
Tu završava skripta, ali u nastavku su polu-korisni dodaci

---


## Struktura maven projekta:
```md
src
  - main
    - java
      - com.example.appname (npr)
        - config
        - domain
          - entities (klase koje odgovaraju jednoj tablici baze - svaki stupac je atribut / varijabla)
          - dto
          - ja sam imal i join tablice
        - controllers
        - services
        - repositories
        Još ostale opcionalne stvari:
        - mappers
        - exceptions
        - ...
        
    - resources
      - application.properties ili application.yml
      - data.sql    (kad koristimo bazu, popunjuje tablice)
      - schema.sql  (kad koristimo bazu, definira tablice)
  - test (uglavnom po strukturi prati tree mape main)
    - java
      - com.example.appname (npr. ali prati main)
        - tu dolaze konkretni testovi
    - resources
      - resursi koji su tu definirani overrideaju one iz main mape (inače se ti koriste svi tam navedeni)
      - data.sql    (ako hocemo drugaciju bazu za testiranje)
      - schema.sql  (npr ako ne zelimo da testovi utjecu na pravu bazu)
```

## application.properties
https://docs.spring.io/spring-boot/appendix/application-properties/index.html

## Annotations

### Application & Configuration
#### @Bean
Marks a method whose return value should be registered as a Spring-managed bean in the application context.

#### @Configuration
Indicates that a class contains bean definitions and Spring configuration.

#### @SpringBootApplication
Main Spring Boot entry annotation combining configuration, auto-configuration, and component scanning.

---

### Dependency Injection & Stereotypes
#### @Autowired
Automatically injects a dependency (bean) from the Spring application context.

#### @Component
Generic stereotype annotation that marks a class as a Spring-managed component.

#### @Repository
Specialized component annotation for persistence-layer classes; also enables automatic exception translation.

#### @Service
Specialized stereotype indicating a business-logic service class.

---

### Web / REST Controllers
#### @ControllerAdvice
Defines global exception handling and cross-cutting behavior for controllers.

#### @ExceptionHandler
Specifies a method that handles a particular exception type in controllers.

#### @ResponseStatus
Sets the HTTP status returned by a controller method or exception handler.

#### @RestController
Combination of `@Controller` and `@ResponseBody`, used for REST APIs returning JSON/XML responses.

---

### HTTP Mapping (REST Endpoints)
#### @DeleteMapping
Maps HTTP DELETE requests to a controller method.

#### @GetMapping
Maps HTTP GET requests to a controller method.

#### @PatchMapping
Maps HTTP PATCH requests to a controller method.

#### @PostMapping
Maps HTTP POST requests to a controller method.

#### @PutMapping
Maps HTTP PUT requests to a controller method.

---

### Persistence / JPA (Database)
#### @Column
Specifies the column mapping details for an entity field.

#### @EmbeddedId
Defines a composite primary key embedded within an entity.

#### @Entity
Marks a class as a JPA entity mapped to a database table.

#### @GeneratedValue
Specifies how the primary key value is generated.

#### @Id
Identifies the primary key field of an entity.

#### @JoinColumn
Specifies the foreign key column used in an entity relationship.

#### @ManyToOne
Defines a many-to-one relationship between entities.

#### @MapsId
Maps a relationship field to part of a composite primary key.

#### @OneToMany
Defines a one-to-many relationship between entities.

#### @Query
Declares a custom JPQL or SQL query for a repository method.

#### @Table
Specifies the database table associated with an entity.

#### @Transactional
Ensures that a method executes within a database transaction.

---

### Validation
#### @NotNull
Ensures that a field value cannot be null during validation.

#### @Valid
Triggers validation of nested objects during request binding or method calls.

#### @Validated
Enables method-level validation for Spring beans.

---

### Lombok
#### @AllArgsConstructor
Generates a constructor containing parameters for all class fields.

#### @Builder
Implements the builder pattern for easier object creation.

#### @Builder.Default
Defines default field values when using the Lombok builder pattern.

#### @Data
Generates getters, setters, `toString`, `equals`, and `hashCode` methods automatically.

#### @EqualsAndHashCode
Generates `equals()` and `hashCode()` implementations for the class.

#### @Log
Creates a logger instance automatically for the class.

#### @NoArgsConstructor
Generates a no-argument constructor.

---

### Testing
#### @AutoConfigureMockMvc
Automatically configures MockMvc for testing Spring MVC controllers.

#### @DirtiesContext
Marks the Spring test context as dirty so it will be reloaded after the test.

#### @ExtendWith
Registers extensions (such as SpringExtension) for JUnit 5 tests.

#### @SpringBootTest
Loads the full Spring Boot application context for integration tests.

#### @Test
Marks a method as a test case.

---

### Java / Other
#### @Override
Indicates that a method overrides a method from a superclass or interface.
