Programowanie zespołowe laboratorium 2 grupa 5. 

# Dokumentacja projetu: **System do zarządzania przychodnią**

## Zespoł projetowy:

| Imię i nazwisko       | E-mail                            | Rola, zakres odpowiedzialności  |
|-----------------------|-----------------------------------|---------------------------------|
| Dominik Machnik       | dominik120801@gmail.com           |
| Kamil Kondziołka      | kk118997@stud.ur.edu.pl           |
| Michał Kula           | mkula737@gmail.com                |
| Patryk Ludwikowski    | patryk.ludwikowski.7@gmail.com    | Lider zespołu                   |
| Zuzanna Heller        | zh117797@stud.ur.edu.pl           |

## Opis systemu

System pozwala na łatwe zarządzanie przychodnią, od rejestracji wizyt przez terminarze lekarzy po zarządzanie zasobami przychodni.

## Cele projektu 

+ Umożliwienie rejestracji (w tym samodzielnej i pośredniej przez recepcje/lekarza) i ewidencji wizyt pacjentów przychodni.
+ Koorydynacja terminarzy dostępności lekarzy.
+ Zarządzanie personelem (np. lekarze, pielęgniarki).
+ System składa się z aplikacji złożonej z kilku modułów oraz bazy danych.

## Zakres projektu 


## Wymagania stawiane aplikacji / systemowi 

+ System powinien mieć kilka modułów:
	+ Logowanie i rejestracja
	+ Komunikaty i powiadomienia
		+ np. o przełożeniu wizyty.
	+ Moduł pacjenta
		+ Przelądanie szczegółów wizyt (nadchodzących i poprzednich)
		+ Dodawanie wizyt (z uwzględnieniem terminarzy lekarzy)
		+ Przekładanie wizyt
		+ Uzupełenianie szczgółów wizyt (notatki, recepta lekarza itd.)
	+ Moduł obsługi personelu
		+ Edytowanie terminarzy lekarzy
	+ Moduł administracji użytkownikami (role)
		+ Dodawanie kont lekarzy, recepcyjnych i pielęgniarek
	+ Moduł raportów
		+ Wypełenienie terminarzy (lekarzy, zasobów, personelu)
		+ Zainteresowanie pacjentów (lekarze, specjalności)
		+ Historia pacjenta (wizyty, badania, recepty, skierowania)
	+ Moduł konfiguracji
+ System powinien umożliwiać generowanie raportów PDF
+ System powinien współpracować z bazą danych

## Panele / zakładki systemu, które będą oferowały potrzebne funkcjonalności 

- Panel administratora 
	- Główne narzędzie administratorów systemu umożliwiające wykonanie wszystkich czynności potrzebnych do zarządzania systemem np. dodawanie, edycja, usuwanie użytkowników, tworzenie i modyfikacja grup, zarządzanie innymi administratorami. 
- Panel innego użytkownika 
	- Funkcjonalność 1
	- ... kolejna funkcjonalność
...
- Zakładka raportów 
	- Generowanie raportów
- Zakładka ustawień 
...

## Typy wymaganych dokumentów w projekcie oraz dostęp do nich 

- Raporty PDF 
	- rodzaje raportów
- Inne dokumenty:
	- ...

## Przepływ informacji w środowisku systemu 

Przepływ dany w systemie jest oparty na interakcji użytkowników z bazą danych, oczywiście pośrednio przez aplikację. 

## Użytkownicy aplikacji i ich uprawnienia 

+ Pacjenci
	+ podgląd powiadomień
	+ zapisują się do lekarzy na wizyty
	+ mogą przeglądać swoje wizyty (w tym poprzednie poprzednie)
	+ mogą przekładać wizyty (lekarze otrzymują powiadomienia)
+ Recepcja
	+ podgląd powiadomień (w celu informowania pacjentów)
	+ pośrednia obsługa pacjentów.
+ Pielęgniarki
	+ masowe konto dla wszystkich pielęgniarek
	+ podgląd zleconych badań
	+ podgląd i uzupełnianie danych pacjenta (wpisy historii pacjenta)
+ Lekarze
	+ podgląd powiadomień
	+ obsługują pacjentów
	+ posiadają terminarz dostępności (zmiany mogą wymagać przełożenia wizyt)
	+ mogą przeglądać wizyty (w tym poprzednie)
	+ mogą przekładać wizyty
+ Administrator
	+ dodawanie specjalnych kont (lekarzy, recepcji, pielęgniarek)
	+ techniczny nadzór i dostęp do wszystkiego

## Interesariusze 

- Interesariusze zewnętrzni 
	- Pacjenci - dostęp bezpośredni (logowanie na własne konto w aplikacji) albo pośredni (zarządzane przez recepcję i/lub lekarzy).
- Interesariusze wewnętrzni 
	- Personel przychodni (recepcja, lekarze, przychodni)

## Założenia

+ Nasz system przekierowuje do (rządowego) systemu e-recept i e-skierowań, i po wypełnieniu nasz system dostaje po chwili informację zwrotną. W ramach uproszczenia nasz system umożliwia ręczne wprowadzenie tych danych.
+ Pacjent ma możliwość łatwego przejścia do Internetowe Konto Pacjenta (przycisk, link do logowania).

## Przykładowe scenariusze

1. Pacjent rejestruje się i zapisuje do wybranego lekarza na wizytę.
	+ Lekarz otrzymuje powiadomienie (konfigurowalne)
2. Lekarz przyjmuje pacjenta na wizytę i (opcjonalnie) ustawia kolejną wizytę.
3. Rejestracja niebezpośrednia: Lekarz/Recepcja rejestruje pacjenta w systemie. 
	+ Pacjent może (ale nie musi korzystać) dostać dane do logowania, które przy pierwszym logowaniu powinien zmienić
4. Pacjent/Lekarz przekłada wizytę.
	+ Nowy termin powinien być w zgodzie z terminarzem lekarza i zasobami (jeśli jakieś mają terminarz)
	+ Powiadomienia dla lekarza/pacjenta.
5. Lekarz konfiguruje swój harmonogram pracy lub dodaje urlop w terminarzu.
	+ Sprawdzanie poprawności względem przyszłych wizyt
6. Lekarz prosi o badania u pielęgniarek

## Diagramy UML
- ###### [Diagram przypadków użycia]
	Wstawić rys. diagramu UML
- ###### [Diagram aktywności]
Wstawić rys. diagramu UML
- ###### [Diagram sekwencji]
Wstawić rys. diagramu UML
- ###### [Diagram klas]
	Wstawić rys. diagramu UML

## Baza danych
###### Diagram ERD

+ Użytkownicy
	+ dane do logowania
	+ dane kontaktowe
	+ ...
	+ Role (pacjent, lekarz, administrator)
+ Lekarze
	+ specjalność
	+ harmonogram (dni i godziny przyjęć)
+ Specjalności
	+ nazwa
	+ standardowy oczekiwany czas wizyty
+ Wizyty
	+ oczekiwany termin
	+ oczekiwana długość (standardowy dla pierwszej wizyty, lub ustawiony przez lekarza)
	+ łączą pacjenta i lekarza
	+ mogą być przełożone (ale powinny być zgodne z terminarzami, w tym niektórych zasobów), lekarz/pacjent otrzymują powiadomienia.
	+ mogą zawierać notatki lekarza
+ Badania
	+ daty utworzenia/modyfikacji
	+ kategorie/tagi
	+ opisowe
+ Recepty
	+ łączą pacjenta i lekarza
	+ daty
	+ tekst (w tym nazwy i dawkowanie, przeciwskazania itd.)
	+ ID z systemu rządowego e-recept
+ Skierowania
	+ łączą pacjenta i lekarza
	+ daty
	+ poradnia/specjalność
	+ tekst: powód/uwagi
	+ informacja zwrotna (po skierowaniu)
	+ ID z systemu rządowego e-skierowań
+ Terminarz
	+ zbiór ciągłych zajętych przedziałów czasowych
		+ od, do
		+ powiązana wizyta LUB specjalna wartość dla urlopu/niedostępności
  	+ system tworzy wstępne przedziały wg. harmonogramu lekarza (dni i godziny przyjęć w ciągu tygodnia).
  	+ lekarz może uzupełnić na przyszłość terminarz (urlopy, sytuacje losowe itd)
+ Powiadomonienia
	+ konto źródłowe (opcjonalne?)
	+ konto docelowe
	+ data
	+ treść
	+ status
	+ typ
	+ opcjonalnie powiazany element (np. wizyta, która jest do przełożenia)

###### Skrypt do utworzenia struktury bazy danych

###### Opis bazy danych

## Wykorzystane technologie 
- Język Java 17
	- JavaFX
	- ...
- Baza danych MySQL
- Inne z opisem

## Pliki instalacyjne wraz z opisem instalacji i konfiguracji wraz pierwszego uruchomienia
