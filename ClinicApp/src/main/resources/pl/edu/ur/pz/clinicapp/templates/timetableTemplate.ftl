<html lang="pl">
<head>
    <meta charset="UTF-8">

    <style>
        body {
            font-family: Calibri, sans-serif;
        }

        img {
            width: 80%;
            margin: auto;
            display: block;
        }
    </style>
</head>
<body>


<div>

    <h2>Terminarz na ${startDate} - ${endDate}</h2>
        <img src="${header}" alt="Kolumny">
        <img src="${timetable}" alt="Terminarz">
    <br>

    <p>Wygenerowano ${.now?string("dd.MM.yyyy hh:mm")}</p>
</div>
</body>
</html>
