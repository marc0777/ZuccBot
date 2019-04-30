package zuccbot.db;

public class Create {
    static final String[] SQL = {
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
                    "\tday\tINTEGER CHECK(day Between 0 and 6),\n" +
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
                    "\tuserType\tINTEGER,\n" +
                    "\tPRIMARY KEY(idTelegram)\n" +
                    ");",

            "CREATE TABLE Events (\n" +
                    "\tID\tINTEGER NOT NULL UNIQUE,\n" +
                    "\tType\tTEXT NOT NULL,\n" +
                    "\tClass\tTEXT NOT NULL CHECK(Class >= 1 and Class < 6),\n" +
                    "\tDate\tINTEGER NOT NULL\n" +
                    ");",
            "CREATE TABLE Homework (\n" +
                    "\tID\tINTEGER NOT NULL UNIQUE,\n" +
                    "\tSubject\tTEXT NOT NULL,\n" +
                    "\tText\tINTEGER NOT NULL\n" +
                    ");",
            "CREATE TABLE Tests (\n" +
                    "\tID\tINTEGER NOT NULL UNIQUE,\n" +
                    "\tSubject\tTEXT NOT NULL,\n" +
                    "\tArguments\tTEXT\n" +
                    ");",
            "CREATE TABLE Activities (\n" +
                    "\tID\tINTEGER NOT NULL UNIQUE,\n" +
                    "\tArgument\tTEXT NOT NULL\n" +
                    ");",
            "CREATE TABLE MissHours (\n" +
                    "\tID\tINTEGER NOT NULL UNIQUE,\n" +
                    "\tHourNumber\tINTEGER NOT NULL,\n" +
                    "\tSubject\tTEXT\n" +
                    ");",
            "CREATE TABLE `Feedback` (\n" +
                    "\t`idTelegram`\tINTEGER NOT NULL,\n" +
                    "\t`text`\tTEXT,\n" +
                    "\tPRIMARY KEY(`idTelegram`)\n" +
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
                    ");"
    };
}
