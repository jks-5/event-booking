# Event-/Terminbuchungssystem

Eine REST API zur Verwaltung von Events und Anmeldungen, entwickelt mit Spring Boot. Veranstalter können Events erstellen und verwalten, Teilnehmer können sich anmelden inklusive Kapazitätsprüfung, automatischer Warteliste und rollenbasierter Berechtigung.

## Inhaltsverzeichnis

- Features
- Tech-Stack
- Rollen & Berechtigungen
- API-Endpunkte
- Setup
- Tests
- API-Dokumentation

## Features

- Event-Verwaltung: Erstellen, Bearbeiten, Löschen und Durchsuchen von Events mit Filter und Paginierungsfunktion
- Buchungssystem mit folgenden Geschäftsregeln:
  - Maximale Teilnehmerzahl pro Event
  - Automatische Warteliste, wenn ein Event voll ist
  - Beim Stornieren einer bestätigten Buchung rückt automatisch der nächste Wartelisten-Platz nach
  - Keine doppelte Anmeldung derselben Person (zusätzlich über einen Datenbank-Constraint abgesichert)
  - Anmeldung ist nach Ablauf der Registrierungsfrist nicht mehr möglich
- Optimistic Locking bei Buchungen, um Race Conditions bei gleichzeitigen Anmeldungen abzufangen
- Authentifizierung & Autorisierung über Spring Security mit JWT
  - Rollenbasiert (Veranstalter, Teilnehmer, Admin)
  - Zusätzliche Besitzer-Prüfung: Ein Veranstalter kann nur eigene Events bearbeiten/löschen
- Zentrale Fehlerbehandlung mit einheitlichem JSON-Fehlerformat
- Interaktive API-Dokumentation über Swagger UI

## Tech-Stack

| Bereich | Technologie |
|---|---|
| Sprache/Framework | Java, Spring Boot |
| Datenbank | PostgreSQL, Spring Data JPA |
| Sicherheit | Spring Security, JWT |
| API-Dokumentation | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito |
| Build-Tool | Maven |

## Rollen & Berechtigungen

| Rolle | Berechtigungen |
|---|---|
| **Teilnehmer** | Events durchsuchen, sich anmelden, eigene Anmeldung stornieren |
| **Veranstalter** | Zusätzlich: eigene Events erstellen/bearbeiten/löschen, Teilnehmerliste eigener Events einsehen |
| **Admin** | Voller Zugriff auf alle Events und Nutzerverwaltung |

## API-Endpunkte

| Methode | Endpunkt | Beschreibung |
|---|---|---|
| GET | `/events` | Events auflisten (mit Filter & Pagination) |
| POST | `/events` | Neues Event erstellen |
| GET | `/events/{id}` | Einzelnes Event abrufen |
| PATCH | `/events/{id}` | Event aktualisieren |
| DELETE | `/events/{id}` | Event löschen |
| GET | `/events/{id}/participants` | Alle Teilnehmer für ein Event auflisten |
| POST | `/events/{id}/booking` | Für ein Event anmelden |
| PATCH | `/events/{id}/booking` | Anmeldung stornieren |
| GET | `/users/me/bookings` | Eigene Buchungen ansehen |
| POST | `/auth/register` | Neuen Nutzer registrieren |
| POST | `/auth/login` | Login, liefert JWT |
| PATCH | `/users/{id}/role` | Rolle von Benutzer ändern |

Die vollständige und aktuelle Übersicht aller Endpunkte inklusive Request/Response-Schemas findest du in der Swagger-Doku (siehe unten).

## Setup

### Option 1

#### Voraussetzungen

- Docker Desktop

#### Umgebungsvariablen setzen

Erstelle eine `.env`-Datei im Root-Verzeichnis des Projekts und trage dort deine Konfiguration ein.

Die Anwendung erwartet folgende Umgebungsvariablen:

```
DB_PASSWORD=postgres-passwort
SECRET_KEY=zufallsstring-für-jwt-signierung
```

#### Anwendung starten

```bash
git clone https://github.com/jks-5/event-booking.git
cd event-booking
docker compose up --build
```

### Option 2

#### Voraussetzungen

- Java
- Maven
- PostgreSQL (lokal laufend, Standardport 5432)

#### Umgebungsvariablen setzen

Windows:

```powershell
$env:DB_PASSWORD="postgres-passwort"
$env:SECRET_KEY="zufallsstring-für-jwt-signierung"
```

Linux/macOS:

```powershell
export DB_PASSWORD="postgres-passwort"
export SECRET_KEY="zufallsstring-für-jwt-signierung"
```

#### Anwendung starten

```bash
git clone https://github.com/jks-5/event-booking.git
cd event-booking
mvn spring-boot:run
```

Die API ist danach unter `http://localhost:8080` erreichbar.

## Tests

Unit-Tests (JUnit 5 + Mockito) für die Service-Schicht ausführen:

```bash
mvn test
```

Aktuell abgedeckt: Buchungslogik (Kapazität, Warteliste, Stornierung), Event-Verwaltung (Besitzer-Prüfung, Terminvalidierung), Authentifizierung und JWT-Erzeugung/Validierung.

## API-Dokumentation

Nach dem Start der Anwendung:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI-Spezifikation (JSON): `http://localhost:8080/v3/api-docs`

Für geschützte Endpunkte: Über `POST /auth/login` ein Token holen, dann oben rechts in der Swagger UI auf **Authorize** klicken und das Token eintragen.
