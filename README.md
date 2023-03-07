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
		+ Dodawanie wizyt (z uwzględnieniem terminarzy lekarzy)
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
+ Lekarze
	+ podgląd powiadomień
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

- Interesariusze zewnętrzni 
	- Pacjenci - dostęp bezpośredni (logowanie na własne konto w aplikacji) albo pośredni (zarządzane przez recepcję i/lub lekarzy).
- Interesariusze wewnętrzni 
	- Personel szpitala (recepcja, lekarze, dyrekcja)
	- Administrator

## Przykładowe scenariusze

1. Pacjent rejestruje się i zapisuje do wybranego lekarza na wizytę.
	+ Lekarz otrzymuje powiadomienie (konfigurowalne)
2. Lekarz przyjmuje pacjenta na wizytę i (opcjonalnie) ustawia kolejną wizytę.
3. Rejestracja niebezpośrednia: Lekarz/Recepcja rejestruje pacjenta w systemie. 
	+ Pacjent może (ale nie musi korzystać) dostać dane do logowania, które przy pierwszym logowaniu powinien zmienić
4. Pacjent/Lekarz przekłada wizytę.
	+ Nowy termin powinien być w zgodzie z terminarzem lekarza i zasobami (jeśli jakieś mają terminarz)
	+ Powiadomienia dla lekarza/pacjenta.
	+ Jeśli nie ma dogodnego terminu dla zasobów (lub jest odległy, lub pacjent prosi?), lekarz podejmuje manualne decyzje.
5. Lekarz prosi o przydział zasobów szpitala dla danej wizyty.
	+ Wyszukiwanie, 
	+ Kategorie,
6. Dyrekcja anuluje przydział zasobu/zawiesza zasób
	+ Lekarz jest informowany i będzie musiał wybrać inny zasób, anulować lub przełożyć wizytę, albo
	+ Opcjonalnie: Przekazuje inny zasób, lekarz jest proszony o potwierdzenie.
7. Dyrekcja konfiguruje harmonogram pracy (personel) lub (cykliczne?) przerwy techniczne dla zasobu (sale, sprzęt).

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
	+ ...
+ Wizyty
	+ termin, (opcjonalnie) długość
	+ mogą być przełożone (ale powinny być zgodne z terminarzami, w tym niektórych zasobów), lekarz/pacjent otrzymują powiadomienia.
	+ łączą pacjenta, lekarza i opcjonalnie różne zasoby
	+ mogą zawierać notatki lekarza, skierowania, recepty (?)
+ Zasoby szpitala
	+ posiadają nazwę, mają różne typy/kategorie, zdjęcie (?)
	+ mogą mieć terminarz (np. sale, sprzęt, personel)
	+ mogą być zawieszone dla użycia (aktualizacja terminarza), wtedy mogą wygenerować powiadomienia dla lekarza, jeśli są powiązane wizyty.
+ Terminarz
	+ posiada stany: ciągłe przedziały (np. dostępność)
	+ posiada zdarzenia: pojedyncze wizyty (mogą mieć (oczekiwaną) długość)

###### Skrypt do utworzenia struktury bazy danych

###### Opis bazy danych

## Wykorzystane technologie 
- Język Java 17
	- JavaFX
	- ...
- Baza danych MySQL
- Inne z opisem

## Pliki instalacyjne wraz z opisem instalacji i konfiguracji wraz pierwszego uruchomienia
