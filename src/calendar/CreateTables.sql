CREATE TABLE "PERSON" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(255),
    "EMAIL" VARCHAR(255),
    "NOTE" VARCHAR(255)
);

CREATE TABLE "EVENT" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "NAME" VARCHAR(255),
    "STARTDATE" TIMESTAMP,
    "ENDDATE" TIMESTAMP,
    "NOTE" VARCHAR(255)
);

CREATE TABLE "ATTENDANCE" (
    "ID" INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "EVENTID" INTEGER REFERENCES EVENT (ID),
    "PERSONID" INTEGER REFERENCES PERSON (ID),
    "PLANNEDARRIVALTIME" TIMESTAMP
);