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
 - Nema implicitnih konverzija, ne rade mi ni eksplicitne konverzije (iako mi se čini da bi u xml trebalo ić smiosleno ??)
 - Prihvaća bilokakav input
 - Sve prolazi bez errora u raw (logicno) i xml (nelogicno). Error za hl7 (logicno)
 - Ako budem moral radit s JSON-om, raspitat se
 - Mozda bi radilo s fixnim message templatom


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

#### Ponavljanje
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
Po njima možemo iterirati sa `for each(var obxSegment in msg['OBX]){...}`.

Ponavljati se mogu i polja (fields). Kad se ponavlja isto polje (između dva znaka `|`) više puta, međusobno se odvajaju znakom `~`.
Npr. rezultati više mjerenja u istom testu `OBX|1|NM|1234^TestCode||5.6~7.8~9.0|mg/dL||N` imamo
```xml
<OBX>
  <OBX.5>
    <OBX.5.1>
      5.6
    </OBX.5.1>
  </OBX.5>
  <OBX.5>
    <OBX.5.1>
      7.8
    </OBX.5.1>
  </OBX.5>
  <OBX.5>
    <OBX.5.1>
      9.0
    </OBX.5.1>
  </OBX.5>
</OBX>
```

// TODO
- potpolja se reprezentiraju s .1, .2, .3...
- ako polje nema potpolja, svejedno se podatak nalazi na trećoj razini/dubini, tj. na SEG.1.1 (radi neke konvencije??)