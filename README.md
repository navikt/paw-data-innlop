# paw-data-innlop

Denne appen leser meldinger fra kafka og dytter data videre til BigQuery.

# Diagram
![image](https://user-images.githubusercontent.com/701351/221847343-2fc0ecbf-cd3a-47aa-9f93-4e4153de1d32.png)

## Hvordan opprette ny tabell / få data inn i BigQuery?

1. Appen din må skrive meldinger til en kafka-topic. Husk å gi `paw-data-innlop` lesetilgang.
2. Du må definere et Avro-skjema for dataene dine. Dette skjemaet definerer tabellen i BigQuery.  
Legg `[skjema].avsc` i `app/src/main/avro`, og kjør `gradle build` for å generere tilhørende kotlin klasser
3. Du må opprette en kafka-topic å skrive til. Meldinger her vil plukkes opp av kafka-connect og dyttes videre til BigQuery. 
Legg til i topic-listen her: `.github/workflows/deploy-topics.yaml`. Navnet på topic vil tilsvare navnet på tabellen i BigQuery.
4. Du må skrive kode som mapper meldingene fra topicen i 1) til avro og publiserer til topicen i 3).
Bruk Kafka Streams og spark det igang i `no/nav/paw/data/innlop/App.kt`
5. That's it. Etter du har deployet skal data flyte inn i BigQuery i sanntid.

# WTF's

### Jeg ser ikke noe feil i loggene, men finner ikke dataene mine i BigQuery?
Du må ha admin-rettigheter for å se dataene. Google cloud console -> hamburger meny -> IAM & Admin -> IAM. Søk opp brukeren din å gi deg selv `BigQuery Admin` rollen.
