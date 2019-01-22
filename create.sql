CREATE TABLE `Aule` (
	`numeroAula`	INTEGER,
	`descrizione`	TEXT,
	`laboratorio`	INTEGER DEFAULT 0,
	`prenotabile`	INTEGER DEFAULT 0,
	PRIMARY KEY(`numeroAula`)
);
CREATE TABLE `Circolari` (
	`id`	INTEGER PRIMARY KEY AUTOINCREMENT,
	`data`	INTEGER,
	`titolo`	TEXT,
	`link`	TEXT UNIQUE,
	`descrizione`	TEXT,
	`allegati`	TEXT
);
CREATE TABLE `GiorniSettimana` (
	`giornoDellaSettimana`	INTEGER CHECK(giornoDellaSettimana Between 1 and 6),
	`nome`	TEXT,
	PRIMARY KEY(`giornoDellaSettimana`)
);
CREATE TABLE `OrarioLezioni` (
	`classe`	INTEGER,
	`sezione`	TEXT,
	`giorno`	INTEGER,
	`nOra`	INTEGER,
	`materia`	TEXT,
	`aula`	INTEGER,
	FOREIGN KEY(`giorno`) REFERENCES `GiorniSettimana`(`giornoDellaSettimana`),
	PRIMARY KEY(`classe`,`giorno`,`nOra`),
	FOREIGN KEY(`aula`) REFERENCES `Aule`(`numeroAula`)
);
CREATE TABLE `Preferenze` (
	`idTelegram`	INTEGER,
	`orarioScolastico`	INTEGER,
	`circolari`	INTEGER,
	`newsRappresentanti`	INTEGER,
	`ultimaCircolareLetta`	INTEGER,
	PRIMARY KEY(`idTelegram`)
);
CREATE TABLE `PrenotazioniAule` (
	`numeroAula`	INTEGER,
	`data`	INTEGER,
	`idTelegram`	INTEGER,
	`oraInizio`	INTEGER,
	`oraFine`	INTEGER
);
CREATE TABLE `Segnalazioni` (
	`data`	INTEGER,
	`idTelegram`	INTEGER,
	`anonimo`	INTEGER,
	`oggetto`	TEXT,
	`contenuto`	TEXT,
	PRIMARY KEY(`idTelegram`,`data`)
);
CREATE TABLE `Utente` (
	`idTelegram`	INTEGER NOT NULL,
	`nome`	TEXT,
	`cognome`	TEXT,
	`classe`	INTEGER,
	`sezione`	TEXT,
	PRIMARY KEY(`idTelegram`)
);
CREATE TABLE `sqlite_sequence` (
	`name`	TEXT,
	`seq`	TEXT
);
CREATE INDEX `UltimeCircolariIndiceSecondario` ON `Circolari` (
	`data`	DESC,
	`id`	DESC
);
CREATE UNIQUE INDEX `prenotazioniAuleIndiceSecondario` ON `PrenotazioniAule` (
	`data`	ASC,
	`oraInizio`	ASC,
	`numeroAula`	ASC
);
