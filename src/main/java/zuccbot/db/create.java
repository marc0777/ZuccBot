package zuccbot.db;

public class create {
    static final String[] sql = {
            "CREATE TABLE Aule (\n" +
                    "\tnumeroAula\tINTEGER,\n" +
                    "\tdescrizione\tTEXT,\n" +
                    "\tlaboratorio\tINTEGER DEFAULT 0,\n" +
                    "\tprenotabile\tINTEGER DEFAULT 0,\n" +
                    "\tPRIMARY KEY(numeroAula)\n" +
                    ");",
            "CREATE TABLE Circolari (\n" +
                    "\tid\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\tdata\tINTEGER,\n" +
                    "\ttitolo\tTEXT,\n" +
                    "\tlink\tTEXT UNIQUE,\n" +
                    "\tdescrizione\tTEXT,\n" +
                    "\tallegati\tTEXT\n" +
                    ");",
            "CREATE TABLE GiorniSettimana (\n" +
                    "\tgiornoDellaSettimana\tINTEGER CHECK(giornoDellaSettimana Between 1 and 6),\n" +
                    "\tnome\tTEXT,\n" +
                    "\tPRIMARY KEY(giornoDellaSettimana)\n" +
                    ");",
            "CREATE TABLE OrarioLezioni (\n" +
                    "\tclasse\tINTEGER,\n" +
                    "\tsezione\tTEXT,\n" +
                    "\tgiorno\tINTEGER,\n" +
                    "\tnOra\tINTEGER,\n" +
                    "\tmateria\tTEXT,\n" +
                    "\taula\tINTEGER,\n" +
                    "\tFOREIGN KEY(giorno) REFERENCES GiorniSettimana(giornoDellaSettimana),\n" +
                    "\tPRIMARY KEY(classe,giorno,nOra),\n" +
                    "\tFOREIGN KEY(aula) REFERENCES Aule(numeroAula)\n" +
                    ");",
            "CREATE TABLE Preferenze (\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\torarioScolastico\tINTEGER,\n" +
                    "\tcircolari\tINTEGER,\n" +
                    "\tnewsRappresentanti\tINTEGER,\n" +
                    "\tultimaCircolareLetta\tINTEGER,\n" +
                    "\tPRIMARY KEY(idTelegram)\n" +
                    ");",
            "CREATE TABLE PrenotazioniAule (\n" +
                    "\tnumeroAula\tINTEGER,\n" +
                    "\tdata\tINTEGER,\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\toraInizio\tINTEGER,\n" +
                    "\toraFine\tINTEGER\n" +
                    ");",
            "CREATE TABLE Segnalazioni (\n" +
                    "\tdata\tINTEGER,\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\tanonimo\tINTEGER,\n" +
                    "\toggetto\tTEXT,\n" +
                    "\tcontenuto\tTEXT,\n" +
                    "\tPRIMARY KEY(idTelegram,data)\n" +
                    ");",
            "CREATE TABLE Utente (\n" +
                    "\tidTelegram\tINTEGER NOT NULL,\n" +
                    "\tnome\tTEXT,\n" +
                    "\tcognome\tTEXT,\n" +
                    "\tclasse\tINTEGER,\n" +
                    "\tsezione\tTEXT,\n" +
                    "\tPRIMARY KEY(idTelegram)\n" +
                    ");",
            "CREATE TABLE sqlite_sequence (\n" +
                    "\tname\tTEXT,\n" +
                    "\tseq\tTEXT\n" +
                    ");",
            "CREATE INDEX UltimeCircolariIndiceSecondario ON Circolari (\n" +
                    "\tdata\tDESC,\n" +
                    "\tid\tDESC\n" +
                    ");",
            "CREATE UNIQUE INDEX prenotazioniAuleIndiceSecondario ON PrenotazioniAule (\n" +
                    "\tdata\tASC,\n" +
                    "\toraInizio\tASC,\n" +
                    "\tnumeroAula\tASC\n" +
                    ");"};
}
