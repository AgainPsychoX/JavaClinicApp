Programowanie zespołowe laboratorium 3 grupa 5. 

# Dokumentacja projetu: **System do zarządzania szpitalem**

## Zespoł projetowy:

| Imię i nazwisko       | E-mail                            | Rola, zakres odpowiedzialności  |
|-----------------------|-----------------------------------|---------------------------------|
| Dominik Machnik       | dominik120801@gmail.com           |
| Kamil Kondziołka      | kk118997@stud.ur.edu.pl           |
| Michał Kula           | mkula737@gmail.com                |
| Patryk Ludwikowski    | patryk.ludwikowski.7@gmail.com    | Lider zespołu                   |
| Zuzanna Heller        | zh117797@stud.ur.edu.pl           |

## Opis systemu

System pozwala na łatwe zarządzanie szpitalem, od rejestracji wizyt przez terminarze lekarzy po zarządzanie zasobami szpitala.

## Cele projektu 

+ Umożliwienie rejestracji (w tym samodzielnej i pośredniej przez recepcje/lekarza) i ewidencji wizyt pacjentów szpitala.
+ Zarządzanie zasobami szpitala: personelem (np. lekarze, pielęgniarki) i materialne (np. sprzęt, sale).
+ Koorydynacja terminarzy dostępności lekarzy, personelu i niektórych zasobów materialnych.
+ System składa się z aplikacji złożonej z kilku modułów oraz bazy danych.

## Zakres projektu 


## Wymagania stawiane aplikacji / systemowi 

+ System powinien mieć kilka modułów:
	+ Logowanie i rejestracja
	+ Komunikaty i powiadomienia
		+ np. o przełożeniu wizyty.
	+ Moduł pacjenta
		+ Przelądanie szczegółów wizyt (nadchodzących i poprzednich)
		+ Dodawanie wizyt
		+ Przekładanie wizyt
		+ Uzupełenianie szczgółów wizyt (notatki, recepta lekarza itd.)
	+ Moduł obsługi zasobów i personelu
		+ Powiązywanie zasobów do wizyt (rezerwowanie; przez lekarzy).
		+ Edytowanie terminarzy (lekarzy, zasobów materialnych (sale, sprzęt); w tym konfigurowanie przerw technicznych).
	+ Moduł administracji użytkownikami (role)
		+ Dodawanie kont lekarzy i recepcyjnych przez dyrektorów szpitali.
		+ Dodawanie kont dyrektorów szpitali przez administratora.
	+ Moduł raportów
		+ Wypełenienie terminarzy (lekarzy, zasobów, personelu)
		+ Zainteresowanie pacjentów (lekarze, specjalności, potrzeby, użycie zasobów)
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
Np. Scentralizowany oparty na bazie danych.

## Użytkownicy aplikacji i ich uprawnienia 

+ Pacjenci
	+ zapisują się do lekarzy na wizyty
	+ mogą przeglądać swoje wizyty (w tym poprzednie poprzednie)
	+ mogą przekładać wizyty
+ Recepcja
	+ pośrednia obsługa pacjentów.
+ Lekarze
	+ obsługują pacjentów
	+ posiadają terminarz dostępności (zmiany mogą wymagać przełożenia wizyt)
	+ mogą przeglądać wizyty (w tym poprzednie)
	+ mogą przekładać wizyty
	+ rezerwują zasoby (sale, sprzęt medyczny, leki?, pielęgniarki)
+ Dyrekcja szpitala
	+ zarządzają szpitalem i lekarzami
	+ udostępniają zasoby lekarzom
+ Administrator
	+ techniczny nadzór i dostęp do wszystkiego

## Interesariusze 

- Interesariusze wewnętrzni 
	- ...
- Interesariusze zewnętrzni 
	- ...

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

###### Skrypt do utworzenia struktury bazy danych

###### Opis bazy danych

## Wykorzystane technologie 
- Język Java 17
	- JavaFX
	- ...
- Baza danych MySQL
- Inne z opisem

## Pliki instalacyjne wraz z opisem instalacji i konfiguracji wraz pierwszego uruchomienia
