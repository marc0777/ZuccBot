package zuccbot.db;

public class create {
    static final String[] sql = {
            "CREATE TABLE Rooms (\n" +
                    "\troomNumber\tINTEGER,\n" +
                    "\tdescription\tTEXT,\n" +
                    "\tlab\tINTEGER DEFAULT 0,\n" +
                    "\tbookable\tINTEGER DEFAULT 0,\n" +
                    "\tPRIMARY KEY(roomNumber)\n" +
                    ");",
            "CREATE TABLE Newsletter (\n" +
                    "\tid\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\tdate\tINTEGER,\n" +
                    "\ttitle\tTEXT,\n" +
                    "\tlink\tTEXT UNIQUE,\n" +
                    "\tdescription\tTEXT,\n" +
                    "\tattachments\tTEXT\n" +
                    ");",
            "CREATE TABLE Week (\n" +
                    "\tday\tINTEGER CHECK(day Between 1 and 6),\n" +
                    "\tname\tTEXT,\n" +
                    "\tPRIMARY KEY(day)\n" +
                    ");",
            "CREATE TABLE TimeTable (\n" +
                    "\tclass\tINTEGER,\n" +
                    "\tsection\tTEXT,\n" +
                    "\tday\tINTEGER,\n" +
                    "\thourNumber\tINTEGER,\n" +
                    "\tsubject\tTEXT,\n" +
                    "\troom\tINTEGER,\n" +
                    "\tFOREIGN KEY(day) REFERENCES Week(day),\n" +
                    "\tPRIMARY KEY(class,day,hourNumber),\n" +
                    "\tFOREIGN KEY(class) REFERENCES Rooms(roomNumber)\n" +
                    ");",
            "CREATE TABLE Preferences (\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\tschoolTime\tINTEGER,\n" +
                    "\tnewsletter\tINTEGER,\n" +
                    "\tdelegatesNews\tINTEGER,\n" +
                    "\tlastReadNewsletter\tINTEGER,\n" +
                    "\tPRIMARY KEY(idTelegram)\n" +
                    ");",
            "CREATE TABLE BookedRooms (\n" +
                    "\troomNumber\tINTEGER,\n" +
                    "\tdate\tINTEGER,\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\tstartHour\tINTEGER,\n" +
                    "\tendHour\tINTEGER\n" +
                    ");",
            "CREATE TABLE Advisory (\n" +
                    "\tdate\tINTEGER,\n" +
                    "\tidTelegram\tINTEGER,\n" +
                    "\tanonymous\tINTEGER,\n" +
                    "\tobject\tTEXT,\n" +
                    "\tcontent\tTEXT,\n" +
                    "\tPRIMARY KEY(idTelegram,date)\n" +
                    ");",
            "CREATE TABLE User (\n" +
                    "\tidTelegram\tINTEGER NOT NULL,\n" +
                    "\tname\tTEXT,\n" +
                    "\tsurname\tTEXT,\n" +
                    "\tclass\tINTEGER,\n" +
                    "\tsection\tTEXT,\n" +
                    "\tPRIMARY KEY(idTelegram)\n" +
                    ");",
            "CREATE TABLE sqlite_sequence (\n" +
                    "\tname\tTEXT,\n" +
                    "\tseq\tTEXT\n" +
                    ");",
            "CREATE INDEX UltimenewsletterIndiceSecondario ON newsletter (\n" +
                    "\tdate\tDESC,\n" +
                    "\tid\tDESC\n" +
                    ");",
            "CREATE UNIQUE INDEX BookedClassesIndiceSecondario ON BookedClasses (\n" +
                    "\tdate\tASC,\n" +
                    "\tstartHour\tASC,\n" +
                    "\tclassNumber\tASC\n" +
                    ");"};
}
