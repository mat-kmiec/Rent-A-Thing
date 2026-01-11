#  Rent-A-Thing: Uniwersalny System Wypożyczalni

> **Projekt zaliczeniowy: Programowanie Zaawansowane**
> Aplikacja webowa (MVC) do zarządzania magazynem i procesem wypożyczania.

---

##  Autorzy (Grupa Projektowa)

1.  **Mateusz Kmieć**
2.  **Michał Jastrzębski**
3.  **Michał Koptewicz**

---

## 1.  Przeznaczenie aplikacji
**Rent-A-Thing** to kompletna aplikacja internetowa służąca do obsługi wypożyczalni wielobranżowych. System pozwala na zarządzanie cyklem życia wypożyczenia – od rezerwacji przedmiotu przez użytkownika, poprzez weryfikację dostępności, aż po zwrot i powiadomienia mailowe.

Projekt został zrealizowany w architekturze (MVC), gdzie logika biznesowa (Backend) jest ściśle zintegrowana z warstwą prezentacji (Frontend w Thymeleaf), co zapewnia szybkość działania i spójność danych.

## 2. Funkcjonalności

## 2.  Funkcjonalności Systemu

Aplikacja została podzielona na logiczne moduły, do których dostęp regulowany jest poprzez system ról (`ROLE_USER`, `ROLE_ADMIN`).

###  Moduł Użytkownika
Interfejs klienta wykonany w technologii **Server-Side Rendering (Thymeleaf)**, zapewniający responsywność i szybkie działanie.

* ** Zaawansowane Bezpieczeństwo (Spring Security):**
    * Rejestracja konta z walidacją siły hasła.
    * Bezpieczne logowanie z wykorzystaniem mechanizmu sesji.
    * Ochrona przed atakami CSRF (Cross-Site Request Forgery) w formularzach HTML.
* **Katalog Przedmiotów:**
    * **Przeglądanie dostępnego asortymentu** z podziałem na kategorie np. Pojazdy, Sprzęt, Elektronika.
    * **Filtrowanie i Sortowanie:** Możliwość wyszukiwania przedmiotów po nazwie, dostępności oraz cenie.
    * **Szczegółowy widok przedmiotu** zawierający opis techniczny, zdjęcie poglądowe oraz aktualny status (Dostępny / Wypożyczony).
* **Proces Wypożyczania:**
    * Interaktywny formularz rezerwacji z wyborem dat (kalendarz).
    * Walidacja Biznesowa: System blokuje próbę wypożyczenia przedmiotu w przeszłości lub z datą zwrotu wcześniejszą niż data rozpoczęcia.
    * Podgląd szacowanego kosztu całkowitego przed zatwierdzeniem transakcji.
* **Panel Klienta:**
    * Wgląd w historię swoich wypożyczeń (aktywne i archiwalne).
    * Możliwość edycji danych osobowych.

### Moduł Administratora (Back-Office)
Dedykowany panel sterowania dostępny tylko dla użytkowników uprzywilejowanych, służący do zarządzania zasobami firmy.

* **Zarządzanie Magazynem (Inventory CRUD):**
    * Dodawanie nowych przedmiotów do bazy danych.
    * Edycja parametrów (zmiana ceny, opisu, statusu technicznego).
    * Wycofywanie przedmiotów z oferty (Soft Delete) – przedmiot znika dla klienta, ale pozostaje w historii bazy danych.
* **Zarządzanie Użytkownikami:**
    * Przegląd listy zarejestrowanych klientów.
    * Możliwość blokowania kont użytkowników naruszających regulamin.
* **Nadzór nad Wypożyczeniami:**
    * Zatwierdzanie zwrotów przedmiotów.
    * Manualna zmiana statusu wypożyczenia (np. w przypadku uszkodzenia sprzętu).

### Powiadomienia i Automatyzacja
System wspiera proces komunikacji z klientem, wykorzystując asynchroniczne mechanizmy Springa.

* **Integracja Email (JavaMailSender):**
    * **Welcome Email:** Automatyczna wiadomość powitalna po rejestracji.
    * **Potwierdzenie Rezerwacji:** E-mail z podsumowaniem zamówienia (przedmiot, daty, koszt) wysyłany natychmiast po zatwierdzeniu w systemie.

---

---

## 3.  Stack Technologiczny



| Obszar | Technologia | Zależność w Maven |
|--------|-------------|-------------------|
| **Język** | Java 21 | `java.version` |
| **Framework** | Spring Boot 4.0.1 | `spring-boot-starter-parent` |
| **Frontend** | Thymeleaf | `spring-boot-starter-thymeleaf` |
| **Security** | Spring Security 6 | `spring-boot-starter-security` |
| **Baza Danych** | MySQL / H2 | `mysql-connector-j` / `h2` |
| **ORM** | Hibernate (JPA) | `spring-boot-starter-data-jpa` |
| **E-mail** | JavaMail | `spring-boot-starter-mail` |
| **Utils** | Lombok | `lombok` |

