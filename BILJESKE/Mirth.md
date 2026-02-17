## E6
Sve je jako dobro objašnjeno tu:
https://dev.to/shreya/es6-handbook-everything-you-need-to-know-1ea7

((Napravljena i lokalna kopija just in case -- file ES6_handbook.md, trebal bi bit u istom direktoriju ko i ovo))

# Mirth

## Data Types

- U svakom kanalu za source connector i svaki destination connector (i za svaki response) postavljamo inbound i outbound **data type**
- Ulančano je da je source *outbound* == destination *inbound*
- Ponekad postoje **implicitne konverzije**
- U svakom konektoru imamo tri prikaza poruke / podataka:
  - Raw data: ono kakva poruku je primljena ("čisti tekst", string). Mora se moći interpretirati kao inbound type
  - Transformed data: interna reprezentacija (**hl7v2** je interno prikazan ko **xml**) + eventualne promjene nakon transformera
  - Encoded data: poruka kakva se šalje dalje (mora biti outbound type) 

### Raw
- Inbound prihvaća sve (bilo kakav niz znakova)
- Outbound prihvaća sve
- Nema implicitnih konverzija

### XML
- Inbound očekuje nešto što liči na xml
  - npr.: `<dummy>test</dummy>`
- Outbound xml
- Implicitna konverzija **IZ** i **U** hl7v2 jer je hl7v2 interno reprezentiran xml-om

### HL7v2
- Inbound primjer: `dummy|test1|test2^test2|test3~test3|test4^test4~test4||` ili

  ```hl7
  MSH|^~\&|SENDING_APPLICATION|SENDING_FACILITY|RECEIVING_APPLICATION|RECEIVING_FACILITY|20110613083617||ADT^A01|934576120110613083617|P|2.3||||
  EVN|A01|20110613083617|||
  PID|1||135769||MOUSE^MICKEY^||19281118|M|||123 Main St.^^Lake Buena Vista^FL^32830||(407)939-1289^^^theMainMouse@disney.com|||||1719|99999999||||||||||||||||||||
  PV1|1|O|||||^^^^^^^^|^^^^^^^^
  ```

- Interno reprezentiran s:
  ```xml
  <HL7Message>
      <dummy>
          <dummy.1>
              <dummy.1.1>test1</dummy.1.1>
          </dummy.1>
          <dummy.2>
              <dummy.2.1>test2</dummy.2.1>
              <dummy.2.2>test2</dummy.2.2>
          </dummy.2>
          <dummy.3>
              <dummy.3.1>test3</dummy.3.1>
          </dummy.3>
          <dummy.3>
              <dummy.3.1>test3</dummy.3.1>
          </dummy.3>
          <dummy.4>
              <dummy.4.1>test4</dummy.4.1>
              <dummy.4.2>test4</dummy.4.2>
          </dummy.4>
          <dummy.4>
              <dummy.4.1>test4</dummy.4.1>
          </dummy.4>
          <dummy.5/>
          <dummy.6/>
      </dummy>
  </HL7Message>
  ```

- Za implicitnu konverziju u hl7v2, transformed data MORA biti xml (znači inbound xml, hl7 ili pomoću transformera predefiniran `msg` u xml)
- Ako želimo sami slagat hl7 (ko string), outbound type mora bit **RAW**
- Za sve ostale opcije koristiti *Message template* i *Mesage tree* u transformerima

### JSON
 - ???????????????
 - Meni apsolutni misterij
 - Nema implicitnih konverzija, ne rade mi ni eksplicitne konverzije (iako mi se čini da bi u xml trebalo ić smisleno ??)
 - Prihvaća bilokakav input
 - Sve prolazi bez errora u raw (logicno) i xml (nelogicno). Error za hl7 (logicno)
 - Ako budem moral radit s JSON-om, raspitat se
 - Mozda bi radilo s fixnim message templatom

 Dodatna literatura:
 - https://stackoverflow.com/questions/21903544/json-parsing-in-mirth-or-json-in-mirth-or-hl7-to-json-in-mirth
 - https://kkrgr8.wordpress.com/2015/10/27/creating-json-from-hl7-using-mirth/

## Korištenje HL7v2 (i XML-a općenito)
### Primjer poruke:
```hl7
MSH|^~\&|SENDING_APPLICATION|SENDING_FACILITY|RECEIVING_APPLICATION|RECEIVING_FACILITY|20110613083617||ADT^A01|934576120110613083617|P|2.3||||
EVN|A01|20110613083617|||
PID|1||135769||MOUSE^MICKEY^||19281118|M|||123 Main St.^^Lake Buena Vista^FL^32830||(407)939-1289^^^theMainMouse@disney.com|||||1719|99999999||||||||||||||||||||
PV1|1|O|||||^^^^^^^^|^^^^^^^^
```
### Dijelovi poruke (Segments, fields, subfields)

Svaka HL7 poruka se sastoji od segmenata, identificiranim troznakovnom oznakom (MSH, EVN, PID i slično). Svaki segment se piše u svojem redu (tehnički su odvojeni znakom `\r`).

Jedan segment se dijeli na polja (fields) odvojena znakom `|`.

Polja se dijele na potpolja (subfields) i odvajaju se s `^`.

Vrlo rijetko, ali i potpolja se mogu dijeliti na dijelove s `&`.

#### Ponavljanje dijelova
Ponekad se segmenti mogu ponavljati (npr. OBX je segment za rezultate pretraga, a u istom izvješću možemo dobiti više nalaza).
Za ovo nema posebnog separatora, samo segmenti imaju istu oznaku (započinju sa `OBX|...`). U XML interpretaciji se pojavljuju zasebno:
```xml
ostali segmenti ...
<OBX>
  podaci iz prvog obx segmenta
</OBX>
<OBX>
  podaci iz drugog obx segmenta
</OBX>
... ostali segmenti
```
Po njima možemo iterirati sa `for each(var obxSegment in msg['OBX']){...}`.

Ponavljati se mogu i polja (fields). Kad se ponavlja isto polje (između dva znaka `|`) više puta, međusobno se odvajaju znakom `~`.
Npr. rezultati više mjerenja u istom testu `OBX|1|NM|1234^TestCode||5.6~7.8~9.0|mg/dL||N` imamo
```xml
<OBX>
  <OBX.5> <!-- Prvo ponavljanje polja OBX.5 -->
    <OBX.5.1>
      5.6
    </OBX.5.1>
  </OBX.5>
  <OBX.5> <!-- Drugo ponavljanje polja OBX.5 -->
    <OBX.5.1>
      7.8
    </OBX.5.1>
  </OBX.5>
  <OBX.5> <!-- Treće ponavljanje polja OBX.5 -->
    <OBX.5.1>
      9.0
    </OBX.5.1>
  </OBX.5>
</OBX>
```

#### Dohvaćanje podataka
*Napomena za ostatak odjeljka: u uglatim zagradama su stringovi (s navodnicim).*

Ako želimo dohvatiti cijeli segment, koristimo `msg['SEG']`, gdje je SEG oznaka segmenta (npr, MSH, PID, NK1, OBX...)

Ako želimo dohvatiti jedno polje segmenta koristimo nastavak s rednim brojem polja u segmentu, tj. `msg['SEG']['SEG.1']`.
Npr. za polje OBX.5 koristimo `msg['OBX']['OBX.5']`.

**VAŽNO** SAM PODATAK UVIJEK ŽIVI (barem) NA TREĆOJ RAZINI. Čak i kad nema više potpolja, ako dohvatimo polje s `msg['OBX']['OBX.5']` dobit ćemo xml objekt:
```xml
<OBX.5>
  <OBX.5.1>
    neka vrijednost
  </OBX.5.1>
</OBX.5>
```
Ako želimo dohvatiti samu vrijednost, moramo zatražini razinu dublje s dodatkom `'.1'`, npr: `msg['OBX']['OBX.5']['OBX.5.1']`.
OSIM ako je potpolje još razdijeljeno na dijelove (pomoću simbola `&`). Tada se pojedini dijelovi dohvaćaju još jednom razinom dublje.

**TL;DR:** Kad polje nema potpolja odvojena s `^`, podatak u polju smatra se prvim potpoljem, znači dohvaćamo ga s `msg['SEG']['SEG.?']['SEG.?.1']` gdje je `?` redni broj polja.

Polja koja sadrže imena (npr. PID.5 za ime pacijenta, PV1.7, PV1.8, PV1.9 imena doktora) odvajaju ime, prezime, srednje ime (...) u potpolja.
Kad trebamo puno ime, praktično je napisat helper funkciju koja ga izvlači jer moramo raditi nešto kao `msg['PID']['PID.5']['PID.5.1'] + msg['PID']['PID.5']['PID.5.2'] + msg['PID']['PID.5']['PID.5.3']`

Više o korištenju XML-a (E4X-a) u User guide-u, poglavlje Using JavaScript in Mirth Connect (https://www.meditecs.com/wp-content/uploads/mirth-connect-user-guide.pdf, str. 435)

## Testiranje JavaScript koda
Izdvojiti čim više koda u zasebne funkcije (Code Template i Code Template Libraries u Mirthu).
U transformerima / filterima ostaviti logiku vezanu uz built in varijable (`msg`, `tmp`) i slične "neizdvojive" dijelove.
Kodove iz templata napisati/prekopirati u zasebne js datoteke u nekom drugom samostalnom okruženju (npr. **Node.js**) i testirati izvan Mirtha.

Testirati pomoću npr. **Mocha**-e i **Chai**-a.

### Mocha = test runner

- `describe()` - grupira testove (može ići u dubini, tj. slagati podgrupe)
- `it()` - označava jedan test, često se označava sa "should be ..."
- `before()` - definira ponašanje (funkciju) koja se poziva jednom prije svih testova unutar iste grupe (u smislu describe-a)
- `beforeEach()` - definira ponašanje (funkciju) koja se poziva prije SVAKOG od testova unutar grupe (u smislu describe-a)
- `after()` - definira ponašanje nakon svih testova u grupi (npr. zatvaranje konekcija i slično čišćenje resursa)
- `afterEach()` - ponašanje nakon SVAKOG od testova u grupi

Primjer:
```js
describe('Math', () => {
  describe('Addition', () => {
    before( () => {
      // Nešto na početku podgrupe
    });
    beforeEach( () => {
      // Nešto na početku svakog testa
    });

    // after, afterEach...

    it('should be equal', () => {
      // Kod testa
    });
    it('second test', () => {
      // Kod testa
    });
  });
  describe('Second Math subgroup', () => {
    // Testovi u drugoj podgrupi
  });
});
describe('Second Group', () => {
  // Testovi u drugoj grupi
});
```

### Chai = assertion library
Pruži nam *easy-to-read* funkcije za assertanje. Kombinira se s Mochom tako da u svakom testu assertamo što očekujemo.
Tri stila: `expect`, `should`, `assert`. Uglavnom se koristi samo jedan (po preferenciji programera).

Primjer:
```js
it('should be equal', () => {
  var x = 1;
  var y = 2;

  expect(x + y).to.equal(y + x);
  // ili
  assert.equal(x + y, y + x);
  // ili
  (x + y).should.equal(y + x);
});
```

Više na https://www.chaijs.com/

## Povezivanje s bazom
### Database reader i Database writer
Tipovi (source i destination) connectora za čitanje ili pisanje u bazu. 
Eksplicitno zadajemo Diver, URL baze, username i password.
Pišemo direktan SQL ili izvršavamo JavaScript kod u stilu JDBC-a.

U slučaju Readera, možemo postaviti interval kojim definiramo kako često se kanal pokreće (tj. izvršava kontakt s bazom).
Možemo i definirati post-process SQL (npr da updateamo stupac u tablici koji kaže kad je zadnji put redak "čitan").

### JavaScript (iz bilo kojeg dijela kanala)
JDBC se može koristiti u bilo kojoj JS skripti / transformeru / filteru i slično. Isto je problem ako je stvaranje konekcije hard kodirano jer se podaci vide u exportovima.
Ja sam koristil funkcije (code template library):
```js
/**
	Create a new database connection using
	environment variables or mirth.properties variables

	@return database connection
*/
function getConnection() {
    
    var driver = getValue("DATABASE_DRIVER", "database.driver");
    var url    = getValue("DATABASE_URL", "database.url");
    var user   = getValue("DATABASE_USERNAME", "database.username");
    var pass   = getValue("DATABASE_PASSWORD", "database.password");
	
	return DatabaseConnectionFactory.createDatabaseConnection(driver, url, user, pass);
}

/**********************************/
/**
	Fetch variable eather from environment variables or mirth properties
	@param {String} envName	- potential name of environment variable
	@param {String} propName	- potential name of mirth property
	@return {String} value of the variable/property
*/
function getValue(envName, propName) {
   
   var v = java.lang.System.getenv(envName);
   if (v != null && v !== "") {
       return v;
   }

   v = getMirthProperty(propName);
   if (v != null && v !== "") {
       return v;
   }

   logger.warn(
       "Neither environment variable '" + envName +
       "' nor mirth property '" + propName + "' is set."
   );
   return null;
}

/**********************************/
/**
	Get the value of a property from mirth.properties by property name

	@param {String} propName - mirth property name
	@return {String} return mirth property value
*/
function getMirthProperty(propName) {
	var file = new java.io.File("/opt/connect/conf/mirth.properties");
	var fis = new java.io.FileInputStream(file);
	var props = new java.util.Properties();
	props.load(fis);
	fis.close();
	return props.getProperty(propName);
}
```
koje izvlače podatke iz environment varijabli ili mirth.properties datoteke.
Sama konekcija gdi je potrebna se naravno dobije samo sa `getConnection()`.

To je korišteno u Dockeru gdi su vrijednosti postavljene pomoću `environment` ili `secrets` u docker-compose.yml.

### Sigurnosni rizici (moja razmišljanja)
Ne kužim baš zakaj bi se koristili Database reader/writer connectori... Hard-codiramo user i password u kanal.
To se sve može vidjet u exportu kanala, a može se i kopirat iz kanala.
Vjerojatno se ovakav princip koristi na način da se u bazi definira neki user za taj kanal (ili grupu klanala) s limitiranim privilegijama???

Nije mi niti ovaj pristup s environment varijablama ili docker secrets najjasniji.
Bolje je jer nije hard kodirano i ne vidi se direktno u exportu, ali svjedno je moguće čitat varijable i propertiese (i loggat ih).

Pretpostavljam da je to jedna od onih stvari koja mi bude kliknula čim vidim u konkretnom primjeru (projektu).


## REST API
Stavimo source connector type HTTP Listener, definiramo port i Base Context Path da definiramo endpoint (npr: `http://localhost:7000/patients/`).

Destination connector stavimo JavaScript writer i napišemo kod koji se izvrši na tom endpointu (npr. kontakt s bazom, čitanje svih redaka neke tablice, konvertanje svakog u JSON objekt, spremanje u array, i vraćamo (`return`) taj array konvertiran u JSON):
```js
var dbConn;
var result;
var patientsArray = [];
channelMap.put("responseContentType", "application/json");

try {
	dbConn = DatabaseConnectionFactory.createDatabaseConnection("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/MirthTests", 'TestingUser', 'TestingUserPass');
	var params = new Packages.java.util.ArrayList();
	params.add($('dob'));
	params.add($('id'));
	result = dbConn.executeCachedQuery('SELECT firstName, lastName, dateOfBirth FROM Patients WHERE dateOfBirth > ? AND ID != ?', params);
	while (result.next()){
		var patientObject={
			first_name: result.getString('firstName'),
			last_name: result.getString('lastName'),
			dob: result.getString('dateOfBirth')	
		};
		patientsArray.push(patientObject);
	}
	channelMap.put("responseStatusCode", 200);
	return JSON.stringify(patientsArray);
} catch(e) {
	logger.info(e);
	channelMap.put("responseStatusCode", 500);
	return [];
} finally {	
	if (dbConn) {
		dbConn.close();
	}
}
```

U source connectoru je bitno definirati Response Content type u `application/json`,
ali ako stavimo `${responseContentType}` možemo ga definirat dinamički u JS kodu pomoći mappiranih varijabli.
Analogno i s Response Status Code (ovo je još praktičnije ako se dogodi neka greška).

## Deployment (Mirth Docker)

Osim pokretanja servera na vlastitom laptopu i stvaranja/importanja/exportanja kanala lokalno, isprobal sam samo docker.

### Jednostavniji primjer:
```yml
# version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mirth-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpass
      MYSQL_DATABASE: mirthdb
      MYSQL_USER: mirthuser
      MYSQL_PASSWORD: mirthpass
    ports:
      - "3307:3306"   # Host port 3307 avoids conflict with local MySQL
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

  mirth:
    image: nextgenhealthcare/connect:4.5.2
    container_name: mirth-connect
    environment:
      DATABASE_TYPE: mysql
      DATABASE_HOST: mysql
      DATABASE_PORT: 3306
      DATABASE_NAME: mirthdb
      DATABASE_USER: mirthuser
      DATABASE_PASSWORD: mirthpass
    ports:
      - "8081:8080"   # Host 8081 avoids conflict with local Mirth
      - "8444:8443"   # Host 8444 for HTTPS
    volumes:
      - mirth-data:/opt/connect/appdata
      - ./mirth-output:/opt/connect/output
    depends_on:
      - mysql

volumes:
  mysql-data:
  mirth-data:

```
Za očuvanje memorije koristimo `volumes`. Bez njih bi se svi podaci izgubili nakon svake `docker compose down` naredbe (resetirana baza sa svim tablicama i redcima).
Postoje "Bind Mount" volumeni, prek kojih se povezuju local path i docker path. Tak docker može vidjet `init.sql` datoteku s mojeg laptopa ili ja na laptopu mogu vidjet `/opt/connect/output` folder (u kojem kanali spremaju generirane PDF-ove). Često se koristi za logove.
Druga vrsta su "Named" volumeni (docker-managed volumeni) u koje docker interno sprema podatke. Ja ga koristim za očuvanje mirth postavki (mirth-data) i baze (mysql-data). **Baza ukjučuje i mirth kanale, usera i sve relevantno za očuvanje mirth servera!**

Pomoću `ports:` odjeljka definiramo preko kojeg *localhost* porta možemo "gledati u" docker containere. Šablona: `localhost_port:docker_serivice_port`. Unutar dockera, jedan service gleda u drugi preko `sevice_name:port` jer svaki service ima "svoj localhost" koji ne dijeli s drugim servisisma.

Odjeljak `mysql: environment:` definira bazu koja se stvara i usera za tu bazu. U `mirth: environment:` varijable `DATABASE_*` su "common mirth.properties varijable" i definiraju credentials za INTERNU bazu u kojoj Mirth "sprema **sebe**" (kanale, alerte, evente, usere, code template ...).

Ja sam istu koristil i za "bussiness" bazu, tj. podatke koje koristim, pišem, čitam... To nije najbolja praksa, bolje bi bilo da se napravi druga schema i drugi user za to.

#### Koncept nadogradnje (netestirano)
MySQL container, kak je gore definiran, stvori bazu(schemu) i usera, a ostatak inicijalizira koristeći init.sql datoteku. [NETESTIRANO:] U njoj bi mogli definira drugu schemu i usera (za bussiness bazu) i koristit te podatke u samim kanalima. Npr.:
```sql
-- Business / application schema
CREATE DATABASE IF NOT EXISTS appdb;

-- Tables and other initialization

-- Business DB user (NO access to mirthdb)
CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'apppass';

GRANT SELECT, INSERT, UPDATE, DELETE
ON appdb.*
TO 'appuser'@'%';

FLUSH PRIVILEGES;
```

U `mirth: environment:` bi morali definirati više varijabli:
```yml
  mirth:
    environment:
      # === Mirth INTERNAL database ===
      DATABASE_TYPE: mysql
      DATABASE_HOST: mysql
      DATABASE_PORT: 3306
      DATABASE_NAME: mirthdb
      DATABASE_USER: mirthuser
      DATABASE_PASSWORD: mirthpass

      # === Application / business database ===
      APP_DB_HOST: mysql
      APP_DB_PORT: 3306
      APP_DB_NAME: appdb
      APP_DB_USER: appuser
      APP_DB_PASSWORD: apppass
```
`DATABASE_*` varijable su standardne i automatski ih mirth prepoznaje za svoju bazu, a `APP_DB_*` bi morali izvlačit programski za JDBC (npr.: `java.lang.System.getenv('APP_DB_USER')`)

### Kompleksniji primjer (ovaj put s *postgres* bazom)
```yml
# version: "3.1"
services:
  mc:
    image: nextgenhealthcare/connect
    environment:
      - DATABASE=postgres
      - DATABASE_MAX_CONNECTIONS=20
      - VMOPTIONS=-Xmx512m
      ###   Everything commented out was moved to ./secrets/secret.properties
      # - DATABASE_URL=jdbc:postgresql://db:5432/mirthdb
      # - DATABASE_DRIVER=org.postgresql.Driver
      # - DATABASE_USERNAME=mirthdb
      # - DATABASE_PASSWORD=mirthdb
      # - KEYSTORE_STOREPASS=docker_storepass
      # - KEYSTORE_KEYPASS=docker_keypass
    secrets:
      - mirth_properties
    volumes:
      - mirth-appdata:/opt/connect/appdata        # Mirth persistent configs/channels
      - ./mirth-output:/opt/connect/output        # PDF output folder
    ports:
      - 8082:8080/tcp
      - 8445:8443/tcp
    depends_on:
      db:
        condition: service_healthy
  db:
    image: postgres
    environment:
      - POSTGRES_USER_FILE=/run/secrets/pg_user
      - POSTGRES_PASSWORD_FILE=/run/secrets/pg_password
      - POSTGRES_DB_FILE=/run/secrets/pg_database
    secrets:
      - pg_user
      - pg_password
      - pg_database
    volumes:
      - postgres-data:/var/lib/postgresql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - 5434:5432   # map to host if you want to connect from DBeaver
    healthcheck:    # optional
      test: ["CMD-SHELL", "pg_isready -U $(cat /run/secrets/pg_user) -d $(cat /run/secrets/pg_database)"]
      interval: 10s
      retries: 5
      timeout: 5s

  mirth-init:
    image: curlimages/curl:8.5.0
    user: "1000:1000"
    depends_on:
      mc:
        condition: service_started
    volumes:
      - ./mirth-init:/mirth-init:ro
      - mirth-appdata:/opt/connect/appdata
    entrypoint: ["/bin/sh", "/mirth-init/init.sh"]
    secrets:
      - mirth_admin_user
      - mirth_admin_pass

volumes:
  postgres-data:
  mirth-appdata:

secrets:
  mirth_properties:
    file: ./secrets/secret.properties
  pg_user:
    file: ./secrets/pg_user
  pg_password:
    file: ./secrets/pg_password
  pg_database:
    file: ./secrets/pg_database
  mirth_admin_user:
    file: ./secrets/mirth_admin_user
  mirth_admin_pass:
    file: ./secrets/mirth_admin_pass
```
Ovdje **NISU** odvojene sheme i useri za business i internal bazu.

Promjene:
- Korištene tajne (Docker Secrets)
- Healthcheck za provjeru je li baza ispravno pokrenuta prije nego ju mirth koristi
- Service `mirth-init` za postavljanje INICIJALNOG STANJA Mirth servera (nakon **prvog** pokretanja `docker compose up` ili nakon brisanja podataka iz volume-a sa `docker compose down -v`)

#### Docker Secrets
```yml
  mc:
    image: nextgenhealthcare/connect
    secrets:
      - mirth_properties
```
Mirth image podržava dva secreta: `mirth_properties` i `mcserver_vmoptions`.
Ako je ovaj prvi definiran, spaja se u mirth.properties file (u nastavku).
Kasnije se u compose file-u ta tajna povezuje s konkretnom datotekom (s local file pathom):
```yml
secrets:
  mirth_properties:
    file: ./secrets/secret.properties
```
Koji može izgledati npr. ovako:
```properties
# secret.properties
database.username=my_mirth_user
database.password=my_mirth_pass
database.url=jdbc:postgresql://db:5432/my_mirth_db
database.driver=org.postgresql.Driver
keystore.storepass=docker_storepass
keystore.keypass=docker_keypass

```
Slično vrijedi i za postgres image, ali tamo se koriste `POSTGRES_*_FILE` environment varijable:
```yml
  db:
    image: postgres
    environment:
      - POSTGRES_USER_FILE=/run/secrets/pg_user
      - POSTGRES_PASSWORD_FILE=/run/secrets/pg_password
      - POSTGRES_DB_FILE=/run/secrets/pg_database
    secrets:
      - pg_user
      - pg_password
      - pg_database
```
Kasnije se svaka od tih tajni povezuje s datotekom pomoću local file patha (datoteke ovaj put sadrže samo username, password ili ime baze - jedan red, bez ikakvog dodatnog konteksta).

#### Healthcheck
Nisam previše proučaval, ChatGPT mi je generiral.
Svrha je da mirth čeka da se baza normalno pokrene prije nego kaj ikaj proba radit.
- `test:` kaže izvrši naredbu
- `[gdje, koju]` - u CMD_SHELLu
- `pg_isready` je built-in PostgreSQL komanda
- `-U ...` username
- `-d ...` database name (izvači/kopira oboje iz secreta)

Mirth čeka da baza bude spremna:
```yml
  mc:
    depends_on:
      db:
        condition: service_healthy
```

#### Inicijalizacija Mirth servera

```yml
  mirth-init:
    image: curlimages/curl:8.5.0
    user: "1000:1000"
    depends_on:
      mc:
        condition: service_started
    volumes:
      - ./mirth-init:/mirth-init:ro
      - mirth-appdata:/opt/connect/appdata
    entrypoint: ["/bin/sh", "/mirth-init/init.sh"]
    secrets:
      - mirth_admin_user
      - mirth_admin_pass
```

- koristimo `curl` image, a **curl** je command-line HTTP client. Šalje poruke (skupa sa request body-jem na mirthov REST API).
- `user: "1000:1000"` definira da se pokreće se sa UID i GID 1000 (defaultno za mirth) da bi mogo imat pristup mirth-appdata volumeu (tj. /opt/connect/appdata folderu mirth-a)
- `depends_on: mc: condition: service_started` čeka da se mirth server pokrene prije nego počne izvršavat skriptu za inicijalizaciju
- `./mirth-init:/mirth-init:ro` bind mount volume na lokalnu mapu u kojoj se nalazi skripta za inicijalizaciju
- u `mirth-appdata` named volume spremamo marker koji kaže dal je server već inicijaliziran ili ne (to osigurava i provjerava skripta)
- `entrypoint: ["/bin/sh", "/mirth-init/init.sh"]` - mijenja defaultni entrypoint slike. "U */bin/sh* pokreni skriptu */mirth-init/inith.sh*" 
- `secrets:` ima fileove sa usernamom i passwordom

Skripta za inicijalizaciju:
```sh
#!/bin/sh
set -e

# ----- CONFIGURATION -----
MIRTH_URL="https://mc:8443"
INITIALIZATION_MARKER="/opt/connect/appdata/.initialized"
USER=$(cat /run/secrets/mirth_admin_user)
PASS=$(cat /run/secrets/mirth_admin_pass)

# -------------------------

# Check marker file
if [ -f "$INITIALIZATION_MARKER" ]; then
  echo "Server previously initialized. Exiting."
  exit 0
fi

echo "Waiting for Mirth to be ready..."

while true; do
    HTTP_CODE=$(
      curl -s -k \
        -u "$USER:$PASS" \
        -H "Accept: application/xml" \
        -H "X-Requested-With: OpenAPI" \
        -o /dev/null \
        -w "%{http_code}" \
        "$MIRTH_URL/api/server/status" \
      || echo "000"
    )

    [ "$HTTP_CODE" -eq 200 ] && break
    sleep 3
done

echo "Mirth is ready!"

# Restore server configuration
HTTP_RESPONSE=$(
  curl -s -k \
    -u "$USER:$PASS" \
    -H "Content-Type: application/xml" \
    -H "X-Requested-With: OpenAPI" \
    -X PUT \
    --data-binary @/mirth-init/server-config-backup.xml \
    -o /tmp/mirth_response.xml \
    -w "%{http_code}" \
    "$MIRTH_URL/api/server/configuration"
)


if [ "$HTTP_RESPONSE" -ge 200 ] && [ "$HTTP_RESPONSE" -lt 300 ]; then
  echo "Configuration restored successfully (HTTP $HTTP_RESPONSE)"
else
  echo "ERROR restoring configuration (HTTP $HTTP_RESPONSE)"
  cat /tmp/mirth_response.xml
  exit 1
fi

# Create marker file
touch "$INITIALIZATION_MARKER"

echo "Initialization complete."
```
1. Konfiguracije
2. Provjera markera (Ako postoji -> exit, tj. ne radi ništa)
3. Infinite loop dok server ne bude spreman (dok endpoint `/api/server/status` ne vrati status 200 OK)
4. Inicijalizacija servera preko `PUT` `/api/server/configuration` endpointa
5. Stvori marker (bez njega bi se server svaki put "reinicijaliziral", tj. izbrisal bi se sav rad)

Detaljnije o `curl` naredbama:
```sh
    HTTP_CODE=$(
      curl -s -k \
        -u "$USER:$PASS" \
        -H "Accept: application/xml" \
        -H "X-Requested-With: OpenAPI" \
        -o /dev/null \
        -w "%{http_code}" \
        "$MIRTH_URL/api/server/status" \
      || echo "000"
    )
```
- `-s` - silent mode (ne ispisuje progress bar, error message i slično)
- `-k` - ignore certificate validation - **OVO NIJE DOBRO ZA PRODUCTION**, ali nisam previše ulazil u to
- `-u` basic authentication (login)
- `-H "Accept: application/xml"` - očekujemo response body u xml-u
- `-H "X-Requested-With: OpenAPI"` - neka Mirth konvencija, više **UVJET** jer mi ni u *Postmanu* bez ovog nije radilo
- `-o /dev/null` - kam spremiti odgovor (vraćeni response body).
Ako nije specificirano, ide na stdout (terminal). `/dev/null` praktički znači "odmah ga izbriši" (to je folder na linuksu u kojem se ništ ne zadržava)
- `-w "%{http_code}"` - ono kaj se zapravo ispisuje na stdout.
Najčešće terminal, ali u ovom slučaju `HTTP_CODE` shell varijabla.
Baz `-s` i `-o` bilo bi još "svega" u varijabli.
`%{...}` su curl "shortcuti"
- `"$MIRTH_URL/api/server/status"` - točan endpoint na koji šaljemo request
- `|| echo "000"` - nije dio curl-a. Ako se dogodi error kod requesta, u nvarijablu `HTTP_CODE` spremi `000`

Druga naredba:
```sh
HTTP_RESPONSE=$(
  curl -s -k \
    -u "$USER:$PASS" \
    -H "Content-Type: application/xml" \
    -H "X-Requested-With: OpenAPI" \
    -X PUT \
    --data-binary @/mirth-init/server-config-backup.xml \
    -o /tmp/mirth_response.xml \
    -w "%{http_code}" \
    "$MIRTH_URL/api/server/configuration"
)
```
- Većinom isto
- `-o /tmp/mirth_response.xml` spremamo response da ga u slučaju errora ispišemo
- `-X PUT` definira HTTP metodu. U prošlom nismo imali jer je bila `GET` (default)
- `--data-binary` šalje sadržaj AS IS (bez nekog čišćenja od strane curla)
- `@/mirth-init/server-config-backup.xml` - koristimo `@` da velimo "kopiraj sadržaj iz sljedećeg file-a" +  file path. Uglavnom, šalje cijelu konfiguraciju servera iz xml filea.

**File koji šaljemo je dobiven iz Administratora kroz Settings -> Backup Config**

##### Alternativa 1
Direktno iz Mirth Administratora. Puno jednostavnije. Postoje "Import Channel", "Import Channel Group", "Import Code Template", "Restore Config" i slične opcije.
Klikneš gumb i izabereš file (koji si prije exportal) i sve se lijepo posloži. Na ovaj način Import grupe odma importa i sve kanale, i eventualne code template (libraryje), i odnose među kanalima (deploy/start dependacies).

##### Alternativa 2 (netestirano)
Prva verzija inicijalizacije, sa `PUT` `/server/configuration` endpointom , je dobra za automatizaciju INICIJALNOG STANJA koje je prethodno bilo ručno napravljeno da bi mogli dobiti export konfiguracije.
Ne funkcionira za automatizaciju proizvoljnog ubacivanja kanala ili grupe kanala u bilo kojem trenutku rada servera.
Postoji `POST` `​/channelgroups​/_bulkUpdate` endpoint, ali za razliku, ali za razliku od **Alternative 1**, importanje grupe ne importa i sve kanale. Zabilježe se samo podaci o kanalima u grupi, ali ne i sami kanali.
Trebalo bi Uploadati pojedine kanale preko `POST` `/channels` endpointa, onda grupu, onda eventualne code template i code template libraryje (isto mislim da sne bi radilo samo library ko ni grupa kanala).
Al čak ni sa svim tim nije gotovo, jer zavisnosti među kanalima i code template libraryjima nije očuvana/postavljena, a ni deploy/start zavisnosti među kanalima.

Deploy/Start dependancies bi mogli postavit kroz `PUT` `/server/channelDependancies` endpoint. Postavljanje zavisnosti kanala za code template bi se trebalo dogoditi kad uploadmo code template library.

Sigurno se MOŽE napraviti neka skripta koja redom radi jedno po jedno, ali čini se prekompleksno. Ako je moguće koristit Administrator (Alternativa 1), sve ide puno jednostavnije.